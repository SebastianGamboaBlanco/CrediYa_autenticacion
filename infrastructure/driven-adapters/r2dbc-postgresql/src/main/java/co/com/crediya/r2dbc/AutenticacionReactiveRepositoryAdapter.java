package co.com.crediya.r2dbc;

import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.gateways.AutenticacionRepository;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.model.gateways.RolRepository;
import co.com.crediya.r2dbc.helper.UsuarioEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class AutenticacionReactiveRepositoryAdapter implements AutenticacionRepository {

    private final UsuarioReactiveRepository usuarioRepository;
    private final UsuarioEntityMapper mapper;
    private final PasswordService passwordService;
    private final RolRepository rolRepository;


    public AutenticacionReactiveRepositoryAdapter(UsuarioReactiveRepository usuarioRepository,
                                       UsuarioEntityMapper mapper,
                                       PasswordService passwordService,
                                       RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.passwordService = passwordService;
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UsuarioCompleto> findUserByEmailForAuthentication(String email) {
        return usuarioRepository.findByCorreo(email)
                .flatMap(usuario -> 
                    rolRepository.findRoleById(usuario.getIdRol())
                        .map(role -> mapper.toUsuarioCompletoWithRole(usuario, role))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> validateUserPassword(String email, String plainPassword) {
        return usuarioRepository.findByCorreo(email)
                .map(usuario -> passwordService.verifyPassword(plainPassword, usuario.getPassword()))
                .defaultIfEmpty(false);
    }

}