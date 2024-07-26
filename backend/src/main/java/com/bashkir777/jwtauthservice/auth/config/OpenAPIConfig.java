package com.bashkir777.jwtauthservice.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "bashkir777",
                        email = "supletsovd@gmail.com"
                )
                , description = "OpenAPI documentation for JWTSecureTFA project"
                , title = "Open API specification")
        , servers = {
        @Server(url = "http://localhost:8080"
                , description = "Local environment"),
        @Server(url = "http://backend:8080",
                description = "Development environment server, accessible within the Docker Compose network")
}
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Bearer token authentication",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
}
