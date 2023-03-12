package com.learnkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LibraryEventsConsumerApplication

fun main(args: Array<String>) {
	runApplication<LibraryEventsConsumerApplication>(*args)
}
