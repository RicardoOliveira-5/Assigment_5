package pt.unl.fct.iadi.bookstore.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pt.unl.fct.iadi.bookstore.controller.dto.BookDTO
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewDTO
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.service.BookStoreService
import java.net.URI
@Tag(name = "BookStore", description = "Operations related to book store")
@RestController
class BookStoreController(
    private val service: BookStoreService
): BookStoreAPI {
    override fun listBooks(): ResponseEntity<List<BookDTO>> {
      val books = service.listBooks().map { book ->
            BookDTO(
                isbn = book.isbn,
                title = book.title,
                author = book.author,
                price = book.price,
                image = book.image
            )
        }
        return ResponseEntity.ok(books)
    }

    override fun createBook(@Valid @RequestBody request: CreateBookRequest): ResponseEntity<Unit> {
        val book = Book(
            isbn = request.isbn,
            title = request.title,
            author = request.author,
            price = request.price,
            image = request.image
        )
        service.createBook(book)
        return ResponseEntity
            .created(URI("/books/${book.isbn}"))
            .build()
    }

    override fun getBook(isbn: String): BookDTO {
        val book = service.getBook(isbn) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return BookDTO(
                    isbn = book.isbn,
                    title = book.title,
                    author = book.author,
                    price = book.price,
                    image = book.image
                )

        }


    override fun replaceBook(
        @PathVariable isbn: String,
        @RequestBody request: CreateBookRequest
    ): ResponseEntity<BookDTO> {
        val exists = try { service.getBook(isbn); true } catch (e: Exception) { false }
        val book = Book(
            isbn = isbn,
            title = request.title,
            author = request.author,
            price = request.price,
            image = request.image
        )
        val updated = service.replaceBook(isbn, book)
        val dto = BookDTO(isbn = updated.isbn, title = updated.title, author = updated.author, price = updated.price, image = updated.image)
        return if (exists) {
            ResponseEntity.ok(dto)
        } else {
            ResponseEntity.created(URI("/books/$isbn")).body(dto)
        }
    }


    override fun updateBookPartially(
        isbn: String,
        request: UpdateBookRequest
    ): ResponseEntity<BookDTO> {
        val updatedBook = service.partialUpdateBook(isbn, request)
        return ResponseEntity.ok(
            BookDTO(
                isbn = updatedBook.isbn,
                title = updatedBook.title,
                author = updatedBook.author,
                price = updatedBook.price,
                image = updatedBook.image
            )
        )
    }

    override fun deleteBook(isbn: String): ResponseEntity<Unit> {
        val deleted = service.deleteBook(isbn)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun listReviews(isbn: String): List<ReviewDTO> {
        val reviews = service.listReviews(isbn)
        return reviews.map { (id, review) ->
            ReviewDTO(
                id = id,
                rating = review.rating,
                comment = review.comment
            )
        }
    }

    override fun createReview(
        isbn: String,
        request: CreateReviewRequest
    ): ResponseEntity<Unit> {
        val review = service.createReview(isbn, request.toReview())
        return ResponseEntity
            .created(URI("/books/$isbn/reviews/${review.id}"))
            .build()

    }

    override fun replaceReview(
        isbn: String,
        reviewId: Long,
        request: CreateReviewRequest
    ): ResponseEntity<ReviewDTO> {
        val review = service.replaceReview(isbn, reviewId, request.toReview())
        return if (review != null) {
            ResponseEntity.ok(
                ReviewDTO(
                    id = review.id,
                    rating = review.rating,
                    comment = review.comment
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun updateReviewPartially(
        isbn: String,
        reviewId: Long,
        request: UpdateReviewRequest
    ): ResponseEntity<Unit> {

        val updatedReview = service.partiallyUpdateReview(isbn, reviewId, request)
        return if (updatedReview != null) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteReview(
        isbn: String,
        reviewId: Long
    ): ResponseEntity<Unit> {
        val deleted = service.deleteReview(isbn, reviewId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/books/reset-for-testing")
    fun resetForTesting(): ResponseEntity<Unit> {
        service.reset()
        return ResponseEntity.noContent().build()
    }

}