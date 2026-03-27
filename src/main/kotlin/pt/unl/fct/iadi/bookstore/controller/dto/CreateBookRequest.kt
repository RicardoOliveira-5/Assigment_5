package pt.unl.fct.iadi.bookstore.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.DecimalMin
import org.hibernate.validator.constraints.URL
import java.math.BigDecimal

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