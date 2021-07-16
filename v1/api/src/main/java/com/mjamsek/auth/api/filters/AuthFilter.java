package com.mjamsek.auth.api.filters;

import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.utils.HttpUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebFilter(urlPatterns = "/auth", filterName = "auth-filter")
@RequestScoped
public class AuthFilter implements Filter {
    
    private static final String REQUEST_ID_PARAM = "request_id";
    
    @Inject
    private ClientService clientService;
    
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
        
        // If no request id, generate one and redirect back to same page with request id
        if (request.getParameter(REQUEST_ID_PARAM) == null) {
            String queryParams = addRequestId(request);
            response.sendRedirect("/auth" + queryParams);
            return;
        }
        
        if (request.getParameter("client_id") == null) {
            response.sendRedirect("/error?err=Unknown%20client!");
            return;
        }
        String clientId = request.getParameter("client_id");
        Optional<ClientEntity> clientOpt = clientService.getClientByClientId(clientId);
        if (clientOpt.isEmpty()) {
            response.sendRedirect("/error?err=Unknown%20client!");
            return;
        }
        
        if (request.getParameter("redirect_uri") == null) {
            response.sendRedirect("/error?err=Invalid%20redirect%20URI!");
            return;
        }
        String redirectUri = request.getParameter("redirect_uri");
        boolean validUri = clientOpt.get().getRedirectUris().stream().anyMatch(uri -> uri.equals(redirectUri));
        if (!validUri) {
            response.sendRedirect("/error?err=Invalid%20redirect%20URI!");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    
    }
    
    private String addRequestId(HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());
        params.putIfAbsent(REQUEST_ID_PARAM, new String[]{requestId});
        return HttpUtil.formatQueryParams(params);
    }
}
