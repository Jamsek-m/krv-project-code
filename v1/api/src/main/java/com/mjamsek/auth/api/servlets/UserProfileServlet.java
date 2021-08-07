package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.servlets.utils.ServletUtil;
import com.mjamsek.auth.lib.enums.ErrorCode;
import com.mjamsek.auth.lib.requests.UserProfileUpdateRequest;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.RoleService;
import com.mjamsek.auth.services.SessionService;
import com.mjamsek.auth.services.TemplateService;
import com.mjamsek.auth.services.UserService;
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
import java.util.*;

import static com.mjamsek.auth.lib.constants.CookieConstants.SESSION_COOKIE;
import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.*;
import static com.mjamsek.auth.utils.StringUtil.fieldValue;
import static com.mjamsek.auth.utils.StringUtil.fieldValueOrElse;

@WebServlet(name = "user-profile-servlet", urlPatterns = PROFILE_SERVLET_PATH)
@RequestScoped
public class UserProfileServlet extends HttpServlet {
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionService sessionService;
    
    @Inject
    private RoleService roleService;
    
    @Inject
    private TemplateService templateService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(POST_PROFILE_REDIRECT_URI_PARAM);
        if (redirectUri == null) {
            redirectUri = PROFILE_SERVLET_PATH;
        }
        
        String errorDescription = ErrorCode.fromCode(fieldValue(req.getParameter(ERROR_PARAM)))
            .map(ErrorCode::description)
            .orElse(null);
        
        Map<String, Object> params = new HashMap<>();
        params.put("formAction", PROFILE_SERVLET_PATH);
        params.put("redirectUri", redirectUri);
        params.put("logoutUri", END_SESSION_SERVLET_PATH);
        params.put("postLogoutRedirectUriParam", POST_LOGOUT_REDIRECT_URI_PARAM);
        params.put("error", errorDescription);
        
        Optional<SessionEntity> activeSession = getActiveSession(req);
        if (activeSession.isPresent()) {
            UserEntity user = activeSession.get().getUser();
            
            params.put("email", fieldValueOrElse(user.getEmail(), ""));
            params.put("firstName", fieldValueOrElse(user.getFirstName(), ""));
            params.put("lastName", fieldValueOrElse(user.getLastName(), ""));
            params.put("avatar", fieldValueOrElse(user.getAvatar(), ""));
            params.put("username", user.getUsername());
            
            Set<String> userScopes = roleService.getUserScopes(activeSession.get().getUser().getId());
            params.put("isAdmin", userScopes.contains("admin"));
            
            String htmlContent = templateService.renderHtml("user_profile", params);
            ServletUtil.renderHtml(htmlContent, resp);
            return;
        }
        
        resp.sendRedirect(ERROR_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.LOGIN_REQUIRED.code()));
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(POST_PROFILE_REDIRECT_URI_PARAM);
        if (redirectUri == null || redirectUri.trim().isBlank()) {
            redirectUri = PROFILE_SERVLET_PATH;
        }
        
        // TODO: handle params properly
        Map<String, String[]> errorParams = new HashMap<>();
        errorParams.put(POST_PROFILE_REDIRECT_URI_PARAM, new String[]{redirectUri});
        
        Optional<SessionEntity> activeSession = getActiveSession(req);
        if (activeSession.isPresent()) {
            Optional<UserProfileUpdateRequest> requestPayload = parseRequestPayload(req, errorParams, resp);
            if (requestPayload.isEmpty()) {
                return;
            }
            
            Optional<ErrorCode> updateError = userService.updateUserProfile(activeSession.get().getUser().getId(), requestPayload.get());
            if (updateError.isPresent()) {
                resp.sendRedirect(PROFILE_SERVLET_PATH + ServletUtil.buildErrorParams(updateError.get().code(), errorParams));
                return;
            }
            
            resp.sendRedirect(fieldValueOrElse(redirectUri, PROFILE_SERVLET_PATH));
            return;
        }
        
        resp.sendRedirect(ERROR_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.LOGIN_REQUIRED.code()));
    }
    
    private Optional<UserProfileUpdateRequest> parseRequestPayload(HttpServletRequest req, Map<String, String[]> params, HttpServletResponse resp) throws IOException {
        UserProfileUpdateRequest payload = new UserProfileUpdateRequest();
    
        payload.setEmail(fieldValue(req.getParameter(EMAIL_PARAM)));
        payload.setFirstName(fieldValue(req.getParameter(FIRST_NAME_PARAM)));
        payload.setLastName(fieldValue(req.getParameter(LAST_NAME_PARAM)));
        payload.setAvatar(fieldValue(req.getParameter(AVATAR_PARAM)));
        
        String password = fieldValue(req.getParameter(PASSWORD_PARAM));
        String confirmPassword = fieldValue(req.getParameter(CONFIRM_PASSWORD_PARAM));
        if (password != null && confirmPassword != null) {
            if (!password.equals(confirmPassword)) {
                resp.sendRedirect(PROFILE_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.PASSWORD_MISMATCH.code(), params));
                return Optional.empty();
            }
            
            if (password.length() < 6) {
                resp.sendRedirect(PROFILE_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.INSECURE_PASSWORD.code(), params));
                return Optional.empty();
            }
            
            payload.setPassword(password);
            
        } else if (password == null ^ confirmPassword == null) {
            resp.sendRedirect(PROFILE_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.MISSING_REQUIRED_FIELDS.code(), params));
            return Optional.empty();
        }
        
        return Optional.of(payload);
    }
    
    private boolean fieldIsSet(String fieldValue) {
        return fieldValue != null && !fieldValue.trim().isBlank();
    }
    
    private Optional<SessionEntity> getActiveSession(HttpServletRequest req) {
        return HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies())
            .map(Cookie::getValue)
            .flatMap(sessionId -> sessionService.getSession(sessionId, req.getRemoteAddr()));
    }
}
