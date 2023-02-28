package com.learnkafka.controller

import com.learnkafka.domain.LibraryEvent
import com.learnkafka.domain.enums.LibraryEventTypeEnum
import com.learnkafka.exception.MissingBodyException
import com.learnkafka.service.LibraryEventsService
import com.learnkafka.utils.ErrorMapper
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class LibraryEventsController {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var libraryEventsService: LibraryEventsService

    @PostMapping("/v1/libraryevent")
    fun postLibraryEvent(
        @Valid @RequestBody libraryEvent: LibraryEvent,
        @RequestParam("synchronously", required = false) synchronously: String?,
        @RequestParam("include_headers", required = false) includeHeaders: String?,
        @RequestHeader headers: Map<String, String>?,
        result: BindingResult
    ): ResponseEntity<LibraryEvent> {
        if(result.hasErrors()) {
            val errors = ErrorMapper.mapBindingResultErrors(result)
            logger.error("Error validating request body. Details: $errors")
            throw MissingBodyException(errorList = errors)
        }
        logger.info("Request body validated! Sending event to Kafka topic")
        libraryEvent.libraryEventType = LibraryEventTypeEnum.NEW

        return libraryEventsService.postLibraryEvent(
            libraryEvent = libraryEvent,
            synchronously = synchronously,
            includeHeaders = includeHeaders,
            headers = headers
        )
    }
}