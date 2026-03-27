package pt.unl.fct.iadi.bookstore.controller.dto
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Data transfer object for Book")
data class BookResponse(
    @field:Schema(description = "ISBN of the book", example = "978-3-16-148410-0")
    val isbn: String,

    @field:Schema(description = "Title of the book", example = "Effective Kotlin")
    val title: String,

    @field:Schema(description = "Author of the book", example = "Marcin Moskala")
    val author: String,

    @field:Schema(
        description = "Price of the book",
        example = "39.99",
        minimum = "0"
    )
    val price: BigDecimal,

    @field:Schema(description = "URL to the book image", example = "https://example.com/image.png")
    val image: String
)