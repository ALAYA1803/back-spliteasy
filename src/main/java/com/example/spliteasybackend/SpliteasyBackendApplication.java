package com.example.spliteasybackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpliteasyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpliteasyBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner printSwaggerUrl() {
		return args -> System.out.println("\n🚀 Swagger disponible en: http://localhost:8080/swagger-ui/index.html\n");
	}
}
