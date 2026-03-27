package pt.unl.fct.iadi.bookstore.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LoggingInterceptor(private val registry: ApiTokenFilter) : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val appName = registry.tokenToApp(request.getHeader("X-Api-Token")) ?: "unknown"
        val principal = request.userPrincipal?.name ?: "anonymous"
        log.info("[{}] [{}] {} {} [{}]", appName, principal, request.method, request.requestURI, response.status)
    }
}