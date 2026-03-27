package pt.unl.fct.iadi.bookstore.controller.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank


data class CreateReviewRequest(

    val rating: Int,

    val comment: String
) {
    fun toReview(): ReviewResponse {
        return ReviewResponse(
            id = null,
            rating = this.rating,
            comment = this.comment,
            author = null
        )
    }
}
