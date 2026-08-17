package com.samuelDawid.medical_clinic.exceptions.handler;

import com.samuelDawid.medical_clinic.dto.ErrorMessageDto;
import com.samuelDawid.medical_clinic.exceptions.MedicalClinicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class MedicalClinicExceptionHandler {
    private static final DateTimeFormatter BASIC_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy:HH:mm");

    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessageDto> handleMedicalClinicException(MedicalClinicException exception) {
        log.warn("Rejected: {} -> {}",exception.getMessage(), exception.getStatus().value());
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorMessageDto(exception.getMessage(), exception.getStatus()
                        .value(), LocalDateTime.now()
                        .format(BASIC_FORMAT)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpected(Exception exception) {
        log.error("Unhandled exception",exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDto(exception.getMessage(), 500, LocalDateTime.now()
                        .format(BASIC_FORMAT)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handleMethodArgument(MethodArgumentNotValidException error) {
        String message = error.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message, error.getStatusCode()
                        .value(), LocalDateTime.now()
                        .format(BASIC_FORMAT)));


    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDto> handleMessageNotReadable(HttpMessageNotReadableException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(error.getMessage(), 400, LocalDateTime.now()
                        .format(BASIC_FORMAT)));
    }
}
