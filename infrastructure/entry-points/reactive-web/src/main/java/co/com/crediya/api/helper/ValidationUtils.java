package co.com.crediya.api.helper;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.exceptions.MultipleValidationException;
import co.com.crediya.model.gateways.RoleRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationUtils {
    
    private final Validator validator;
    private final RoleRepository roleRepository;



    public <T> Mono<T> validateRequest(T object) {
        log.debug("Starting Bean Validation for object: {}", object.getClass().getSimpleName());
        
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            List<Tuple3<String, String, String>> errors = violations.stream()
                .filter(violation -> violation != null)
                .map(violation -> {
                    try {
                        String campo = Objects.isNull(violation.getPropertyPath()) ? "unknown": violation.getPropertyPath().toString() ;
                        String mensaje = Objects.isNull(violation.getMessage()) ? "Validation error" : violation.getMessage();
                        String valor = Objects.isNull(violation.getInvalidValue()) ? "" : violation.getInvalidValue().toString();
                        
                        log.debug("Creating tuple - Field: '{}', Message: '{}', Value: '{}'", campo, mensaje, valor);
                        return Tuples.of(campo, mensaje, valor);
                    } catch (Exception e) {
                        log.error("Complete error processing violation: {}", e.getMessage(), e);
                        return Tuples.of("error", "Error processing validation", "");
                    }
                })
                .collect(Collectors.toList());
            
            log.warn("Validation failed - Object: {}, Number of errors: {}", 
                object.getClass().getSimpleName(), errors.size());
                
            return Mono.error(new MultipleValidationException(errors));
        }
        
        log.debug("Bean Validation successful for: {}", object.getClass().getSimpleName());
        return Mono.just(object);
    }


    public void validateDocumentoIdentidadOrThrow(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            log.warn("Identity document null or empty");
            throw new BusinessException(ErrorCode.DOCUMENT_FORMAT_INVALID, documentoIdentidad);
        }
        
        if (!documentoIdentidad.matches("\\d{4,20}")) {
            log.warn("Invalid document format: {} - Must contain between 4 and 20 digits", documentoIdentidad);
            throw new BusinessException(ErrorCode.DOCUMENT_FORMAT_INVALID, documentoIdentidad);
        }
        
        log.debug("Valid identity document: {}", documentoIdentidad);
    }


    public Mono<Void> validateRolOrThrow(Long idRol) {
        if (idRol == null) {
            log.warn("Role ID null");
            return Mono.error(new BusinessException(ErrorCode.ROLE_INVALID, String.valueOf(idRol)));
        }
        
        log.debug("Validating role existence with ID: {}", idRol);
        
        return roleRepository.roleExists(idRol)
            .flatMap(existe -> {
                if (!existe) {
                    log.warn("Role with ID {} does not exist in database", idRol);
                    return Mono.error(new BusinessException(ErrorCode.ROLE_INVALID, String.valueOf(idRol)));
                }
                log.debug("Role with ID {} validated successfully", idRol);
                return Mono.empty();
            })
            .onErrorMap(error -> {
                if (error instanceof BusinessException) {
                    return error;
                }
                log.error("Error querying role existence with ID {}: {}", idRol, error.getMessage(), error);
                return new BusinessException(ErrorCode.ROLE_INVALID, String.valueOf(idRol));
            })
            .then();
    }

}