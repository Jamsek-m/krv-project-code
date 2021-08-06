package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.servlets.utils.ConsentSupplier;
import com.mjamsek.auth.api.servlets.utils.ServletUtil;
import com.mjamsek.auth.lib.enums.ErrorCode;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.*;
import com.mjamsek.auth.utils.HttpUtil;
import com.mjamsek.rest.exceptions.UnauthorizedException;

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

@WebServlet(name = "password-login-servlet", urlPatterns = PASSWORD_LOGIN_SERVLET_PATH)
@RequestScoped
public class PasswordLoginServlet extends HttpServlet {
    
    @Inject
    private CredentialsService credentialsService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private TemplateService templateService;
    
    @Inject
    private SessionService sessionService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String error = req.getParameter(ERROR_PARAM);
        String errorDescription = ErrorCode.fromCode(error)
            .map(ErrorCode::description)
            .orElse(error);
        
        if (clientId == null) {
            resp.setStatus(401);
            resp.sendRedirect(ERROR_SERVLET_PATH + ServletUtil.buildErrorParams("invalid_client_id", requestId));
            return;
        }
        Optional<ClientEntity> clientOpt = clientService.getClientByClientId(clientId);
        if (clientOpt.isEmpty()) {
            resp.setStatus(401);
            resp.sendRedirect(ERROR_SERVLET_PATH + ServletUtil.buildErrorParams("invalid_client_id", requestId));
            return;
        }
        ClientEntity client = clientOpt.get();
        String requestedClientScopes = client.getScopes().stream()
            .map(ClientScopeEntity::getName)
            .reduce("", String::concat);
        
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        params.put("clientName", client.getName());
        params.put("requestId", requestId);
        params.put("redirectUri", redirectUri);
        params.put("error", errorDescription);
        params.put("scopeValue", requestedClientScopes);
        String htmlContent = templateService.renderHtml("password_login", params);
        
        ServletUtil.renderHtml(htmlContent, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter(USERNAME_PARAM);
        String password = req.getParameter(PASSWORD_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        // String rememberMe = req.getParameter(REMEMBER_ME_PARAM);
        
        if (requestId == null) {
            throw new UnauthorizedException("Invalid request state!");
        }
        
        if (clientId == null) {
            throw new UnauthorizedException("Unknown client!");
        }
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
        if (redirectUri == null) {
            throw new UnauthorizedException("Invalid redirect URI!");
        }
        
        try {
            UserEntity user = credentialsService.checkPasswordCredentials(username, password);
            
            // If user is validated, associate session with user and set cookie
            Cookie sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies())
                .orElseThrow(() -> new UnauthorizedException("Invalid session state!"));
            SessionEntity session = sessionService.associateUserWithSession(sessionCookie.getValue(), user.getId());
            resp.addCookie(ServletUtil.createSessionCookie(session.getId()));
            
            // If user requires consent, redirect to consent page
            boolean terminate = checkConsent(user.getId(), clientId, () -> {
                redirectToConsentPage(resp, requestId, user.getId(), redirectUri);
                return true;
            });
            
            if (terminate) {
                return;
            }
    
            // Consent not required, redirect directly back to client
            redirectBackToClient(resp, requestId, user.getId(), redirectUri, client, session);
            
        }  catch (UnauthorizedException e) {
            resp.sendRedirect(PASSWORD_LOGIN_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code(), req.getParameterMap()));
        }
    }
    
    private void redirectToConsentPage(HttpServletResponse resp, String requestId, String userId, String redirectUri) throws IOException {
        // Add code to request (user's credentials are already validated at this point)
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        // Redirect to consent page
        
        resp.sendRedirect(CONSENT_SERVLET_PATH + ServletUtil.buildConsentUriParams(request, redirectUri));
    }
    
    private void redirectBackToClient(HttpServletResponse resp, String requestId, String userId, String redirectUri, ClientEntity client, SessionEntity session) throws IOException {
        // Generate authorization code
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        boolean validRedirectUri = authorizationService.validateRedirectUri(redirectUri, client);
        // If redirect URI is correct, then redirect back to client, with code attached
        if (validRedirectUri) {
            resp.sendRedirect(redirectUri + ServletUtil.buildRedirectUriParams(request, session));
            return;
        }
    
        resp.setStatus(401);
        resp.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams("Invalid redirect URI!"));
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
    
}
