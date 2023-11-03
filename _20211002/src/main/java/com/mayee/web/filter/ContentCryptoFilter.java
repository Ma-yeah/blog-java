package com.mayee.web.filter;

import com.mayee.web.wrapper.ContentCryptoRequestWrapper;
import com.mayee.web.wrapper.ContentCryptoResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentCryptoFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCryptoRequestWrapper requestWrapper = new ContentCryptoRequestWrapper(request);
        ContentCryptoResponseWrapper responseWrapper = new ContentCryptoResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);
    }
}
