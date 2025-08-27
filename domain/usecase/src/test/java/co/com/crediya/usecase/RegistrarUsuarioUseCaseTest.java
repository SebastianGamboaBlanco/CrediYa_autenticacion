package co.com.crediya.usecase;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.exceptions.*;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("RegistrarUsuarioUseCase Tests")
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    
    private static final String NOMBRES_VALIDOS = "Juan Carlos";
    private static final String APELLIDOS_VALIDOS = "Pérez García";
    private static final String CORREO_VALIDO = "juan.perez@example.com";
    private static final Integer SALARIO_VALIDO = 5000000;
    private static final String DOCUMENTO_VALIDO = "12345678";
    private static final String FECHA_NACIMIENTO_VALIDA = "1990-05-15";
    private static final String TELEFONO_VALIDO = "3001234567";
    private static final Long ID_ROL_VALIDO = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrarUsuarioUseCase = new RegistrarUsuarioUseCase(usuarioRepository);
    }

    @Nested
    @DisplayName("Registro Exitoso")
    class RegistroExitoso {

        @Test
        @DisplayName("Debe registrar usuario cuando datos son válidos")
        void debeRegistrarUsuarioCuandoDatosSonValidos() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.registrarUsuario(any(Usuario.class), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .verifyComplete();

            // Verify interactions
            verify(usuarioRepository).existeEmail(CORREO_VALIDO);
            verify(usuarioRepository).existeDocumentoIdentidad(DOCUMENTO_VALIDO);
            verify(usuarioRepository).registrarUsuario(any(Usuario.class), eq(DOCUMENTO_VALIDO), 
                eq(FECHA_NACIMIENTO_VALIDA), eq(TELEFONO_VALIDO), eq(ID_ROL_VALIDO));
        }

        @Test
        @DisplayName("Debe crear usuario con los datos correctos antes de registrar")
        void debeCrearUsuarioConDatosCorrectosAntesDeRegistrar() {
            // Arrange
            when(usuarioRepository.existeEmail(anyString()))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(anyString()))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.registrarUsuario(any(Usuario.class), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Mono.empty());

            String nombres = "María Elena";
            String apellidos = "González Martínez";
            String correo = "maria.gonzalez@test.com";
            Integer salario = 8000000;

            // Act
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    nombres, apellidos, correo, salario,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .verifyComplete();

            // Assert - Verificar que el usuario se creó con los datos correctos
            verify(usuarioRepository).registrarUsuario(argThat(usuario -> 
                usuario.getNombres().equals(nombres) &&
                usuario.getApellidos().equals(apellidos) &&
                usuario.getCorreoElectronico().equals(correo) &&
                usuario.getSalarioBase().equals(salario)
            ), eq(DOCUMENTO_VALIDO), eq(FECHA_NACIMIENTO_VALIDA), eq(TELEFONO_VALIDO), eq(ID_ROL_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Email Duplicado")
    class ValidacionEmailDuplicado {

        @Test
        @DisplayName("Debe fallar cuando el email ya existe")
        void debeFallarCuandoEmailYaExiste() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(CorreoExistInvalidException.class)
                .verify();

            // Verify que no se verificó documento ni se intentó registrar
            verify(usuarioRepository, never()).existeDocumentoIdentidad(anyString());
            verify(usuarioRepository, never()).registrarUsuario(any(), anyString(), anyString(), anyString(), anyLong());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "usuario.test@domain.com",
            "admin@company.co",
            "user123@service.org"
        })
        @DisplayName("Debe fallar para diferentes emails existentes")
        void debeFallarParaDiferentesEmailsExistentes(String emailExistente) {
            // Arrange
            when(usuarioRepository.existeEmail(emailExistente))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, emailExistente, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(CorreoExistInvalidException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe contener el email en el mensaje de error")
        void debeContenerEmailEnMensajeDeError() {
            // Arrange
            String emailDuplicado = "duplicado@test.com";
            when(usuarioRepository.existeEmail(emailDuplicado))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, emailDuplicado, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectErrorMatches(error -> 
                    error instanceof CorreoExistInvalidException &&
                    error.getMessage().contains(emailDuplicado))
                .verify();
        }
    }

    @Nested
    @DisplayName("Validación de Documento Duplicado")
    class ValidacionDocumentoDuplicado {

        @Test
        @DisplayName("Debe fallar cuando el documento ya existe")
        void debeFallarCuandoDocumentoYaExiste() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(DocumentoExistInvalidException.class)
                .verify();

            // Verify que no se intentó registrar
            verify(usuarioRepository, never()).registrarUsuario(any(), anyString(), anyString(), anyString(), anyLong());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "87654321", "11223344", "55667788"})
        @DisplayName("Debe fallar para diferentes documentos existentes")
        void debeFallarParaDiferentesDocumentosExistentes(String documentoExistente) {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(documentoExistente))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    documentoExistente, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(DocumentoExistInvalidException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe contener el documento en el mensaje de error")
        void debeContenerDocumentoEnMensajeDeError() {
            // Arrange
            String documentoDuplicado = "99988877";
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(documentoDuplicado))
                .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    documentoDuplicado, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectErrorMatches(error -> 
                    error instanceof DocumentoExistInvalidException &&
                    error.getMessage().contains(documentoDuplicado))
                .verify();
        }
    }

    @Nested
    @DisplayName("Validación de Datos del Usuario")
    class ValidacionDatosUsuario {

        @Test
        @DisplayName("Debe fallar cuando nombres son inválidos")
        void debeFallarCuandoNombresSonInvalidos() {
            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    null, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(NombreInvalidException.class)
                .verify();

            // Verify que no se llamó a ningún repository
            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando apellidos son inválidos")
        void debeFallarCuandoApellidosSonInvalidos() {
            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, "", CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(ApellidoInvalidException.class)
                .verify();

            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando correo es inválido")
        void debeFallarCuandoCorreoEsInvalido() {
            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, "correo-invalido", SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(CorreoFormatoInvalidException.class)
                .verify();

            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando salario es inválido")
        void debeFallarCuandoSalarioEsInvalido() {
            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, -1000,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(SalarioRangoInvalidException.class)
                .verify();

            verifyNoInteractions(usuarioRepository);
        }
    }

    @Nested
    @DisplayName("Manejo de Errores del Repository")
    class ManejoErroresRepository {

        @Test
        @DisplayName("Debe propagar error al verificar existencia de email")
        void debePropagarErrorAlVerificarExistenciaEmail() {
            // Arrange
            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.error(repositoryError));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(RuntimeException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe propagar error al verificar existencia de documento")
        void debePropagarErrorAlVerificarExistenciaDocumento() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            RuntimeException repositoryError = new RuntimeException("Network timeout");
            when(usuarioRepository.existeDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.error(repositoryError));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(RuntimeException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe propagar error al registrar usuario")
        void debePropagarErrorAlRegistrarUsuario() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(false));
            RuntimeException repositoryError = new RuntimeException("Insert failed");
            when(usuarioRepository.registrarUsuario(any(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Mono.error(repositoryError));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .expectError(RuntimeException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Flujo Reactivo")
    class FlujoReactivo {

        @Test
        @DisplayName("Debe ejecutar validaciones en el orden correcto")
        void debeEjecutarValidacionesEnOrdenCorrecto() {
            // Arrange - Usar InOrder para verificar secuencia
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.existeDocumentoIdentidad(DOCUMENTO_VALIDO))
                .thenReturn(Mono.just(false));
            when(usuarioRepository.registrarUsuario(any(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Mono.empty());

            // Act
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .verifyComplete();

            // Assert - Verificar orden de ejecución
            var inOrder = inOrder(usuarioRepository);
            inOrder.verify(usuarioRepository).existeEmail(CORREO_VALIDO);
            inOrder.verify(usuarioRepository).existeDocumentoIdentidad(DOCUMENTO_VALIDO);
            inOrder.verify(usuarioRepository).registrarUsuario(any(), anyString(), anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("Debe manejar cancelación del flujo reactivo")
        void debeManejarCancelacionDelFlujoReactivo() {
            // Arrange
            when(usuarioRepository.existeEmail(CORREO_VALIDO))
                .thenReturn(Mono.just(false).delayElement(java.time.Duration.ofMillis(100)));

            // Act & Assert
            StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(
                    NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO,
                    DOCUMENTO_VALIDO, FECHA_NACIMIENTO_VALIDA, TELEFONO_VALIDO, ID_ROL_VALIDO))
                .thenCancel()
                .verify();
        }
    }
}