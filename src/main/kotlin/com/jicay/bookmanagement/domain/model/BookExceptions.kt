package com.jicay.bookmanagement.domain.model

class BookNotFoundException(name: String) : RuntimeException("Book '$name' not found")

class BookAlreadyReservedException(name: String) : RuntimeException("Book '$name' is already reserved")
