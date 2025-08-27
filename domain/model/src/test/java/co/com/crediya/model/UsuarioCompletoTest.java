package co.com.crediya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UsuarioCompleto Domain Model Tests")
class UsuarioCompletoTest {

    private static final Long ID_VALIDO = 1L;
    private static final String NOMBRES_VALIDOS = "Juan Carlos";
    private static final String APELLIDOS_VALIDOS = "Pérez González";
    private static final String CORREO_VALIDO = "juan.perez@email.com";
    private static final String DOCUMENTO_VALIDO = "12345678";
    private static final LocalDate FECHA_VALIDA = LocalDate.of(1990, 5, 15);
    private static final String TELEFONO_VALIDO = "3001234567";
    private static final Long ROL_VALIDO = 1L;
    private static final Integer SALARIO_VALIDO = 3500000;

    @Nested
    @DisplayName("Constructor y Getters")
    class ConstructorYGetters {

        @Test
        @DisplayName("Debe crear UsuarioCompleto con todos los datos válidos")
        void debeCrearUsuarioCompletoConDatosValidos() {

            UsuarioCompleto usuario = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertNotNull(usuario);
            assertEquals(ID_VALIDO, usuario.getId());
            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(DOCUMENTO_VALIDO, usuario.getDocumentoIdentidad());
            assertEquals(FECHA_VALIDA, usuario.getFechaNacimiento());
            assertEquals(TELEFONO_VALIDO, usuario.getTelefono());
            assertEquals(ROL_VALIDO, usuario.getIdRol());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());
        }

        @Test
        @DisplayName("Debe crear UsuarioCompleto con valores nulos (sin validaciones)")
        void debeCrearUsuarioCompletoConValoresNulos() {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                null, null, null, null, null, null, null, null, null
            ));
        }

        @ParameterizedTest
        @DisplayName("Debe crear UsuarioCompleto con diferentes combinaciones de datos")
        @MethodSource("combinacionesDatosValidos")
        void debeCrearUsuarioCompletoConDiferentesCombinaciones(
                Long id, String nombres, String apellidos, String correo, String documento,
                LocalDate fecha, String telefono, Long rol, Integer salario) {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                id, nombres, apellidos, correo, documento, fecha, telefono, rol, salario
            ));
        }

        static Stream<Arguments> combinacionesDatosValidos() {
            return Stream.of(
                Arguments.of(1L, "Juan", "Pérez", "juan@test.com", "12345678", 
                           LocalDate.now(), "3001234567", 1L, 3000000),
                Arguments.of(2L, "María José", "García López", "maria@test.com", "87654321", 
                           LocalDate.of(1985, 3, 10), "3009876543", 2L, 5000000),
                Arguments.of(999L, "Carlos Eduardo", "Martínez Sánchez", "carlos@test.com", "11111111", 
                           LocalDate.of(2000, 12, 25), "3111111111", 3L, 7500000)
            );
        }
    }

    @Nested
    @DisplayName("Inmutabilidad y Encapsulación")
    class InmutabilidadYEncapsulacion {

        @Test
        @DisplayName("Los campos deben ser inmutables después de la creación")
        void losCamposDebenSerInmutables() {

            UsuarioCompleto usuario = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertEquals(ID_VALIDO, usuario.getId());
            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(DOCUMENTO_VALIDO, usuario.getDocumentoIdentidad());
            assertEquals(FECHA_VALIDA, usuario.getFechaNacimiento());
            assertEquals(TELEFONO_VALIDO, usuario.getTelefono());
            assertEquals(ROL_VALIDO, usuario.getIdRol());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());

            assertEquals(ID_VALIDO, usuario.getId());
            assertEquals(NOMBRES_VALIDOS, usuario.getNombres());
            assertEquals(APELLIDOS_VALIDOS, usuario.getApellidos());
            assertEquals(CORREO_VALIDO, usuario.getCorreoElectronico());
            assertEquals(DOCUMENTO_VALIDO, usuario.getDocumentoIdentidad());
            assertEquals(FECHA_VALIDA, usuario.getFechaNacimiento());
            assertEquals(TELEFONO_VALIDO, usuario.getTelefono());
            assertEquals(ROL_VALIDO, usuario.getIdRol());
            assertEquals(SALARIO_VALIDO, usuario.getSalarioBase());
        }

        @Test
        @DisplayName("No debe existir setters públicos")
        void noDebeExistirSettersPublicos() {

            assertFalse(
                java.util.Arrays.stream(UsuarioCompleto.class.getMethods())
                    .anyMatch(method -> method.getName().startsWith("set")),
                "No debería haber setters públicos en UsuarioCompleto"
            );
        }

        @Test
        @DisplayName("Todos los getters deben estar presentes")
        void todosLosGettersDebenEstarPresentes() {

            String[] expectedGetters = {
                "getId", "getNombres", "getApellidos", "getCorreoElectronico",
                "getDocumentoIdentidad", "getFechaNacimiento", "getTelefono", 
                "getIdRol", "getSalarioBase"
            };

            for (String getterName : expectedGetters) {
                assertTrue(
                    java.util.Arrays.stream(UsuarioCompleto.class.getMethods())
                        .anyMatch(method -> method.getName().equals(getterName)),
                    "Debería existir el método " + getterName
                );
            }
        }
    }

    @Nested
    @DisplayName("Casos Edge y Boundary")
    class CasosEdgeYBoundary {

        @Test
        @DisplayName("Debe manejar correctamente valores extremos de ID")
        void debeManejarValoresExtremosDeId() {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                0L, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                Long.MAX_VALUE, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                -1L, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar fechas extremas")
        void debeManejarFechasExtremas() {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.MIN, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.MAX, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.now(), TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar strings vacíos")
        void debeManejarStringsVacios() {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, "", "", "", "", FECHA_VALIDA, "", ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar valores extremos de salario")
        void debeManejarValoresExtremosDeSalario() {

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, 0
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, Integer.MAX_VALUE
            ));

            assertDoesNotThrow(() -> new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, -1
            ));
        }
    }

    @Nested
    @DisplayName("Comparación de Objetos")
    class ComparacionDeObjetos {

        @Test
        @DisplayName("Debe permitir crear múltiples instancias con los mismos datos")
        void debePermitirCrearMultiplesInstanciasConMismosDatos() {

            UsuarioCompleto usuario1 = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            UsuarioCompleto usuario2 = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertNotSame(usuario1, usuario2);

            assertEquals(usuario1.getId(), usuario2.getId());
            assertEquals(usuario1.getNombres(), usuario2.getNombres());
            assertEquals(usuario1.getApellidos(), usuario2.getApellidos());
            assertEquals(usuario1.getCorreoElectronico(), usuario2.getCorreoElectronico());
            assertEquals(usuario1.getDocumentoIdentidad(), usuario2.getDocumentoIdentidad());
            assertEquals(usuario1.getFechaNacimiento(), usuario2.getFechaNacimiento());
            assertEquals(usuario1.getTelefono(), usuario2.getTelefono());
            assertEquals(usuario1.getIdRol(), usuario2.getIdRol());
            assertEquals(usuario1.getSalarioBase(), usuario2.getSalarioBase());
        }
    }

    @Nested
    @DisplayName("Validación de Tipos")
    class ValidacionDeTipos {

        @Test
        @DisplayName("Debe mantener los tipos correctos en los getters")
        void debeManternerTiposCorrectosEnGetters() {

            UsuarioCompleto usuario = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertTrue(usuario.getId() instanceof Long);
            assertTrue(usuario.getNombres() instanceof String);
            assertTrue(usuario.getApellidos() instanceof String);
            assertTrue(usuario.getCorreoElectronico() instanceof String);
            assertTrue(usuario.getDocumentoIdentidad() instanceof String);
            assertTrue(usuario.getFechaNacimiento() instanceof LocalDate);
            assertTrue(usuario.getTelefono() instanceof String);
            assertTrue(usuario.getIdRol() instanceof Long);
            assertTrue(usuario.getSalarioBase() instanceof Integer);
        }

        @Test
        @DisplayName("Debe preservar referencias de objetos inmutables")
        void debePreservarReferenciasDeObjetosInmutables() {

            LocalDate fechaOriginal = LocalDate.of(1990, 5, 15);

            UsuarioCompleto usuario = new UsuarioCompleto(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, fechaOriginal, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertEquals(fechaOriginal, usuario.getFechaNacimiento());
        }
    }
}