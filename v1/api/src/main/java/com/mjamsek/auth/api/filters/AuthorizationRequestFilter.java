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
        
        // Handle only GET & POST requests, skip otherwise
        if (!request.getMethod().equalsIgnoreCase(HttpMethod.GET) &&
            !request.getMethod().equalsIgnoreCase(HttpMethod.POST)) {
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
            
                String requestId = request.getParameter(REQUEST_ID_PARAM);
                if (requestId == null) {
                    // Validation filter ensures this value is always present
                    String clientId = request.getParameter(CLIENT_ID_PARAM);
                    AuthorizationRequestEntity newRequest = createNewRequest(request, clientId, pkceChallenge, givenMethod);
                    String queryParams = addRequestId(request, newRequest);
                
                    if (request.getMethod().equalsIgnoreCase(HttpMethod.GET)) {
                        response.sendRedirect(AUTHORIZATION_SERVLET_PATH + queryParams);
                        return;
                    } else if (request.getMethod().equalsIgnoreCase(HttpMethod.POST)) {
                        // TODO: verify
                        context.setRequestId(newRequest.getId());
                        context.setPkceMethod(givenMethod);
                        chain.doFilter(request, response);
                        return;
                    }
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
            AuthorizationRequestEntity newRequest = createNewRequest(request, clientId, null, null);
            String queryParams = addRequestId(request, newRequest);
    
            if (request.getMethod().equalsIgnoreCase(HttpMethod.GET)) {
                response.sendRedirect(AUTHORIZATION_SERVLET_PATH + queryParams);
                return;
            } else if (request.getMethod().equalsIgnoreCase(HttpMethod.POST)) {
                // TODO: verify
                context.setRequestId(newRequest.getId());
                context.setPkceMethod(null);
                chain.doFilter(request, response);
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    
    }
    
    private AuthorizationRequestEntity createNewRequest(HttpServletRequest request, String clientId, String pkceChallenge, PKCEMethod pkceMethod) {
        AuthorizationRequestEntity authRequest;
        if (pkceChallenge != null && pkceMethod != null) {
            authRequest = authorizationService.initializeRequest(clientId, request.getRemoteAddr(), pkceChallenge, pkceMethod);
        } else {
            authRequest = authorizationService.initializeRequest(clientId, request.getRemoteAddr());
        }
        return authRequest;
    }
    
    private String addRequestId(HttpServletRequest request, AuthorizationRequestEntity requestEntity) throws BadRequestException {
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());
        params.put(REQUEST_ID_PARAM, new String[]{requestEntity.getId()});
        return HttpUtil.formatQueryParams(params);
    }
}
