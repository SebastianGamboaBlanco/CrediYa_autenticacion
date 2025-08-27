package co.com.crediya.model.gateways;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.UsuarioCompleto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Interfaces Contract Tests")
class RepositoryContractTest {

    @Nested
    @DisplayName("UsuarioRepository Interface Contract")
    class UsuarioRepositoryContractTest {

        @Test
        @DisplayName("Debe ser una interfaz")
        void debeSerUnaInterfaz() {

            assertTrue(UsuarioRepository.class.isInterface(), 
                "UsuarioRepository debe ser una interfaz");
        }

        @Test
        @DisplayName("Debe tener método registrarUsuario con la firma correcta")
        void debeTenerMetodoRegistrarUsuarioConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod(
                "registrarUsuario", 
                Usuario.class, String.class, String.class, String.class, Long.class
            );

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("registrarUsuario", method.getName());
            
            Class<?>[] expectedParams = {Usuario.class, String.class, String.class, String.class, Long.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método existeEmail con la firma correcta")
        void debeTenerMetodoExisteEmailConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod("existeEmail", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("existeEmail", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método existeDocumentoIdentidad con la firma correcta")
        void debeTenerMetodoExisteDocumentoIdentidadConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod("existeDocumentoIdentidad", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("existeDocumentoIdentidad", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método buscarPorDocumentoIdentidad con la firma correcta")
        void debeTenerMetodoBuscarPorDocumentoIdentidadConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod("buscarPorDocumentoIdentidad", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("buscarPorDocumentoIdentidad", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener exactamente 4 métodos públicos")
        void debeTenerExactamente4MetodosPublicos() {

            Method[] methods = UsuarioRepository.class.getDeclaredMethods();

            assertEquals(4, methods.length, 
                "UsuarioRepository debe tener exactamente 4 métodos públicos");
        }

        @Test
        @DisplayName("Todos los métodos deben retornar Mono")
        void todosLosMetodosDebenRetornarMono() {

            Method[] methods = UsuarioRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertEquals(Mono.class, method.getReturnType(),
                    "El método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("No debe tener métodos default")
        void noDebeTenerMetodosDefault() {

            Method[] methods = UsuarioRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertFalse(method.isDefault(),
                    "El método " + method.getName() + " no debe ser default");
            });
        }
    }

    @Nested
    @DisplayName("RolRepository Interface Contract")
    class RolRepositoryContractTest {

        @Test
        @DisplayName("Debe ser una interfaz")
        void debeSerUnaInterfaz() {

            assertTrue(RolRepository.class.isInterface(), 
                "RolRepository debe ser una interfaz");
        }

        @Test
        @DisplayName("Debe tener método existeRol con la firma correcta")
        void debeTenerMetodoExisteRolConFirmaCorrecta() throws NoSuchMethodException {

            Method method = RolRepository.class.getMethod("existeRol", Long.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("existeRol", method.getName());
            
            Class<?>[] expectedParams = {Long.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener exactamente 1 método público")
        void debeTenerExactamente1MetodoPublico() {

            Method[] methods = RolRepository.class.getDeclaredMethods();

            assertEquals(1, methods.length, 
                "RolRepository debe tener exactamente 1 método público");
        }

        @Test
        @DisplayName("El método debe retornar Mono")
        void elMetodoDebeRetornarMono() {

            Method[] methods = RolRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertEquals(Mono.class, method.getReturnType(),
                    "El método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("No debe tener métodos default")
        void noDebeTenerMetodosDefault() {
            Method[] methods = RolRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertFalse(method.isDefault(),
                    "El método " + method.getName() + " no debe ser default");
            });
        }
    }

    @Nested
    @DisplayName("Repository Interfaces Design Contract")
    class RepositoryInterfacesDesignContractTest {

        @Test
        @DisplayName("UsuarioRepository debe estar en el paquete correcto")
        void usuarioRepositoryDebeEstarEnPaqueteCorrecto() {

            assertEquals("co.com.crediya.model.gateways", 
                UsuarioRepository.class.getPackage().getName(),
                "UsuarioRepository debe estar en el paquete de gateways del dominio");
        }

        @Test
        @DisplayName("RolRepository debe estar en el paquete correcto")
        void rolRepositoryDebeEstarEnPaqueteCorrecto() {

            assertEquals("co.com.crediya.model.gateways", 
                RolRepository.class.getPackage().getName(),
                "RolRepository debe estar en el paquete de gateways del dominio");
        }

        @Test
        @DisplayName("Las interfaces no deben extender otras interfaces")
        void lasInterfacesNoDebenExtenderOtrasInterfaces() {

            assertEquals(0, UsuarioRepository.class.getInterfaces().length,
                "UsuarioRepository no debe extender otras interfaces");
                
            assertEquals(0, RolRepository.class.getInterfaces().length,
                "RolRepository no debe extender otras interfaces");
        }

        @Test
        @DisplayName("Las interfaces no deben tener campos públicos")
        void lasInterfacesNoDebenTenerCamposPublicos() {

            assertEquals(0, UsuarioRepository.class.getFields().length,
                "UsuarioRepository no debe tener campos públicos");
                
            assertEquals(0, RolRepository.class.getFields().length,
                "RolRepository no debe tener campos públicos");
        }
    }

    @Nested
    @DisplayName("Method Signature Validation")
    class MethodSignatureValidationTest {

        @Test
        @DisplayName("UsuarioRepository.registrarUsuario debe usar tipos de dominio correctos")
        void registrarUsuarioDebeUsarTiposDeDominioCorrectos() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod(
                "registrarUsuario", 
                Usuario.class, String.class, String.class, String.class, Long.class
            );

            Class<?>[] paramTypes = method.getParameterTypes();
            assertEquals(Usuario.class, paramTypes[0], "Primer parámetro debe ser Usuario");
            assertEquals(String.class, paramTypes[1], "Segundo parámetro debe ser String (documentoIdentidad)");
            assertEquals(String.class, paramTypes[2], "Tercer parámetro debe ser String (fechaNacimiento)");
            assertEquals(String.class, paramTypes[3], "Cuarto parámetro debe ser String (telefono)");
            assertEquals(Long.class, paramTypes[4], "Quinto parámetro debe ser Long (idRol)");
        }

        @Test
        @DisplayName("UsuarioRepository.buscarPorDocumentoIdentidad debe retornar el tipo correcto")
        void buscarPorDocumentoIdentidadDebeRetornarTipoCorrecto() throws NoSuchMethodException {

            Method method = UsuarioRepository.class.getMethod("buscarPorDocumentoIdentidad", String.class);

            assertEquals(Mono.class, method.getReturnType());

            assertNotNull(method);
            assertFalse(method.isSynthetic());
        }
    }

    @Nested
    @DisplayName("Repository Pattern Compliance")
    class RepositoryPatternComplianceTest {

        @Test
        @DisplayName("Los repositorios deben seguir convención de naming")
        void losRepositoriosDebenSeguirConvencionDeNaming() {
            // Assert
            assertTrue(UsuarioRepository.class.getSimpleName().endsWith("Repository"),
                "UsuarioRepository debe terminar con 'Repository'");
                
            assertTrue(RolRepository.class.getSimpleName().endsWith("Repository"),
                "RolRepository debe terminar con 'Repository'");
        }

        @Test
        @DisplayName("Los repositorios deben usar reactive types")
        void losRepositoriosDebenUsarReactiveTypes() {

            Method[] usuarioMethods = UsuarioRepository.class.getDeclaredMethods();
            Method[] rolMethods = RolRepository.class.getDeclaredMethods();

            Arrays.stream(usuarioMethods).forEach(method -> {
                assertTrue(method.getReturnType().equals(Mono.class),
                    "UsuarioRepository método " + method.getName() + " debe retornar Mono");
            });

            Arrays.stream(rolMethods).forEach(method -> {
                assertTrue(method.getReturnType().equals(Mono.class),
                    "RolRepository método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("Los repositorios no deben depender de infraestructura")
        void losRepositoriosNoDebenDependerDeInfraestructura() {

            Method[] usuarioMethods = UsuarioRepository.class.getDeclaredMethods();
            Method[] rolMethods = RolRepository.class.getDeclaredMethods();

            Arrays.stream(usuarioMethods).forEach(method -> {
                Arrays.stream(method.getParameterTypes()).forEach(paramType -> {
                    assertFalse(paramType.getPackage().getName().startsWith("org.springframework"),
                        "UsuarioRepository no debe depender de Spring en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("javax.persistence"),
                        "UsuarioRepository no debe depender de JPA en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("io.r2dbc"),
                        "UsuarioRepository no debe depender de R2DBC en " + method.getName());
                });
            });

            Arrays.stream(rolMethods).forEach(method -> {
                Arrays.stream(method.getParameterTypes()).forEach(paramType -> {
                    assertFalse(paramType.getPackage().getName().startsWith("org.springframework"),
                        "RolRepository no debe depender de Spring en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("javax.persistence"),
                        "RolRepository no debe depender de JPA en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("io.r2dbc"),
                        "RolRepository no debe depender de R2DBC en " + method.getName());
                });
            });
        }
    }
}