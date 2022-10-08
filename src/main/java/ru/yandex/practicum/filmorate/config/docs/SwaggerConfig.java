package ru.yandex.practicum.filmorate.config.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI dataOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Filmorate")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .email("difirton@yandex.ru")
                                                .url("https://github.com/Difirton")
                                                .name("Dmitriy Kruglov")
                                )
                );
    }
}
