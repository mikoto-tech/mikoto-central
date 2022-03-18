package net.mikoto.pixiv.central.service.impl;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.api.service.central.CaptchaService;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;

/**
 * @author mikoto
 * @date 2022/2/19 1:15
 */
public class CaptchaServiceImpl implements CaptchaService {
    public static final String RE_CAPTCHA_CONFIRM_API = "https://www.google.com/recaptcha/api/siteverify";
    private static final MediaType FROM = MediaType.get("application/x-www-form-urlencoded;charset=utf-8");
    private static final String SUCCESS_KEY = "success";

    /**
     * Verify a captcha.
     *
     * @param okHttpClient      The ok http client.
     * @param reCaptchaResponse The response of re captcha.
     * @return Json result.
     */
    @Override
    public boolean verifyCaptcha(@NotNull OkHttpClient okHttpClient, String reCaptchaResponse) throws IOException {
        // Verify
        // build request
        String verifyRequestString = "secret=" +
                MAIN_PROPERTIES.getProperty("RECAPTCHA_KEY") +
                "&response=" +
                reCaptchaResponse;
        RequestBody body = RequestBody.create(verifyRequestString, FROM);
        Request verifyRequest = new Request.Builder()
                .url(RE_CAPTCHA_CONFIRM_API)
                .post(body)
                .build();
        // execute a call
        Response verifyResponse = okHttpClient.newCall(verifyRequest).execute();
        // load json
        JSONObject verifyResponseJson = JSONObject.parseObject(Objects.requireNonNull(verifyResponse.body()).string());

        return verifyResponseJson.getBoolean(SUCCESS_KEY);
    }
}
