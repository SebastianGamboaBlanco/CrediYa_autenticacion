package co.com.crediya.model;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    private static final String NOMBRES_VALIDOS = "Juan Carlos";
    private static final String APELLIDOS_VALIDOS = "Pérez González";
    private static final String CORREO_VALIDO = "juan.perez@email.com";
    private static final Integer SALARIO_VALIDO = 3500000;

    @Nested
    @DisplayName("Constructor - Usuario válido")
    class ConstructorValido {

        @Test
        @DisplayName("Debe crear user con todos los datos válidos")
        void debeCrearUsuarioConDatosValidos() {

            User user = new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO);

            assertNotNull(user);
            assertEquals(NOMBRES_VALIDOS, user.getFirstName());
            assertEquals(APELLIDOS_VALIDOS, user.getLastName());
            assertEquals(CORREO_VALIDO, user.getEmail());
            assertEquals(SALARIO_VALIDO, user.getBaseSalary());
        }

        @ParameterizedTest
        @DisplayName("Debe crear user con diferentes salarios válidos")
        @ValueSource(ints = {0, 1, 1000000, 7500000, 15000000})
        void debeCrearUsuarioConSalariosValidos(int salario) {
            assertDoesNotThrow(() -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario));
        }

        @ParameterizedTest
        @DisplayName("Debe crear user con diferentes correos válidos")
        @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co",
            "test123@test-domain.com",
            "a@b.co",
            "user+tag@example.org"
        })
        void debeCrearUsuarioConCorreosValidos(String correo) {

            assertDoesNotThrow(() -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Nombres")
    class ValidacionNombres {

        @ParameterizedTest
        @DisplayName("Debe lanzar BusinessException cuando el nombre es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConNombreInvalido(String nombre) {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(nombre, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO)
            );
            assertNotNull(exception);
            assertEquals("nombres", exception.getField());
            assertEquals(ErrorCode.FIRST_NAME_REQUIRED, exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Debe aceptar nombres válidos")
        @ValueSource(strings = {
            "Juan",
            "María José",
            "Carlos Eduardo",
            "A",
            "José María de los Ángeles"
        })
        void debeAceptarNombresValidos(String nombre) {

            assertDoesNotThrow(() -> new User(nombre, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Apellidos")
    class ValidacionApellidos {

        @ParameterizedTest
        @DisplayName("Debe lanzar BusinessException cuando el apellido es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConApellidoInvalido(String apellido) {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(NOMBRES_VALIDOS, apellido, CORREO_VALIDO, SALARIO_VALIDO)
            );
            assertNotNull(exception);
            assertEquals("apellidos", exception.getField());
            assertEquals(ErrorCode.LAST_NAME_REQUIRED, exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Debe aceptar apellidos válidos")
        @ValueSource(strings = {
            "Pérez",
            "García López",
            "de la Cruz",
            "Martínez-Sánchez",
            "O'Connor"
        })
        void debeAceptarApellidosValidos(String apellido) {

            assertDoesNotThrow(() -> new User(NOMBRES_VALIDOS, apellido, CORREO_VALIDO, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Correo Electrónico")
    class ValidacionCorreo {

        @ParameterizedTest
        @DisplayName("Debe lanzar BusinessException cuando el correo es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConCorreoNuloOVacio(String correo) {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO)
            );
            assertNotNull(exception);
            assertEquals("correoElectronico", exception.getField());
            assertEquals(ErrorCode.EMAIL_REQUIRED, exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Debe lanzar BusinessException con formato inválido")
        @ValueSource(strings = {
            "correo-sin-arroba",
            "@dominio.com",
            "user@",
            "user@dominio",
            "user.@dominio.com",
            ".user@dominio.com",
            "user@dominio.",
            "user@@dominio.com",
            "user @dominio.com",
            "user@dominio .com"
        })
        void debeLanzarExcepcionConFormatoInvalido(String correo) {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO)
            );
            assertNotNull(exception);
            assertEquals("correoElectronico", exception.getField());
            assertEquals(ErrorCode.EMAIL_FORMAT_INVALID, exception.getErrorCode());
            assertTrue(exception.getMessage().contains(correo));
        }
    }

    @Nested
    @DisplayName("Validación de Salario Base")
    class ValidacionSalarioBase {

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el salario es nulo")
        void debeLanzarExcepcionConSalarioNulo() {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, null)
            );
            assertNotNull(exception);
            assertEquals("salarioBase", exception.getField());
            assertEquals(ErrorCode.SALARY_REQUIRED, exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Debe lanzar BusinessException con salarios fuera de rango")
        @ValueSource(ints = {-1, -100, 15000001, 20000000})
        void debeLanzarExcepcionConSalarioFueraDeRango(int salario) {

            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario)
            );
            assertNotNull(exception);
            assertEquals("salarioBase", exception.getField());
            assertEquals(ErrorCode.SALARY_OUT_OF_RANGE, exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Debe aceptar salarios en los límites del rango")
        @CsvSource({
            "0, Salario mínimo",
            "15000000, Salario máximo"
        })
        void debeAceptarSalariosEnLimites(int salario, String descripcion) {

            assertDoesNotThrow(
                () -> new User(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario),
                descripcion
            );
        }
    }

}