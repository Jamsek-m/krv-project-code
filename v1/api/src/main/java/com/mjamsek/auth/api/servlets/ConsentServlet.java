package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.lib.enums.RequestConsentState;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
import com.mjamsek.auth.services.AuthorizationService;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.services.TemplateService;
import com.mjamsek.auth.utils.HttpUtil;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.*;

@WebServlet(name = "consent-servlet", urlPatterns = CONSENT_SERVLET_PATH)
@RequestScoped
public class ConsentServlet extends HttpServlet {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private TemplateService templateService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
    
        if (requestId == null) {
            throw new UnauthorizedException("Invalid request state!");
        }
    
        authorizationService.getRequestEntityById(requestId)
            .orElseThrow(() -> new UnauthorizedException("Invalid request state!"));
    
        if (clientId == null) {
            throw new UnauthorizedException("Unknown client!");
        }
    
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
    
        if (redirectUri == null) {
            throw new UnauthorizedException("Invalid redirect URI!");
        }
        
        List<String> clientScopes = client.getScopes().stream().map(ClientScopeEntity::getName).collect(Collectors.toList());
        Map<String, Object> params = new HashMap<>();
        params.put("clientName", client.getName());
        params.put("requestId", requestId);
        params.put("clientId", client.getClientId());
        params.put("redirectUri", redirectUri);
        params.put("scopes", clientScopes);
        String htmlContent = templateService.renderHtml("consent", params);
    
        resp.setContentType(MediaType.TEXT_HTML);
        resp.setStatus(Response.Status.OK.getStatusCode());
        resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        resp.setHeader("Pragma", "no-cache");
    
        try(PrintWriter pw = resp.getWriter()) {
            pw.print(htmlContent);
        }
    
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String consent = req.getParameter("consent");
        
        if (requestId == null) {
            throw new UnauthorizedException("Invalid request state!");
        }
        
        AuthorizationRequestEntity request = authorizationService.getRequestEntityById(requestId)
            .orElseThrow(() -> new UnauthorizedException("Invalid request state!"));
        
        if (clientId == null) {
            throw new UnauthorizedException("Unknown client!");
        }
        
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
        
        if (redirectUri == null) {
            throw new UnauthorizedException("Invalid redirect URI!");
        }
        
        boolean validRedirectUri = authorizationService.validateRedirectUri(redirectUri, client);
        // If redirect URI is correct, then redirect back to client, with code attached
        if (validRedirectUri) {
            if (consent.equals(RequestConsentState.ALLOWED.name())) {
                authorizationService.addClientConsent(request.getUser().getId(), clientId);
                resp.sendRedirect(redirectUri + buildRedirectUriParams(request));
            } else {
                resp.sendRedirect(redirectUri + buildErrorRedirectUriParams(request, "Consent for client was rejected!"));
            }
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
    
    private String buildErrorRedirectUriParams(AuthorizationRequestEntity request, String errorMessage) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(ERROR_PARAM, new String[]{HttpUtil.encodeURI(errorMessage)});
        return HttpUtil.formatQueryParams(params);
    }
}
