package pt.unl.fct.iadi.bookstore.service

class BookAlreadyExistsException(isbn: String) : RuntimeException("Book with ISBN $isbn already exists")
