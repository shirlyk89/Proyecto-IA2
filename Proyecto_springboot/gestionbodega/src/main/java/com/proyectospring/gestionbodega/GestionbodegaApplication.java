package com.proyectospring.gestionbodega;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionbodegaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionbodegaApplication.class, args);

		// String hash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin123");
        // System.out.println("====== HASH GENERADO PARA admin123 ======");
       //  System.out.println("=========================================");
    
	}

}
