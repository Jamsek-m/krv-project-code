package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.servlets.utils.ServletUtil;
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
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.CookieConstants.SESSION_COOKIE;
import static com.mjamsek.auth.lib.constants.RequestConstants.POST_LOGOUT_REDIRECT_URI_PARAM;
import static com.mjamsek.auth.lib.constants.ServerPaths.END_SESSION_SERVLET_PATH;

@WebServlet(name = "end-session-servlet", urlPatterns = END_SESSION_SERVLET_PATH, loadOnStartup = 1)
@RequestScoped
public class EndSessionServlet extends HttpServlet {
    
    @Inject
    private SessionService sessionService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Cookie> sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies());
        sessionCookie.ifPresent(cookie -> {
            sessionService.endSession(cookie.getValue());
            resp.addCookie(ServletUtil.clearSessionCookie());
        });
        
        String postLogoutUri = req.getParameter(POST_LOGOUT_REDIRECT_URI_PARAM);
        if (postLogoutUri != null) {
            resp.sendRedirect(postLogoutUri);
            return;
        }
        
        resp.setStatus(204);
    }
    
}

