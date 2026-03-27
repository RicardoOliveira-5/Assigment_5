package pt.unl.fct.iadi.bookstore.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewDTO
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review

@Service
class BookStoreService{
    companion object {
        private const val BOOK_NOT_FOUND = "Book with ISBN %s not found"
        private const val BOOK_ALREADY_EXISTS = "Book with ISBN %s already exists"
    }
    //val author = SecurityContextHolder.getContext().authentication.name
    /*
    val books: MutableList<Book> = mutableListOf(Book(
        isbn = "978-3-16-148410-0",
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        price = 10.99.toBigDecimal(),
        image = "https://example.com/great-gatsby.jpg",
        reviews = mutableMapOf(
            1L to Review(1L, 5, "Amazing read!", author ="admin" ),
            2L to Review(2L, 10, "Really enjoyed it.", author = "admin")
        )
    ))

     */
    val books: MutableList<Book> = mutableListOf()

    private fun getCurrentUser(): String =
        SecurityContextHolder.getContext().authentication.name

    fun listBooks(): List<Book> {
        return books
    }

    fun createBook(book: Book): Book {
        if(books.any { it.isbn == book.isbn }) {
            throw BookAlreadyExistsException(BOOK_ALREADY_EXISTS)
        }
        books.add(book)
        return book
    }
    // get a single book
    fun getBook(isbn: String): Book? {
        return books.find { it.isbn == isbn } ?: throw BookNotFoundException(BOOK_NOT_FOUND)
    }
    fun replaceBook(isbn: String, book: Book): Book? {
        val existingBook = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        books.remove(existingBook)
        books.add(book)
        return book
    }
    fun partialUpdateBook(isbn: String, request: UpdateBookRequest): Book {
        val existingBook = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        val updatedBook = existingBook.copy(
            title = request.title ?: existingBook.title,
            author = request.author ?: existingBook.author,
            price = request.price ?: existingBook.price,
            image = request.image ?: existingBook.image
        )
        books.remove(existingBook)
        books.add(updatedBook)
        return updatedBook
    }
    fun deleteBook(isbn: String): Boolean {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        return books.remove(book)

    }

    fun listReviews(isbn: String): MutableMap<Long, Review> {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        return book.reviews
    }

    fun createReview(isbn: String, review: ReviewDTO): Review {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        val newId = (book.reviews.keys.maxOrNull() ?: 0L) + 1
        val review = Review(id = newId, rating = review.rating, comment = review.comment, author = getCurrentUser())
        book.reviews[newId] = review
        return review
    }
    fun replaceReview(isbn: String, reviewId: Long, review: ReviewDTO): Review? {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        val updatedReview = Review(id = reviewId, rating = review.rating, comment = review.comment, author = getCurrentUser())
        book.reviews[reviewId] = updatedReview
        return updatedReview
    }

    fun partiallyUpdateReview(isbn: String, reviewId: Long, review: UpdateReviewRequest): Review? {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        val existingReview = book.reviews[reviewId] ?: throw ReviewNotFoundException(reviewId)
        val updatedReview = existingReview.copy(
            rating = review.rating ?: existingReview.rating,
            comment = review.comment ?: existingReview.comment
        )
        book.reviews[reviewId] = updatedReview
        return updatedReview
    }

    fun deleteReview(isbn: String, reviewId: Long): Boolean {
        val book = getBook(isbn) ?: throw BookNotFoundException(BOOK_NOT_FOUND)
        return if (book.reviews.containsKey(reviewId)) {
            book.reviews.remove(reviewId)
            true
        } else {
            false
        }
    }

}