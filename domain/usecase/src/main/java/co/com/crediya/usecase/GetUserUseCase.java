package co.com.crediya.usecase;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class GetUserUseCase {
    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<CompleteUser> findByDocumentIdentity(String documentIdentity) {
        return userRepository.findByDocumentIdentity(documentIdentity);
    }

    public Mono<CompleteUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}