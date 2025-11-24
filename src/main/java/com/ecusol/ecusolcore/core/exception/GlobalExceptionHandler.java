// core/excepcion/GlobalExceptionHandler.java
package com.ecusol.ecusolcore.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Capturar RuntimeException (Lo que lanzas en AuthService)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        // Aquí capturamos el texto exacto que pusiste en el throw new RuntimeException("...")
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", "ERROR");

        // Devolvemos un 400 Bad Request para que el front sepa que fue un error de lógica/datos
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. Capturar cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Error interno del servidor: " + ex.getMessage());
        errorResponse.put("status", "INTERNAL_ERROR");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}