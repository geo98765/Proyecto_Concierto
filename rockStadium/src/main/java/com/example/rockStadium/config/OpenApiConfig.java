package com.example.rockStadium.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RockStadium API")
                        .version("1.0.0")
                        .description("API para búsqueda de conciertos, artistas y servicios cercanos a venues.\n\n" +
                                "**Características principales:**\n" +
                                "- 🎸 Búsqueda de artistas en Spotify\n" +
                                "- 🎫 Eventos en tiempo real desde Ticketmaster\n" +
                                "- 🏨 Hoteles cercanos a venues\n" +
                                "- 🚇 Opciones de transporte público\n" +
                                "- 🅿️ Estacionamientos cercanos\n" +
                                "- 🌤️ Información del clima\n" +
                                "- ⭐ Gestión de artistas y géneros favoritos")
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
                                .description("Servidor Local de Desarrollo"),
                        new Server()
                                .url("https://api.rockstadium.com")
                                .description("Servidor de Producción (cuando esté disponible)")
                ));
    }
}