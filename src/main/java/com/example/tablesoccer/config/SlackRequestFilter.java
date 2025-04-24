package com.example.tablesoccer.config;

import com.example.tablesoccer.service.SlackSignatureVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class SlackRequestFilter extends OncePerRequestFilter {

    private static final String SLACK_TIMESTAMP_HEADER = "X-Slack-Request-Timestamp";
    private static final String SLACK_SIGNATURE_HEADER = "X-Slack-Signature";
    private static final String SLACK_URL_PREFIX = "/slack/";

    @Autowired
    private SlackSignatureVerifier slackSignatureVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Only validate requests to Slack endpoints
        if (path.startsWith(SLACK_URL_PREFIX)) {
            // Wrap the request to be able to read the body multiple times
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            
            // Get the headers needed for validation
            String timestamp = request.getHeader(SLACK_TIMESTAMP_HEADER);
            String signature = request.getHeader(SLACK_SIGNATURE_HEADER);
            
            // Get the raw request body
            String requestBody = wrappedRequest.getReader().lines().collect(Collectors.joining());
            
            // Validate the request
            if (!slackSignatureVerifier.isValidRequest(timestamp, signature, requestBody)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Slack signature");
                return;
            }
            
            filterChain.doFilter(wrappedRequest, response);
        } else {
            // For non-Slack endpoints, just continue the filter chain
            filterChain.doFilter(request, response);
        }
    }
}