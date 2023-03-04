package com.learnkafka.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.learnkafka.domain.enums.LibraryEventTypeEnum
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class LibraryEvent(
    @JsonProperty("library_event_id")
    @field:Pattern(regexp = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})|^$", message = "library id is mandatory")
    var libraryEventId: String? = null,

    var libraryEventType: LibraryEventTypeEnum? = null,

    @field:NotNull(message = "book is mandatory")
    @field:Valid
    val book : Book
)