package co.com.crediya.r2dbc;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.gateways.AuthenticationRepository;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.model.gateways.RoleRepository;
import co.com.crediya.r2dbc.helper.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class AuthenticationReactiveRepositoryAdapter implements AuthenticationRepository {

    private final UserReactiveRepository userRepository;
    private final UserEntityMapper mapper;
    private final PasswordService passwordService;
    private final RoleRepository roleRepository;


    public AuthenticationReactiveRepositoryAdapter(UserReactiveRepository userRepository,
                                       UserEntityMapper mapper,
                                       PasswordService passwordService,
                                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordService = passwordService;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompleteUser> findUserByEmailForAuthentication(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> 
                    roleRepository.findRoleById(user.getRoleId())
                        .map(role -> mapper.toCompleteUserWithRole(user, role))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> validateUserPassword(String email, String plainPassword) {
        return userRepository.findByEmail(email)
                .map(user -> passwordService.verifyPassword(plainPassword, user.getPassword()))
                .defaultIfEmpty(false);
    }

}