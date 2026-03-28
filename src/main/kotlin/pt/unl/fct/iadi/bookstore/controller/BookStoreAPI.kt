package pt.unl.fct.iadi.bookstore.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest


@Tag(
    name = "Bookstore",
    description = "REST API for managing books and reviews in the bookstore"
)
@RequestMapping("/books")
interface BookStoreAPI {

    @Operation(
        summary = "List all books",
        description = "Returns the complete list of books"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "List of Books",
            content = [Content(mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = BookResponse::class))
            )]
        )
    )
    @GetMapping
    fun getAllBooks(): List<BookResponse>

    @Operation(
        summary = "Create a new book",
        description = "Creates a new book"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201", description = "Book created",
            headers = [Header(
                name = "Location", description = "URI of the created book",
                schema = Schema(type = "string", format = "uri")
            )],
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "409", description = "Book with this ISBN already exists",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    )
    @PostMapping
    fun createBook(@Valid @RequestBody request: CreateBookRequest): ResponseEntity<BookResponse>

    @Operation(
        summary = "Get a book by ISBN",
        description = "Returns a book using ISBN"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book retrieved successfully",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = BookResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{isbn}")
    fun getBook(@PathVariable isbn: String): BookResponse


    @Operation(
        summary = "Replace a book",
        description = "Replaces a book or creates it if it does not exist"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book replaced successfully",
                content = [Content(schema = Schema(implementation = BookResponse::class))]
            ),
            ApiResponse(
                responseCode = "201",
                description = "Book created through upsert",
                content = [Content(schema = Schema(implementation = BookResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/{isbn}")
    fun replaceBook(@PathVariable isbn: String,
                    @Valid @RequestBody request: CreateBookRequest): ResponseEntity<BookResponse>



    @Operation(
        summary = "Partially update a book",
        description = "Updates selected fields of a book"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book updated successfully",
                content = [Content(schema = Schema(implementation = BookResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PatchMapping("/{isbn}")
    fun updateBook(@PathVariable isbn: String,
                   @Valid @RequestBody request: UpdateBookRequest): BookResponse


    @Operation(
        summary = "Delete a book",
        description = "Deletes a book and all its associated reviews"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Book deleted"),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{isbn}")
    fun deleteBook(@PathVariable isbn: String): ResponseEntity<Void>

    @Operation(
        summary = "List reviews of a book",
        description = "Returns all reviews associated with a specific book"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Reviews retrieved successfully",
                content = [
                    Content(
                        array = ArraySchema(schema = Schema(implementation = ReviewResponse::class))
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{isbn}/reviews")
    fun getBookReviews(@PathVariable isbn: String): List<ReviewResponse>

    @Operation(
        summary = "Create a review",
        description = "Adds a new review to a book"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Review created",
                content = [Content(schema = Schema(implementation = ReviewResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/{isbn}/reviews")
    fun createReview(@PathVariable isbn: String,
                     @Valid @RequestBody request: CreateReviewRequest): ResponseEntity<ReviewResponse>


    @Operation(
        summary = "Replace a review",
        description = "Replaces a review or creates it if it does not exist"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Review replaced successfully",
                content = [Content(schema = Schema(implementation = ReviewResponse::class))]
            ),
            ApiResponse(
                responseCode = "201",
                description = "Review created through upsert",
                content = [Content(schema = Schema(implementation = ReviewResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Review not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PreAuthorize("@reviewSecurity.isAuthor(#reviewId, authentication.name)")
    @PutMapping("/{isbn}/reviews/{reviewId}")
    fun replaceReview(@PathVariable isbn: String,
                      @Parameter(name = "reviewId", description = "Review id", required = true)
                      @PathVariable reviewId: Long,
                      @Valid @RequestBody request: CreateReviewRequest): ReviewResponse


    @Operation(
        summary = "Partially update a review",
        description = "Updates selected fields of a review"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Review updated successfully",
                content = [Content(schema = Schema(implementation = ReviewResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Review not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PreAuthorize("@reviewSecurity.isAuthor(#reviewId, authentication.name)")
    @PatchMapping("/{isbn}/reviews/{reviewId}")
    fun updateReview(@PathVariable isbn: String,
                     @Parameter(name = "reviewId", description = "Review id", required = true)
                     @PathVariable reviewId:Long,
                     @Valid @RequestBody request: UpdateReviewRequest): ReviewResponse


    @Operation(
        summary = "Delete a review",
        description = "Deletes a review and all its associated reviews"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Review deleted"),
            ApiResponse(
                responseCode = "404",
                description = "Review not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PreAuthorize("@reviewSecurity.isAuthor(#reviewId, authentication.name) or hasRole('ADMIN')")
    @DeleteMapping("/{isbn}/reviews/{reviewId}")
    fun deleteReview(@PathVariable isbn: String,
                     @Parameter(name = "reviewId", description = "Review id", required = true)
                     @PathVariable reviewId:Long): ResponseEntity<Void>
}