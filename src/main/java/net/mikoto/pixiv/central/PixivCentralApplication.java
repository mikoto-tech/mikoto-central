package net.mikoto.pixiv.central;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import static net.mikoto.pixiv.central.util.FileUtil.createDir;
import static net.mikoto.pixiv.central.util.FileUtil.createFile;

/**
 * @author mikoto
 */
@SpringBootApplication
public class PixivCentralApplication {
    public static final Properties PROPERTIES = new Properties();

    public static void main(String[] args) {
        try {
            createDir("config");
            createFile(new File("config/config.properties"), IOUtils.toString(Objects.requireNonNull(PixivCentralApplication.class.getClassLoader().getResourceAsStream("config.properties")), StandardCharsets.UTF_8));
            PROPERTIES.load(new FileReader("config/config.properties"));

            SpringApplication.run(PixivCentralApplication.class, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
