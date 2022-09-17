package net.mikoto.central.service.impl;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.central.service.CaptchaService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/21
 */
@Service("captchaService")
public class CaptchaServiceImpl implements CaptchaService {
    public static final String RE_CAPTCHA_CONFIRM_API = "https://www.google.com/recaptcha/api/siteverify";
    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final MediaType FROM = MediaType.get("application/x-www-form-urlencoded;charset=utf-8");
    private static final String SUCCESS_KEY = "success";

    @Override
    public boolean verify(String secret, String reCaptchaResponse) throws IOException {
        String verifyRequestString = "secret=" +
                secret +
                "&response=" +
                reCaptchaResponse;
        RequestBody body = RequestBody.create(verifyRequestString, FROM);
        Request verifyRequest = new Request.Builder()
                .url(RE_CAPTCHA_CONFIRM_API)
                .post(body)
                .build();
        // execute a call
        Response verifyResponse = OK_HTTP_CLIENT.newCall(verifyRequest).execute();
        // load json
        JSONObject verifyResponseJson = JSONObject.parseObject(Objects.requireNonNull(verifyResponse.body()).string());
        verifyResponse.close();
        return verifyResponseJson.getBooleanValue(SUCCESS_KEY);
    }
}
