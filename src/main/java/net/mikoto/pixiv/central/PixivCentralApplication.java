package net.mikoto.pixiv.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author mikoto
 */
@SpringBootApplication
@EntityScan("net.mikoto.pixiv.core.model")
public class PixivCentralApplication {
    public static void main(String[] args) {
        SpringApplication.run(PixivCentralApplication.class, args);
    }
}
