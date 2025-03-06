package com.example.noweat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NoweatApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoweatApplication.class, args);
    }
}
