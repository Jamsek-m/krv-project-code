package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.AuthorizationService;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.services.CredentialsService;
import com.mjamsek.auth.utils.HttpUtil;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.CONSENT_SERVLET_PATH;
import static com.mjamsek.auth.lib.constants.ServerPaths.PASSWORD_LOGIN_SERVLET_PATH;

@WebServlet(name = "password-login-servlet", urlPatterns = PASSWORD_LOGIN_SERVLET_PATH)
@RequestScoped
public class PasswordLoginServlet extends HttpServlet {
    
    @Inject
    private CredentialsService credentialsService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private ClientService clientService;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String username = req.getParameter(USERNAME_PARAM);
        String password = req.getParameter(PASSWORD_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        
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
        
        // throws exception if credentials don't match (TODO: refactor to handle error gracefully)
        UserEntity user = credentialsService.checkPasswordCredentials(username, password);
        
        boolean consentRequired = authorizationService.checkIfConsentRequired(clientId);
        if (!consentRequired) {
            // Consent not required, redirect directly back to client
            redirectBack(resp, requestId, user.getId(), redirectUri, client);
            return;
        }
        
        // Check if user allowed this client
        Optional<ClientConsentEntity> consent = authorizationService.getClientConsent(user.getId(), clientId);
        if (consent.isPresent()) {
            // User already consented to this client
            redirectBack(resp, requestId, user.getId(), redirectUri, client);
        } else {
            // User hasn't consented yet.
            redirectToConsentPage(resp, requestId, user.getId(), redirectUri);
        }
        
    }
    
    private void redirectToConsentPage(HttpServletResponse resp, String requestId, String userId, String redirectUri) throws IOException {
        // Add code to request (user's credentials are already validated at this point)
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        // Redirect to consent page
        resp.sendRedirect(CONSENT_SERVLET_PATH + buildConsentUriParams(request, redirectUri));
    }
    
    private void redirectBack(HttpServletResponse resp, String requestId, String userId, String redirectUri, ClientEntity client) throws IOException {
        // Generate authorization code
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        boolean validRedirectUri = authorizationService.validateRedirectUri(redirectUri, client);
        // If redirect URI is correct, then redirect back to client, with code attached
        if (validRedirectUri) {
            resp.sendRedirect(redirectUri + buildRedirectUriParams(request));
            return;
        }
        throw new UnauthorizedException("Invalid redirect URI!");
    }
    
    private String buildRedirectUriParams(AuthorizationRequestEntity request) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(AUTHORIZATION_CODE_PARAM, new String[]{request.getCode()});
        return HttpUtil.formatQueryParams(params);
    }
    
    private String buildConsentUriParams(AuthorizationRequestEntity request, String redirectUri) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(CLIENT_ID_PARAM, new String[]{request.getClient().getClientId()});
        params.put(REDIRECT_URI_PARAM, new String[]{redirectUri});
        return HttpUtil.formatQueryParams(params);
    }
    
}
