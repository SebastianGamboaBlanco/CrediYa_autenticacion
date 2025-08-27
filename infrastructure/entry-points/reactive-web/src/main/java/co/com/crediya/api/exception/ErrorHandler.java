package co.com.crediya.api.exception;

import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.model.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class ErrorHandler {

    public Mono<ServerResponse> handleError(Throwable error) {
        String errorType = error.getClass().getSimpleName();
        String errorMessage = error.getMessage();
        
        log.error("Error procesando request - Tipo: {}, Mensaje: {}", errorType, errorMessage, error);

        // Errores de validación del dominio
        if (isValidationError(error)) {
            log.warn("Error de validación del dominio - Tipo: {}, Mensaje: {}", errorType, errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
        }

        // Email ya existe
        if (error instanceof CorreoExistInvalidException) {
            log.warn("Intento de registro con email existente - Error: {}", errorMessage);
            return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.conflict(errorMessage));
        }

        // Documento de identidad ya existe
        if (error instanceof DocumentoExistInvalidException) {
            log.warn("Intento de registro con documento de identidad existente - Error: {}", errorMessage);
            return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.conflict(errorMessage));
        }

        // Errores de validación de Bean Validation
        if (error instanceof IllegalArgumentException) {
            log.warn("Error de validación Bean Validation - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
        }

        // Error de formato de fecha
        if (error instanceof DateTimeParseException) {
            String mensajeClaro = "Formato de fecha inválido. Use el formato MM-dd-yyyy (ejemplo: 05-15-1990)";
            log.warn("Error de formato de fecha - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(mensajeClaro));
        }

        // Error de formato de documento de identidad
        if (error instanceof DocumentoFormatoInvalidException) {
            log.warn("Error de formato de documento - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
        }

        // Error de rol inválido
        if (error instanceof RolInvalidException) {
            log.warn("Error de rol inválido - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
        }

        // Error de violación de foreign key (rol inexistente en BD)
        if (error.getMessage() != null && error.getMessage().contains("fk_usuario_rol")) {
            String mensajeClaro = "El rol especificado no existe en el sistema";
            log.warn("Violación de Foreign Key constraint - Rol inexistente: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(mensajeClaro));
        }

        // Error genérico
        log.error("Error interno no manejado - Tipo: {}, Mensaje: {}", errorType, errorMessage, error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(UsuarioResponse.internalError("Error interno del servidor"));
    }

    private boolean isValidationError(Throwable error) {
        return error instanceof NombreInvalidException ||
               error instanceof ApellidoInvalidException ||
               error instanceof CorreoInvalidException ||
               error instanceof CorreoFormatoInvalidException ||
               error instanceof SalarioInvalidException ||
               error instanceof SalarioRangoInvalidException ||
               error instanceof RolInvalidException;
    }
}