package com.learnkafka.configuration

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.KafkaException
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.*
import org.springframework.util.backoff.FixedBackOff
import java.util.function.BiFunction

const val TOPIC_NAME = "LIBRARY_EVENTS"

@Configuration
@EnableKafka
class KafkaConfig {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    private lateinit var properties: KafkaProperties

    @Value("\${topics.retry}")
    private lateinit var retryTopic: String

    @Value("\${topics.dlt}")
    private lateinit var dltTopic: String

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun kafkaListenerContainerFactory(
        configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
        kafkaConsumerFactory: ObjectProvider<ConsumerFactory<Any, Any>>
    ) : ConcurrentKafkaListenerContainerFactory<*,*> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable{
            DefaultKafkaConsumerFactory(this.properties.buildConsumerProperties())
        })
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.containerProperties.pollTimeout = 1000000
        // it sets 3 differents threads, which one with a poll loop that will process the events paralelly between the partitions
        factory.setConcurrency(3)
        factory.setCommonErrorHandler(getErrorHandler())
        return factory
    }

    private fun getErrorHandler(): CommonErrorHandler {
        // setting retries to happen in a 5 seconds interval and the max attempts of 5 before it fails
        val backoff = FixedBackOff(5000L, 5L)

        // instantiating error handler with customized retry/DLT topics and a fixed backoff (could be exponential)
        val errorHandler = DefaultErrorHandler(publishRecoverer(), backoff)

        // if such an exception is thrown, then there won't be retries
        errorHandler.addNotRetryableExceptions(
            IllegalArgumentException::class.java, NullPointerException::class.java
        )

        // if such an exception is thrown, then there will be retries
        errorHandler.addRetryableExceptions(KafkaException::class.java)

        // It enables us to log retries information
        errorHandler.setRetryListeners (
            object: RetryListener {
                override fun failedDelivery(record: ConsumerRecord<*, *>, ex: Exception, deliveryAttempt: Int) {
                    logger.error("Failed Record in retry  listener." +
                            "\nMessage: ${record.value()}" +
                            "\nException: ${ex.message}" +
                            "\nDelivery attempt: $deliveryAttempt")
                }
            }
        )
        return errorHandler
    }

    fun publishRecoverer(): DeadLetterPublishingRecoverer {
        return DeadLetterPublishingRecoverer(kafkaTemplate,
            BiFunction { r: ConsumerRecord<*, *>, e: Exception? ->
                if (e is KafkaException)
                    return@BiFunction TopicPartition(retryTopic, r.partition())
                else
                    return@BiFunction TopicPartition(dltTopic, r.partition())
            }
        )
    }
}