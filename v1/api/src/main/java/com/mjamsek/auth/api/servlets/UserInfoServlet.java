package com.mjamsek.auth.api.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjamsek.auth.config.OidcServerConfig;
import com.mjamsek.auth.lib.responses.UserInfoResponse;
import com.mjamsek.auth.mappers.UserMapper;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.TokenService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.auth.utils.HttpUtil;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;

import static com.mjamsek.auth.lib.constants.ServerPaths.USERINFO_SERVLET_PATH;

@WebServlet(name = "userinfo-servlet", urlPatterns = USERINFO_SERVLET_PATH, loadOnStartup = 1)
@RequestScoped
public class UserInfoServlet extends HttpServlet {
    
    @Inject
    private TokenService tokenService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private OidcServerConfig serverConfig;
    
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processUserInfo(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processUserInfo(req, resp);
    }
    
    private void processUserInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String token = HttpUtil.getCredentialsFromRequest(req)
                .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
    
            Jws<Claims> parsedToken = tokenService.validateToken(token)
                .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
    
            UserEntity user = userService.getUserEntityById(parsedToken.getBody().getSubject())
                .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
    
            UserInfoResponse userInfo = new UserInfoResponse();
            userInfo.setIssuer(serverConfig.getIssuer());
            userInfo.setSubject(user.getId());
            userInfo.setEmail(user.getEmail());
            userInfo.setFamilyName(user.getLastName());
            userInfo.setGivenName(user.getFirstName());
            userInfo.setName(user.getFirstName() + " " + user.getLastName());
            userInfo.setPreferredUsername(user.getUsername());
            userInfo.setAttributes(UserMapper.attrsToMap(user.getAttributes()));
    
            resp.setContentType(MediaType.APPLICATION_JSON);
            resp.setStatus(Response.Status.OK.getStatusCode());
            try(PrintWriter pw = resp.getWriter()) {
                pw.print(objectMapper.writeValueAsString(userInfo));
            }
        } catch (UnauthorizedException e) {
            resp.setContentType(MediaType.APPLICATION_JSON);
            resp.setStatus(e.getStatus());
            try(PrintWriter pw = resp.getWriter()) {
                pw.print(objectMapper.writeValueAsString(e.getResponse()));
            }
        }
    }
    
}
