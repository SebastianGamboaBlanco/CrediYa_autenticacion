package co.com.crediya.model.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exceptions Tests")
class ExceptionTest {

    @Nested
    @DisplayName("NombreInvalidException")
    class NombreInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con mensaje por defecto")
        void debeCrearExcepcionConMensajePorDefecto() {

            NombreInvalidException exception = new NombreInvalidException();

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            NombreInvalidException exception = new NombreInvalidException();

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("ApellidoInvalidException")
    class ApellidoInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con mensaje por defecto")
        void debeCrearExcepcionConMensajePorDefecto() {

            ApellidoInvalidException exception = new ApellidoInvalidException();

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            ApellidoInvalidException exception = new ApellidoInvalidException();

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("CorreoInvalidException")
    class CorreoInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con mensaje por defecto")
        void debeCrearExcepcionConMensajePorDefecto() {

            CorreoInvalidException exception = new CorreoInvalidException();

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            CorreoInvalidException exception = new CorreoInvalidException();

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("CorreoFormatoInvalidException")
    class CorreoFormatoInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con correo específico en el mensaje")
        void debeCrearExcepcionConCorreoEspecifico() {

            String correoInvalido = "correo-invalido";

            CorreoFormatoInvalidException exception = new CorreoFormatoInvalidException(correoInvalido);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(correoInvalido));
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            CorreoFormatoInvalidException exception = new CorreoFormatoInvalidException("test@invalid");

            assertTrue(exception instanceof RuntimeException);
        }

        @Test
        @DisplayName("Debe manejar correo nulo")
        void debeManejarCorreoNulo() {

            assertDoesNotThrow(() -> new CorreoFormatoInvalidException(null));
        }
    }

    @Nested
    @DisplayName("CorreoExistInvalidException")
    class CorreoExistInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con correo específico en el mensaje")
        void debeCrearExcepcionConCorreoEspecifico() {

            String correoExistente = "usuario@existe.com";

            CorreoExistInvalidException exception = new CorreoExistInvalidException(correoExistente);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(correoExistente));
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            CorreoExistInvalidException exception = new CorreoExistInvalidException("test@exists.com");

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("DocumentoExistInvalidException")
    class DocumentoExistInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con documento específico en el mensaje")
        void debeCrearExcepcionConDocumentoEspecifico() {

            String documentoExistente = "12345678";

            DocumentoExistInvalidException exception = new DocumentoExistInvalidException(documentoExistente);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(documentoExistente));
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            DocumentoExistInvalidException exception = new DocumentoExistInvalidException("87654321");

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("DocumentoFormatoInvalidException")
    class DocumentoFormatoInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con documento específico en el mensaje")
        void debeCrearExcepcionConDocumentoEspecifico() {

            String documentoInvalido = "123abc";

            DocumentoFormatoInvalidException exception = new DocumentoFormatoInvalidException(documentoInvalido);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(documentoInvalido));
        }

        @Test
        @DisplayName("Debe incluir requisitos de formato en el mensaje")
        void debeIncluirRequisitosDeFormatoEnMensaje() {

            DocumentoFormatoInvalidException exception = new DocumentoFormatoInvalidException("invalid");

            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("4 y 20 dígitos"));
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            DocumentoFormatoInvalidException exception = new DocumentoFormatoInvalidException("invalid");

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("SalarioInvalidException")
    class SalarioInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con mensaje por defecto")
        void debeCrearExcepcionConMensajePorDefecto() {

            SalarioInvalidException exception = new SalarioInvalidException();

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            SalarioInvalidException exception = new SalarioInvalidException();

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("SalarioRangoInvalidException")
    class SalarioRangoInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con mensaje por defecto")
        void debeCrearExcepcionConMensajePorDefecto() {

            SalarioRangoInvalidException exception = new SalarioRangoInvalidException();

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().length() > 0);
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            SalarioRangoInvalidException exception = new SalarioRangoInvalidException();

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("RolInvalidException")
    class RolInvalidExceptionTest {

        @Test
        @DisplayName("Debe crear excepción con rol nulo")
        void debeCrearExcepcionConRolNulo() {

            RolInvalidException exception = new RolInvalidException(null);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("null"));
        }

        @Test
        @DisplayName("Debe crear excepción con rol específico en el mensaje")
        void debeCrearExcepcionConRolEspecifico() {

            Long rolInvalido = 999L;

            RolInvalidException exception = new RolInvalidException(rolInvalido);

            assertNotNull(exception);
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(rolInvalido.toString()));
        }

        @Test
        @DisplayName("Debe ser RuntimeException")
        void debeSerRuntimeException() {

            RolInvalidException exception = new RolInvalidException(1L);

            assertTrue(exception instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("Casos Edge de Excepciones")
    class CasosEdgeExcepciones {

        @Test
        @DisplayName("Todas las excepciones deben tener mensajes no vacíos")
        void todasLasExcepcionesDebenTenerMensajesNoVacios() {

            assertFalse(new NombreInvalidException().getMessage().isEmpty());
            assertFalse(new ApellidoInvalidException().getMessage().isEmpty());
            assertFalse(new CorreoInvalidException().getMessage().isEmpty());
            assertFalse(new CorreoFormatoInvalidException("test").getMessage().isEmpty());
            assertFalse(new CorreoExistInvalidException("test").getMessage().isEmpty());
            assertFalse(new DocumentoExistInvalidException("test").getMessage().isEmpty());
            assertFalse(new DocumentoFormatoInvalidException("test").getMessage().isEmpty());
            assertFalse(new SalarioInvalidException().getMessage().isEmpty());
            assertFalse(new SalarioRangoInvalidException().getMessage().isEmpty());
            assertFalse(new RolInvalidException(1L).getMessage().isEmpty());
        }

        @Test
        @DisplayName("Excepciones parametrizadas deben manejar valores edge")
        void excepcionesParametrizadasDebenManejarValoresEdge() {
            assertDoesNotThrow(() -> new CorreoFormatoInvalidException(""));
            assertDoesNotThrow(() -> new CorreoFormatoInvalidException(" "));
            assertDoesNotThrow(() -> new CorreoExistInvalidException(""));
            assertDoesNotThrow(() -> new DocumentoExistInvalidException(""));
            assertDoesNotThrow(() -> new DocumentoFormatoInvalidException(""));
            assertDoesNotThrow(() -> new RolInvalidException(0L));
            assertDoesNotThrow(() -> new RolInvalidException(-1L));
        }
    }
}