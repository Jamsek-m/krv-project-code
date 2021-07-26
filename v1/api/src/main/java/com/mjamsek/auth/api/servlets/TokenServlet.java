package com.mjamsek.auth.api.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjamsek.auth.lib.BasicCredentialsWrapper;
import com.mjamsek.auth.lib.enums.TokenGrantType;
import com.mjamsek.auth.lib.requests.token.AuthorizationCodeRequest;
import com.mjamsek.auth.lib.requests.token.ClientCredentialsRequest;
import com.mjamsek.auth.lib.requests.token.PasswordRequest;
import com.mjamsek.auth.lib.requests.token.RefreshTokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.auth.services.TokenService;
import com.mjamsek.auth.utils.HttpUtil;
import com.mjamsek.rest.exceptions.RestException;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.TOKEN_SERVLET_PATH;

@WebServlet(name = "token-servlet", urlPatterns = TOKEN_SERVLET_PATH, loadOnStartup = 1)
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
        try {
            TokenResponse tokenResponse = processRequestPayload(req);
            prepareResponse(resp, Response.Status.OK.getStatusCode(), tokenResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Missing or malformed request permission!");
            prepareResponse(resp, Response.Status.UNAUTHORIZED.getStatusCode(), body);
        } catch (RestException e) {
            prepareResponse(resp, e.getStatus(), e.getResponse());
        }
    }
    
    private <T> void prepareResponse(HttpServletResponse resp, int status, T payload) throws IOException {
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setStatus(status);
        resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        resp.setHeader("Pragma", "no-cache");
    
        String serializedPayload = objectMapper.writeValueAsString(payload);
        try (PrintWriter pw = resp.getWriter()) {
            pw.print(serializedPayload);
        }
    }
    
    private TokenResponse processRequestPayload(HttpServletRequest req) throws IllegalArgumentException {
        TokenGrantType grantType = TokenGrantType.fromString(req.getParameter(GRANT_TYPE_PARAM))
            .orElseThrow(() -> new RestException("Unrecognized grant_type!"));
        
        if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
            var request = AuthorizationCodeRequest.Builder.newBuilder()
                .code(req.getParameter(AUTHORIZATION_CODE_PARAM))
                .clientId(req.getParameter(CLIENT_ID_PARAM))
                .build();
            return tokenService.authorizationGrant(request);
        } else if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
            var request = RefreshTokenRequest.Builder.newBuilder()
                .refreshToken(req.getParameter(REFRESH_TOKEN_PARAM))
                .scope(req.getParameter(SCOPE_PARAM))
                .build();
            return tokenService.refreshTokenGrant(request);
        } else if (grantType.equals(TokenGrantType.PASSWORD)) {
            var request = PasswordRequest.Builder.newBuilder()
                .scope(req.getParameter(SCOPE_PARAM))
                .clientId(req.getParameter(CLIENT_ID_PARAM))
                .username(req.getParameter(USERNAME_PARAM))
                .password(req.getParameter(PASSWORD_PARAM))
                .build();
            return tokenService.passwordGrant(request);
        } else if (grantType.equals(TokenGrantType.CLIENT_CREDENTIALS)) {
            return parseClientCredentials(req);
        }
        
        throw new RestException("Unrecognized grant_type!");
    }
    
    private TokenResponse parseClientCredentials(HttpServletRequest req) {
        BasicCredentialsWrapper credentials = HttpUtil.getCredentialsFromRequest(req)
            .flatMap(basicAuth -> {
                String decodedAuth = new String(Base64.getDecoder().decode(basicAuth));
                String[] authParts = decodedAuth.split(":");
                if (authParts.length == 2) {
                    return Optional.of(new BasicCredentialsWrapper(authParts[0], authParts[1]));
                }
                return Optional.empty();
            })
            .or(() -> {
                String clientId = req.getParameter(CLIENT_ID_PARAM);
                String clientSecret = req.getParameter(CLIENT_SECRET_PARAM);
                if (clientId != null && clientSecret != null) {
                    return Optional.of(new BasicCredentialsWrapper(clientId, clientSecret));
                }
                return Optional.empty();
            })
            .orElseThrow(() -> new RestException("Malformed or missing authentication parameters!"));
        
        var request = ClientCredentialsRequest.Builder.newBuilder()
            .scope(req.getParameter(SCOPE_PARAM))
            .clientId(credentials.getUsername())
            .clientSecret(credentials.getPassword())
            .build();
        return tokenService.clientCredentialsGrant(request);
    }
    
}
