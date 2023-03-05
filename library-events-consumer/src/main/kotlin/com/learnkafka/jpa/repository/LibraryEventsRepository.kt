package com.learnkafka.jpa.repository

import com.learnkafka.jpa.domain.LibraryEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LibraryEventsRepository: CrudRepository<LibraryEvent, String>