package com.mjamsek.auth.api.servlets.utils;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.utils.HttpUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.mjamsek.auth.lib.constants.CookieConstants.SESSION_COOKIE;
import static com.mjamsek.auth.lib.constants.HttpConstants.PRAGMA_HEADER;
import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.RequestConstants.REDIRECT_URI_PARAM;

public class ServletUtil {
    
    public static void renderHtml(String htmlContent, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);
        response.setStatus(Response.Status.OK.getStatusCode());
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(PRAGMA_HEADER, "no-cache");
        try (PrintWriter pw = response.getWriter()) {
            pw.print(htmlContent);
        }
    }
    
    public static Cookie createSessionCookie(String sessionId) {
        Cookie cookie = new Cookie(SESSION_COOKIE, sessionId);
        cookie.setSecure(HttpUtil.useSecureCookie());
        int expireInMinutes = ConfigurationUtil.getInstance()
            .getInteger("config.session.expiration")
            .orElse(60); // Defaults to 1 hour
        cookie.setMaxAge(expireInMinutes * 60);
        cookie.setPath("/");
        return cookie;
    }
    
    public static Cookie clearSessionCookie() {
        Cookie cookie = new Cookie(SESSION_COOKIE, "");
        cookie.setSecure(HttpUtil.useSecureCookie());
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
    
    public static String buildRedirectUriParams(AuthorizationRequestEntity request, SessionEntity session) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(AUTHORIZATION_CODE_PARAM, new String[]{request.getCode()});
        params.put(SESSION_STATE_PARAM, new String[]{session.getId()});
        return HttpUtil.formatQueryParams(params);
    }
    
    public static String buildConsentUriParams(AuthorizationRequestEntity request, String redirectUri) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(CLIENT_ID_PARAM, new String[]{request.getClient().getClientId()});
        params.put(REDIRECT_URI_PARAM, new String[]{redirectUri});
        return HttpUtil.formatQueryParams(params);
    }
    
    public static String buildErrorParams(String errorMessage) {
        return buildErrorParams(errorMessage, null, null);
    }
    
    public static String buildErrorParams(String errorMessage, String requestId) {
        return buildErrorParams(errorMessage, requestId, null);
    }
    
    public static String buildErrorParams(String errorMessage, String requestId, String sessionId) {
        Map<String, String[]> params = new HashMap<>();
        params.put(ERROR_PARAM, new String[]{HttpUtil.encodeURI(errorMessage)});
        if (requestId != null) {
            params.put(REQUEST_ID_PARAM, new String[]{requestId});
        }
        if (sessionId != null) {
            params.put(SESSION_STATE_PARAM, new String[]{sessionId});
        }
        return HttpUtil.formatQueryParams(params);
    }
}
