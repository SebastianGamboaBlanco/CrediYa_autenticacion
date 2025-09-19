package co.com.crediya.api.exception;

import co.com.crediya.api.dto.FieldError;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.dto.ValidationErrorResponse;
import co.com.crediya.api.dto.AuthErrorResponse;
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
import org.springframework.core.io.buffer.DataBuffer;

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
        
        log.error("Error processing request - Type: {}, Message: {}", errorType, errorMessage, error);

        if (error instanceof BusinessException businessException) {
            return handleBusinessException(businessException);
        }

        if (error instanceof InsufficientPermissionsException permissionsException) {
            return handleInsufficientPermissionsException(permissionsException);
        }

        if (error instanceof MultipleValidationException multipleValidationException) {
            log.warn("Multiple validation errors - Count: {}", multipleValidationException.getErrors().size());
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ValidationErrorResponse.from(multipleValidationException.getErrors()));
        }

        if (error instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().isEmpty() ? "unknown" : 
                               mismatchedInputException.getPath().get(0).getFieldName();
            String message = "Required field not provided: " + fieldName;
            
            List<FieldError> fieldErrors = List.of(new FieldError(fieldName, message, null));
            ValidationErrorResponse response = new ValidationErrorResponse(1, fieldErrors, TraceUtils.getCurrentTrace());
            
            log.warn("Missing required field - Field: {}", fieldName);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
        }

        if (error instanceof IllegalArgumentException) {
            log.warn("Bean Validation error - Message: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(errorMessage));
        }

        if (error instanceof DateTimeParseException) {
            String mensajeClaro = "Invalid date format. Use MM-dd-yyyy format (example: 05-15-1990)";
            log.warn("Date format error - Message: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(mensajeClaro));
        }


        if (error.getMessage() != null && error.getMessage().contains("fk_usuario_rol")) {
            String mensajeClaro = "The specified role does not exist in the system";
            log.warn("Foreign Key constraint violation - Non-existent role: {}", errorMessage);
            return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(mensajeClaro));
        }

        log.error("Unhandled internal error - Type: {}, Message: {}", errorType, errorMessage, error);

        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(String.valueOf(error)));
    }

    public Mono<ServerResponse> handleAuthenticationError(Throwable error) {
        log.error("JWT AUTHENTICATION ERROR - ENTERING HANDLER: {}", error.getMessage());

        AuthErrorResponse response = buildAuthErrorResponse(error);
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    private AuthErrorResponse buildAuthErrorResponse(Throwable error) {
        String errorMessage = error.getMessage();
        
        if (errorMessage != null && errorMessage.contains("JWT token not found")) {
            return AuthErrorResponse.tokenRequired();
        } else if (errorMessage != null && errorMessage.contains("invalid")) {
            return AuthErrorResponse.tokenInvalid();
        } else {
            return AuthErrorResponse.unauthorized("Authentication error");
        }
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException businessException) {
        String errorMessage = businessException.getMessage();
        ErrorType errorType = businessException.getErrorType();
        
        log.warn("Business error - Type: {}, Field: {}, Value: {}, Message: {}", 
                errorType, businessException.getField(), businessException.getValue(), errorMessage);
        
        return switch (errorType) {
            case VALIDATION, FORMAT, OUT_OF_RANGE -> ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(errorMessage));
                
            case ALREADY_EXISTS -> ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.conflict(errorMessage));
        };
    }

    private Mono<ServerResponse> handleInsufficientPermissionsException(InsufficientPermissionsException permissionsException) {
        String errorMessage = permissionsException.getMessage();
        String userId = permissionsException.getUserId();
        String requiredRole = permissionsException.getRequiredRole();
        String userRole = permissionsException.getUserRole();
        
        log.warn("Insufficient permissions - User ID: {}, Current role: {}, Required role: {}, Message: {}", 
                userId, userRole, requiredRole, errorMessage);
        
        return ServerResponse.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserResponse.error(errorMessage));
    }

    public Mono<Void> handleJwtAuthenticationError(ServerWebExchange exchange, Throwable error) {
        log.error("JWT AUTHENTICATION ERROR - CENTRALIZED HANDLER: {}", error.getMessage());
        
        AuthErrorResponse authResponse = buildAuthErrorResponse(error);
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        try {
            String responseBody = objectMapper.writeValueAsString(authResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializing authentication response", e);
            return exchange.getResponse().setComplete();
        }
    }

}