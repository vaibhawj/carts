package com.example.carts.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

@Configuration
class RequestLoggingConfig {

    @Bean
    fun requestLoggingFilter(): WebFilter {
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            val startTime = Instant.now()
            val request = exchange.request
            val logger = LoggerFactory.getLogger("REQUEST_LOGGER")
            
            // Log request details
            logger.info("REQUEST: {} {} from {}", 
                request.method, 
                request.uri.path, 
                request.remoteAddress?.address?.hostAddress ?: "unknown")
            
            chain.filter(exchange).doFinally { signalType ->
                val endTime = Instant.now()
                val duration = Duration.between(startTime, endTime).toMillis()
                val response = exchange.response
                
                // Log response details
                logger.info("RESPONSE: {} {} -> {} {}ms", 
                    request.method,
                    request.uri.path,
                    response.statusCode?.value() ?: "unknown",
                    duration)
            }
        }
    }
}