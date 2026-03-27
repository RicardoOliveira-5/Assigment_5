package pt.unl.fct.iadi.bookstore.controller.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank


data class CreateReviewRequest(

    @field:Min(1)
    @field:Max(10)
    val rating: Int,

    @field:NotBlank
    val comment: String
) {
    fun toReview(): ReviewDTO {
        return ReviewDTO(
            id = null,
            rating = this.rating,
            comment = this.comment
        )
    }
}
