package pt.unl.fct.iadi.bookstore.security

import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.service.BookStoreService

@Component
class ReviewSecurity(private val service: BookStoreService) {

    fun isAuthor(isbn: String, reviewId: Long, authentication: Authentication): Boolean {
        val review = service.listReviews(isbn)[reviewId] ?: return false
        return review.author == authentication.name
    }
}