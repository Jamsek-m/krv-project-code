package com.mjamsek.auth.api.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjamsek.auth.lib.WellKnownConfig;
import com.mjamsek.auth.services.WellKnownService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;

import static com.mjamsek.auth.lib.constants.ServerPaths.WELL_KNOWN_SERVLET_PATH;

@WebServlet(name = "well-known-servlet", urlPatterns = WELL_KNOWN_SERVLET_PATH, loadOnStartup = 1)
@RequestScoped
public class WellKnownServlet extends HttpServlet {
    
    @Inject
    private WellKnownService wellKnownService;
    
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WellKnownConfig wellKnownConfig = wellKnownService.getConfig();
        String stringifiedConfig = objectMapper.writeValueAsString(wellKnownConfig);
        
        resp.setStatus(Response.Status.OK.getStatusCode());
        resp.setContentType(MediaType.APPLICATION_JSON);
    
        try(PrintWriter pw = resp.getWriter()) {
            pw.print(stringifiedConfig);
        }
    }
}
