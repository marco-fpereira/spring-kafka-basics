package com.learnkafka.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class Book(
    @JsonProperty("book_id")
    @field:Pattern(regexp = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})|^\$")
    var bookId: String? = null,

    @JsonProperty("book_name")
    @field:NotBlank(message = "book name is mandatory")
    val bookName: String,

    @JsonProperty("book_author")
    @field:NotBlank(message = "book author is mandatory")
    val bookAuthor: String
)
