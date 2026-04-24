package com.company.aiinterview.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GeminiRateLimiter {
    
    private final Semaphore semaphore;
    private final long requestIntervalMs;
    private volatile long lastRequestTime = 0;

    public GeminiRateLimiter(@Value("${gemini.requests-per-minute:60}") int requestsPerMinute) {
        // Convert requests per minute to requests per second
        this.semaphore = new Semaphore(1, true);
        this.requestIntervalMs = (60000 / requestsPerMinute); // milliseconds between requests
        log.info("GeminiRateLimiter initialized: {} requests/minute (~{}ms interval)", 
                 requestsPerMinute, requestIntervalMs);
    }

    public <T> T executeWithRateLimit(java.util.concurrent.Callable<T> task) throws Exception {
        try {
            // Wait up to 30 seconds to acquire the semaphore
            if (!semaphore.tryAcquire(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Rate limiter timeout after 30 seconds");
            }
            
            // Enforce minimum interval between requests
            long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
            if (timeSinceLastRequest < requestIntervalMs) {
                long sleepTime = requestIntervalMs - timeSinceLastRequest;
                log.debug("Rate limiting: sleeping for {}ms", sleepTime);
                Thread.sleep(sleepTime);
            }
            
            lastRequestTime = System.currentTimeMillis();
            log.debug("Executing Gemini API request");
            return task.call();
        } finally {
            semaphore.release();
        }
    }
}

