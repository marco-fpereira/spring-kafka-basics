package com.learnkafka.dto

import com.learnkafka.domain.Book
import com.learnkafka.domain.LibraryEvent
import com.learnkafka.domain.enums.LibraryEventTypeEnum
import java.util.*

class DataGenerator {

    companion object {


        fun generateLibraryEventWithIds() {
            val id = UUID.randomUUID().toString()
            LibraryEvent(
                libraryEventId = id,
                libraryEventType = LibraryEventTypeEnum.NEW,
                book = Book(
                    bookId = id,
                    bookName = "Kafka 1",
                    bookAuthor = "John"
                )
            )
        }

        fun generateLibraryEventWithoutIds() =
            LibraryEvent(
                libraryEventType = LibraryEventTypeEnum.NEW,
                book = Book(
                    bookName = "Kafka",
                    bookAuthor = "John"
                )
            )
    }
}