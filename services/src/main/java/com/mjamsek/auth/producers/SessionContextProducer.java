package com.mjamsek.auth.producers;

import com.mjamsek.auth.lib.SessionContext;
import com.mjamsek.auth.utils.HttpUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.mjamsek.auth.lib.constants.CookieConstants.SESSION_COOKIE;

@RequestScoped
public class SessionContextProducer {
    
    @Inject
    private HttpServletRequest httpRequest;
    
    @Produces
    @RequestScoped
    public SessionContext produceSessionContext() {
        return HttpUtil.getCookieByName(SESSION_COOKIE, httpRequest.getCookies())
            .map(Cookie::getValue)
            .map(SessionContext::new)
            .orElseGet(SessionContext::new);
    }
    
}
