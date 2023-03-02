package com.learnkafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.learnkafka.dto.DataGenerator
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.kafka.KafkaException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture


@ExtendWith(MockitoExtension::class)    // to mock listenable future class
class LibraryEventsProducerUnitTest {

    // test have no assertions 'cause methods does not return anything, just verifying logs

    @InjectMocks
    private lateinit var libraryEventsProducer: LibraryEventsProducer

    @Mock
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Spy    // Used when you don't want to mock the behavior of the method, you really want this to be executed
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `send library event synchronous when it fails`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()

        `when`(kafkaTemplate.send(any(), any(), any()))
            .thenReturn(getCompletableFutureWithException())

        assertThrows<KafkaException> {
            libraryEventsProducer.sendLibraryEventSynchronous(key = key, value = value)
        }
    }

    @Test
    fun `send library event synchronous when it succeeds`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()

        `when`(kafkaTemplate.send(any(), any(), any()))
            .thenReturn(getCompletableFutureWithMessage())

        libraryEventsProducer.sendLibraryEventAsynchronous(key = key, value = value)
    }

    @Test
    fun `send library event asynchronous when it fails`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()
        `when`(kafkaTemplate.send(any(), any(), any()))
            .thenReturn(getCompletableFutureWithException())

            libraryEventsProducer.sendLibraryEventAsynchronous(key = key, value = value)
    }

    @Test
    fun `send library event asynchronous when it succeeds`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()

        `when`(kafkaTemplate.send(any(), any(), any()))
            .thenReturn(getCompletableFutureWithMessage())

        libraryEventsProducer.sendLibraryEventAsynchronous(key = key, value = value)
    }

    @Test
    fun `send library event asynchronous with headers when it fails`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()

        val headers = mutableMapOf<String,String>()
        headers["content-type"] = MediaType.APPLICATION_JSON.toString()

        `when`(kafkaTemplate.send(any<ProducerRecord<String, String>>()))
            .thenReturn(getCompletableFutureWithException())

        libraryEventsProducer.sendLibraryEventWithHeaders(key = key, value = value, headers = headers)
    }

    @Test
    fun `send library event asynchronous with headers when it succeeds`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithIds()

        val key = libraryEvent.libraryEventId!!
        val value = libraryEvent.book.toString()

        val headers = mutableMapOf<String,String>()
        headers["content-type"] = MediaType.APPLICATION_JSON.toString()

        `when`(kafkaTemplate.send(any<ProducerRecord<String, String>>()))
            .thenReturn(getCompletableFutureWithMessage())

        libraryEventsProducer.sendLibraryEventWithHeaders(key = key, value = value, headers = headers)
    }

    private fun getCompletableFutureWithException(): CompletableFuture<SendResult<String, String>> {
        val future = CompletableFuture<SendResult<String, String>>()
        future.completeExceptionally(Exception("Exception Calling Kafka"))
        return future
    }


    private fun getCompletableFutureWithMessage(): CompletableFuture<SendResult<String, String>> {
        val future = CompletableFuture<SendResult<String, String>>()
        val producerRecord = ProducerRecord("", "", "")
        val recordMetadata = RecordMetadata(
            TopicPartition("", 0), 0, 0, 1L, 0, 0)
        val sendResult = SendResult<String, String>(producerRecord, recordMetadata)
        future.complete(sendResult)
        return future
    }
}