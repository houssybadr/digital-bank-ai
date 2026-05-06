package ma.enset.ebankingbackend.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Digital Banking API",
        version = "1.0",
        description = "REST API for Digital Banking Application with AI Agent",
        contact = @Contact(name = "Dev Team", email = "dev@ebank.ma")
    )
)
public class OpenApiConfig {
}
