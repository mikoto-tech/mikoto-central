package net.mikoto.pixiv.central.service;

import okhttp3.OkHttpClient;

import java.io.IOException;

/**
 * @author mikoto
 * @date 2022/2/19 1:14
 */
public interface CaptchaService {
    /**
     * Verify a captcha.
     *
     * @param okHttpClient      The ok http client.
     * @param reCaptchaResponse The response of re captcha.
     * @return A boolean
     * @throws IOException An error.
     */
    boolean verifyCaptcha(OkHttpClient okHttpClient, String reCaptchaResponse) throws IOException;
}
