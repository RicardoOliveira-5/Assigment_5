package pt.unl.fct.iadi.bookstore.domain

data class Review(
    val id : Long,
    var rating: Int,
    var comment: String,
    val author : String
)