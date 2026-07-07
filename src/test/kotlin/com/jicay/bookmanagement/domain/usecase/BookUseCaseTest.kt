package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.model.BookAlreadyReservedException
import com.jicay.bookmanagement.domain.model.BookNotFoundException
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo"),
            Book("Hamlet", "William Shakespeare")
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book("Hamlet", "William Shakespeare"),
            Book("Les Misérables", "Victor Hugo")
        )
    }

    test("add book") {
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo")

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("reserve book should reserve an available book") {
        val port = mockk<BookPort>()
        val useCase = BookUseCase(port)
        every { port.getBook("Hamlet") } returns Book("Hamlet", "Shakespeare", reserved = false)
        justRun { port.reserveBook(any()) }

        useCase.reserveBook("Hamlet")

        verify(exactly = 1) { port.reserveBook("Hamlet") }
    }

    test("reserve book should throw when book does not exist") {
        val port = mockk<BookPort>()
        val useCase = BookUseCase(port)
        every { port.getBook("Unknown") } returns null

        shouldThrow<BookNotFoundException> {
            useCase.reserveBook("Unknown")
        }

        verify(exactly = 0) { port.reserveBook(any()) }
    }

    test("reserve book should throw when book is already reserved") {
        val port = mockk<BookPort>()
        val useCase = BookUseCase(port)
        every { port.getBook("Hamlet") } returns Book("Hamlet", "Shakespeare", reserved = true)

        shouldThrow<BookAlreadyReservedException> {
            useCase.reserveBook("Hamlet")
        }

        verify(exactly = 0) { port.reserveBook(any()) }
    }

})