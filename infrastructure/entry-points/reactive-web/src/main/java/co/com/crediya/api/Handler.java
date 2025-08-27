package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.api.dto.UsuarioConsultaResponse;
import co.com.crediya.api.exception.ErrorHandler;
import co.com.crediya.api.helper.ValidationUtils;
import co.com.crediya.usecase.RegistrarUsuarioUseCase;
import co.com.crediya.usecase.ConsultarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ConsultarUsuarioUseCase consultarUsuarioUseCase;
    private final ValidationUtils validationUtils;
    private final ErrorHandler errorHandler;

    public Mono<ServerResponse> registrarUsuario(ServerRequest serverRequest) {
        log.info("Iniciando proceso de registro de usuario");
                
        return serverRequest.bodyToMono(UsuarioRequest.class)
                .doOnNext(request -> log.info("Request recibido - Email: {}, DocumentoID: {}", 
                        request.getCorreoElectronico(), request.getDocumentoIdentidad()))
                .doOnError(error -> log.error("Error deserializando request body", error))
                .flatMap(validationUtils::validateRequest)
                .doOnNext(request -> log.debug("Validaciones Bean Validation completadas exitosamente"))
                .flatMap(request -> 
                    validationUtils.validateRolOrThrow(request.getIdRol())
                        .then(Mono.just(request))
                )
                .doOnNext(request -> log.debug("Validaciones de negocio completadas exitosamente"))
                .flatMap(request -> 
                    registrarUsuarioUseCase.registrarUsuario(
                        request.getNombres(),
                        request.getApellidos(), 
                        request.getCorreoElectronico(),
                        request.getSalarioBase(),
                        request.getDocumentoIdentidad(),
                        request.getFechaNacimiento(),
                        request.getTelefono(),
                        request.getIdRol())
                        .doOnSuccess(unused -> log.info("Usuario registrado exitosamente - Email: {}", 
                                request.getCorreoElectronico()))
                        .then(ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(UsuarioResponse.success("Usuario registrado exitosamente")))
                )
                .doOnError(error -> log.error("Error en flujo de registro - Tipo: {}, Mensaje: {}", 
                        error.getClass().getSimpleName(), error.getMessage()))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> validarExistenciaUsuario(ServerRequest serverRequest) {
        String documentoIdentidad = serverRequest.pathVariable("documentoIdentidad");
        
        log.info("Iniciando consulta de usuario - DocumentoID: {}", documentoIdentidad);

        return Mono.fromRunnable(() -> validationUtils.validateDocumentoIdentidadOrThrow(documentoIdentidad))
                .then(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documentoIdentidad))
                .doOnNext(usuario -> log.info("Usuario encontrado - DocumentoID: {}",
                        documentoIdentidad ))
                .map(UsuarioConsultaResponse::success)
                .flatMap(response -> 
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response)
                )
                .switchIfEmpty(
                    ServerResponse.status(404)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(UsuarioConsultaResponse.notFound(documentoIdentidad))
                )
                .doOnError(error -> log.error("Error en consulta de usuario - DocumentoID: {}, Tipo: {}, Mensaje: {}", 
                        documentoIdentidad, error.getClass().getSimpleName(), error.getMessage()))
                .onErrorResume(errorHandler::handleError);
    }


}
