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
import pt.unl.fct.iadi.bookstore.service.ForbiddenException
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

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception) =
        ResponseEntity(ApiError("INTERNAL_ERROR", ex.message ?: "Unexpected error"), HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(
        ex: BookNotFoundException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {

        val language = request.getHeader("Accept-Language") ?: "en"

        val message =
            if (language.startsWith("pt"))
                "Livro não encontrado"
            else
                "Book not found"

        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_LANGUAGE, language)

        val body = mapOf("message" to message)

        return ResponseEntity(body, headers, HttpStatus.NOT_FOUND)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String,String>> {

        val message = ex.bindingResult
            .fieldErrors
            .firstOrNull()?.defaultMessage ?: "Validation error"

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("message" to message))
    }
    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(e: BookNotFoundException) =
        ResponseEntity.status(404).body( mapOf("message" to (e.message ?: "Book not found")))



    @ExceptionHandler(BookAlreadyExistsException::class)
    fun handleBookExists(e: BookAlreadyExistsException) =
        ResponseEntity.status(409).body( mapOf("message" to (e.message ?: "Book already exists")))

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(e: ForbiddenException) =
        ResponseEntity.status(403).body( mapOf("message" to (e.message ?: "Forbidden")))
}