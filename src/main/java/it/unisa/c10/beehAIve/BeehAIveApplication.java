package it.unisa.c10.beehAIve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeehAIveApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeehAIveApplication.class, args);
	}

}
