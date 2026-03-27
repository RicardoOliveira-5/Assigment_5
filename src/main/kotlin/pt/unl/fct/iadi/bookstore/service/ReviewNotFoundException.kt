package pt.unl.fct.iadi.bookstore.service

class ReviewNotFoundException(reviewId: Long) : RuntimeException("Review with ID $reviewId not found")