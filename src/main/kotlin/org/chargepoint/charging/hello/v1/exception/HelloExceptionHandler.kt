package org.chargepoint.charging.hello.v1.exception

import jakarta.validation.ConstraintViolationException
import org.chargepoint.charging.hello.v1.dto.HelloResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class HelloExceptionHandler {
    private val log = LoggerFactory.getLogger(HelloExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentException(exception:MethodArgumentNotValidException):ResponseEntity<HelloResponse>{
        log.error(String.format("Invalid argument: %s"+exception.typeMessageCode),exception)
        return ResponseEntity.badRequest().body(HelloResponse(exception.message,false))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationException(exception:ConstraintViolationException):ResponseEntity<HelloResponse>{
        log.error(String.format("Invalid argument: %s",exception.message?:"Unknown error"),exception)
        return ResponseEntity.badRequest().body(HelloResponse(exception.constraintViolations.map { it.message }.joinToString ( "," ),false))
    }

}