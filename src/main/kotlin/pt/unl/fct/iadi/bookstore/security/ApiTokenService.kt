package pt.unl.fct.iadi.bookstore.security

import org.springframework.stereotype.Service

@Service
class ApiTokenService {

    private val tokens = mapOf(
        "catalog-app" to "token-catalog-abc123",
        "mobile-app" to "token-mobile-def456",
        "web-app" to "token-web-ghi789"
    )

    fun getAppNameFromToken(token: String?): String? {
        return tokens.entries.find { it.value == token }?.key
    }

    fun isValidToken(token: String?): Boolean {
        return tokens.containsValue(token)
    }
}