package com.learnkafka.jpa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.learnkafka.jpa.domain.LibraryEvent
import com.learnkafka.jpa.repository.LibraryEventsRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LibraryEventsService {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var libraryEventsRepository: LibraryEventsRepository

    fun persistLibraryEvent(consumerRecord: ConsumerRecord<String, String>) {
        val libraryEvent: LibraryEvent =
            objectMapper.readValue(consumerRecord.value(), LibraryEvent::class.java)
        logger.info("Object Mapped from string: $libraryEvent")
        libraryEvent.book.libraryEvent = libraryEvent

        libraryEventsRepository.save(libraryEvent)
        logger.info("Library event successfully persisted!")
    }
}