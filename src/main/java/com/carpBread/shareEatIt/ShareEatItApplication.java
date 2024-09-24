package com.carpBread.shareEatIt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShareEatItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareEatItApplication.class, args);
	}

}
