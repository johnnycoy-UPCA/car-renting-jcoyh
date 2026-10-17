package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.input.rest.exception;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.AutoNoDisponibleException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.AlquilerNoValidoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.ClienteNoValidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT FOUND - Cuando lo que busca no existe en H2
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorRespuesta> manejarRuntimeExceptionGenérica(RuntimeException ex) {
        String mensaje = ex.getMessage();
        
        // Establecer el tipo de excepción para clasificarla correctamente
        if (mensaje != null && mensaje.contains("no se encuentra registrado")) {
            ErrorRespuesta error = new ErrorRespuesta(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                mensaje
            );
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // 409 CONFLICT - Alquileres Activos detectados
        if (mensaje != null && mensaje.contains("Operación Denegada")) {
            ErrorRespuesta error = new ErrorRespuesta(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                mensaje
            );
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        // Error genérico para excepciones no clasificadas
        ErrorRespuesta errorGenerico = new ErrorRespuesta(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            mensaje
        );
        return new ResponseEntity<>(errorGenerico, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 409 CONFLICT - Excepciones específicas de (Auto)
    @ExceptionHandler(AutoNoDisponibleException.class)
    public ResponseEntity<ErrorRespuesta> manejarAutoNoDisponible(AutoNoDisponibleException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 400 BAD REQUEST - Datos Inválidos (Días <= 0, etc.)
    @ExceptionHandler(AlquilerNoValidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarAlquilerNoValido(AlquilerNoValidoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 400 BAD REQUEST - Duplicidad o Datos Erróneos de Clientes
    @ExceptionHandler(ClienteNoValidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarClienteNoValido(ClienteNoValidoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
