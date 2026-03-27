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
        val token = request.getHeader("X-Api-Token")
        val appName = registry.tokenToApp(token) ?: "unknown" // call the function, not index it
        val principal = request.userPrincipal?.name ?: "anonymous"
        log.info("[{}] [{}] {} {} [{}]", appName, principal, request.method, request.requestURI, response.status)
    }
}