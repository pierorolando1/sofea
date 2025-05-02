package com.example.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BibliotecaJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaJavaApplication.class, args);
	}

}
