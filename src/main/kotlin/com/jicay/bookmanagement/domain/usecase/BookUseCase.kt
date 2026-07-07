package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.model.BookAlreadyReservedException
import com.jicay.bookmanagement.domain.model.BookNotFoundException
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBook(name: String) {
        val book = bookPort.getBook(name) ?: throw BookNotFoundException(name)
        if (book.reserved) {
            throw BookAlreadyReservedException(name)
        }
        bookPort.reserveBook(name)
    }
}