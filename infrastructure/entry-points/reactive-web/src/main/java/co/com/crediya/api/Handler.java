package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.dto.UserQueryResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.TokenValidationRequest;
import co.com.crediya.api.exception.ErrorHandler;
import co.com.crediya.api.processor.UserProcessor;
import co.com.crediya.api.processor.AuthenticationProcessor;
import co.com.crediya.api.processor.TokenValidationProcessor;
import co.com.crediya.api.response.UserResponseBuilder;
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
    private final UserProcessor userProcessor;
    private final AuthenticationProcessor authenticationProcessor;
    private final TokenValidationProcessor tokenValidationProcessor;
    private final UserResponseBuilder userResponseBuilder;
    private final ErrorHandler errorHandler;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        log.info("Starting user registration process");
        
        return RoleValidator.requirePermissions(serverRequest.exchange())
                .then(serverRequest.bodyToMono(UserRequest.class))
                .doOnError(error -> log.error("Error deserializing request body", error))
                .flatMap(userProcessor::processRegistration)
                .then(userResponseBuilder.buildResponse(HttpStatus.OK, 
                        UserResponse.success("User registered successfully")))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> validateUserExistence(ServerRequest serverRequest) {
        String documentIdentity = serverRequest.pathVariable("documentIdentity");
        log.info("Starting user query - DocumentID: {}", documentIdentity);

        return userProcessor.processQuery(documentIdentity)
                .flatMap(user -> userResponseBuilder.buildResponse(HttpStatus.OK,
                        UserQueryResponse.success(user)))
                .switchIfEmpty(Mono.fromRunnable(() -> log.info("User not found - DocumentID: {}", documentIdentity))
                        .then(userResponseBuilder.buildResponse(HttpStatus.NOT_FOUND,
                        UserQueryResponse.notFound(documentIdentity))))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> validateUserExistenceByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        log.info("Starting user query by email - Email: {}", email);

        return userProcessor.processQueryByEmail(email)
                .flatMap(user -> userResponseBuilder.buildResponse(HttpStatus.OK,
                        UserQueryResponse.success(user)))
                .switchIfEmpty(Mono.fromRunnable(() -> log.info("User not found - Email: {}", email))
                        .then(userResponseBuilder.buildResponse(HttpStatus.NOT_FOUND,
                        UserQueryResponse.notFoundByEmail(email))))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("Starting authentication process");
        
        return serverRequest.bodyToMono(LoginRequest.class)
                .doOnNext(request -> log.info("Authenticating user: {}", request.getEmail()))
                .flatMap(authenticationProcessor::processAuthentication)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(errorHandler::handleError);
    }

    public Mono<ServerResponse> validateToken(ServerRequest serverRequest) {
        log.info("Starting token validation process");
        
        return serverRequest.bodyToMono(TokenValidationRequest.class)
                .doOnNext(request -> log.info("Validating token"))
                .flatMap(tokenValidationProcessor::processTokenValidation)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(errorHandler::handleError);
    }

}
