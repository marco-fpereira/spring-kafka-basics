package com.learnkafka.producer

import com.learnkafka.configuration.TOPIC_NAME
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.KafkaException
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Component
class LibraryEventsProducer {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    fun sendLibraryEventSynchronous(key: String, value: String) {
        logger.info("Sending library event synchronously")
        val result: SendResult<String, String>
        try {
            result = kafkaTemplate.send(TOPIC_NAME, key, value).get(3L, TimeUnit.SECONDS)
            handleSuccess(key, value, result)
        } catch (ex: Exception) { handleFailure(key, ex) }
    }

    fun sendLibraryEventAsynchronous(key: String, value: String){
        logger.info("Sending library event asynchronously")
        val future: CompletableFuture<SendResult<String, String>> =
            kafkaTemplate.send(TOPIC_NAME, key, value)
        handleResult(future, key, value)
    }

    fun sendLibraryEventWithHeaders(key: String, value: String, headers: Map<String, String>){
        logger.info("Sending library event asynchronously with headers")
        val recordHeaders = mutableListOf<Header>()
        headers.forEach { (key, value) ->
            recordHeaders.add(RecordHeader(key, value.toByteArray()))
        }
        val record = ProducerRecord(TOPIC_NAME, null, key, value, recordHeaders)
        val future = kafkaTemplate.send(record)
        handleResult(future, key, value)
    }

    private fun handleFailure(key: String, ex: Throwable): SendResult<String, String>? {
        logger.error("Error sending the message of key $key")
        logger.error("Error detail: ${ex.message}")
        throw KafkaException(ex.message)
    }

    private fun handleSuccess(key: String, value: String, result: SendResult<String, String>) {
        logger.info(
            "Message sent successfully for the following informations: " +
            "\nKey: $key, " +
            "\nValue: $value, " +
            "\nPartition: ${result.recordMetadata.partition()}" +
            "\nOffset: ${result.recordMetadata.offset()}" +
            "\nTimestamp: ${result.recordMetadata.timestamp()}"
        )

    }

    private fun handleResult(
        future: CompletableFuture<SendResult<String, String>>,
        key: String,
        value: String
    ) {
        future.thenApply { result ->
            handleSuccess(key, value, result)
            return@thenApply result
        }
        future.exceptionally { throwable -> handleFailure(key, throwable) }
    }
}