package rso.frontend.backend.interceptor;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LogContextInterceptor extends OncePerRequestFilter
{
    private final ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("applicationName", applicationContext.getId());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
