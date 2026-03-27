package pt.unl.fct.iadi.bookstore.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestLoggingFilter(
    private val apiTokenService: ApiTokenService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val token = request.getHeader("X-Api-Token")
        val appName = apiTokenService.getAppNameFromToken(token) ?: "unknown"

        chain.doFilter(request, response)

        val principal = SecurityContextHolder.getContext().authentication?.name ?: "anonymous"

        logger.info("[${appName}] [${principal}] ${request.method} ${request.requestURI} [${response.status}]")
    }
}