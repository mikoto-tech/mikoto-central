package net.mikoto.pixiv.central.service.impl;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.api.pojo.User;
import net.mikoto.pixiv.api.service.central.UserService;
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
import static net.mikoto.pixiv.central.util.Sha256Util.getSha256;
import static net.mikoto.pixiv.central.util.StringUtil.getRandomString;

/**
 * @author mikoto
 * @date 2022/2/19 1:04
 */
public class UserServiceImpl implements UserService {
    private static final String SUCCESS_KEY = "success";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final long HOUR = 3600 * 1000;

    /**
     * Register a user.
     *
     * @param userName         The name of the user.
     * @param userPassword     The password of the user(raw).
     * @param url              The url of pixiv database.
     * @param key              The key of pixiv database.
     * @param profile          normal profile of user.
     * @param okHttpClient     The ok http client.
     * @param simpleDateFormat A date format.
     * @return Json result.
     * @throws IOException An exception.
     */
    @Override
    public boolean register(@NotNull OkHttpClient okHttpClient, String url, String key, String profile, String userName, String userPassword, @NotNull SimpleDateFormat simpleDateFormat) throws IOException {
        String salt = getRandomString(5);
        String saltyPassword = getSha256(userPassword + salt + "|MIKOTO_PIXIV_CENTRAL|CODED BY MIKOTO|LIKE YOU FOREVER 林诗涵|");
        String userKey = getSha256(userName + System.currentTimeMillis() + "|MIKOTO_PIXIV_CENTRAL|CODED BY MIKOTO|LIKE YOU FOREVER 林诗涵|");
        // build request
        String registerUrl = url +
                "/central/insertUser?key=" + key +
                "&userName=" + userName +
                "&userPassword=" + saltyPassword +
                "&userSalt=" + salt +
                "&userKey=" + userKey +
                "&profileUrl=" + profile +
                "&createTime=" + simpleDateFormat.format(new Date()) +
                "&updateTime=" + simpleDateFormat.format(new Date());
        Request registerRequest = new Request.Builder()
                .url(registerUrl)
                .get()
                .build();
        // execute a call
        Response registerResponse = okHttpClient.newCall(registerRequest).execute();
        JSONObject registerResponseJson = JSONObject.parseObject(Objects.requireNonNull(registerResponse.body()).string());
        return registerResponseJson.getBoolean(SUCCESS_KEY);
    }

    /**
     * Login a user
     *
     * @param okHttpClient The ok http client.
     * @param url          The url of pixiv database.
     * @param userName     The name of the user.
     * @param userPassword The password of the user(raw).
     * @param key          The key of pixiv database.
     * @return Token
     * @throws IOException              An exception.
     * @throws NoSuchAlgorithmException An exception.
     * @throws InvalidKeySpecException  An exception.
     * @throws SignatureException       An exception.
     * @throws InvalidKeyException      An exception.
     */
    @Override
    public String login(@NotNull OkHttpClient okHttpClient, String url, String key, String userName, String userPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IllegalAccessException {
        String loginUrl = url +
                "/central/getUserByUserName?key=" + key +
                "&userName=" + userName;
        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .get()
                .build();
        Response loginResponse = okHttpClient.newCall(loginRequest).execute();
        JSONObject loginResponseJson = JSONObject.parseObject(Objects.requireNonNull(loginResponse.body()).string());
        if (loginResponseJson.getBoolean(SUCCESS_KEY)) {
            User user = new User();
            user.loadJson(loginResponseJson.getJSONObject("body"));
            // Confirm user
            String saltyPassword = getSha256(userPassword + user.getUserSalt() + "|MIKOTO_PIXIV_CENTRAL|CODED BY MIKOTO|LIKE YOU FOREVER 林诗涵|");
            if (saltyPassword.equals(user.getUserPassword())) {
                // Return token if success.
                JSONObject userTokenJson = new JSONObject();
                userTokenJson.put("userName", user.getUserName());
                userTokenJson.put("userId", user.getId());
                Date now = new Date();
                userTokenJson.put("publishTime", SIMPLE_DATE_FORMAT.format(now));
                userTokenJson.put("expireTime", SIMPLE_DATE_FORMAT.format(now.getTime() + 5 * HOUR));
                userTokenJson.put("publisher", "MIKOTO_PIXIV_CENTRAL|CODED BY MIKOTO|LIKE YOU FOREVER 林诗涵");
                String userTokenString = Base64.encodeBase64String(userTokenJson.toJSONString().getBytes(StandardCharsets.UTF_8));
                String sign = sign(userTokenString, getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));
                return userTokenString + "." + sign;
            } else {
                return null;
            }
        }
        return null;
    }
}
