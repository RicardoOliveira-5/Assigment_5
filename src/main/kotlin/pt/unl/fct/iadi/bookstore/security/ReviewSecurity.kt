package pt.unl.fct.iadi.bookstore.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.service.BookStoreService

@Component
class ReviewSecurity(private val service: BookStoreService) {

    fun isAuthor(reviewId: Long, authentication: Authentication): Boolean {
        val review = service.findReviewById(reviewId)
        val username = authentication.name
        return review.author == username
    }
}