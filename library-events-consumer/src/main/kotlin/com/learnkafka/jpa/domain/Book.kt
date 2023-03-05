package com.learnkafka.jpa.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
data class Book(
    @Id
    @JsonProperty("book_id")
    @Column(name = "book_id")
    var bookId: String,

    @JsonProperty("book_name")
    @Column(name = "book_name")
    val bookName: String,

    @JsonProperty("book_author")
    @Column(name = "book_author")
    val bookAuthor: String,

    @OneToOne
    @JoinColumn(name = "library_event_id")
    @JsonIgnore
    var libraryEvent: LibraryEvent
) {
    override fun toString(): String {
        return "Book(bookId='$bookId', bookName='$bookName', bookAuthor='$bookAuthor')"
    }
}
