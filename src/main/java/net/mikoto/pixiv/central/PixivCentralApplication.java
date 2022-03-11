package net.mikoto.pixiv.central;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.central.exception.DatabaseUrlException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;
import static net.mikoto.pixiv.central.util.FileUtil.createDir;
import static net.mikoto.pixiv.central.util.FileUtil.createFile;
import static net.mikoto.pixiv.central.util.RsaUtil.getKeyPair;

/**
 * @author mikoto
 */
@SpringBootApplication
public class PixivCentralApplication {
    private static final String AUTO = "auto";
    private static final String RSA_PUBLIC_KEY = "RSA_PUBLIC_KEY";
    private static final String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";
    private static final String SUCCESS = "success";

    public static void main(String[] args) {
        try {
            createDir("config");
            createFile(new File("config/config.properties"), IOUtils.toString(Objects.requireNonNull(PixivCentralApplication.class.getClassLoader().getResourceAsStream("config.properties")), StandardCharsets.UTF_8));
            MAIN_PROPERTIES.load(new FileReader("config/config.properties"));

            // Init key pair
            if (AUTO.equals(MAIN_PROPERTIES.getProperty(RSA_PUBLIC_KEY)) || AUTO.equals(MAIN_PROPERTIES.getProperty(RSA_PRIVATE_KEY))) {
                KeyPair keyPair = getKeyPair();
                MAIN_PROPERTIES.setProperty(RSA_PUBLIC_KEY, new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));
                MAIN_PROPERTIES.setProperty(RSA_PRIVATE_KEY, new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded())));
                System.out.println("Using temp keyPair:");
                System.out.println(RSA_PRIVATE_KEY + ":" + MAIN_PROPERTIES.getProperty(RSA_PRIVATE_KEY));
                System.out.println(RSA_PUBLIC_KEY + ":" + MAIN_PROPERTIES.getProperty(RSA_PUBLIC_KEY));
            }

            // Confirm database
            OkHttpClient okHttpClient = new OkHttpClient();
            String confirmDatabaseUrl = MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL") + "/database";
            Request confirmDatabaseRequest = new Request.Builder()
                    .url(confirmDatabaseUrl)
                    .get()
                    .build();
            // execute a call
            Response confirmDatabaseResponse = okHttpClient.newCall(confirmDatabaseRequest).execute();

            // load json
            JSONObject confirmDatabaseResponseJson;
            try {
                confirmDatabaseResponseJson = JSONObject.parseObject(Objects.requireNonNull(confirmDatabaseResponse.body()).string());
            } catch (JSONException e) {
                throw new DatabaseUrlException("Wrong database url.");
            }
            if (!confirmDatabaseResponseJson.getBoolean(SUCCESS)) {
                throw new DatabaseUrlException("Wrong database url.");
            }

            SpringApplication.run(PixivCentralApplication.class, args);
        } catch (IOException | DatabaseUrlException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
