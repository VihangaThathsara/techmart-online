package lk.jiat.techmart.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.util.TimerUtil;
import lk.jiat.techmart.web.util.LookupUtil;

import java.io.IOException;

@WebFilter("/*")
public class PerformanceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = TimerUtil.start();
        try {
            chain.doFilter(request, response);
        } finally {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse resp = (HttpServletResponse) response;
                String path = req.getRequestURI().substring(req.getContextPath().length());
                if (!path.startsWith("/assets/")) {
                    long elapsed = TimerUtil.elapsedMillis(start);
                    try {
                        MetricsRemote metricsRemote = LookupUtil.metricsRemote();
                        metricsRemote.incrementCounter("HTTP Requests");
                        metricsRemote.recordMetric("Last HTTP Time", String.valueOf(elapsed), "ms");
                    } catch (Exception ignored) {
                        // The filter must never block a customer request if metrics lookup is unavailable.
                    }
                }
            }
        }
    }
}
