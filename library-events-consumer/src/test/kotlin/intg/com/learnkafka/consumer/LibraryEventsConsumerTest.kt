//package com.learnkafka.consumer
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.learnkafka.configuration.TOPIC_NAME
//import com.learnkafka.dto.DataGenerator
//import com.learnkafka.jpa.service.LibraryEventsService
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.*
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.mock.mockito.SpyBean
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.support.Acknowledgment
//import org.springframework.kafka.test.EmbeddedKafkaBroker
//import org.springframework.kafka.test.context.EmbeddedKafka
//import org.springframework.kafka.test.utils.ContainerTestUtils
//import org.springframework.test.context.TestPropertySource
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//
//
//@SpringBootTest
//@EmbeddedKafka(topics = [TOPIC_NAME], partitions = 3)
//@TestPropertySource(properties =
//[
//"spring.kafka.producer.bootstrap-servers=\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
//"spring.kafka.consumer.bootstrap-servers=\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
//"spring.kafka.admin.properties.bootstrap.servers=\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
//"spring.kafka.consumer.group-id=library-events-listener-group"
//])
//class LibraryEventsConsumerTest {
//
//    @Autowired
//    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker
//
//    @Autowired
//    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
//
//    @Autowired
//    private lateinit var endpointRegistry: KafkaListenerEndpointRegistry
//
//    @SpyBean
//    private lateinit var libraryEventsConsumerSpy: LibraryEventsConsumer
//
//    @SpyBean
//    private lateinit var libraryEventsServiceSpy: LibraryEventsService
//
//    @SpyBean    // Used when you don't want to mock the behavior of the method, you really want this to be executed
//    private lateinit var objectMapper: ObjectMapper
//
//
//    @BeforeEach
//    fun setup() {
//        endpointRegistry.listenerContainers.forEach{ messageListenerContainer ->
//            // make sure the container will wait until all partitions are assigned to it before test starts
//            ContainerTestUtils.waitForAssignment(
//                messageListenerContainer,
//                embeddedKafkaBroker.partitionsPerTopic
//            )
//        }
//    }
//
//    @Test
//    fun `publish new library event`() {
//        val libraryEvent = DataGenerator.generateLibraryEventWithIds()
//        libraryEvent.book.libraryEvent = libraryEvent
//        val jsonLibraryEvent = ObjectMapper().writeValueAsString(libraryEvent)
//
//        kafkaTemplate.send(TOPIC_NAME, jsonLibraryEvent).get()
//
//        val latch = CountDownLatch(1)
//        latch.await(10, TimeUnit.SECONDS)
//
//        verify(libraryEventsConsumerSpy, times(1))
//            .onMessage(any<ConsumerRecord<String, String>>(), any(Acknowledgment::class.java))
//
//        verify(libraryEventsServiceSpy, times(1))
//            .persistLibraryEvent(any<ConsumerRecord<String, String>>())
//
//    }
//}