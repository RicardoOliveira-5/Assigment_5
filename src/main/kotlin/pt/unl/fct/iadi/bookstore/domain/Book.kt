package pt.unl.fct.iadi.bookstore.domain

import java.math.BigDecimal

data class Book(
    val isbn: String,
    var title: String,
    var author: String,
    var price: BigDecimal,
    var image: String,
    val reviews: MutableMap<Long, Review> = mutableMapOf()
)