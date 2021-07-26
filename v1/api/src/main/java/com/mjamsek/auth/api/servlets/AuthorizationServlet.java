package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.api.context.AuthorizationFlowContext;
import com.mjamsek.auth.services.TemplateService;
import org.apache.logging.log4j.util.Strings;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.mjamsek.auth.lib.constants.RequestConstants.REDIRECT_URI_PARAM;
import static com.mjamsek.auth.lib.constants.RequestConstants.REQUEST_ID_PARAM;
import static com.mjamsek.auth.lib.constants.ServerPaths.AUTHORIZATION_SERVLET_PATH;

@WebServlet(name = "authorization-servlet", urlPatterns = AUTHORIZATION_SERVLET_PATH)
@RequestScoped
public class AuthorizationServlet extends HttpServlet {
    
    @Inject
    private TemplateService templateService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(MediaType.TEXT_HTML);
        resp.setStatus(Response.Status.OK.getStatusCode());
        resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        resp.setHeader("Pragma", "no-cache");
    
        AuthorizationFlowContext context = (AuthorizationFlowContext) req.getAttribute(AuthorizationFlowContext.CONTEXT_ID);
        
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", context.getClientId());
        params.put("clientName", context.getClientName());
        params.put("requestId", requestId);
        params.put("redirectUri", redirectUri);
        params.put("scopeValue", Strings.join(context.getScopes(), ' '));
        String htmlContent = templateService.renderHtml("authorization", params);
        
        try(PrintWriter pw = resp.getWriter()) {
            pw.print(htmlContent);
        }
    }
}
