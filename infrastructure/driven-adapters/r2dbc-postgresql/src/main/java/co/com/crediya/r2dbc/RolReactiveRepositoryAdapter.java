package co.com.crediya.r2dbc;

import co.com.crediya.model.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RolReactiveRepositoryAdapter implements RolRepository {
    
    private final RolReactiveRepository rolReactiveRepository;
    
    @Override
    public Mono<Boolean> existeRol(Long idRol) {
        return rolReactiveRepository.existsById(idRol);
    }
}