package com.mjamsek.auth.api.filters;

import com.mjamsek.auth.api.context.AuthorizationFlowContext;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.RequestConstants.*;
import static com.mjamsek.auth.lib.constants.ServerPaths.ERROR_SERVLET_PATH;

@WebFilter(filterName = "authorization-validation-filter")
@RequestScoped
public class AuthorizationValidationFilter implements Filter {
    
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
        
        if (request.getParameter(CLIENT_ID_PARAM) == null) {
            response.sendRedirect(ERROR_SERVLET_PATH + buildErrorParams("Unknown client!"));
            return;
        }
        String clientId = request.getParameter(CLIENT_ID_PARAM);
        Optional<ClientEntity> clientOpt = clientService.getClientByClientId(clientId);
        if (clientOpt.isEmpty()) {
            response.sendRedirect(ERROR_SERVLET_PATH + buildErrorParams("Unknown client!"));
            return;
        }
        AuthorizationFlowContext context = createContext(clientOpt.get());
        request.setAttribute(AuthorizationFlowContext.CONTEXT_ID, context);
        
        if (request.getParameter(REDIRECT_URI_PARAM) == null) {
            response.sendRedirect(ERROR_SERVLET_PATH + buildErrorParams("Invalid redirect URI!"));
            return;
        }
        String redirectUri = request.getParameter(REDIRECT_URI_PARAM);
        boolean validUri = clientOpt.get().getRedirectUris().stream().anyMatch(uri -> uri.equals(redirectUri));
        if (!validUri) {
            response.sendRedirect(ERROR_SERVLET_PATH + buildErrorParams("Invalid redirect URI!"));
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    
    }
    
    private String buildErrorParams(String errorMessage) {
        Map<String, String[]> params = new HashMap<>();
        params.put(ERROR_PARAM, new String[]{HttpUtil.encodeURI(errorMessage)});
        return HttpUtil.formatQueryParams(params);
    }
    
    private AuthorizationFlowContext createContext(ClientEntity client) {
        AuthorizationFlowContext context = new AuthorizationFlowContext();
        context.setClientName(client.getName());
        context.setClientId(client.getClientId());
        
        List<String> scopes = client.getScopes().stream().map(ClientScopeEntity::getName).collect(Collectors.toList());
        context.setScopes(scopes);
        return context;
    }
}
