package com.example.spliteasybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;  // Importar esta

@EnableJpaAuditing  // Activar auditoría JPA
@SpringBootApplication
public class SpliteasyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpliteasyBackendApplication.class, args);
	}

}
