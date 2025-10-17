package com.kifiya.promotion_quoter.config.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimitConfig.class);

    @Value("${rate.limit.enabled:false}")
    private boolean rateLimitEnabled;

    @Value("${rate.limit.requests.per.minute:100}")
    private int requestsPerMinute;

    @Value("${rate.limit.capacity:100}")
    private int capacity;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    public Filter rateLimitingFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
                if (!rateLimitEnabled) {
                    try {
                        filterChain.doFilter(servletRequest, servletResponse);
                    } catch (Exception e) {
                        log.error("Error in filter chain: ", e);
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                    return;
                }

                HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
                String clientKey = getClientKey(httpRequest); // Use IP as key for now

                Bucket bucket = buckets.computeIfAbsent(clientKey, k -> createNewBucket());

                if (bucket.tryConsume(1)) {
                    try {
                        filterChain.doFilter(servletRequest, servletResponse);
                    } catch (Exception e) {
                        log.error("Error processing request: ", e);
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } else {
                    log.warn("Rate limit exceeded for client: {}", clientKey);
                    ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_REQUEST_URI_TOO_LONG,
                            "Too many requests. Please try again later.");
                }
            }

            private Bucket createNewBucket() {
                Bandwidth limit = Bandwidth.builder()
                        .capacity(capacity)
                        .refillGreedy(requestsPerMinute, Duration.ofMinutes(1))
                        .build();
                return Bucket.builder()
                        .addLimit(limit)
                        .build();
            }

            private String getClientKey(HttpServletRequest request) {
                String ip = request.getRemoteAddr();
                return ip != null ? ip : "unknown";
            }
        };
    }
}
