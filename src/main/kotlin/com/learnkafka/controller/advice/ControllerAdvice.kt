package com.learnkafka.controller.advice

import com.learnkafka.exception.MissingBodyException
import com.learnkafka.exception.dto.ErrorResponseDTO
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(MissingBodyException::class)
    fun handleMissingBodyException(ex: MissingBodyException)
    : ResponseEntity<List<ErrorResponseDTO>> = ResponseEntity.badRequest().body(ex.errorList)

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException)
    : ResponseEntity<List<ErrorResponseDTO>> {
        val errorResponses = mutableListOf<ErrorResponseDTO>()
        ex.constraintViolations.forEach {
            errorResponses.add(ErrorResponseDTO(it.invalidValue.toString(), it.message))
        }
        return ResponseEntity.badRequest().body(errorResponses)
    }
}