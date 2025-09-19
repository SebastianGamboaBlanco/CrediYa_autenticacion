package co.com.crediya.usecase;

import co.com.crediya.model.User;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.gateways.PasswordService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("RegisterUserUseCase Tests")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordService passwordService;
    
    private RegisterUserUseCase registerUserUseCase;
    
    private static final String FIRST_NAME_VALID = "Juan Carlos";
    private static final String LAST_NAME_VALID = "Pérez García";
    private static final String EMAIL_VALID = "juan.perez@example.com";
    private static final Integer BASE_SALARY_VALID = 5000000;
    private static final String DOCUMENT_IDENTITY_VALID = "12345678";
    private static final String BIRTH_DATE_VALID = "1990-05-15";
    private static final String PHONE_VALID = "3001234567";
    private static final Long ROLE_ID_VALID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerUserUseCase = new RegisterUserUseCase(userRepository, passwordService);
        
        // Setup common password service behavior
        when(passwordService.generateRandomPassword()).thenReturn("randomPassword123");
        when(passwordService.encryptPassword("randomPassword123")).thenReturn("encryptedPassword");
    }

    @Nested
    @DisplayName("Registro Exitoso")
    class RegistroExitoso {

        @Test
        @DisplayName("Debe registrar usuario cuando datos son válidos")
        void debeRegistrarUsuarioCuandoDatosSonValidos() {
            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(DOCUMENT_IDENTITY_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.registerUser(any(User.class), anyString(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Mono.empty());

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .verifyComplete();

            verify(userRepository).emailExists(EMAIL_VALID);
            verify(userRepository).documentIdentityExists(DOCUMENT_IDENTITY_VALID);
            verify(userRepository).registerUser(any(User.class), eq(DOCUMENT_IDENTITY_VALID), 
                eq(BIRTH_DATE_VALID), eq(PHONE_VALID), eq(ROLE_ID_VALID), eq("encryptedPassword"));
        }

        @Test
        @DisplayName("Debe crear usuario con los datos correctos antes de registrar")
        void debeCrearUsuarioConDatosCorrectosAntesDeRegistrar() {

            when(userRepository.emailExists(anyString()))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(anyString()))
                .thenReturn(Mono.just(false));
            when(userRepository.registerUser(any(User.class), anyString(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Mono.empty());

            String firstName = "María Elena";
            String lastName = "González Martínez";
            String email = "maria.gonzalez@test.com";
            Integer baseSalary = 8000000;

            StepVerifier.create(registerUserUseCase.registerUser(
                    firstName, lastName, email, baseSalary,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .verifyComplete();

            verify(userRepository).registerUser(argThat(user -> 
                user.getFirstName().equals(firstName) &&
                user.getLastName().equals(lastName) &&
                user.getEmail().equals(email) &&
                user.getBaseSalary().equals(baseSalary)
            ), eq(DOCUMENT_IDENTITY_VALID), eq(BIRTH_DATE_VALID), eq(PHONE_VALID), eq(ROLE_ID_VALID), eq("encryptedPassword"));
        }
    }

    @Nested
    @DisplayName("Validación de Email Duplicado")
    class ValidacionEmailDuplicado {

        @Test
        @DisplayName("Debe fallar cuando el email ya existe")
        void debeFallarCuandoEmailYaExiste() {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(true));
            when(userRepository.documentIdentityExists(anyString()))
                .thenReturn(Mono.just(false));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verify(userRepository).emailExists(EMAIL_VALID);
            verify(userRepository).documentIdentityExists(DOCUMENT_IDENTITY_VALID);
            verify(userRepository, never()).registerUser(any(), anyString(), anyString(), anyString(), anyLong(), anyString());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "usuario.test@domain.com",
            "admin@company.co",
            "user123@service.org"
        })
        @DisplayName("Debe fallar para diferentes emails existentes")
        void debeFallarParaDiferentesEmailsExistentes(String emailExistente) {

            when(userRepository.emailExists(emailExistente))
                .thenReturn(Mono.just(true));
            when(userRepository.documentIdentityExists(anyString()))
                .thenReturn(Mono.just(false));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, emailExistente, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();
                
            verify(userRepository).emailExists(emailExistente);
        }

        @Test
        @DisplayName("Debe contener el email en el mensaje de error")
        void debeContenerEmailEnMensajeDeError() {

            String emailDuplicado = "duplicado@test.com";
            when(userRepository.emailExists(emailDuplicado))
                .thenReturn(Mono.just(true));
            when(userRepository.documentIdentityExists(anyString()))
                .thenReturn(Mono.just(false));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, emailDuplicado, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectErrorMatches(error -> 
                    error instanceof BusinessException &&
                    error.getMessage().contains(emailDuplicado))
                .verify();
                
            verify(userRepository).emailExists(emailDuplicado);
        }
    }

    @Nested
    @DisplayName("Validación de Documento Duplicado")
    class ValidacionDocumentoDuplicado {

        @Test
        @DisplayName("Debe fallar cuando el documento ya existe")
        void debeFallarCuandoDocumentoYaExiste() {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(DOCUMENT_IDENTITY_VALID))
                .thenReturn(Mono.just(true));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verify(userRepository, never()).registerUser(any(), anyString(), anyString(), anyString(), anyLong(), anyString());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "87654321", "11223344", "55667788"})
        @DisplayName("Debe fallar para diferentes documentos existentes")
        void debeFallarParaDiferentesDocumentosExistentes(String documentoExistente) {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(documentoExistente))
                .thenReturn(Mono.just(true));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    documentoExistente, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe contener el documento en el mensaje de error")
        void debeContenerDocumentoEnMensajeDeError() {

            String documentoDuplicado = "99988877";
            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(documentoDuplicado))
                .thenReturn(Mono.just(true));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    documentoDuplicado, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectErrorMatches(error -> 
                    error instanceof BusinessException &&
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

            StepVerifier.create(registerUserUseCase.registerUser(
                    null, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando apellidos son inválidos")
        void debeFallarCuandoApellidosSonInvalidos() {

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, "", EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando correo es inválido")
        void debeFallarCuandoCorreoEsInvalido() {

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, "correo-invalido", BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Debe fallar cuando salario es inválido")
        void debeFallarCuandoSalarioEsInvalido() {

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, -1000,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(BusinessException.class)
                .verify();

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("Manejo de Errores del Repository")
    class ManejoErroresRepository {

        @Test
        @DisplayName("Debe propagar error al verificar existencia de email")
        void debePropagarErrorAlVerificarExistenciaEmail() {

            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.error(repositoryError));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(RuntimeException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe propagar error al verificar existencia de documento")
        void debePropagarErrorAlVerificarExistenciaDocumento() {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            RuntimeException repositoryError = new RuntimeException("Network timeout");
            when(userRepository.documentIdentityExists(DOCUMENT_IDENTITY_VALID))
                .thenReturn(Mono.error(repositoryError));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .expectError(RuntimeException.class)
                .verify();
        }

        @Test
        @DisplayName("Debe propagar error al registrar usuario")
        void debePropagarErrorAlRegistrarUsuario() {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(DOCUMENT_IDENTITY_VALID))
                .thenReturn(Mono.just(false));
            RuntimeException repositoryError = new RuntimeException("Insert failed");
            when(userRepository.registerUser(any(), anyString(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Mono.error(repositoryError));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
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

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.documentIdentityExists(DOCUMENT_IDENTITY_VALID))
                .thenReturn(Mono.just(false));
            when(userRepository.registerUser(any(), anyString(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Mono.empty());

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .verifyComplete();

            var inOrder = inOrder(userRepository);
            inOrder.verify(userRepository).emailExists(EMAIL_VALID);
            inOrder.verify(userRepository).documentIdentityExists(DOCUMENT_IDENTITY_VALID);
            inOrder.verify(userRepository).registerUser(any(), anyString(), anyString(), anyString(), anyLong(), anyString());
        }

        @Test
        @DisplayName("Debe manejar cancelación del flujo reactivo")
        void debeManejarCancelacionDelFlujoReactivo() {

            when(userRepository.emailExists(EMAIL_VALID))
                .thenReturn(Mono.just(false).delayElement(java.time.Duration.ofMillis(100)));

            StepVerifier.create(registerUserUseCase.registerUser(
                    FIRST_NAME_VALID, LAST_NAME_VALID, EMAIL_VALID, BASE_SALARY_VALID,
                    DOCUMENT_IDENTITY_VALID, BIRTH_DATE_VALID, PHONE_VALID, ROLE_ID_VALID))
                .thenCancel()
                .verify();
        }
    }
}