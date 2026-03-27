package pt.unl.fct.iadi.bookstore.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.NOT_FOUND)
class ReviewNotFoundException(reviewId: Long) : RuntimeException("Review with ID $reviewId not found")