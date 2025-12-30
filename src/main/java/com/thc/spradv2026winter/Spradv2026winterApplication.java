package com.thc.spradv2026winter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Spradv2026winterApplication {
    public static void main(String[] args) {
        SpringApplication.run(Spradv2026winterApplication.class, args);
    }
}
