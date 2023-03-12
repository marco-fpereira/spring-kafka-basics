package com.learnkafka.consumer

import com.learnkafka.jpa.service.LibraryEventsService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RetryLibraryEventsConsumer {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var libraryEventsService: LibraryEventsService

    @KafkaListener(
        topics = ["\${topics.retry}"],
        groupId = "retry-library-events-listener-group"
    )
    fun onMessage(consumerRecord: ConsumerRecord<String, String>) {
        logger.info("consumer record in retry listener: $consumerRecord")
        libraryEventsService.persistLibraryEvent(consumerRecord)
    }
}