package pt.unl.fct.iadi.bookstore.controller.dto

data class ReviewDTO(
    val id: Long?,
    val rating: Int,
    val comment: String,
    val author : String
)
