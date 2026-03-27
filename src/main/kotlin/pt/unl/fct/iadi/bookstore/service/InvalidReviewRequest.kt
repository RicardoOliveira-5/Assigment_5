package pt.unl.fct.iadi.bookstore.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidReviewException(message: String) : RuntimeException(message)