package pt.unl.fct.iadi.bookstore.controller.dto

import java.math.BigDecimal

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class UpdateBookRequest(

    @field:Size(min = 1)
    val title: String? = null,

    @field:Size(min = 1)
    val author: String? = null,

    @field:DecimalMin("0.0", inclusive = false)
    val price: BigDecimal? = null,

    @field:URL
    val image: String? = null
)