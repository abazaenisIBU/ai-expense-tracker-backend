package com.example.aiexpensetracker.rest.configuration.SwaggerConfiguration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AI Expense Tracker",
                version = "1.0.0",
                description = "Backend for AI Expense Tracker application. This application helps users manage their expenses effectively, categorize expenses using AI suggestions, and view detailed reports.",
                contact = @Contact(name = "AI Expense Tracker Support", email = "support@aiexpensetracker.com")
        ),
        servers = {
                @Server(url = "/", description = "Default Local Server URL")
        }
)

public class SwaggerConfiguration {

}