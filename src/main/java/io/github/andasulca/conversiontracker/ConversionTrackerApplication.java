package io.github.andasulca.conversiontracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConversionTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversionTrackerApplication.class, args);
        System.out.println("It works!");
    }
}
