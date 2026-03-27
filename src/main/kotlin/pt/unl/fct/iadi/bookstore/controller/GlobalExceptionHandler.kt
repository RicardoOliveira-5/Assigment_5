package pt.unl.fct.iadi.bookstore.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import pt.unl.fct.iadi.bookstore.service.BookAlreadyExistsException
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.InvalidReviewException
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException

data class ApiError(val error: String, val message: String)

@RestControllerAdvice
class GlobalExceptionHandler {



    @ExceptionHandler(BookAlreadyExistsException::class)
    fun handleBookAlreadyExists(ex: BookAlreadyExistsException) =
        ResponseEntity(ApiError("CONFLICT", ex.message ?: "Book already exists"), HttpStatus.CONFLICT)

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException) =
        ResponseEntity(ApiError("NOT_FOUND", ex.message ?: "Review not found"), HttpStatus.NOT_FOUND)
/*
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception) =
        ResponseEntity(ApiError("INTERNAL_ERROR", ex.message ?: "Unexpected error"), HttpStatus.INTERNAL_SERVER_ERROR)

 */

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException, request: WebRequest): ResponseEntity<ApiError> {
        val language = request.getHeader("Accept-Language") ?: "en"
        val message = if (language.startsWith("pt")) "Livro não encontrado" else "Book not found"
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_LANGUAGE, language)
        return ResponseEntity(ApiError("NOT_FOUND", message), headers, HttpStatus.NOT_FOUND)
    }
    @ExceptionHandler(InvalidReviewException::class)
    fun handleInvalidReview(ex: InvalidReviewException) =
        ResponseEntity(ApiError("BAD_REQUEST", ex.message ?: "Invalid review"), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String,String>> {

        val message = ex.bindingResult
            .fieldErrors
            .firstOrNull()?.defaultMessage ?: "Validation error"

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("message" to message))
    }
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiError> =
        ResponseEntity(ApiError("BAD_REQUEST", ex.message ?: "Invalid input"), HttpStatus.BAD_REQUEST)
}