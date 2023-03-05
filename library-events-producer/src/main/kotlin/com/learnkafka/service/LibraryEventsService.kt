package com.learnkafka.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.learnkafka.domain.LibraryEvent
import com.learnkafka.producer.LibraryEventsProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class LibraryEventsService {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var libraryEventsProducer: LibraryEventsProducer

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    fun postLibraryEvent(
        synchronously: String?,
        libraryEvent: LibraryEvent,
        includeHeaders: String?,
        headers: Map<String, String>? = mapOf()
    ) : ResponseEntity<LibraryEvent> {

        generateId().apply {
            libraryEvent.libraryEventId = this
            libraryEvent.book.bookId = this
        }

        val key = libraryEvent.libraryEventId!!
        val value = objectMapper.writeValueAsString(libraryEvent)

        val boolSynchronously = !synchronously.isNullOrBlank() && synchronously == "true"
        val boolIncludeHeaders = !includeHeaders.isNullOrBlank() && includeHeaders == "true"

        if(boolIncludeHeaders) libraryEventsProducer.sendLibraryEventWithHeaders(key, value, headers!!)
        else if(boolSynchronously) libraryEventsProducer.sendLibraryEventSynchronous(key, value)
        else libraryEventsProducer.sendLibraryEventAsynchronous(key, value)

        logger.info("Event sent to Kafka topic")
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent)
    }

    private fun generateId() = UUID.randomUUID().toString()

}