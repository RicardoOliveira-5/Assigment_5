package pt.unl.fct.iadi.bookstore.controller.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class UpdateReviewRequest(

    @field:Min(1)
    @field:Max(5)
    val rating: Int? = null,

    @field:Size(min = 1)
    val comment: String? = null
)