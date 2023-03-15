package com.learnkafka.dto

import com.learnkafka.jpa.domain.Book
import com.learnkafka.jpa.domain.LibraryEvent
import com.learnkafka.jpa.domain.enums.LibraryEventTypeEnum
import java.util.*

class DataGenerator {

    companion object {

        fun generateLibraryEventWithIds() : LibraryEvent{
            val id = UUID.randomUUID().toString()
            return LibraryEvent(
                libraryEventId = id,
                libraryEventType = LibraryEventTypeEnum.NEW,
                book = Book(
                    bookId = id,
                    bookName = "Kafka 1",
                    bookAuthor = "John"
                )
            )
        }
    }
}