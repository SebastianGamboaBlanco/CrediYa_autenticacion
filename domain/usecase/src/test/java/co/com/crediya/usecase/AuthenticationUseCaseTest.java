package co.com.crediya.usecase;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.valueobjects.Role;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.AuthenticationRepository;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.model.valueobjects.TokenClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationUseCase Tests")
class AuthenticationUseCaseTest {

    @Mock
    private AuthenticationRepository authenticationRepository;
    
    @Mock
    private JwtTokenService jwtTokenService;
    
    private AuthenticationUseCase authenticationUseCase;
    
    private static final String EMAIL_VALIDO = "juan.perez@example.com";
    private static final String PASSWORD_VALIDO = "password123";
    private static final String TOKEN_STRING = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    private static final String USER_ID = "1";
    
    private CompleteUser usuarioCompleto;
    private JwtToken jwtToken;
    private TokenClaims tokenClaims;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationUseCase = new AuthenticationUseCase(authenticationRepository, jwtTokenService);

        role = Role.of(1L, "ADMIN", "Administrator");
        
        usuarioCompleto = new CompleteUser(
                1L, "Juan Carlos", "Pérez García", EMAIL_VALIDO,
                "12345678", LocalDate.of(1990, 5, 15),
                "3001234567", 1L, 5000000
        );
        usuarioCompleto.setRole(role);
        
        jwtToken = JwtToken.fromString(TOKEN_STRING);
        
        tokenClaims = TokenClaims.builder()
                .document(USER_ID)
                .email(EMAIL_VALIDO)
                .role(role)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .issuer("crediya-auth-service")
                .build();
    }

    @Nested
    @DisplayName("Autenticación Exitosa")
    class AutenticacionExitosa {

        @Test
        @DisplayName("Debe autenticar usuario con credenciales válidas")
        void debeAutenticarUsuarioConCredencialesValidas() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.just(true));
            when(jwtTokenService.generateToken(any(TokenClaims.class)))
                    .thenReturn(Mono.just(jwtToken));

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .assertNext(token -> {
                        assertThat(token).isNotNull();
                        assertThat(token.getValue()).isEqualTo(TOKEN_STRING);
                    })
                    .verifyComplete();


            verify(authenticationRepository).findUserByEmailForAuthentication(EMAIL_VALIDO);
            verify(authenticationRepository).validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO);
            verify(jwtTokenService).generateToken(any(TokenClaims.class));
        }

        @Test
        @DisplayName("Debe generar token con claims correctos")
        void debeGenerarTokenConClaimsCorrectos() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.just(true));
            when(jwtTokenService.generateToken(any(TokenClaims.class)))
                    .thenReturn(Mono.just(jwtToken));

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectNextCount(1)
                    .verifyComplete();

            verify(jwtTokenService).generateToken(argThat(claims -> 
                    claims.getDocument().equals("1") &&
                    claims.getEmail().equals(EMAIL_VALIDO) &&
                    claims.getRole().equals(role) &&
                    claims.getIssuer().equals("crediya-auth-service")
            ));
        }
    }

    @Nested
    @DisplayName("Validación de Credenciales Fallida")
    class ValidacionCredencialesFallida {

        @Test
        @DisplayName("Debe fallar cuando el email no existe")
        void debeFallarCuandoEmailNoExiste() {
            // Given
            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.empty());

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectError(BusinessException.class)
                    .verify();

            verify(authenticationRepository).findUserByEmailForAuthentication(EMAIL_VALIDO);
            verify(authenticationRepository, never()).validateUserPassword(anyString(), anyString());
            verify(jwtTokenService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Debe contener el email en el mensaje de error cuando no existe")
        void debeContenerEmailEnMensajeDeErrorCuandoNoExiste() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.empty());

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectErrorMatches(error -> 
                            error instanceof BusinessException &&
                            ((BusinessException) error).getErrorCode() == ErrorCode.EMAIL_NOT_EXISTS
                    )
                    .verify();
        }

        @Test
        @DisplayName("Debe fallar cuando la contraseña es inválida")
        void debeFallarCuandoPasswordEsInvalido() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.just(false));

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectError(BusinessException.class)
                    .verify();


            verify(authenticationRepository).findUserByEmailForAuthentication(EMAIL_VALIDO);
            verify(authenticationRepository).validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO);
            verify(jwtTokenService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Debe retornar error específico para contraseña inválida")
        void debeRetornarErrorEspecificoParaPasswordInvalido() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.just(false));

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectErrorMatches(error -> 
                            error instanceof BusinessException &&
                            ((BusinessException) error).getErrorCode() == ErrorCode.INVALID_PASSWORD
                    )
                    .verify();
        }
    }

    @Nested
    @DisplayName("Validación de Token")
    class ValidacionToken {

        @Test
        @DisplayName("Debe validar token correctamente cuando es válido")
        void debeValidarTokenCorrectamenteCuandoEsValido() {

            when(jwtTokenService.isTokenValid(jwtToken))
                    .thenReturn(Mono.just(true));


            StepVerifier.create(authenticationUseCase.validateToken(jwtToken))
                    .expectNext(true)
                    .verifyComplete();

            verify(jwtTokenService).isTokenValid(jwtToken);
        }

        @Test
        @DisplayName("Debe validar token correctamente cuando es inválido")
        void debeValidarTokenCorrectamenteCuandoEsInvalido() {

            when(jwtTokenService.isTokenValid(jwtToken))
                    .thenReturn(Mono.just(false));

            StepVerifier.create(authenticationUseCase.validateToken(jwtToken))
                    .expectNext(false)
                    .verifyComplete();

            verify(jwtTokenService).isTokenValid(jwtToken);
        }
    }

    @Nested
    @DisplayName("Extracción de Claims")
    class ExtraccionClaims {

        @Test
        @DisplayName("Debe extraer claims del token correctamente")
        void debeExtraerClaimsDelTokenCorrectamente() {

            when(jwtTokenService.validateAndParseClaims(jwtToken))
                    .thenReturn(Mono.just(tokenClaims));

            StepVerifier.create(authenticationUseCase.extractClaims(jwtToken))
                    .assertNext(claims -> {
                        assertThat(claims).isNotNull();
                        assertThat(claims.getDocument()).isEqualTo(USER_ID);
                        assertThat(claims.getEmail()).isEqualTo(EMAIL_VALIDO);
                        assertThat(claims.getRole()).isEqualTo(role);
                        assertThat(claims.getIssuer()).isEqualTo("crediya-auth-service");
                    })
                    .verifyComplete();

            verify(jwtTokenService).validateAndParseClaims(jwtToken);
        }

        @Test
        @DisplayName("Debe propagar error cuando falla la extracción de claims")
        void debePropagarErrorCuandoFallaExtraccionClaims() {

            RuntimeException expectedException = new RuntimeException("Token inválido");
            when(jwtTokenService.validateAndParseClaims(jwtToken))
                    .thenReturn(Mono.error(expectedException));

            StepVerifier.create(authenticationUseCase.extractClaims(jwtToken))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(jwtTokenService).validateAndParseClaims(jwtToken);
        }
    }

    @Nested
    @DisplayName("Casos Edge y Errores")
    class CasosEdgeYErrores {

        @Test
        @DisplayName("Debe manejar error en generación de token")
        void debeManejarErrorEnGeneracionToken() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.just(true));
            when(jwtTokenService.generateToken(any(TokenClaims.class)))
                    .thenReturn(Mono.error(new RuntimeException("Error generando token")));

            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectError(RuntimeException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe manejar error en validación de contraseña")
        void debeManejarErrorEnValidacionPassword() {

            when(authenticationRepository.findUserByEmailForAuthentication(EMAIL_VALIDO))
                    .thenReturn(Mono.just(usuarioCompleto));
            when(authenticationRepository.validateUserPassword(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .thenReturn(Mono.error(new RuntimeException("Error en validación")));


            StepVerifier.create(authenticationUseCase.authenticate(EMAIL_VALIDO, PASSWORD_VALIDO))
                    .expectError(RuntimeException.class)
                    .verify();
        }

    }
}