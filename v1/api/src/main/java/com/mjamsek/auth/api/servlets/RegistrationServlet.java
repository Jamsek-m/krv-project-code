package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.servlets.utils.ServletUtil;
import com.mjamsek.auth.lib.enums.ErrorCode;
import com.mjamsek.auth.lib.requests.RegistrationRequest;
import com.mjamsek.auth.services.TemplateService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.auth.services.settings.ConfigProvider;

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
import java.util.Objects;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.ERROR_SERVLET_PATH;
import static com.mjamsek.auth.lib.constants.ServerPaths.REGISTRATION_SERVLET_PATH;

@WebServlet(name = "registration-servlet", urlPatterns = REGISTRATION_SERVLET_PATH, loadOnStartup = 1)
@RequestScoped
public class RegistrationServlet extends HttpServlet {
    
    @Inject
    private UserService userService;
    
    @Inject
    private ConfigProvider configProvider;
    
    @Inject
    private TemplateService templateService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(POST_REGISTRATION_REDIRECT_URI_PARAM);
        
        boolean terminate = checkRegistrationIsEnabled(resp, redirectUri);
        if (terminate) {
            return;
        }
        
        String error = req.getParameter(ERROR_PARAM);
        String errorDescription = ErrorCode.fromCode(error)
            .map(ErrorCode::description)
            .orElse(error);
        
        Map<String, Object> params = new HashMap<>();
        params.put("error", errorDescription);
        params.put("redirectUri", redirectUri);
        String htmlTemplate = templateService.renderHtml("registration", params);
        
        ServletUtil.renderHtml(htmlTemplate, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(POST_REGISTRATION_REDIRECT_URI_PARAM);
    
        boolean terminate = checkRegistrationIsEnabled(resp, redirectUri);
        if (terminate) {
            return;
        }
        
        Map<String, String[]> errorParams = new HashMap<>();
        errorParams.put(POST_REGISTRATION_REDIRECT_URI_PARAM, new String[] {redirectUri});
        
        RegistrationRequest requestPayload = createRequestPayload(req);
        terminate = validateParams(requestPayload, errorParams, resp);
        if (terminate) {
            return;
        }
        
        Optional<ErrorCode> registrationError = userService.registerUser(requestPayload);
        if (registrationError.isPresent()) {
            resp.sendRedirect(REGISTRATION_SERVLET_PATH + ServletUtil.buildErrorParams(registrationError.get().code(), errorParams));
            return;
        }
    
        resp.sendRedirect(Objects.requireNonNullElse(redirectUri, "/auth"));
    }
    
    private boolean validateParams(RegistrationRequest request, Map<String, String[]> params, HttpServletResponse resp) throws IOException {
        if (request.getUsername() == null || request.getUsername().trim().isBlank() ||
            request.getPassword() == null || request.getPassword().trim().isBlank() ||
            request.getConfirmPassword() == null || request.getConfirmPassword().trim().isBlank()) {
            resp.sendRedirect(REGISTRATION_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.MISSING_REQUIRED_FIELDS.code(), params));
            return true;
        }
    
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            resp.sendRedirect(REGISTRATION_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.PASSWORD_MISMATCH.code(), params));
            return true;
        }
        
        if (request.getPassword().length() < 6) {
            resp.sendRedirect(REGISTRATION_SERVLET_PATH + ServletUtil.buildErrorParams(ErrorCode.INSECURE_PASSWORD.code(), params));
            return true;
        }
        return false;
    }
    
    private boolean checkRegistrationIsEnabled(HttpServletResponse resp, String redirectUri) throws IOException {
        Optional<Boolean> registrationEnabled = configProvider.getBoolean("static.config.registration.enabled");
        if (registrationEnabled.isEmpty() || !registrationEnabled.get()) {
            if (redirectUri == null) {
                resp.sendRedirect(ERROR_SERVLET_PATH + ServletUtil.buildErrorParams("Registration disabled!"));
                return true;
            }
            resp.sendRedirect(redirectUri + ServletUtil.buildErrorParams("Registration disabled!"));
            return true;
        }
        return false;
    }
    
    private RegistrationRequest createRequestPayload(HttpServletRequest req) {
        RegistrationRequest payload = new RegistrationRequest();
        payload.setUsername(req.getParameter(USERNAME_PARAM));
        payload.setPassword(req.getParameter(PASSWORD_PARAM));
        payload.setConfirmPassword(req.getParameter(CONFIRM_PASSWORD_PARAM));
        payload.setFirstName(req.getParameter(FIRST_NAME_PARAM));
        payload.setLastName(req.getParameter(LAST_NAME_PARAM));
        payload.setEmail(req.getParameter(EMAIL_PARAM));
        payload.setAvatar(req.getParameter(AVATAR_PARAM));
        return payload;
    }
}
