package com.example.rockstadium.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI/Swagger configuration with security
 * Configura la documentación de la API con autenticación HTTP Basic
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        // Nombre del esquema de seguridad
        final String securitySchemeName = "basicAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("RockStadium API")
                        .version("1.0.0")
                        .description("""
                                API for concert discovery, artists and nearby venue services.
                                
                                ## Authentication
                                This API uses **HTTP Basic Authentication**. 
                                
                                ### How to authenticate in Swagger:
                                1. Click the **Authorize** button (🔒) at the top right
                                2. Enter your email as username
                                3. Enter your password
                                4. Click **Authorize**
                                
                                ### User Roles:
                                - **ROLE_USER**: Regular users - can manage their own data
                                - **ROLE_ADMIN**: Administrators - full access to all endpoints
                                
                                ### Testing Credentials:
                                Register a new account using `/api/v1/users/register` endpoint
                                or use existing test credentials.
                                """)
                        .contact(new Contact()
                                .name("RockStadium Team")
                                .email("support@rockstadium.com")
                                .url("https://github.com/tu-usuario/rockstadium"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.rockstadium.com")
                                .description("Production Server (when available)")
                ))
                // Agregar el esquema de seguridad HTTP Basic
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Enter your email and password for HTTP Basic Authentication")
                        )
                )
                // Aplicar el esquema de seguridad globalmente
                // Esto mostrará el candado 🔒 en todos los endpoints protegidos
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}