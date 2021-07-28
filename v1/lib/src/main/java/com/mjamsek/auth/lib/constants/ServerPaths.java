package com.mjamsek.auth.lib.constants;

public class ServerPaths {
    
    public static final String AUTHORIZATION_SERVLET_PATH = "/protocol/oidc/auth";
    public static final String CONSENT_SERVLET_PATH = "/consent";
    public static final String PASSWORD_LOGIN_SERVLET_PATH = "/password-login";
    public static final String TOKEN_SERVLET_PATH = "/protocol/oidc/token";
    public static final String ERROR_SERVLET_PATH = "/error";
    public static final String USERINFO_SERVLET_PATH = "/protocol/oidc/userinfo";
    public static final String WELL_KNOWN_SERVLET_PATH = "/protocol/oidc/.well-known";
    public static final String JWKS_SERVLET_PATH = "/protocol/oidc/jwks";
    public static final String END_SESSION_SERVLET_PATH = "/protocol/oidc/logout";
    // public static final String TOKEN_INTROSPECT_SERVLET_PATH = "/protocol/oidc/token-inspect";
    public static final String CHECK_SESSION_IFRAME_SERVLET_PATH = "/protocol/oidc/check-sso.html";
    
}
