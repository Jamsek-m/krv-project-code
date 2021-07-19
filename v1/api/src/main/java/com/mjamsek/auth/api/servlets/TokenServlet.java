package com.mjamsek.auth.api.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjamsek.auth.lib.enums.TokenGrantType;
import com.mjamsek.auth.lib.requests.TokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.auth.services.TokenService;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import java.util.Base64;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.TOKEN_SERVLET_PATH;

@WebServlet(name = "token-servlet", urlPatterns = TOKEN_SERVLET_PATH)
@RequestScoped
public class TokenServlet extends HttpServlet {
    
    @Inject
    private TokenService tokenService;
    
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TokenResponse tokenResponse = processRequestPayload(req);
        String payload = objectMapper.writeValueAsString(tokenResponse);
        
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setStatus(Response.Status.OK.getStatusCode());
        try (PrintWriter pw = resp.getWriter()) {
            pw.print(payload);
        }
    }
    
    private TokenResponse processRequestPayload(HttpServletRequest req) {
        TokenGrantType grantType = TokenGrantType.fromString(req.getParameter("grant_type"));
        switch (grantType) {
            case AUTHORIZATION_CODE:
                return tokenService.authorizationFlow(parseAuthorizationCodeGrant(grantType, req));
            case CLIENT_CREDENTIALS:
                return tokenService.clientCredentialsFlow(parseClientCredentialsGrant(grantType, req));
            default:
            case PASSWORD:
                return tokenService.passwordFlow(parsePasswordGrant(grantType, req));
        }
    }
    
    private TokenRequest.PasswordTokenRequest parsePasswordGrant(TokenGrantType grantType, HttpServletRequest req) {
        TokenRequest.PasswordTokenRequest token = new TokenRequest.PasswordTokenRequest();
        token.setGrantType(grantType);
        token.setPassword(req.getParameter(PASSWORD_PARAM));
        token.setUsername(req.getParameter(USERNAME_PARAM));
        return token;
    }
    
    private TokenRequest.ClientCredentialsTokenRequest parseClientCredentialsGrant(TokenGrantType grantType, HttpServletRequest req) {
        TokenRequest.ClientCredentialsTokenRequest token = new TokenRequest.ClientCredentialsTokenRequest();
        token.setGrantType(grantType);
        
        String basicCredentials = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (basicCredentials.startsWith("Basic")) {
            basicCredentials = basicCredentials.replace("Basic ", "");
        }
        
        String decodedCreds = new String(Base64.getDecoder().decode(basicCredentials));
        String[] credentialParts = decodedCreds.split(":");
        if (credentialParts.length != 2) {
            throw new UnauthorizedException("Invalid credentials!");
        }
        token.setClientId(credentialParts[0]);
        token.setClientSecret(credentialParts[1]);
        return token;
    }
    
    private TokenRequest.AuthorizationCodeTokenRequest parseAuthorizationCodeGrant(TokenGrantType grantType, HttpServletRequest req) {
        TokenRequest.AuthorizationCodeTokenRequest token = new TokenRequest.AuthorizationCodeTokenRequest();
        token.setGrantType(grantType);
        token.setCode(req.getParameter(AUTHORIZATION_CODE_PARAM));
        return token;
    }
    
}
