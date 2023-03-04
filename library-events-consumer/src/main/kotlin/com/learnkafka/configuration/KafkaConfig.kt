package com.learnkafka.configuration

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

public const val TOPIC_NAME = "LIBRARY_EVENTS"

@Configuration
@EnableKafka
class KafkaConfig {

    private lateinit var properties: KafkaProperties

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
        /* it sets 3 differents threads, which one with a poll loop that
         * will process the events paralelly between the partitions
        */
        factory.setConcurrency(3)
        return factory
    }

}