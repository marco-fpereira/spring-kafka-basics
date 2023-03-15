package com.learnkafka.configuration

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.TopicBuilder

public const val TOPIC_NAME = "LIBRARY_EVENTS"

@Configuration
@Profile("local")
class KafkaConfig {


    @Bean
    fun libraryEvents(): NewTopic {
        return TopicBuilder
            .name(TOPIC_NAME)
            .partitions(3)
            .replicas(3)
            .build()
    }
}