package net.mikoto.pixiv.central.service.impl;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.central.service.PatcherService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;
import static net.mikoto.pixiv.central.util.RsaUtil.getPrivateKey;
import static net.mikoto.pixiv.central.util.RsaUtil.sign;

/**
 * @author mikoto
 * @date 2022/3/6 2:31
 */
public class PatcherServiceImpl implements PatcherService {
    private static final String SUCCESS_KEY = "success";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final long HOUR = 3600 * 1000;

    /**
     * Get the token.
     *
     * @param okHttpClient The ok http client.
     * @param url          The url of pixiv-database
     * @param userKey      The key of user.
     * @param address      The address of token.
     * @param key          The key of pixiv-database.
     * @return Token and pixiv-forward and pixiv-database server.
     * @throws NoSuchAlgorithmException An exception.
     * @throws InvalidKeySpecException  An exception.
     * @throws SignatureException       An exception.
     * @throws InvalidKeyException      An exception.
     */
    @Override
    public JSONObject login(@NotNull OkHttpClient okHttpClient, String url, String key, String address, String userKey) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IOException {
        JSONObject outputJson = new JSONObject();

        String loginUrl = url +
                "/central/getUserByUserKey?key=" + key +
                "&userKey=" + userKey;
        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .get()
                .build();
        Response loginResponse = okHttpClient.newCall(loginRequest).execute();
        JSONObject loginResponseJson = JSONObject.parseObject(Objects.requireNonNull(loginResponse.body()).string());
        if (loginResponseJson.getBoolean(SUCCESS_KEY)) {
            JSONObject patcherTokenJson = new JSONObject();
            patcherTokenJson.put("key", userKey);
            Date now = new Date();
            patcherTokenJson.put("publishTime", SIMPLE_DATE_FORMAT.format(now));
            patcherTokenJson.put("expireTime", SIMPLE_DATE_FORMAT.format(now.getTime() + 5 * HOUR));
            patcherTokenJson.put("publisher", "MIKOTO_PIXIV_CENTRAL|CODED BY MIKOTO|LIKE YOU FOREVER 林诗涵");
            String userTokenString = Base64.encodeBase64String(patcherTokenJson.toJSONString().getBytes(StandardCharsets.UTF_8));
            String sign = sign(userTokenString, getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));

            outputJson.put("token", userTokenString + "." + sign);
            outputJson.put("pixivDatabase", MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL"));
        }
        return outputJson;
    }
}
