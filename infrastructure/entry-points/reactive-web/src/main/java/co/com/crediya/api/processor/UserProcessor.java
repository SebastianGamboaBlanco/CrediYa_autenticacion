package co.com.crediya.api.processor;

import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.helper.ValidationUtils;
import co.com.crediya.model.CompleteUser;
import co.com.crediya.usecase.GetUserUseCase;
import co.com.crediya.usecase.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProcessor {
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ValidationUtils validationUtils;

    public Mono<Void> processRegistration(UserRequest request) {
        MDC.put("operation", "registration");
        MDC.put("email", request.getEmail());
        MDC.put("documentId", request.getDocumentIdentity());
        
        log.info("START - Processing registration - Email: {}, DocumentID: {}", 
                request.getEmail(), request.getDocumentIdentity());
        
        return validationUtils.validateRequest(request)
                .doOnNext(req -> log.debug("Bean validation completed"))
                .flatMap(req -> 
                    validationUtils.validateRolOrThrow(req.getRoleId())
                        .then(Mono.just(req))
                )
                .doOnNext(req -> log.debug("Business validations completed"))
                .flatMap(req -> 
                    registerUserUseCase.registerUser(
                        req.getFirstName(),
                        req.getLastName(), 
                        req.getEmail(),
                        req.getBaseSalary(),
                        req.getDocumentIdentity(),
                        req.getBirthDate(),
                        req.getPhone(),
                        req.getRoleId())
                )
                .doOnSuccess(unused -> 
                    log.info("SUCCESSFUL END - User registered - Email: {}", 
                            request.getEmail()))
                .doOnError(error -> 
                    log.error("ERROR END - Error processing registration - Email: {}, Type: {}, Message: {}", 
                            request.getEmail(), error.getClass().getSimpleName(), error.getMessage()));
    }

    public Mono<CompleteUser> processQuery(String documentIdentity) {
        MDC.put("operation", "query");
        MDC.put("documentId", documentIdentity);

        log.info("START - Processing query - DocumentID: {}", documentIdentity);

        return Mono.fromRunnable(() -> validationUtils.validateDocumentoIdentidadOrThrow(documentIdentity))
                .then(getUserUseCase.findByDocumentIdentity(documentIdentity))
                .doOnNext(user ->
                    log.info("SUCCESSFUL END - User found - DocumentID: {}, UserID: {}",
                            documentIdentity, user.getId()))
                .doOnError(error ->
                    log.error("ERROR END - Error processing query - DocumentID: {}, Type: {}, Message: {}",
                            documentIdentity, error.getClass().getSimpleName(), error.getMessage()));
    }

    public Mono<CompleteUser> processQueryByEmail(String email) {
        MDC.put("operation", "queryByEmail");
        MDC.put("email", email);

        log.info("START - Processing query by email - Email: {}", email);

        return getUserUseCase.findByEmail(email)
                .doOnNext(user ->
                    log.info("SUCCESSFUL END - User found - Email: {}, UserID: {}",
                            email, user.getId()))
                .doOnError(error ->
                    log.error("ERROR END - Error processing query by email - Email: {}, Type: {}, Message: {}",
                            email, error.getClass().getSimpleName(), error.getMessage()));
    }
}