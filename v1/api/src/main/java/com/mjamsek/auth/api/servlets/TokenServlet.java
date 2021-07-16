package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.lib.requests.TokenRequest;
import com.mjamsek.auth.services.TokenService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "token-servlet", urlPatterns = "/token")
@RequestScoped
public class TokenServlet extends HttpServlet {
    
    @Inject
    private TokenService tokenService;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TokenRequest tokenRequest = parseRequestPayload(req);
        
        
        
    
    
    
    
    
    }
    
    private TokenRequest parseRequestPayload(HttpServletRequest req) {
        TokenRequest payload = new TokenRequest();
        payload.setCode(req.getParameter("code"));
        payload.setGrantType(req.getParameter("grant_type"));
        payload.setRedirectUri(req.getParameter("redirect_uri"));
        return payload;
    }
    
}
