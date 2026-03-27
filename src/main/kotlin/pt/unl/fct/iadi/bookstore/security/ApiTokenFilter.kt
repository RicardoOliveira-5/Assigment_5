package pt.unl.fct.iadi.bookstore.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiTokenFilter(
    private val apiTokenService: ApiTokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val token = request.getHeader("X-Api-Token")

        if (!apiTokenService.isValidToken(token)) {

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write(
                """{"error":"UNAUTHORIZED","message":"Missing or invalid X-Api-Token"}"""
            )
            return
        }

        filterChain.doFilter(request, response)
    }

    fun tokenToApp(token: String?): String? {
        return apiTokenService.getAppNameFromToken(token)
    }
}