package com.example.carts.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.Instant

@Configuration
class RequestLoggingConfig {

    @Bean
    fun requestLoggingFilter(): Filter {
        return RequestLoggingFilter()
    }
}

class RequestLoggingFilter : Filter {
    private val logger = org.slf4j.LoggerFactory.getLogger("REQUEST_LOGGER")

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val startTime = Instant.now()

            // Log request details
            logger.info(
                "REQUEST: {} {} from {}",
                request.method,
                request.requestURI,
                request.remoteAddr ?: "unknown"
            )

            try {
                chain.doFilter(request, response)
            } finally {
                val endTime = Instant.now()
                val duration = Duration.between(startTime, endTime).toMillis()

                // Log response details
                logger.info(
                    "RESPONSE: {} {} -> {} {}ms",
                    request.method,
                    request.requestURI,
                    response.status,
                    duration
                )
            }
        } else {
            chain.doFilter(request, response)
        }
    }
}