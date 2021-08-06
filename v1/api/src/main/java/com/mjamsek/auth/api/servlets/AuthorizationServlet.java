package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.context.AuthorizationFlowContext;
import com.mjamsek.auth.api.servlets.utils.ConsentSupplier;
import com.mjamsek.auth.api.servlets.utils.ServletUtil;
import com.mjamsek.auth.lib.enums.PKCEMethod;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.services.AuthorizationService;
import com.mjamsek.auth.services.SessionService;
import com.mjamsek.auth.utils.HttpUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.CookieConstants.SESSION_COOKIE;
import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.*;

@WebServlet(name = "authorization-servlet", urlPatterns = AUTHORIZATION_SERVLET_PATH, loadOnStartup = 1)
@RequestScoped
public class AuthorizationServlet extends HttpServlet {
    
    @Inject
    private SessionService sessionService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String prompt = req.getParameter(PROMPT_PARAM);
        Optional<Cookie> sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies());
        
        AuthorizationFlowContext context = (AuthorizationFlowContext) req.getAttribute(AuthorizationFlowContext.CONTEXT_ID);
        
        if (sessionCookie.isPresent()) {
            // If session cookie is present, check for existing (valid) session
            Optional<SessionEntity> existingSession = sessionService.getSession(sessionCookie.get().getValue(), req.getRemoteAddr());
            if (existingSession.isPresent() && existingSession.get().getUser() != null) {
                handleExistingSession(req, resp, existingSession.get());
                return;
            }
            // If session cookie is present, but does not point to valid
            // session (ie. same user ip), continue as if no cookie was set
        }
        
        // If prompt=none, user cannot be asked for credentials and since no valid cookies
        // are provided, throw error instead
        if (prompt != null && prompt.equals("none")) {
            ClientEntity client = getClientFromRequest(req);
            returnErrorToClient(req, resp, client, "login_required");
            return;
        }
        
        // No session information is provided, assume no session is connected to user agent
        // Start of a new session
        createSession(req, resp);
        
        // Redirect to login page
        Map<String, String[]> params = new HashMap<>(req.getParameterMap());
        // Retrieve request id from param or from context
        String requestId = Optional.ofNullable(req.getParameter(REQUEST_ID_PARAM))
            .or(() -> Optional.ofNullable(context.getRequestId()))
            .orElse(null);
        params.put(REQUEST_ID_PARAM, new String[]{requestId});
        
        resp.sendRedirect(PASSWORD_LOGIN_SERVLET_PATH + HttpUtil.formatQueryParams(params));
    }
    
    private void createSession(HttpServletRequest req, HttpServletResponse resp) {
        SessionEntity session = sessionService.startSession(req.getRemoteAddr());
        resp.addCookie(ServletUtil.createSessionCookie(session.getId()));
    }
    
    private void handleExistingSession(HttpServletRequest req, HttpServletResponse resp, SessionEntity session) throws IOException, ServletException {
        String prompt = req.getParameter(PROMPT_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String pkceChallenge = req.getParameter(CODE_CHALLENGE_PARAM);
        AuthorizationFlowContext context = (AuthorizationFlowContext) req.getAttribute(AuthorizationFlowContext.CONTEXT_ID);
        PKCEMethod pkceMethod = context.getPkceMethod();
        
        if (prompt != null && prompt.equals("none")) {
            
            // If client requires consent and user has not yet granted it, throw error
            boolean terminate = checkConsent(session.getUser().getId(), clientId, () -> {
                ClientEntity client = getClientFromRequest(req);
                returnErrorToClient(req, resp, client, "consent_required");
                return true;
            });
            if (terminate) {
                return;
            }
            
            // If consent was granted, perform silent authentication
            
            AuthorizationRequestEntity request = authorizationService.initializeSessionRequest(clientId, session.getId(), req.getRemoteAddr(), pkceChallenge, pkceMethod);
            silentAuthentication(req, resp, session, request);
            return;
        } else if (prompt != null && prompt.equals("login")) {
            // Client explicitly demand a new login prompt
            resp.sendRedirect(PASSWORD_LOGIN_SERVLET_PATH + HttpUtil.formatQueryParams(req.getParameterMap()));
            return;
        }
        
        // Server recognized valid session and no special actions regarding login have been required,
        // therefore silent login is preferred, ie. authenticate user automatically without asking again for credentials
        AuthorizationRequestEntity request = authorizationService.initializeSessionRequest(clientId, session.getId(), req.getRemoteAddr(), pkceChallenge, pkceMethod);
        
        // If user has not yet consented, redirect to consent page, otherwise perform silent login
        boolean terminate = checkConsent(session.getUser().getId(), clientId, () -> {
            String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
            resp.sendRedirect(CONSENT_SERVLET_PATH + ServletUtil.buildConsentUriParams(request, redirectUri));
            return true;
        });
        if (terminate) {
            return;
        }
        
        // If user has already consented to client, perform silent authentication
        silentAuthentication(req, resp, session, request);
    }
    
    /**
     * @param userId           id of a user
     * @param clientId         id of a client
     * @param onRequireConsent returns <code>true</code>, if stream should be terminated, after invocation
     * @return true if stream should be terminated after invocation
     */
    private boolean checkConsent(String userId, String clientId, ConsentSupplier onRequireConsent) throws IOException, ServletException {
        boolean consentRequired = authorizationService.checkIfConsentRequired(clientId);
        if (consentRequired) {
            Optional<ClientConsentEntity> consent = authorizationService.getClientConsent(userId, clientId);
            if (consent.isEmpty()) {
                return onRequireConsent.onRequiredConsent();
            }
        }
        return false;
    }
    
    private void returnErrorToClient(HttpServletRequest req, HttpServletResponse resp, ClientEntity client, String error) throws IOException {
        // Redirect back to client with authorization code
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        // Generate authorization code
        
        boolean validRedirectUri = authorizationService.validateRedirectUri(redirectUri, client);
        // If redirect URI is correct, then redirect back to client, with code attached
        if (validRedirectUri) {
            // Redirect back to client
            resp.sendRedirect(redirectUri + ServletUtil.buildErrorParams(error));
            return;
        }
        
        // If redirect uri is not valid for given client, throw error
        resp.setStatus(401);
        // resp.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams("invalid_redirect_uri"));
    }
    
    private ClientEntity getClientFromRequest(HttpServletRequest req) {
        AuthorizationFlowContext context = (AuthorizationFlowContext) req.getAttribute(AuthorizationFlowContext.CONTEXT_ID);
        return Optional.ofNullable(req.getParameter(REQUEST_ID_PARAM))
            .or(() -> {
                if (context != null) {
                    return Optional.ofNullable(context.getRequestId());
                }
                return Optional.empty();
            })
            .flatMap(requestId -> authorizationService.getRequestEntityById(requestId))
            .map(AuthorizationRequestEntity::getClient)
            .orElseThrow();
    }
    
    private void silentAuthentication(HttpServletRequest req, HttpServletResponse resp, SessionEntity session, AuthorizationRequestEntity request) throws IOException {
        // Redirect back to client with authorization code
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        // Generate authorization code
        
        boolean validRedirectUri = authorizationService.validateRedirectUri(redirectUri, request.getClient());
        // If redirect URI is correct, then redirect back to client, with code attached
        if (validRedirectUri) {
            // Create new session cookie (extends session lifetime)
            resp.addCookie(ServletUtil.createSessionCookie(session.getId()));
            // Redirect back to client
            resp.sendRedirect(redirectUri + ServletUtil.buildRedirectUriParams(request, session));
            return;
        }
        
        // If redirect uri is not valid for given client, throw error
        resp.setStatus(401);
        resp.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams("invalid_redirect_uri"));
    }
    
}
