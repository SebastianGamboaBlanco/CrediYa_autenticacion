package co.com.crediya.r2dbc;

import co.com.crediya.model.User;
import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.gateways.UserRepository;
import co.com.crediya.r2dbc.helper.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserEntityMapper mapper;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository,
                                          UserEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Mono<Void> registerUser(User user, String documentIdentity, String birthDate, 
                                   String phone, Long roleId, String password) {
        return repository.save(mapper.toEntity(user, documentIdentity, birthDate, phone, roleId, password))
                .doOnError(error -> log.info("Error saving user - Email: {}, Error: {}",
                        user.getEmail(), error.getMessage()))
                .then();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> emailExists(String email) {
        return repository.existsByEmail(email)
                .doOnError(error -> log.info("Error checking email - {}: {}", email, error.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> documentIdentityExists(String documentIdentity) {
        return repository.existsByDocumentIdentity(documentIdentity)
                .doOnError(error -> log.info("Error checking document - {}: {}", documentIdentity, error.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompleteUser> findByDocumentIdentity(String documentIdentity) {
        return repository.findByDocumentIdentity(documentIdentity)
                .map(mapper::toCompleteUser)
                .doOnError(error -> log.info("Error finding user - {}: {}", documentIdentity, error.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompleteUser> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toCompleteUser)
                .doOnError(error -> log.info("Error finding user by email - {}: {}", email, error.getMessage()));
    }
}