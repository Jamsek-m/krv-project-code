package com.mjamsek.auth.api.servlets;

import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.ServerPaths.USERINFO_SERVLET_PATH;

@WebServlet(name = "userinfo-servlet", urlPatterns = USERINFO_SERVLET_PATH)
public class UserInfoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = getCredentialsFromRequest(req)
            .orElseThrow(() -> new UnauthorizedException(""));
        
        
        
    }
    
    Optional<String> getCredentialsFromRequest(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        return Optional.ofNullable(authorizationHeader)
            .map(String::trim)
            .map(headerValue -> {
                if (headerValue.startsWith("Bearer")) {
                    return headerValue.replaceAll("Bearer ", "");
                }
                return headerValue;
            });
    }
}
