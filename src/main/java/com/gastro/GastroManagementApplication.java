package com.gastro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GastroManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GastroManagementApplication.class, args);
    }
}