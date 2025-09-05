package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.api.dto.UsuarioConsultaResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.exception.ErrorHandler;
import co.com.crediya.api.processor.UsuarioProcessor;
import co.com.crediya.api.processor.AutenticacionProcessor;
import co.com.crediya.api.response.UsuarioResponseBuilder;
import co.com.crediya.api.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final UsuarioProcessor usuarioProcessor;
    private final AutenticacionProcessor autenticacionProcessor;
    private final UsuarioResponseBuilder usuarioResponseBuilder;
    private final ErrorHandler errorHandler;

    public Mono<ServerResponse> registrarUsuario(ServerRequest serverRequest) {
        log.info("Iniciando proceso de registro de usuario");
        
        return RoleValidator.requirePermissions(serverRequest.exchange())
                .then(serverRequest.bodyToMono(UsuarioRequest.class))
                .doOnError(error -> log.error("Error deserializando request body", error))
                .flatMap(usuarioProcessor::procesarRegistro)
                .then(usuarioResponseBuilder.buildResponse(HttpStatus.OK, 
                        UsuarioResponse.success("Usuario registrado exitosamente")))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> validarExistenciaUsuario(ServerRequest serverRequest) {
        String documentoIdentidad = serverRequest.pathVariable("documentoIdentidad");
        log.info("Iniciando consulta de usuario - DocumentoID: {}", documentoIdentidad);

        return usuarioProcessor.procesarConsulta(documentoIdentidad)
                .flatMap(usuario -> usuarioResponseBuilder.buildResponse(HttpStatus.OK, 
                        UsuarioConsultaResponse.success(usuario)))
                .switchIfEmpty(usuarioResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, 
                        UsuarioConsultaResponse.notFound(documentoIdentidad)))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("Iniciando proceso de autenticación");
        
        return serverRequest.bodyToMono(LoginRequest.class)
                .doOnNext(request -> log.info("Autenticando usuario: {}", request.getEmail()))
                .flatMap(autenticacionProcessor::procesarAutenticacion)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(errorHandler::handleError);
    }


}
