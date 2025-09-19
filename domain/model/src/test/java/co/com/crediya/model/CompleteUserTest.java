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

@DisplayName("CompleteUser Domain Model Tests")
class CompleteUserTest {

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
        @DisplayName("Debe crear CompleteUser con todos los datos válidos")
        void debeCrearCompleteUserConDatosValidos() {

            CompleteUser user = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertNotNull(user);
            assertEquals(ID_VALIDO, user.getId());
            assertEquals(NOMBRES_VALIDOS, user.getFirstName());
            assertEquals(APELLIDOS_VALIDOS, user.getLastName());
            assertEquals(CORREO_VALIDO, user.getEmail());
            assertEquals(DOCUMENTO_VALIDO, user.getDocumentIdentity());
            assertEquals(FECHA_VALIDA, user.getBirthDate());
            assertEquals(TELEFONO_VALIDO, user.getPhone());
            assertEquals(ROL_VALIDO, user.getRoleId());
            assertEquals(SALARIO_VALIDO, user.getBaseSalary());
        }

        @Test
        @DisplayName("Debe crear CompleteUser con valores nulos (sin validaciones)")
        void debeCrearCompleteUserConValoresNulos() {

            assertDoesNotThrow(() -> new CompleteUser(
                null, null, null, null, null, null, null, null, null
            ));
        }

        @ParameterizedTest
        @DisplayName("Debe crear CompleteUser con diferentes combinaciones de datos")
        @MethodSource("combinacionesDatosValidos")
        void debeCrearCompleteUserConDiferentesCombinaciones(
                Long id, String nombres, String apellidos, String correo, String documento,
                LocalDate fecha, String telefono, Long rol, Integer salario) {

            assertDoesNotThrow(() -> new CompleteUser(
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

            CompleteUser user = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertEquals(ID_VALIDO, user.getId());
            assertEquals(NOMBRES_VALIDOS, user.getFirstName());
            assertEquals(APELLIDOS_VALIDOS, user.getLastName());
            assertEquals(CORREO_VALIDO, user.getEmail());
            assertEquals(DOCUMENTO_VALIDO, user.getDocumentIdentity());
            assertEquals(FECHA_VALIDA, user.getBirthDate());
            assertEquals(TELEFONO_VALIDO, user.getPhone());
            assertEquals(ROL_VALIDO, user.getRoleId());
            assertEquals(SALARIO_VALIDO, user.getBaseSalary());

            assertEquals(ID_VALIDO, user.getId());
            assertEquals(NOMBRES_VALIDOS, user.getFirstName());
            assertEquals(APELLIDOS_VALIDOS, user.getLastName());
            assertEquals(CORREO_VALIDO, user.getEmail());
            assertEquals(DOCUMENTO_VALIDO, user.getDocumentIdentity());
            assertEquals(FECHA_VALIDA, user.getBirthDate());
            assertEquals(TELEFONO_VALIDO, user.getPhone());
            assertEquals(ROL_VALIDO, user.getRoleId());
            assertEquals(SALARIO_VALIDO, user.getBaseSalary());
        }

        @Test
        @DisplayName("No debe existir setters públicos")
        void noDebeExistirSettersPublicos() {

            assertFalse(
                java.util.Arrays.stream(CompleteUser.class.getMethods())
                    .anyMatch(method -> method.getName().startsWith("set")),
                "No debería haber setters públicos en CompleteUser"
            );
        }

        @Test
        @DisplayName("Todos los getters deben estar presentes")
        void todosLosGettersDebenEstarPresentes() {

            String[] expectedGetters = {
                "getId", "getFirstName", "getLastName", "getEmail",
                "getDocumentIdentity", "getBirthDate", "getPhone",
                "getRoleId", "getBaseSalary"
            };

            for (String getterName : expectedGetters) {
                assertTrue(
                    java.util.Arrays.stream(CompleteUser.class.getMethods())
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

            assertDoesNotThrow(() -> new CompleteUser(
                0L, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new CompleteUser(
                Long.MAX_VALUE, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new CompleteUser(
                -1L, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar fechas extremas")
        void debeManejarFechasExtremas() {

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.MIN, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.MAX, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, LocalDate.now(), TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar strings vacíos")
        void debeManejarStringsVacios() {

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, "", "", "", "", FECHA_VALIDA, "", ROL_VALIDO, SALARIO_VALIDO
            ));
        }

        @Test
        @DisplayName("Debe manejar valores extremos de salario")
        void debeManejarValoresExtremosDeSalario() {

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, 0
            ));

            assertDoesNotThrow(() -> new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, Integer.MAX_VALUE
            ));

            assertDoesNotThrow(() -> new CompleteUser(
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

            CompleteUser user1 = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            CompleteUser user2 = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertNotSame(user1, user2);

            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getFirstName(), user2.getFirstName());
            assertEquals(user1.getLastName(), user2.getLastName());
            assertEquals(user1.getEmail(), user2.getEmail());
            assertEquals(user1.getDocumentIdentity(), user2.getDocumentIdentity());
            assertEquals(user1.getBirthDate(), user2.getBirthDate());
            assertEquals(user1.getPhone(), user2.getPhone());
            assertEquals(user1.getRoleId(), user2.getRoleId());
            assertEquals(user1.getBaseSalary(), user2.getBaseSalary());
        }
    }

    @Nested
    @DisplayName("Validación de Tipos")
    class ValidacionDeTipos {

        @Test
        @DisplayName("Debe mantener los tipos correctos en los getters")
        void debeManternerTiposCorrectosEnGetters() {

            CompleteUser user = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, FECHA_VALIDA, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertTrue(user.getId() instanceof Long);
            assertTrue(user.getFirstName() instanceof String);
            assertTrue(user.getLastName() instanceof String);
            assertTrue(user.getEmail() instanceof String);
            assertTrue(user.getDocumentIdentity() instanceof String);
            assertTrue(user.getBirthDate() instanceof LocalDate);
            assertTrue(user.getPhone() instanceof String);
            assertTrue(user.getRoleId() instanceof Long);
            assertTrue(user.getBaseSalary() instanceof Integer);
        }

        @Test
        @DisplayName("Debe preservar referencias de objetos inmutables")
        void debePreservarReferenciasDeObjetosInmutables() {

            LocalDate fechaOriginal = LocalDate.of(1990, 5, 15);

            CompleteUser user = new CompleteUser(
                ID_VALIDO, NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CORREO_VALIDO,
                DOCUMENTO_VALIDO, fechaOriginal, TELEFONO_VALIDO, ROL_VALIDO, SALARIO_VALIDO
            );

            assertEquals(fechaOriginal, user.getBirthDate());
        }
    }
}