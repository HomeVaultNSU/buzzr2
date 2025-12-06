package ru.nsu.ccfit.buzzr.discovery.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nsu.ccfit.buzzr.discovery.core.exception.AgentException;

@Slf4j
@RestControllerAdvice
public class AgentExceptionHandler {

    @ExceptionHandler(AgentException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        log.error("RuntimeException caught {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}