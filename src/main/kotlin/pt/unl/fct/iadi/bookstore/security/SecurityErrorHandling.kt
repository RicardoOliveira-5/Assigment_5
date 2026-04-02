package pt.unl.fct.iadi.bookstore.security


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException

data class SecurityError(val error: String, val message: String)

@ControllerAdvice
class SecurityErrorHandling {

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<SecurityError> {
        val body = SecurityError(error = "UNAUTHORIZED", message = ex.message ?: "Invalid credentials")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<SecurityError> {
        val body = SecurityError(error = "FORBIDDEN", message = ex.message ?: "Access denied")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body)
    }

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException): ResponseEntity<SecurityError> {
        val body = SecurityError(error = "NOT_FOUND", message = ex.message ?: "Review not found")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }
}