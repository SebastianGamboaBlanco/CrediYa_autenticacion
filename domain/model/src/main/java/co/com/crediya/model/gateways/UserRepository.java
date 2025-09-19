package co.com.crediya.model.gateways;

import co.com.crediya.model.User;
import co.com.crediya.model.CompleteUser;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> registerUser(User user, String documentIdentity, String birthDate, 
                           String phone, Long roleId, String password);

    Mono<Boolean> emailExists(String email);

    Mono<Boolean> documentIdentityExists(String documentIdentity);

    Mono<CompleteUser> findByDocumentIdentity(String documentIdentity);

    Mono<CompleteUser> findByEmail(String email);

}
