package com.learnkafka.controller.advice

import com.learnkafka.exception.MissingBodyException
import com.learnkafka.exception.dto.ErrorResponseDTO
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.KafkaException
import org.springframework.web.bind.annotation.ExceptionHandler

@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MissingBodyException::class)
    fun handleMissingBodyException(ex: MissingBodyException)
    : ResponseEntity<List<ErrorResponseDTO>> {
        logger.error("Error details: ${ex.message}")
        return ResponseEntity.badRequest().body(ex.errorList)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException)
    : ResponseEntity<List<ErrorResponseDTO>> {
        val errorResponses = mutableListOf<ErrorResponseDTO>()
        ex.constraintViolations.forEach {
            errorResponses.add(ErrorResponseDTO(it.invalidValue.toString(), it.message))
        }
        logger.error("Error details: ${ex.message}")
        return ResponseEntity.badRequest().body(errorResponses)
    }

/*    @ExceptionHandler(KafkaException::class)
    fun handleKafkaException(ex: KafkaException)
    : KafkaException {
        logger.error("Handled error details: ${ex.message}")
        throw ex
    }*/
}