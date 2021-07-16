package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.lib.requests.AuthorizationRequest;

import javax.enterprise.context.RequestScoped;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "authz", urlPatterns = "/auth")
@RequestScoped
public class AuthServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher dispatcher = context.getRequestDispatcher("/authorization.html");
        dispatcher.forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthorizationRequest payload = parseRequestBody(req);
        
        String token = "123";
        
        resp.sendRedirect("/error?err=temp%20redirect");
    }
    
    private AuthorizationRequest parseRequestBody(HttpServletRequest req) {
        AuthorizationRequest payload = new AuthorizationRequest();
        
        payload.setClientId(req.getParameter("client_id"));
        payload.setScope(req.getParameter("scope"));
        payload.setRedirectUri(req.getParameter("redirect_uri"));
        payload.setResponseType(req.getParameter("response_type"));
        payload.setState(req.getParameter("state"));
        
        return payload;
    }
}
