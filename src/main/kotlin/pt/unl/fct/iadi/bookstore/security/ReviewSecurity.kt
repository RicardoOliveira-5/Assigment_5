package pt.unl.fct.iadi.bookstore.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.service.BookStoreService
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException

@Component
class ReviewSecurity(private val service: BookStoreService) {

    fun isAuthor(isbn: String, reviewId: Long, authentication: Authentication): Boolean {
        return try {
            service.getBook(isbn)
            val review = service.findReviewById(reviewId)
            review.author == authentication.name
        } catch (e: ReviewNotFoundException) {
            false  // deixa o controller/service lançar o 404
        }
    }
}