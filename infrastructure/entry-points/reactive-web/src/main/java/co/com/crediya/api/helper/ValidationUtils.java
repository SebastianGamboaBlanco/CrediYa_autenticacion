package co.com.crediya.api.helper;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.exceptions.MultipleValidationException;
import co.com.crediya.model.gateways.RolRepository;
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
    private final RolRepository rolRepository;



    public <T> Mono<T> validateRequest(T object) {
        log.debug("Iniciando validación Bean Validation para objeto: {}", object.getClass().getSimpleName());
        
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            List<Tuple3<String, String, String>> errors = violations.stream()
                .filter(violation -> violation != null)
                .map(violation -> {
                    try {
                        String campo = Objects.isNull(violation.getPropertyPath()) ? "unknown": violation.getPropertyPath().toString() ;
                        String mensaje = Objects.isNull(violation.getMessage()) ? "Error de validación" : violation.getMessage();
                        String valor = Objects.isNull(violation.getInvalidValue()) ? "" : violation.getInvalidValue().toString();
                        
                        log.debug("Creando tupla - Campo: '{}', Mensaje: '{}', Valor: '{}'", campo, mensaje, valor);
                        return Tuples.of(campo, mensaje, valor);
                    } catch (Exception e) {
                        log.error("Error completo procesando violation: {}", e.getMessage(), e);
                        return Tuples.of("error", "Error procesando validación", "");
                    }
                })
                .collect(Collectors.toList());
            
            log.warn("Validación fallida - Objeto: {}, Número de errores: {}", 
                object.getClass().getSimpleName(), errors.size());
                
            return Mono.error(new MultipleValidationException(errors));
        }
        
        log.debug("Validación Bean Validation exitosa para: {}", object.getClass().getSimpleName());
        return Mono.just(object);
    }


    public void validateDocumentoIdentidadOrThrow(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            log.warn("Documento de identidad nulo o vacío");
            throw new BusinessException(ErrorCode.DOCUMENTO_FORMAT_INVALID, documentoIdentidad);
        }
        
        if (!documentoIdentidad.matches("\\d{4,20}")) {
            log.warn("Formato de documento inválido: {} - Debe contener entre 4 y 20 dígitos", documentoIdentidad);
            throw new BusinessException(ErrorCode.DOCUMENTO_FORMAT_INVALID, documentoIdentidad);
        }
        
        log.debug("Documento de identidad válido: {}", documentoIdentidad);
    }


    public Mono<Void> validateRolOrThrow(Long idRol) {
        if (idRol == null) {
            log.warn("ID de rol nulo");
            return Mono.error(new BusinessException(ErrorCode.ROL_INVALID, String.valueOf(idRol)));
        }
        
        log.debug("Validando existencia de rol con ID: {}", idRol);
        
        return rolRepository.existeRol(idRol)
            .flatMap(existe -> {
                if (!existe) {
                    log.warn("Rol con ID {} no existe en la base de datos", idRol);
                    return Mono.error(new BusinessException(ErrorCode.ROL_INVALID, String.valueOf(idRol)));
                }
                log.debug("Rol con ID {} validado exitosamente", idRol);
                return Mono.empty();
            })
            .onErrorMap(error -> {
                if (error instanceof BusinessException) {
                    return error;
                }
                log.error("Error consultando existencia de rol con ID {}: {}", idRol, error.getMessage(), error);
                return new BusinessException(ErrorCode.ROL_INVALID, String.valueOf(idRol));
            })
            .then();
    }

}