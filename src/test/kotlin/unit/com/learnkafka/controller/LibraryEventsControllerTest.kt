package com.learnkafka.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.learnkafka.dto.DataGenerator
import com.learnkafka.service.LibraryEventsService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.validation.BindingResult


@WebMvcTest(LibraryEventsController::class)
@AutoConfigureMockMvc
class LibraryEventsControllerTest {

    @Autowired
    private lateinit var mockMvc : MockMvc

    @MockBean
    private lateinit var libraryEventsService: LibraryEventsService

    @MockBean
    private lateinit var result : BindingResult

    @Test
    fun `post library event test when it succeeds`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithoutIds()
        val jsonLibraryEvent = serializeValue(libraryEvent)

        val headers = HttpHeaders()
        headers.set("content-type", MediaType.APPLICATION_JSON.toString())

        `when`(result.hasErrors()).thenReturn(false)

        `when`(libraryEventsService.postLibraryEvent(
            libraryEvent = libraryEvent,
            synchronously = "false",
            includeHeaders = "false"
        )).thenReturn(ResponseEntity(libraryEvent, HttpStatus.CREATED))

        mockMvc.perform(post(
            "/v1/libraryevent?synchronously=false&include_headers=false")
            .content(jsonLibraryEvent)
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `post library event test when payload is wrong`() {
        val libraryEvent = DataGenerator.generateLibraryEventWithoutIds()

        val headers = HttpHeaders()
        headers.set("content-type", MediaType.APPLICATION_JSON.toString())

        `when`(result.hasErrors()).thenReturn(false)

        `when`(libraryEventsService.postLibraryEvent(
            libraryEvent = libraryEvent,
            synchronously = "false",
            includeHeaders = "false"
        )).thenReturn(ResponseEntity(libraryEvent, HttpStatus.CREATED))

        mockMvc.perform(post(
            "/v1/libraryevent?synchronously=false&include_headers=false")
            .content("")
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError)
    }


    private fun serializeValue(value: Any) : String {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        mapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        val writter = mapper.writer().withDefaultPrettyPrinter()
        return writter.writeValueAsString(value)

    }
}