package pt.unl.fct.iadi.bookstore.controller

import org.springframework.web.bind.annotation.RestController
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.service.BookStoreService


@RestController
class BookstoreController(
    private val service: BookStoreService,
) : BookStoreAPI {

    override fun getAllBooks() = service.getAllBooks()

    override fun createBook(request: CreateBookRequest) = service.createBook(request)

    override fun getBook(isbn: String) = service.getBook(isbn)

    override fun replaceBook(isbn: String, request: CreateBookRequest) = service.replaceBook(isbn, request)

    override fun updateBook(isbn: String, request: UpdateBookRequest) = service.updateBook(isbn, request)

    override fun deleteBook(isbn: String) = service.deleteBook(isbn)

    override fun getBookReviews(isbn: String) = service.getBookReviews(isbn)

    override fun createReview(isbn: String, request: CreateReviewRequest) = service.createReview(isbn, request)

    override fun replaceReview(isbn: String, reviewId: Long, request: CreateReviewRequest) = service.replaceReview(isbn,  request)

    override fun updateReview(isbn: String, reviewId: Long, request: UpdateReviewRequest) = service.updateReview(isbn, reviewId, request)

    override fun deleteReview(isbn: String, reviewId:Long) = service.deleteReview(isbn, reviewId)


}