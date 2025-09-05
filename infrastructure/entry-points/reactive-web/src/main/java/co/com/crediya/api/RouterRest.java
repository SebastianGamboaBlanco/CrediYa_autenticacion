package co.com.crediya.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.api.dto.UsuarioConsultaResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class RouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/usuarios",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "registrarUsuario",
            operation = @Operation(
                operationId = "registrarUsuario",
                summary = "Registrar nuevo usuario",
                description = "Registra un nuevo usuario en el sistema con validaciones de negocio",
                tags = {"Usuarios"},
                requestBody = @RequestBody(
                    description = "Datos del usuario a registrar",
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UsuarioRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Usuario registrado exitosamente",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"0\",\"mensaje\":\"Usuario registrado exitosamente\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Datos de entrada inválidos",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"mensaje\":\"El nombre es obligatorio\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "Usuario ya existe",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"mensaje\":\"El correo electrónico juan.perez@email.com ya se encuentra registrado\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"mensaje\":\"Error interno del servidor\"}"
                            )
                        )
                    )
                }
            )
        ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "Autenticar usuario",
                            description = "Autentica un usuario con email y contraseña, retornando un token JWT",
                            tags = {"Autenticación"},
                            requestBody = @RequestBody(
                                    description = "Credenciales de autenticación",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = LoginRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login exitoso",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"0\",\"mensaje\":\"Login exitoso\",\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"tokenType\":\"Bearer\",\"expiresAt\":\"2024-01-01T12:00:00Z\",\"role\":{\"id\":1,\"name\":\"ADMIN\"}}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales inválidas",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"1\",\"mensaje\":\"Credenciales inválidas\"}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos de entrada inválidos",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"1\",\"mensaje\":\"El email es obligatorio\"}"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
        @RouterOperation(
            path = "/api/v1/usuarios/{documentoIdentidad}",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "validarExistenciaUsuario",
            operation = @Operation(
                operationId = "consultarUsuario",
                summary = "Consultar usuario por documento de identidad",
                description = "Busca y retorna la información de un usuario usando su documento de identidad",
                tags = {"Usuarios"},
                parameters = {
                    @Parameter(
                        name = "documentoIdentidad",
                        description = "Número de documento de identidad del usuario",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string", example = "12345678")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Usuario encontrado",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioConsultaResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"0\",\"mensaje\":\"Usuario encontrado\",\"usuario\":{\"id\":1,\"nombres\":\"Juan Carlos\",\"apellidos\":\"Pérez González\",\"correoElectronico\":\"juan.perez@email.com\",\"documentoIdentidad\":\"12345678\",\"fechaNacimiento\":\"1990-05-15\",\"telefono\":\"3001234567\",\"idRol\":1,\"salarioBase\":3500000}}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Usuario no encontrado",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioConsultaResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"mensaje\":\"Usuario no encontrado con documento: 87654321\",\"usuario\":null}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Documento de identidad inválido",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsuarioConsultaResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"mensaje\":\"Documento de identidad inválido: 123abc. Debe contener entre 4 y 20 dígitos numéricos.\",\"usuario\":null}"
                            )
                        )
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::registrarUsuario)
                .andRoute(POST("/api/v1/login"), handler::login)
                .andRoute(GET("/api/v1/usuarios/{documentoIdentidad}"), handler::validarExistenciaUsuario);
    }
}
