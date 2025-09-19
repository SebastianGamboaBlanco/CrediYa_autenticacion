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
import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.dto.UserQueryResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.dto.TokenValidationRequest;
import co.com.crediya.api.dto.TokenValidationResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Users", description = "API for user management")
public class RouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/users",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "registerUser",
            operation = @Operation(
                operationId = "registerUser",
                summary = "Register new user",
                description = "Registers a new user in the system with business validations",
                tags = {"Users"},
                requestBody = @RequestBody(
                    description = "User data to register",
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "User registered successfully",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"0\",\"message\":\"User registered successfully\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"Name is required\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "User already exists",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"Email juan.perez@email.com is already registered\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"Internal server error\"}"
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
                            summary = "Authenticate user",
                            description = "Authenticates a user with email and password, returning a JWT token",
                            tags = {"Authentication"},
                            requestBody = @RequestBody(
                                    description = "Authentication credentials",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = LoginRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Successful login",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"0\",\"message\":\"Successful login\",\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"tokenType\":\"Bearer\",\"expiresAt\":\"2024-01-01T12:00:00Z\",\"role\":{\"id\":1,\"name\":\"ADMIN\"}}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Invalid credentials",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"1\",\"message\":\"Invalid credentials\"}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoginResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":\"1\",\"message\":\"Email is required\"}"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/auth/validate",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "validateToken",
                    operation = @Operation(
                            operationId = "validateToken",
                            summary = "Validate JWT token",
                            description = "Validates a JWT token and returns claims information if valid",
                            tags = {"Authentication"},
                            requestBody = @RequestBody(
                                    description = "Token to validate",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TokenValidationRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Token validated successfully",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TokenValidationResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":0,\"message\":\"Valid token\",\"valid\":true,\"subject\":\"123\",\"email\":\"user@example.com\",\"role\":{\"id\":1,\"name\":\"ADMIN\"},\"issuedAt\":\"2024-01-01T10:00:00Z\",\"expiresAt\":\"2024-01-01T11:00:00Z\",\"issuer\":\"crediya-auth-service\"}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Invalid token",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TokenValidationResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":1,\"message\":\"Invalid or expired token\",\"valid\":false}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TokenValidationResponse.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{\"code\":1,\"message\":\"Token is required\",\"valid\":false}"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
        @RouterOperation(
            path = "/api/v1/users/{documentIdentity}",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "validateUserExistence",
            operation = @Operation(
                operationId = "getUserByDocument",
                summary = "Get user by identity document",
                description = "Search and return user information using their identity document",
                tags = {"Users"},
                parameters = {
                    @Parameter(
                        name = "documentIdentity",
                        description = "User's identity document number",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string", example = "12345678")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "User found",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"0\",\"message\":\"User found\",\"user\":{\"id\":1,\"firstName\":\"Juan Carlos\",\"lastName\":\"Pérez González\",\"email\":\"juan.perez@email.com\",\"documentIdentity\":\"12345678\",\"birthDate\":\"1990-05-15\",\"phone\":\"3001234567\",\"roleId\":1,\"baseSalary\":3500000}}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"User not found with document: 87654321\",\"user\":null}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid identity document",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"Invalid identity document: 123abc. Must contain between 4 and 20 numeric digits.\",\"user\":null}"
                            )
                        )
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users/email/{email:.+}",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "validateUserExistenceByEmail",
            operation = @Operation(
                operationId = "getUserByEmail",
                summary = "Get user by email",
                description = "Search and return user information using their email address",
                tags = {"Users"},
                parameters = {
                    @Parameter(
                        name = "email",
                        description = "User's email address",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string", example = "juan.perez@email.com")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "User found",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"0\",\"message\":\"User found\",\"user\":{\"id\":1,\"firstName\":\"Juan Carlos\",\"lastName\":\"Pérez González\",\"email\":\"juan.perez@email.com\",\"documentIdentity\":\"12345678\",\"birthDate\":\"1990-05-15\",\"phone\":\"3001234567\",\"roleId\":1,\"baseSalary\":3500000}}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"User not found with email: test@email.com\",\"user\":null}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid email format",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserQueryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"code\":\"1\",\"message\":\"Invalid email format: invalid-email\",\"user\":null}"
                            )
                        )
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/users"), handler::registerUser)
                .andRoute(POST("/api/v1/login"), handler::login)
                .andRoute(POST("/api/v1/auth/validate"), handler::validateToken)
                .andRoute(GET("/api/v1/users/{documentIdentity}"), handler::validateUserExistence)
                .andRoute(GET("/api/v1/users/email/{email:.+}"), handler::validateUserExistenceByEmail);
    }
}
