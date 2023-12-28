package it.unisa.c10.beehAIve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class BeehAIveApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeehAIveApplication.class, args);
	}

}
