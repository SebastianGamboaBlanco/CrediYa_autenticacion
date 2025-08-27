package co.com.crediya.api.helper;

import co.com.crediya.model.exceptions.DocumentoFormatoInvalidException;
import co.com.crediya.model.exceptions.RolInvalidException;
import co.com.crediya.model.gateways.RolRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationUtils {
    
    private final Validator validator;
    private final RolRepository rolRepository;

    /**
     * Valida un objeto usando Bean Validation y retorna Mono reactivo
     * 
     * @param object Objeto a validar con anotaciones jakarta.validation
     * @param <T> Tipo del objeto
     * @return Mono.just(object) si es válido, Mono.error(IllegalArgumentException) si no
     */
    public <T> Mono<T> validateRequest(T object) {
        log.debug("Iniciando validación Bean Validation para objeto: {}", object.getClass().getSimpleName());
        
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            
            log.warn("Validación fallida - Objeto: {}, Errores: {}", object.getClass().getSimpleName(), errorMessage);
            log.debug("Detalles violaciones: {}", violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList()));
                
            return Mono.error(new IllegalArgumentException(errorMessage));
        }
        
        log.debug("Validación Bean Validation exitosa para: {}", object.getClass().getSimpleName());
        return Mono.just(object);
    }

    /**
     * Valida el formato del documento de identidad y lanza excepción si es inválido
     * 
     * @param documentoIdentidad Documento a validar
     * @throws DocumentoFormatoInvalidException si el formato es inválido
     */
    public void validateDocumentoIdentidadOrThrow(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            log.warn("Documento de identidad nulo o vacío");
            throw new DocumentoFormatoInvalidException(documentoIdentidad);
        }
        
        if (!documentoIdentidad.matches("\\d{4,20}")) {
            log.warn("Formato de documento inválido: {} - Debe contener entre 4 y 20 dígitos", documentoIdentidad);
            throw new DocumentoFormatoInvalidException(documentoIdentidad);
        }
        
        log.debug("Documento de identidad válido: {}", documentoIdentidad);
    }

    /**
     * Valida que el ID de rol exista en la base de datos de forma reactiva
     * 
     * @param idRol ID del rol a validar
     * @return Mono<Void> que completa exitosamente si el rol existe, error si no existe
     */
    public Mono<Void> validateRolOrThrow(Long idRol) {
        if (idRol == null) {
            log.warn("ID de rol nulo");
            return Mono.error(new RolInvalidException(null));
        }
        
        log.debug("Validando existencia de rol con ID: {}", idRol);
        
        return rolRepository.existeRol(idRol)
            .flatMap(existe -> {
                if (!existe) {
                    log.warn("Rol con ID {} no existe en la base de datos", idRol);
                    return Mono.error(new RolInvalidException(idRol));
                }
                log.debug("Rol con ID {} validado exitosamente", idRol);
                return Mono.empty();
            })
            .onErrorMap(error -> {
                if (error instanceof RolInvalidException) {
                    return error;
                }
                log.error("Error consultando existencia de rol con ID {}: {}", idRol, error.getMessage(), error);
                return new RolInvalidException(idRol);
            })
            .then();
    }

}