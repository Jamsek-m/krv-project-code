package com.mjamsek.auth.api.servlets;

import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.services.ClientService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mjamsek.auth.lib.constants.RequestConstants.CLIENT_ID_PARAM;
import static com.mjamsek.auth.lib.constants.RequestConstants.ORIGIN_PARAM;
import static com.mjamsek.auth.lib.constants.ServerPaths.CHECK_SESSION_IFRAME_SERVLET_PATH;

@WebServlet(name = "check-sso-iframe-init-servlet", urlPatterns = CHECK_SESSION_IFRAME_SERVLET_PATH + "/init", loadOnStartup = 1)
@RequestScoped
public class CheckSsoIframeServlet extends HttpServlet {
    
    @Inject
    private ClientService clientService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String origin = req.getParameter(ORIGIN_PARAM);
        String clientId = req.getParameter(CLIENT_ID_PARAM);
        
        if (origin == null || clientId == null) {
            resp.setStatus(403);
            return;
        }
    
        Optional<ClientEntity> clientOpt = clientService.getClientByClientId(clientId);
        if (clientOpt.isEmpty()) {
            resp.setStatus(403);
            return;
        }
        
        ClientEntity client = clientOpt.get();
        Set<String> allowedWebOrigins = new HashSet<>(client.getWebOrigins());
        if (allowedWebOrigins.contains("*") || allowedWebOrigins.contains(origin)) {
            resp.setStatus(204);
        } else {
            resp.setStatus(403);
        }
    }
}
