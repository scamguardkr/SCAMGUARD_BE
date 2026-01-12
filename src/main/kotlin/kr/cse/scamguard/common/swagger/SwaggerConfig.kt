package kr.cse.scamguard.common.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class SwaggerConfig (
    @Value("\${swagger.server-url}")
    private val serverUrl: String
) {

    @Bean
    fun openApi(): OpenAPI {
        val server: Server = Server();
        server.url = serverUrl;

        val securityRequirement : SecurityRequirement = SecurityRequirement().addList("JWT")

        return OpenAPI()
            .info(configurationInfo())
            .addSecurityItem(securityRequirement)
            .components(securitySchemes())
            .addServersItem(server);
    }

    private fun securitySchemes(): Components {
        val securitySchemeAccessToken = SecurityScheme()
            .name("JWT")
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        return Components()
            .addSecuritySchemes("JWT", securitySchemeAccessToken)
    }

    private fun configurationInfo(): Info {
        return Info()
            .title("SCAMGUARD API")
            .description("SCAMGUARD API 문서입니다")
            .version(LocalDate.now().toString())
    }
}

