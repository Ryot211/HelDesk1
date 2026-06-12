package com.ryot.helpdesk.exception;


import com.ryot.helpdesk.dto.Error.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> businessException(
            BusinessException ex,
            HttpServletRequest request) {
        return  construirRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorDto> manejarNoSuchElementException(
            NoSuchElementException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> manejarJsonMalFormado(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ){
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "El JSON enviado no vale",
                request.getRequestURI()
        );
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorDto> manejarRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ){
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> manejarExceptionGeneral(
            Exception ex,
            HttpServletRequest request
    ){
        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Paso algo inesperado internamente",
                request.getRequestURI()
        );
    }
    private ResponseEntity<ApiErrorDto> construirRespuesta(
            HttpStatus status,
            String mensaje,
            String path
    ){
        ApiErrorDto erro = new ApiErrorDto(
                status.value(),
                status.name(),
                mensaje,
                path,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(erro);
    }
}
