package com.mjamsek.auth.api.filters;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "cors-custom-filter", urlPatterns = "/*")
public class CorsCustomFilter implements Filter {
    
    private String allowedMethods;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.allowedMethods = ConfigurationUtil.getInstance()
            .get("kumuluzee.cors-filter.servlet.supported-methods")
            .orElse("*");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Access-Control-Allow-Methods", this.allowedMethods);
            chain.doFilter(request, httpResponse);
            return;
        }
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    
    }
}
