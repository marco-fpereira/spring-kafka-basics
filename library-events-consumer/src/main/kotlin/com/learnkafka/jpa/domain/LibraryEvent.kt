package com.learnkafka.jpa.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.learnkafka.jpa.domain.enums.LibraryEventTypeEnum
import jakarta.persistence.*

@Entity
data class LibraryEvent(
    @JsonProperty("library_event_id")
    @Id
    @Column(name = "library_event_id")
    var libraryEventId: String,

    @JsonProperty("library_event_type")
    @Enumerated(EnumType.STRING)
    @Column(name = "library_event_type")
    var libraryEventType: LibraryEventTypeEnum,

    @OneToOne(mappedBy = "libraryEvent", cascade = [CascadeType.ALL])
    val book : Book
)