package com.imperial.qr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
        int estado,
        String error,
        String mensaje,
        LocalDateTime fecha,
        Map<String, String> detalles
    ) {
        public static ErrorResponse simple(int estado, String error, String mensaje) {
            return new ErrorResponse(estado, error, mensaje, LocalDateTime.now(), null);
        }

        public static ErrorResponse conDetalles(int estado, String error, String mensaje, Map<String, String> detalles) {
            return new ErrorResponse(estado, error, mensaje, LocalDateTime.now(), detalles);
        }
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.simple(HttpStatus.NOT_FOUND.value(), "No Encontrado", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse.simple(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Regla de Negocio No Cumplida", ex.getMessage()));
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarConflicto(ConflictoNegocioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.simple(HttpStatus.CONFLICT.value(), "Conflicto", ex.getMessage()));
    }

    @ExceptionHandler({AccesoDenegadoException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.simple(HttpStatus.FORBIDDEN.value(), "Acceso Denegado", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.conDetalles(HttpStatus.BAD_REQUEST.value(), "Datos Inválidos", "Error de validación en la solicitud", errores));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.simple(HttpStatus.BAD_REQUEST.value(), "Solicitud Inválida", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.simple(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error Interno", ex.getMessage()));
    }
}
