package com.mjamsek.auth.api.filters;

import com.mjamsek.auth.api.context.AuthorizationFlowContext;
import com.mjamsek.auth.lib.enums.PKCEMethod;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.services.AuthorizationService;
import com.mjamsek.auth.utils.HttpUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.AUTHORIZATION_SERVLET_PATH;
import static com.mjamsek.auth.lib.constants.ServerPaths.ERROR_SERVLET_PATH;

@WebFilter(filterName = "authorization-request-filter")
@RequestScoped
public class AuthorizationRequestFilter implements Filter {
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Handle only GET requests, skip otherwise
        if (!request.getMethod().equalsIgnoreCase(HttpMethod.GET)) {
            chain.doFilter(request, response);
            return;
        }
        
        AuthorizationFlowContext context = (AuthorizationFlowContext) request.getAttribute(AuthorizationFlowContext.CONTEXT_ID);
        // Validate PKCE challenge params
        if (context.getPkceMethod() != null && !context.getPkceMethod().equals(PKCEMethod.NONE)) {
            String pkceChallenge = request.getParameter(CODE_CHALLENGE_PARAM);
            String pkceMethod = request.getParameter(CODE_CHALLENGE_METHOD_PARAM);
            if (pkceChallenge == null || pkceMethod == null) {
                response.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams("Missing PKCE challenge!"));
                return;
            }
            try {
                PKCEMethod givenMethod = PKCEMethod.fromString(pkceMethod);
                if (!givenMethod.equals(context.getPkceMethod())) {
                    response.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams("Invalid PKCE challenge method!"));
                    return;
                }
    
                if (request.getParameter(REQUEST_ID_PARAM) == null) {
                    // Validation filter ensures this value is always present
                    String clientId = request.getParameter(CLIENT_ID_PARAM);
                    String queryParams = addRequestId(request, clientId, pkceChallenge, givenMethod);
                    response.sendRedirect(AUTHORIZATION_SERVLET_PATH + queryParams);
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendRedirect(ERROR_SERVLET_PATH + HttpUtil.buildErrorParams(e.getMessage()));
                return;
            }
        }
        
        // If no request id, generate one and redirect back to same page with request id
        if (request.getParameter(REQUEST_ID_PARAM) == null) {
            // Validation filter ensures this value is always present
            String clientId = request.getParameter(CLIENT_ID_PARAM);
            String queryParams = addRequestId(request, clientId);
            response.sendRedirect(AUTHORIZATION_SERVLET_PATH + queryParams);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    
    }
    
    private String addRequestId(HttpServletRequest request, String clientId) throws BadRequestException {
        return addRequestId(request, clientId, null, null);
    }
    
    private String addRequestId(HttpServletRequest request, String clientId, String pkceChallenge, PKCEMethod pkceMethod) throws BadRequestException {
        AuthorizationRequestEntity authRequest;
        if (pkceChallenge != null && pkceMethod != null) {
            authRequest = authorizationService.initializeRequest(clientId, request.getRemoteAddr(), pkceChallenge, pkceMethod);
        } else {
            authRequest = authorizationService.initializeRequest(clientId, request.getRemoteAddr());
        }
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());
        params.put(REQUEST_ID_PARAM, new String[]{authRequest.getId()});
        return HttpUtil.formatQueryParams(params);
    }
}
