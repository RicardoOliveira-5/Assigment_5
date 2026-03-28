package pt.unl.fct.iadi.bookstore.service

import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import java.net.URI

@Service
class BookStoreService {

    private val books = mutableMapOf<String, Book>()
    private val reviews = mutableMapOf<String, MutableList<Review>>()
    private var reviewCounter = 1L

    fun getAllBooks(): List<BookResponse> {
        return books.values.map { it.toResponse() }
    }


    fun createBook(book: CreateBookRequest): ResponseEntity<BookResponse> {
        if (books.containsKey(book.isbn)) {
            throw BookAlreadyExistsException(book.isbn)
        }

        val newBook = book.toBook()

        books[newBook.isbn] = newBook

        val location = URI.create("/books/${book.isbn}")

        return ResponseEntity
            .created(location)
            .build()
    }

    fun getBook(isbn: String): BookResponse {
        val book = books[isbn] ?: throw BookNotFoundException(isbn)

        return book.toResponse()
    }

    fun replaceBook(isbn: String, book: CreateBookRequest): ResponseEntity<BookResponse> {
        return if (!books.containsKey(isbn)) {
            val newBook = book.toBook().copy(isbn = isbn) // usa o isbn do path
            books[isbn] = newBook
            val location = URI.create("/books/$isbn")
            ResponseEntity.created(location).body(newBook.toResponse())
        } else {
            val updatedBook = book.toBook().copy(isbn = isbn)
            books[isbn] = updatedBook
            ResponseEntity.ok(updatedBook.toResponse())
        }
    }

    fun updateBook(isbn: String, book: UpdateBookRequest): BookResponse {
        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }

        val existingBook = books[isbn]!!
        val updatedBook = existingBook.copy(
            title = book.title ?: existingBook.title,
            author = book.author ?: existingBook.author,
            price = book.price ?: existingBook.price,
            image = book.image ?: existingBook.image
        )

        books[isbn] = updatedBook

        return updatedBook.toResponse()
    }

    fun deleteBook(isbn:String): ResponseEntity<Void>{
        if(!books.containsKey(isbn)){
            throw BookNotFoundException(isbn)
        }

        books.remove(isbn)
        reviews.remove(isbn)
        return ResponseEntity.noContent().build()
    }

    fun getBookReviews(isbn: String): List<ReviewResponse> {
        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }

        return reviews[isbn]?.map { it.toResponse() } ?: emptyList()

    }

    fun createReview(isbn: String, review: CreateReviewRequest): ResponseEntity<ReviewResponse> {

        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }
        // 2️⃣ Validação manual do DTO (rating e comment)
        if (review.rating !in 1..5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }


        // ✅ SÓ DEPOIS: autenticação
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw AccessDeniedException("User not authenticated")


        val newReview = Review(
            id = reviewCounter++,
            rating = review.rating,
            comment = review.comment,
            author = username
        )

        reviews.computeIfAbsent(isbn) { mutableListOf() }.add(newReview)

        val location = URI.create("/books/$isbn/reviews/${newReview.id}")

        return ResponseEntity
            .created(location)
            .body(newReview.toResponse())
    }

    fun replaceReview(isbn: String, reviewId: Long, review: CreateReviewRequest): ResponseEntity<ReviewResponse> {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)

        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw AccessDeniedException("User not authenticated")

        val reviewsList = reviews.computeIfAbsent(isbn) { mutableListOf() }

        val existingIndex = reviewsList.indexOfFirst { it.id == reviewId }

        return if (existingIndex >= 0) {
            val existingReview = reviewsList[existingIndex]
            if (existingReview.author != username) throw AccessDeniedException("Cannot modify another user's review")

            val updatedReview = existingReview.copy(
                rating = review.rating,
                comment = review.comment
            )
            reviewsList[existingIndex] = updatedReview
            ResponseEntity.ok(updatedReview.toResponse()) // 200
        } else {
            val newReview = Review(
                id = reviewCounter++,
                rating = review.rating,
                comment = review.comment,
                author = username
            )
            reviewsList.add(newReview)
            val location = URI.create("/books/$isbn/reviews/${newReview.id}")
            ResponseEntity.created(location).body(newReview.toResponse()) // 201
        }
    }
    fun updateReview(isbn: String, id: Long, review: UpdateReviewRequest): ReviewResponse {
        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }

        val existingReviews = reviews[isbn] ?: throw ReviewNotFoundException(id)

        val reviewIndex = existingReviews.indexOfFirst { it.id == id }
        if (reviewIndex == -1) {
            throw ReviewNotFoundException(id)
        }

        val existingReview = existingReviews[reviewIndex]
        val updatedReview = existingReview.copy(
            rating = review.rating ?: existingReview.rating,
            comment = review.comment ?: existingReview.comment
        )

        existingReviews[reviewIndex] = updatedReview

        return updatedReview.toResponse()
    }

    fun deleteReview(isbn: String, id: Long): ResponseEntity<Void> {
        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }

        val existingReviews = reviews[isbn] ?: throw ReviewNotFoundException(id)

        val reviewIndex = existingReviews.indexOfFirst { it.id == id }
        if (reviewIndex == -1) {
            throw ReviewNotFoundException(id)
        }

        existingReviews.removeAt(reviewIndex)

        return ResponseEntity.noContent().build()
    }

    private fun Book.toResponse() =
        BookResponse(isbn, title, author, price, image)

    private fun Review.toResponse() =
        ReviewResponse(id, rating, comment, author)

    fun findReviewById(reviewId: Long): Review {
        reviews.values.forEach { reviewList ->
            reviewList.forEach { review ->
                if (review.id == reviewId) {
                    return review
                }
            }
        }
        throw ReviewNotFoundException(reviewId)
    }
}