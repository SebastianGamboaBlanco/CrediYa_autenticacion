package co.com.crediya.api.exception;

import co.com.crediya.api.dto.FieldError;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.api.dto.ValidationErrorResponse;
import co.com.crediya.api.dto.AuthErrorResponse;
import co.com.crediya.api.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import co.com.crediya.api.helper.TraceUtils;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorType;
import co.com.crediya.model.exceptions.MultipleValidationException;
import co.com.crediya.model.exceptions.InsufficientPermissionsException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.util.List;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class ErrorHandler {

    private final ObjectMapper objectMapper;

    public ErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<ServerResponse> handleError(Throwable error) {
        String errorType = error.getClass().getSimpleName();
        String errorMessage = error.getMessage();
        
        log.error("Error procesando request - Tipo: {}, Mensaje: {}", errorType, errorMessage, error);

        if (error instanceof BusinessException businessException) {
            return handleBusinessException(businessException);
        }

        if (error instanceof InsufficientPermissionsException permissionsException) {
            return handleInsufficientPermissionsException(permissionsException);
        }

        if (error instanceof MultipleValidationException multipleValidationException) {
            log.warn("Múltiples errores de validación - Cantidad: {}", multipleValidationException.getErrors().size());
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ValidationErrorResponse.from(multipleValidationException.getErrors()));
        }

        if (error instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().isEmpty() ? "unknown" : 
                               mismatchedInputException.getPath().get(0).getFieldName();
            String message = "Campo obligatorio no proporcionado: " + fieldName;
            
            List<FieldError> fieldErrors = List.of(new FieldError(fieldName, message, null));
            ValidationErrorResponse response = new ValidationErrorResponse(1, fieldErrors, TraceUtils.getCurrentTrace());
            
            log.warn("Campo obligatorio faltante - Campo: {}", fieldName);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
        }

        if (error instanceof IllegalArgumentException) {
            log.warn("Error de validación Bean Validation - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
        }

        if (error instanceof DateTimeParseException) {
            String mensajeClaro = "Formato de fecha inválido. Use el formato MM-dd-yyyy (ejemplo: 05-15-1990)";
            log.warn("Error de formato de fecha - Mensaje: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(mensajeClaro));
        }


        if (error.getMessage() != null && error.getMessage().contains("fk_usuario_rol")) {
            String mensajeClaro = "El rol especificado no existe en el sistema";
            log.warn("Violación de Foreign Key constraint - Rol inexistente: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(mensajeClaro));
        }

        log.error("Error interno no manejado - Tipo: {}, Mensaje: {}", errorType, errorMessage, error);
        /*return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(UsuarioResponse.internalError("Error interno del servidor"));*/
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(String.valueOf(error)));
    }

    public Mono<ServerResponse> handleAuthenticationError(Throwable error) {
        log.error("🔥 ERROR DE AUTENTICACION JWT - ENTRANDO AL HANDLER: {}", error.getMessage());

        AuthErrorResponse response = buildAuthErrorResponse(error);
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    private AuthErrorResponse buildAuthErrorResponse(Throwable error) {
        String errorMessage = error.getMessage();
        
        if (errorMessage != null && errorMessage.contains("Token JWT no encontrado")) {
            return AuthErrorResponse.tokenRequired();
        } else if (errorMessage != null && errorMessage.contains("inválido")) {
            return AuthErrorResponse.tokenInvalid();
        } else {
            return AuthErrorResponse.unauthorized("Error de autenticación");
        }
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException businessException) {
        String errorMessage = businessException.getMessage();
        ErrorType errorType = businessException.getErrorType();
        
        log.warn("Error de negocio - Tipo: {}, Campo: {}, Valor: {}, Mensaje: {}", 
                errorType, businessException.getField(), businessException.getValue(), errorMessage);
        
        return switch (errorType) {
            case VALIDATION, FORMAT, OUT_OF_RANGE -> ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
                
            case ALREADY_EXISTS -> ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.conflict(errorMessage));
        };
    }

    private Mono<ServerResponse> handleInsufficientPermissionsException(InsufficientPermissionsException permissionsException) {
        String errorMessage = permissionsException.getMessage();
        String userId = permissionsException.getUserId();
        String requiredRole = permissionsException.getRequiredRole();
        String userRole = permissionsException.getUserRole();
        
        log.warn("Permisos insuficientes - Usuario ID: {}, Rol actual: {}, Rol requerido: {}, Mensaje: {}", 
                userId, userRole, requiredRole, errorMessage);
        
        return ServerResponse.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UsuarioResponse.error(errorMessage));
    }

    public Mono<Void> handleJwtAuthenticationError(ServerWebExchange exchange, Throwable error) {
        log.error("🔥 ERROR DE AUTENTICACION JWT - HANDLER CENTRALIZADO: {}", error.getMessage());
        
        AuthErrorResponse authResponse = buildAuthErrorResponse(error);
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        try {
            String responseBody = objectMapper.writeValueAsString(authResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializando respuesta de autenticación", e);
            return exchange.getResponse().setComplete();
        }
    }

}