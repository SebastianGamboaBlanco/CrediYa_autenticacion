package co.com.crediya.usecase;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("GetUserUseCase Tests")
class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    private GetUserUseCase getUserUseCase;
    
    private static final String DOCUMENTO_VALIDO = "12345678";
    private static final String DOCUMENTO_INEXISTENTE = "99999999";
    
    private CompleteUser usuarioCompletoEsperado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getUserUseCase = new GetUserUseCase(userRepository);
        
        usuarioCompletoEsperado = new CompleteUser(
            1L,
            "Juan Carlos",
            "Pérez García",
            "juan.perez@example.com",
            "12345678",
            LocalDate.of(1990, 5, 15),
            "3001234567",
            1L,
            5000000
        );
    }

    @Nested
    @DisplayName("Consulta Exitosa")
    class ConsultaExitosa {

        @Test
        @DisplayName("Debe retornar usuario cuando documento existe")
        void debeRetornarUsuarioCuandoDocumentoExiste() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe retornar el usuario correcto con todos los campos")
        void debeRetornarUsuarioCorrectoConTodosLosCampos() {

            CompleteUser usuarioCompleto = new CompleteUser(
                2L,
                "María Elena",
                "González Martínez",
                "maria.gonzalez@test.com",
                "87654321",
                LocalDate.of(1985, 12, 25),
                "3109876543",
                2L,
                8000000
            );
            
            when(userRepository.findByDocumentIdentity("87654321"))
                .thenReturn(Mono.just(usuarioCompleto));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity("87654321"))
                .assertNext(usuario -> {
                    assert usuario.getFirstName().equals("María Elena");
                    assert usuario.getLastName().equals("González Martínez");
                    assert usuario.getEmail().equals("maria.gonzalez@test.com");
                    assert usuario.getBaseSalary().equals(8000000);
                    assert usuario.getDocumentIdentity().equals("87654321");
                    assert usuario.getBirthDate().equals(LocalDate.of(1985, 12, 25));
                    assert usuario.getPhone().equals("3109876543");
                    assert usuario.getRoleId().equals(2L);
                    assert usuario.getId().equals(2L);
                })
                .verifyComplete();
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "87654321", "11223344", "55667788"})
        @DisplayName("Debe consultar correctamente diferentes documentos")
        void debeConsultarCorrectamenteDiferentesDocumentos(String documento) {

            CompleteUser usuario = new CompleteUser(
                3L,
                "Usuario Test",
                "Apellido Test",
                "test@example.com",
                documento,
                LocalDate.now().minusYears(25),
                "3001112233",
                1L,
                3000000
            );
            
            when(userRepository.findByDocumentIdentity(documento))
                .thenReturn(Mono.just(usuario));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(documento))
                .expectNext(usuario)
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(documento);
        }
    }

    @Nested
    @DisplayName("Usuario No Encontrado")
    class UsuarioNoEncontrado {

        @Test
        @DisplayName("Debe retornar empty cuando usuario no existe")
        void debeRetornarEmptyCuandoUsuarioNoExiste() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_INEXISTENTE))
                .thenReturn(Mono.empty());

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_INEXISTENTE))
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(DOCUMENTO_INEXISTENTE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"00000000", "99999999", "88888888"})
        @DisplayName("Debe manejar diferentes documentos inexistentes")
        void debeManejarDiferentesDocumentosInexistentes(String documentoInexistente) {

            when(userRepository.findByDocumentIdentity(documentoInexistente))
                .thenReturn(Mono.empty());

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(documentoInexistente))
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Manejo de Errores")
    class ManejoDeErrores {

        @Test
        @DisplayName("Debe propagar error del repository")
        void debePropagarErrorDelRepository() {

            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(repositoryError));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectError(RuntimeException.class)
                .verify();

            verify(userRepository).findByDocumentIdentity(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe manejar timeout del repository")
        void debeManejarTimeoutDelRepository() {
            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(new java.util.concurrent.TimeoutException("Query timeout")));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe manejar errores de conectividad")
        void debeManejarErroresDeConectividad() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(new java.net.ConnectException("Connection refused")));


            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectError(java.net.ConnectException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Validación de Parámetros")
    class ValidacionParametros {

        @Test
        @DisplayName("Debe manejar documento null")
        void debeManejarDocumentoNull() {

            when(userRepository.findByDocumentIdentity(null))
                .thenReturn(Mono.empty());

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(null))
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(null);
        }

        @Test
        @DisplayName("Debe manejar documento vacío")
        void debeManejarDocumentoVacio() {

            when(userRepository.findByDocumentIdentity(""))
                .thenReturn(Mono.empty());

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(""))
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity("");
        }

        @Test
        @DisplayName("Debe manejar documento con espacios")
        void debeManejarDocumentoConEspacios() {

            String documentoConEspacios = "  12345678  ";
            when(userRepository.findByDocumentIdentity(documentoConEspacios))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(documentoConEspacios))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(documentoConEspacios);
        }
    }

    @Nested
    @DisplayName("Comportamiento Reactivo")
    class ComportamientoReactivo {

        @Test
        @DisplayName("Debe delegar al repository sin cache ni transformaciones")
        void debeDelegarAlRepositorySinCacheNiTransformaciones() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe manejar cancelación durante ejecución")
        void debeManejarCancelacionDuranteEjecucion() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado)
                    .delayElement(java.time.Duration.ofMillis(100)));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenCancel()
                .verify();
        }

        @Test
        @DisplayName("Debe crear nuevos Mono para cada invocación")
        void debeCrearNuevosMonoParaCadaInvocacion() {

            when(userRepository.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(userRepository, times(2)).findByDocumentIdentity(DOCUMENTO_VALIDO);
        }
    }

    @Nested
    @DisplayName("Integración con Repository")
    class IntegracionConRepository {

        @Test
        @DisplayName("Debe delegar completamente al repository")
        void debeDelegarCompletamenteAlRepository() {
            when(userRepository.findByDocumentIdentity(anyString()))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            String documentoTest = "TEST123";

            StepVerifier.create(getUserUseCase.findByDocumentIdentity(documentoTest))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(userRepository).findByDocumentIdentity(documentoTest);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Debe retornar exactamente lo que retorna el repository")
        void debeRetornarExactamenteLQueRetornaElRepository() {

            CompleteUser usuario1 = new CompleteUser(
                10L,
                "Usuario1",
                "Apellido1",
                "user1@test.com",
                "10001111",
                LocalDate.now(),
                "3001111111",
                1L,
                1000000
            );
            
            CompleteUser usuario2 = new CompleteUser(
                11L,
                "Usuario2",
                "Apellido2",
                "user2@test.com",
                "10002222",
                LocalDate.now(),
                "3002222222",
                2L,
                2000000
            );

            when(userRepository.findByDocumentIdentity("10001111"))
                .thenReturn(Mono.just(usuario1));
            when(userRepository.findByDocumentIdentity("10002222"))
                .thenReturn(Mono.just(usuario2));
            when(userRepository.findByDocumentIdentity("99999999"))
                .thenReturn(Mono.empty());

            StepVerifier.create(getUserUseCase.findByDocumentIdentity("10001111"))
                .expectNext(usuario1)
                .verifyComplete();

            StepVerifier.create(getUserUseCase.findByDocumentIdentity("10002222"))
                .expectNext(usuario2)
                .verifyComplete();

            StepVerifier.create(getUserUseCase.findByDocumentIdentity("99999999"))
                .verifyComplete();
        }
    }
}