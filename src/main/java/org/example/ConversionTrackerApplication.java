package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConversionTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConversionTrackerApplication.class, args);
        System.out.println("It works!");
    }
}
