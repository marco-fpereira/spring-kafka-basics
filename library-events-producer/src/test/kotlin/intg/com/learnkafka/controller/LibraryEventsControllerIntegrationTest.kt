package com.learnkafka.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.learnkafka.domain.Book
import com.learnkafka.domain.LibraryEvent
import com.learnkafka.dto.DataGenerator
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = ["LIBRARY_EVENTS"], partitions = 3)
//@ActiveProfiles("test")
@TestPropertySource(properties =
    ["spring.kafka.producer.bootstrap-servers=\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
    "spring.kafka.admin.properties.bootstrap.servers=\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}"]
)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private lateinit var consumer : Consumer<String, String>

    @BeforeEach
    fun setUp() {
        val consumerProps = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker)

        val configs = mutableMapOf<String, Any>()
        configs.putAll(consumerProps)

        consumer = DefaultKafkaConsumerFactory(configs, StringDeserializer(), StringDeserializer())
            .createConsumer()
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer)
    }

    @AfterEach
    fun shutDown() {
        consumer.close()
    }

    @Test
    fun postLibraryEventTest(){
        val request = generateRequest()

        val responseEntity: ResponseEntity<LibraryEvent> = restTemplate.exchange(
            "/v1/libraryevent",
            HttpMethod.POST,
            request,
            LibraryEvent::class.java
        )

        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
    }

    @Test
    @Timeout(50)
    fun postLibraryEventTestWithHeaders(){

        val request = generateRequest(withHeaders = true)

        val responseEntity: ResponseEntity<LibraryEvent> = restTemplate.exchange(
            "/v1/libraryevent?include_headers=true",
            HttpMethod.POST,
            request,
            LibraryEvent::class.java
        )

        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        val consumerRecord: ConsumerRecord<String, String> =
            KafkaTestUtils.getSingleRecord(consumer, "LIBRARY_EVENTS")

        val libraryEvent = deserializeValue(consumerRecord.value())
        assertEquals(libraryEvent.bookName, request.body!!.book.bookName)
    }

    @Test
    fun postLibraryEventTestSynchronously(){
        val request = generateRequest()

        val responseEntity: ResponseEntity<LibraryEvent> = restTemplate.exchange(
            "/v1/libraryevent?synchronously=true",
            HttpMethod.POST,
            request,
            LibraryEvent::class.java
        )

        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
    }

    private fun generateRequest(withHeaders: Boolean = false): HttpEntity<LibraryEvent> {
        val libraryEvent = DataGenerator.generateLibraryEventWithoutIds()

        val headers = HttpHeaders()
        headers.set("content-type", MediaType.APPLICATION_JSON.toString())
        if(withHeaders) headers.set("event-source", "scanner")
        return HttpEntity(libraryEvent, headers)
    }

    private fun deserializeValue(value: String) =
        ObjectMapper().readValue(value, Book::class.java)

}