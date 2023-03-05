package com.learnkafka.consumer

import com.learnkafka.configuration.TOPIC_NAME
import com.learnkafka.jpa.service.LibraryEventsService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.PartitionOffset
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class LibraryEventsConsumer : AcknowledgingMessageListener<String,String>{

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var libraryEventsService: LibraryEventsService

    @KafkaListener(
        topics = [TOPIC_NAME],
/*        topicPartitions = [TopicPartition(
            topic = TOPIC_NAME,partitionOffsets = [PartitionOffset(partition = "0", initialOffset = "0")]
        )]
*/
    )
    override fun onMessage(
        consumerRecord: ConsumerRecord<String, String>,
        acknowledgment: Acknowledgment?
    ) {
        if (acknowledgment != null) {
            logger.info("consumer record: $consumerRecord")
            libraryEventsService.persistLibraryEvent(consumerRecord)
            acknowledgment.acknowledge()
        } else{
            logger.error("Acknowledgmente is null")
            throw Exception("Acknowledgmente is null")
        }
    }
}