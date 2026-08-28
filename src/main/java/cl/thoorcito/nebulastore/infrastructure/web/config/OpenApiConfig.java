package cl.thoorcito.nebulastore.infrastructure.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("NebulaStore API")
                .version("1.0")
                .description("API REST para NebulaStore, una tienda de impresion 3D: " +
                              "catalogo de productos (filamentos, maquinas y piezas " +
                              "personalizadas) y gestion de pedidos con validacion de stock.")
                .contact(new Contact().name("Thoorcito"))
                .license(new License().name("MIT License")));
    }
}