package co.com.crediya.api.processor;

import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.helper.ValidationUtils;
import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.usecase.ConsultarUsuarioUseCase;
import co.com.crediya.usecase.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioProcessor {
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ConsultarUsuarioUseCase consultarUsuarioUseCase;
    private final ValidationUtils validationUtils;

    public Mono<Void> procesarRegistro(UsuarioRequest request) {
        return Mono.deferContextual(ctx -> {
            String traceId = ctx.getOrDefault("traceId", "");
            MDC.put("traceId", traceId);
            MDC.put("operation", "registro");
            MDC.put("email", request.getCorreoElectronico());
            MDC.put("documentoId", request.getDocumentoIdentidad());
            
            log.info("INICIO - Procesando registro - Email: {}, DocumentoID: {}", 
                    request.getCorreoElectronico(), request.getDocumentoIdentidad());
            
            return validationUtils.validateRequest(request)
                    .doOnNext(req -> {
                        MDC.put("traceId", traceId);
                        log.debug("Validaciones Bean Validation completadas");
                    })
                    .flatMap(req -> 
                        validationUtils.validateRolOrThrow(req.getIdRol())
                            .then(Mono.just(req))
                    )
                    .doOnNext(req -> {
                        MDC.put("traceId", traceId);
                        log.debug("Validaciones de negocio completadas");
                    })
                    .flatMap(req -> 
                        registrarUsuarioUseCase.registrarUsuario(
                            req.getNombres(),
                            req.getApellidos(), 
                            req.getCorreoElectronico(),
                            req.getSalarioBase(),
                            req.getDocumentoIdentidad(),
                            req.getFechaNacimiento(),
                            req.getTelefono(),
                            req.getIdRol())
                    )
                    .doOnSuccess(unused -> {
                        MDC.put("traceId", traceId);
                        log.info("FIN EXITOSO - Usuario registrado - Email: {}", 
                                request.getCorreoElectronico());
                    })
                    .doOnError(error -> {
                        MDC.put("traceId", traceId);
                        log.error("FIN CON ERROR - Error procesando registro - Email: {}, Tipo: {}, Mensaje: {}", 
                                request.getCorreoElectronico(), error.getClass().getSimpleName(), error.getMessage());
                    });
        });
    }

    public Mono<UsuarioCompleto> procesarConsulta(String documentoIdentidad) {
        return Mono.deferContextual(ctx -> {
            String traceId = ctx.getOrDefault("traceId", "");
            MDC.put("traceId", traceId);
            MDC.put("operation", "consulta");
            MDC.put("documentoId", documentoIdentidad);
            
            log.info("INICIO - Procesando consulta - DocumentoID: {}", documentoIdentidad);
            
            return Mono.fromRunnable(() -> validationUtils.validateDocumentoIdentidadOrThrow(documentoIdentidad))
                    .then(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documentoIdentidad))
                    .doOnNext(usuario -> {
                        MDC.put("traceId", traceId);
                        log.info("FIN EXITOSO - Usuario encontrado - DocumentoID: {}, UsuarioID: {}", 
                                documentoIdentidad, usuario.getId());
                    })
                    .doOnError(error -> {
                        MDC.put("traceId", traceId);
                        log.error("FIN CON ERROR - Error procesando consulta - DocumentoID: {}, Tipo: {}, Mensaje: {}", 
                                documentoIdentidad, error.getClass().getSimpleName(), error.getMessage());
                    });
        });
    }
}