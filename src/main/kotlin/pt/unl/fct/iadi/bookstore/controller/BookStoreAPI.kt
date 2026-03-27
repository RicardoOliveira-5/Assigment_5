package pt.unl.fct.iadi.bookstore.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import pt.unl.fct.iadi.bookstore.controller.dto.BookDTO
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewDTO
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest


interface BookStoreAPI {



    // -------------------------
    // US1 - List Books
    // -------------------------

    @Operation(summary = "List all books")
    @ApiResponse(
        responseCode = "200",
        description = "List of books",
        content = [Content(array = ArraySchema(schema = Schema(implementation = BookDTO::class)))]
    )
    @RequestMapping(value = ["/books"], method = [RequestMethod.GET], produces = ["application/json"])
    fun listBooks(): ResponseEntity<List<BookDTO>>

    // -------------------------
    // US2 - Create Book
    // -------------------------

    @Operation(summary = "Create a new book", operationId = "createBook")
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
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])   )
    @RequestMapping(value = ["/books"],consumes = ["application/json"],method = [RequestMethod.POST])
    fun createBook(@Valid @RequestBody request: CreateBookRequest): ResponseEntity<Unit>


    // -------------------------
    // US3 - Get Book
    // -------------------------

    @GetMapping("/books/{isbn}", produces = ["application/json"])
    @Operation(summary = "Get book by ISBN")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Book found",
            content = [Content(schema = Schema(implementation = BookDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun getBook(@PathVariable isbn: String): BookDTO

    // -------------------------
    // US4 - Replace Book
    // -------------------------

    @Operation(summary = "Replace a book")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Book replaced"),
        ApiResponse(responseCode = "201", description = "Book created"),
        ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @RequestMapping("/books/{isbn}", consumes = ["application/json"], method = [RequestMethod.PUT])
    fun replaceBook(
        @PathVariable isbn: String,
        @Valid @RequestBody request: CreateBookRequest
    ): ResponseEntity<Unit>

    // -------------------------
    // US5 - Partial Update Book
    // -------------------------

    @Operation(summary = "Partially update a book")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Book updated"),
        ApiResponse(responseCode = "404",
            description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "400",
            description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))] )
    )
    @PatchMapping("/books/{isbn}", consumes = ["application/json"])
    fun updateBookPartially(
        @PathVariable isbn: String,
        @RequestBody  @Valid request: UpdateBookRequest
    ): ResponseEntity<BookDTO>


    // -------------------------
    // US6 - Delete Book
    // -------------------------

    @Operation(summary = "Delete a book")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Book deleted"),
        ApiResponse(
            responseCode = "404",
            description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/books/{isbn}")
    fun deleteBook(
        @PathVariable isbn: String
    ): ResponseEntity<Unit>


    // -------------------------
    // US7 - List Reviews
    // -------------------------

    @Operation(summary = "List reviews for a book")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of reviews",
            content = [Content(array = ArraySchema(schema = Schema(implementation = ReviewDTO::class)))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @GetMapping("/books/{isbn}/reviews")
    fun listReviews(
        @PathVariable isbn: String
    ): List<ReviewDTO>


    // -------------------------
    // US8 - Create Review
    // -------------------------

    @Operation(summary = "Create review for a book")
    @ApiResponses(
        ApiResponse(responseCode = "201",
            description = "Review created",
            headers = [Header(name = "Location", description = "URI of created review",
                    schema = Schema(type = "string", format = "uri"))]

        ),
        ApiResponse(responseCode = "404",
            description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "400",
            description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    )
    @PostMapping("/books/{isbn}/reviews", consumes = ["application/json"])
    fun createReview(
        @PathVariable isbn: String,
        @Valid @RequestBody request: CreateReviewRequest
    ): ResponseEntity<Unit>


    // -------------------------
    // US9 - Replace Review
    // -------------------------

    @Operation(summary = "Replace review")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Review replaced"),
        ApiResponse(
            responseCode = "404",
            description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PutMapping("/books/{isbn}/reviews/{reviewId}", consumes = ["application/json"])
    fun replaceReview(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long,
        @Valid @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ReviewDTO>


    // -------------------------
    // US10 - Partial Update Review
    // -------------------------

    @Operation(summary = "Partially update review")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Review updated"),
        ApiResponse(
            responseCode = "404",
            description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PatchMapping("/books/{isbn}/reviews/{reviewId}", consumes = ["application/json"])
    fun updateReviewPartially(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<Unit>


    // -------------------------
    // US11 - Delete Review
    // -------------------------

    @Operation(summary = "Delete review")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Review deleted"),
        ApiResponse(
            responseCode = "404",
            description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/books/{isbn}/reviews/{reviewId}")
    fun deleteReview(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long
    ): ResponseEntity<Unit>



}