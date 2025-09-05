package co.com.crediya.usecase;

import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.gateways.UsuarioRepository;
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

@DisplayName("ConsultarUsuarioUseCase Tests")
class ConsultarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    
    private ConsultarUsuarioUseCase consultarUsuarioUseCase;
    
    private static final String DOCUMENTO_VALIDO = "12345678";
    private static final String DOCUMENTO_INEXISTENTE = "99999999";
    
    private UsuarioCompleto usuarioCompletoEsperado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consultarUsuarioUseCase = new ConsultarUsuarioUseCase(usuarioRepository);
        
        usuarioCompletoEsperado = new UsuarioCompleto(
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

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe retornar el usuario correcto con todos los campos")
        void debeRetornarUsuarioCorrectoConTodosLosCampos() {

            UsuarioCompleto usuarioCompleto = new UsuarioCompleto(
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
            
            when(usuarioRepository.buscarPorDocumentoIdentidad("87654321"))
                .thenReturn(Mono.just(usuarioCompleto));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad("87654321"))
                .assertNext(usuario -> {
                    assert usuario.getNombres().equals("María Elena");
                    assert usuario.getApellidos().equals("González Martínez");
                    assert usuario.getCorreoElectronico().equals("maria.gonzalez@test.com");
                    assert usuario.getSalarioBase().equals(8000000);
                    assert usuario.getDocumentoIdentidad().equals("87654321");
                    assert usuario.getFechaNacimiento().equals(LocalDate.of(1985, 12, 25));
                    assert usuario.getTelefono().equals("3109876543");
                    assert usuario.getIdRol().equals(2L);
                    assert usuario.getId().equals(2L);
                })
                .verifyComplete();
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "87654321", "11223344", "55667788"})
        @DisplayName("Debe consultar correctamente diferentes documentos")
        void debeConsultarCorrectamenteDiferentesDocumentos(String documento) {

            UsuarioCompleto usuario = new UsuarioCompleto(
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
            
            when(usuarioRepository.buscarPorDocumentoIdentidad(documento))
                .thenReturn(Mono.just(usuario));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documento))
                .expectNext(usuario)
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(documento);
        }
    }

    @Nested
    @DisplayName("Usuario No Encontrado")
    class UsuarioNoEncontrado {

        @Test
        @DisplayName("Debe retornar empty cuando usuario no existe")
        void debeRetornarEmptyCuandoUsuarioNoExiste() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_INEXISTENTE))
                .thenReturn(Mono.empty());

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_INEXISTENTE))
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(DOCUMENTO_INEXISTENTE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"00000000", "99999999", "88888888"})
        @DisplayName("Debe manejar diferentes documentos inexistentes")
        void debeManejarDiferentesDocumentosInexistentes(String documentoInexistente) {

            when(usuarioRepository.buscarPorDocumentoIdentidad(documentoInexistente))
                .thenReturn(Mono.empty());

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documentoInexistente))
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
            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(repositoryError));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectError(RuntimeException.class)
                .verify();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe manejar timeout del repository")
        void debeManejarTimeoutDelRepository() {
            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(new java.util.concurrent.TimeoutException("Query timeout")));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe manejar errores de conectividad")
        void debeManejarErroresDeConectividad() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(new java.net.ConnectException("Connection refused")));


            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
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

            when(usuarioRepository.buscarPorDocumentoIdentidad(null))
                .thenReturn(Mono.empty());

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(null))
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(null);
        }

        @Test
        @DisplayName("Debe manejar documento vacío")
        void debeManejarDocumentoVacio() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(""))
                .thenReturn(Mono.empty());

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(""))
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad("");
        }

        @Test
        @DisplayName("Debe manejar documento con espacios")
        void debeManejarDocumentoConEspacios() {

            String documentoConEspacios = "  12345678  ";
            when(usuarioRepository.buscarPorDocumentoIdentidad(documentoConEspacios))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documentoConEspacios))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(documentoConEspacios);
        }
    }

    @Nested
    @DisplayName("Comportamiento Reactivo")
    class ComportamientoReactivo {

        @Test
        @DisplayName("Debe delegar al repository sin cache ni transformaciones")
        void debeDelegarAlRepositorySinCacheNiTransformaciones() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO);
        }

        @Test
        @DisplayName("Debe manejar cancelación durante ejecución")
        void debeManejarCancelacionDuranteEjecucion() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado)
                    .delayElement(java.time.Duration.ofMillis(100)));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenCancel()
                .verify();
        }

        @Test
        @DisplayName("Debe crear nuevos Mono para cada invocación")
        void debeCrearNuevosMonoParaCadaInvocacion() {

            when(usuarioRepository.buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(DOCUMENTO_VALIDO))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(usuarioRepository, times(2)).buscarPorDocumentoIdentidad(DOCUMENTO_VALIDO);
        }
    }

    @Nested
    @DisplayName("Integración con Repository")
    class IntegracionConRepository {

        @Test
        @DisplayName("Debe delegar completamente al repository")
        void debeDelegarCompletamenteAlRepository() {
            when(usuarioRepository.buscarPorDocumentoIdentidad(anyString()))
                .thenReturn(Mono.just(usuarioCompletoEsperado));

            String documentoTest = "TEST123";

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad(documentoTest))
                .expectNext(usuarioCompletoEsperado)
                .verifyComplete();

            verify(usuarioRepository).buscarPorDocumentoIdentidad(documentoTest);
            verifyNoMoreInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe retornar exactamente lo que retorna el repository")
        void debeRetornarExactamenteLQueRetornaElRepository() {

            UsuarioCompleto usuario1 = new UsuarioCompleto(
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
            
            UsuarioCompleto usuario2 = new UsuarioCompleto(
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

            when(usuarioRepository.buscarPorDocumentoIdentidad("10001111"))
                .thenReturn(Mono.just(usuario1));
            when(usuarioRepository.buscarPorDocumentoIdentidad("10002222"))
                .thenReturn(Mono.just(usuario2));
            when(usuarioRepository.buscarPorDocumentoIdentidad("99999999"))
                .thenReturn(Mono.empty());

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad("10001111"))
                .expectNext(usuario1)
                .verifyComplete();

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad("10002222"))
                .expectNext(usuario2)
                .verifyComplete();

            StepVerifier.create(consultarUsuarioUseCase.consultarPorDocumentoIdentidad("99999999"))
                .verifyComplete();
        }
    }
}