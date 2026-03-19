package com.example.banvexe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BanvexeApplication {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
            .directory("./") 
            .ignoreIfMissing()
            .load();

	System.out.println(">>> CHECK GOOGLE ID: " + System.getProperty("GOOGLE_CLIENT_ID"));

    // Map vào System Properties
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

    SpringApplication.run(BanvexeApplication.class, args);
	}

}
