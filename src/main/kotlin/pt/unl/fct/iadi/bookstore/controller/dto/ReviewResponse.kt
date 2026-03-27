package pt.unl.fct.iadi.bookstore.controller.dto

data class ReviewResponse(
    val id: Long?,
    val rating: Int,
    val comment: String,
    val author : String?
)
