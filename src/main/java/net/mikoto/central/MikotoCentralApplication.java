package net.mikoto.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author mikoto
 */
@SpringBootApplication
@EntityScan("net.mikoto.oauth2.model")
public class MikotoCentralApplication {
    public static void main(String[] args) {
        SpringApplication.run(MikotoCentralApplication.class, args);
    }
}
