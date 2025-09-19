package co.com.crediya.model.gateways;

import co.com.crediya.model.User;
import co.com.crediya.model.CompleteUser;
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
    @DisplayName("UserRepository Interface Contract")
    class UserRepositoryContractTest {

        @Test
        @DisplayName("Debe ser una interfaz")
        void debeSerUnaInterfaz() {

            assertTrue(UserRepository.class.isInterface(), 
                "UserRepository debe ser una interfaz");
        }

        @Test
        @DisplayName("Debe tener método registerUser con la firma correcta")
        void debeTenerMetodoRegistrarUserConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod(
                "registerUser",
                User.class, String.class, String.class, String.class, Long.class, String.class
            );

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("registerUser", method.getName());

            Class<?>[] expectedParams = {User.class, String.class, String.class, String.class, Long.class, String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método emailExists con la firma correcta")
        void debeTenerMetodoExisteEmailConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod("emailExists", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("emailExists", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método documentIdentityExists con la firma correcta")
        void debeTenerMetodoExisteDocumentoIdentidadConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod("documentIdentityExists", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("documentIdentityExists", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener método findByDocumentIdentity con la firma correcta")
        void debeTenerMetodoBuscarPorDocumentoIdentidadConFirmaCorrecta() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod("findByDocumentIdentity", String.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("findByDocumentIdentity", method.getName());
            
            Class<?>[] expectedParams = {String.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener exactamente 4 métodos públicos")
        void debeTenerExactamente4MetodosPublicos() {

            Method[] methods = UserRepository.class.getDeclaredMethods();

            assertEquals(4, methods.length, 
                "UserRepository debe tener exactamente 4 métodos públicos");
        }

        @Test
        @DisplayName("Todos los métodos deben retornar Mono")
        void todosLosMetodosDebenRetornarMono() {

            Method[] methods = UserRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertEquals(Mono.class, method.getReturnType(),
                    "El método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("No debe tener métodos default")
        void noDebeTenerMetodosDefault() {

            Method[] methods = UserRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertFalse(method.isDefault(),
                    "El método " + method.getName() + " no debe ser default");
            });
        }
    }

    @Nested
    @DisplayName("RoleRepository Interface Contract")
    class RoleRepositoryContractTest {

        @Test
        @DisplayName("Debe ser una interfaz")
        void debeSerUnaInterfaz() {

            assertTrue(RoleRepository.class.isInterface(), 
                "RoleRepository debe ser una interfaz");
        }

        @Test
        @DisplayName("Debe tener método roleExists con la firma correcta")
        void debeTenerMetodoExisteRolConFirmaCorrecta() throws NoSuchMethodException {

            Method method = RoleRepository.class.getMethod("roleExists", Long.class);

            assertNotNull(method);
            assertEquals(Mono.class, method.getReturnType());
            assertEquals("roleExists", method.getName());
            
            Class<?>[] expectedParams = {Long.class};
            assertArrayEquals(expectedParams, method.getParameterTypes());
        }

        @Test
        @DisplayName("Debe tener exactamente 1 método público")
        void debeTenerExactamente1MetodoPublico() {

            Method[] methods = RoleRepository.class.getDeclaredMethods();

            assertEquals(1, methods.length, 
                "RoleRepository debe tener exactamente 1 método público");
        }

        @Test
        @DisplayName("El método debe retornar Mono")
        void elMetodoDebeRetornarMono() {

            Method[] methods = RoleRepository.class.getDeclaredMethods();

            Arrays.stream(methods).forEach(method -> {
                assertEquals(Mono.class, method.getReturnType(),
                    "El método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("No debe tener métodos default")
        void noDebeTenerMetodosDefault() {
            Method[] methods = RoleRepository.class.getDeclaredMethods();

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
        @DisplayName("UserRepository debe estar en el paquete correcto")
        void usuarioRepositoryDebeEstarEnPaqueteCorrecto() {

            assertEquals("co.com.crediya.model.gateways", 
                UserRepository.class.getPackage().getName(),
                "UserRepository debe estar en el paquete de gateways del dominio");
        }

        @Test
        @DisplayName("RoleRepository debe estar en el paquete correcto")
        void rolRepositoryDebeEstarEnPaqueteCorrecto() {

            assertEquals("co.com.crediya.model.gateways", 
                RoleRepository.class.getPackage().getName(),
                "RoleRepository debe estar en el paquete de gateways del dominio");
        }

        @Test
        @DisplayName("Las interfaces no deben extender otras interfaces")
        void lasInterfacesNoDebenExtenderOtrasInterfaces() {

            assertEquals(0, UserRepository.class.getInterfaces().length,
                "UserRepository no debe extender otras interfaces");
                
            assertEquals(0, RoleRepository.class.getInterfaces().length,
                "RoleRepository no debe extender otras interfaces");
        }

        @Test
        @DisplayName("Las interfaces no deben tener campos públicos")
        void lasInterfacesNoDebenTenerCamposPublicos() {

            assertEquals(0, UserRepository.class.getFields().length,
                "UserRepository no debe tener campos públicos");
                
            assertEquals(0, RoleRepository.class.getFields().length,
                "RoleRepository no debe tener campos públicos");
        }
    }

    @Nested
    @DisplayName("Method Signature Validation")
    class MethodSignatureValidationTest {

        @Test
        @DisplayName("UserRepository.registerUser debe usar tipos de dominio correctos")
        void registerUserDebeUsarTiposDeDominioCorrectos() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod(
                "registerUser", 
                User.class, String.class, String.class, String.class, Long.class
            );

            Class<?>[] paramTypes = method.getParameterTypes();
            assertEquals(User.class, paramTypes[0], "Primer parámetro debe ser User");
            assertEquals(String.class, paramTypes[1], "Segundo parámetro debe ser String (documentoIdentidad)");
            assertEquals(String.class, paramTypes[2], "Tercer parámetro debe ser String (fechaNacimiento)");
            assertEquals(String.class, paramTypes[3], "Cuarto parámetro debe ser String (telefono)");
            assertEquals(Long.class, paramTypes[4], "Quinto parámetro debe ser Long (idRol)");
        }

        @Test
        @DisplayName("UserRepository.findByDocumentIdentity debe retornar el tipo correcto")
        void findByDocumentIdentityDebeRetornarTipoCorrecto() throws NoSuchMethodException {

            Method method = UserRepository.class.getMethod("findByDocumentIdentity", String.class);

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
            assertTrue(UserRepository.class.getSimpleName().endsWith("Repository"),
                "UserRepository debe terminar con 'Repository'");
                
            assertTrue(RoleRepository.class.getSimpleName().endsWith("Repository"),
                "RoleRepository debe terminar con 'Repository'");
        }

        @Test
        @DisplayName("Los repositorios deben usar reactive types")
        void losRepositoriosDebenUsarReactiveTypes() {

            Method[] usuarioMethods = UserRepository.class.getDeclaredMethods();
            Method[] rolMethods = RoleRepository.class.getDeclaredMethods();

            Arrays.stream(usuarioMethods).forEach(method -> {
                assertTrue(method.getReturnType().equals(Mono.class),
                    "UserRepository método " + method.getName() + " debe retornar Mono");
            });

            Arrays.stream(rolMethods).forEach(method -> {
                assertTrue(method.getReturnType().equals(Mono.class),
                    "RoleRepository método " + method.getName() + " debe retornar Mono");
            });
        }

        @Test
        @DisplayName("Los repositorios no deben depender de infraestructura")
        void losRepositoriosNoDebenDependerDeInfraestructura() {

            Method[] usuarioMethods = UserRepository.class.getDeclaredMethods();
            Method[] rolMethods = RoleRepository.class.getDeclaredMethods();

            Arrays.stream(usuarioMethods).forEach(method -> {
                Arrays.stream(method.getParameterTypes()).forEach(paramType -> {
                    assertFalse(paramType.getPackage().getName().startsWith("org.springframework"),
                        "UserRepository no debe depender de Spring en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("javax.persistence"),
                        "UserRepository no debe depender de JPA en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("io.r2dbc"),
                        "UserRepository no debe depender de R2DBC en " + method.getName());
                });
            });

            Arrays.stream(rolMethods).forEach(method -> {
                Arrays.stream(method.getParameterTypes()).forEach(paramType -> {
                    assertFalse(paramType.getPackage().getName().startsWith("org.springframework"),
                        "RoleRepository no debe depender de Spring en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("javax.persistence"),
                        "RoleRepository no debe depender de JPA en " + method.getName());
                    assertFalse(paramType.getPackage().getName().startsWith("io.r2dbc"),
                        "RoleRepository no debe depender de R2DBC en " + method.getName());
                });
            });
        }
    }
}