package org.example.demo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
@WebFilter("/api/*")
public class ApiFilter implements Filter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        httpReq.setCharacterEncoding("UTF-8");
        httpResp.setCharacterEncoding("UTF-8");
        httpResp.setContentType("application/json");

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unhandled exception processing {} {}", httpReq.getMethod(), httpReq.getRequestURI(), e);
            if (!httpResp.isCommitted()) {
                httpResp.resetBuffer();
                httpResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpResp.setCharacterEncoding("UTF-8");
                httpResp.setContentType("application/json");
                MAPPER.writeValue(httpResp.getWriter(), Map.of("error", "Internal server error"));
            }
        }
    }
}
