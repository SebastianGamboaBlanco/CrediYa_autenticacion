package co.com.crediya.usecase;

import co.com.crediya.model.User;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.model.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public RegisterUserUseCase(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Mono<Void> registerUser(String firstName, String lastName, String email, Integer baseSalary,
                                   String documentIdentity, String birthDate, String phone, Long roleId) {
        return Mono.fromCallable(() -> new User(firstName, lastName, email, baseSalary))
                .flatMap(user -> validateExistingUser(user, documentIdentity)
                        .then(generateEncryptedCredentials())
                        .flatMap(credentials -> persistUser(user, documentIdentity, birthDate, phone, roleId, credentials)));
    }

    private Mono<Void> validateExistingUser(User user, String documentIdentity) {
        return validateUniqueEmail(user.getEmail())
                .then(validateUniqueDocument(documentIdentity));
    }

    private Mono<Void> validateUniqueEmail(String email) {
        return userRepository.emailExists(email)
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, email));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUniqueDocument(String documentIdentity) {
        return userRepository.documentIdentityExists(documentIdentity)
                .flatMap(documentExists -> {
                    if (documentExists) {
                        return Mono.error(new BusinessException(ErrorCode.DOCUMENT_ALREADY_EXISTS, documentIdentity));
                    }
                    return Mono.empty();
                });
    }

    private Mono<String> generateEncryptedCredentials() {
        return Mono.fromCallable(() -> {
            String randomPassword = passwordService.generateRandomPassword();
            return passwordService.encryptPassword(randomPassword);
        });
    }

    private Mono<Void> persistUser(User user, String documentIdentity, String birthDate, 
                                  String phone, Long roleId, String encryptedPassword) {
        return userRepository.registerUser(user, documentIdentity, birthDate, phone, roleId, encryptedPassword);
    }
}
