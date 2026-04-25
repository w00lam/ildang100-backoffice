package com.ildang100.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Ildang100BackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ildang100BackofficeApplication.class, args);
    }

}
