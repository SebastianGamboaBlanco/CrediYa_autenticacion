package co.com.crediya.model;

import co.com.crediya.model.exceptions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario Domain Model Tests")
class UsuarioTest {

    private static final String NOMBRES_VALIDOS = "Juan Carlos";
    private static final String APELLIDOS_VALIDOS = "Pérez González";
    private static final String CORREO_VALIDO = "juan.perez@email.com";
    private static final Integer SALARIO_VALIDO = 3500000;

    @Nested
    @DisplayName("Constructor - Usuario válido")
    class ConstructorValido {

        @Test
        @DisplayName("Debe crear usuario con todos los datos válidos")
        void debeCrearUsuarioConDatosValidos() {

            Usuario usuario = new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO);

            assertNotNull(usuario);
            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());
        }

        @ParameterizedTest
        @DisplayName("Debe crear usuario con diferentes salarios válidos")
        @ValueSource(ints = {0, 1, 1000000, 7500000, 15000000})
        void debeCrearUsuarioConSalariosValidos(int salario) {
            assertDoesNotThrow(() -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario));
        }

        @ParameterizedTest
        @DisplayName("Debe crear usuario con diferentes correos válidos")
        @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co",
            "test123@test-domain.com",
            "a@b.co",
            "user+tag@example.org"
        })
        void debeCrearUsuarioConCorreosValidos(String correo) {

            assertDoesNotThrow(() -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Nombres")
    class ValidacionNombres {

        @ParameterizedTest
        @DisplayName("Debe lanzar NombreInvalidException cuando el nombre es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConNombreInvalido(String nombre) {

            NombreInvalidException exception = assertThrows(
                NombreInvalidException.class,
                () -> new Usuario(nombre, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO)
            );
            assertNotNull(exception);
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

            assertDoesNotThrow(() -> new Usuario(nombre, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Apellidos")
    class ValidacionApellidos {

        @ParameterizedTest
        @DisplayName("Debe lanzar ApellidoInvalidException cuando el apellido es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConApellidoInvalido(String apellido) {

            ApellidoInvalidException exception = assertThrows(
                ApellidoInvalidException.class,
                () -> new Usuario(NOMBRES_VALIDOS, apellido, CORREO_VALIDO, SALARIO_VALIDO)
            );
            assertNotNull(exception);
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

            assertDoesNotThrow(() -> new Usuario(NOMBRES_VALIDOS, apellido, CORREO_VALIDO, SALARIO_VALIDO));
        }
    }

    @Nested
    @DisplayName("Validación de Correo Electrónico")
    class ValidacionCorreo {

        @ParameterizedTest
        @DisplayName("Debe lanzar CorreoInvalidException cuando el correo es nulo o vacío")
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void debeLanzarExcepcionConCorreoNuloOVacio(String correo) {

            CorreoInvalidException exception = assertThrows(
                CorreoInvalidException.class,
                () -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO)
            );
            assertNotNull(exception);
        }

        @ParameterizedTest
        @DisplayName("Debe lanzar CorreoFormatoInvalidException con formato inválido")
        @ValueSource(strings = {
            "correo-sin-arroba",
            "@dominio.com",
            "usuario@",
            "usuario@dominio",
            "usuario.@dominio.com",
            ".usuario@dominio.com",
            "usuario@dominio.",
            "usuario@@dominio.com",
            "usuario @dominio.com",
            "usuario@dominio .com"
        })
        void debeLanzarExcepcionConFormatoInvalido(String correo) {

            CorreoFormatoInvalidException exception = assertThrows(
                CorreoFormatoInvalidException.class,
                () -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, correo, SALARIO_VALIDO)
            );
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains(correo));
        }
    }

    @Nested
    @DisplayName("Validación de Salario Base")
    class ValidacionSalarioBase {

        @Test
        @DisplayName("Debe lanzar SalarioInvalidException cuando el salario es nulo")
        void debeLanzarExcepcionConSalarioNulo() {

            SalarioInvalidException exception = assertThrows(
                SalarioInvalidException.class,
                () -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, null)
            );
            assertNotNull(exception);
        }

        @ParameterizedTest
        @DisplayName("Debe lanzar SalarioRangoInvalidException con salarios fuera de rango")
        @ValueSource(ints = {-1, -100, 15000001, 20000000})
        void debeLanzarExcepcionConSalarioFueraDeRango(int salario) {

            SalarioRangoInvalidException exception = assertThrows(
                SalarioRangoInvalidException.class,
                () -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario)
            );
            assertNotNull(exception);
        }

        @ParameterizedTest
        @DisplayName("Debe aceptar salarios en los límites del rango")
        @CsvSource({
            "0, Salario mínimo",
            "15000000, Salario máximo"
        })
        void debeAceptarSalariosEnLimites(int salario, String descripcion) {

            assertDoesNotThrow(
                () -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, salario),
                descripcion
            );
        }
    }

    @Nested
    @DisplayName("Inmutabilidad y Encapsulación")
    class InmutabilidadYEncapsulacion {

        @Test
        @DisplayName("Los campos deben ser inmutables después de la creación")
        void losCamposDebenSerInmutables() {

            Usuario usuario = new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO, SALARIO_VALIDO);

            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());

            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());
        }

        @Test
        @DisplayName("No debe existir setters públicos")
        void noDebeExistirSettersPublicos() {

            assertFalse(
                java.util.Arrays.stream(Usuario.class.getMethods())
                    .anyMatch(method -> method.getName().startsWith("set")),
                "No debería haber setters públicos en Usuario"
            );
        }
    }

    @Nested
    @DisplayName("Casos Edge y Boundary")
    class CasosEdgeYBoundary {

        @Test
        @DisplayName("Debe validar correctamente con caracteres especiales en nombres")
        void debeValidarNombresConCaracteresEspeciales() {

            assertDoesNotThrow(() -> new Usuario("José María", "de la Ñ-Cruz", CORREO_VALIDO, SALARIO_VALIDO));
        }

        @Test
        @DisplayName("Debe manejar correos con múltiples puntos")
        void debeValidarCorreosConMultiplesPuntos() {

            assertDoesNotThrow(() -> new Usuario(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, "user.name.lastname@domain.co.uk", SALARIO_VALIDO));
        }
    }
}