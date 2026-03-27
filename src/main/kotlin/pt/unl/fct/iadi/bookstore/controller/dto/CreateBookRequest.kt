package pt.unl.fct.iadi.bookstore.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.DecimalMin
import org.hibernate.validator.constraints.URL
import pt.unl.fct.iadi.bookstore.domain.Book
import java.math.BigDecimal
import kotlin.collections.emptyList

data class CreateBookRequest(
    @field:NotBlank(message = "ISBN must not be blank")
    val isbn: String,

    @field:NotBlank(message = "Title must not be blank")
    val title: String,

    @field:NotBlank(message = "Author must not be blank")
    val author: String,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    val price: BigDecimal,

    @field:URL(message = "Image URL must not be blank")
    val image: String
)
{
    fun toBook(): Book {
        return Book(
            isbn = this.isbn,
            title = this.title,
            author = this.author,
            price = this.price,
            image = this.image,
        )
    }
}