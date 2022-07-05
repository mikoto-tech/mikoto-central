package net.mikoto.pixiv.central.service.impl;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.pixiv.central.client.RecaptchaClient;
import net.mikoto.pixiv.central.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * Created at 2022/7/5, 下午5:12
 * For pixiv-central
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    private final RecaptchaClient recaptchaClient;

    @Value("${mikoto.pixiv.secret}")
    private String secret;

    @Autowired
    public CaptchaServiceImpl(RecaptchaClient recaptchaClient) {
        this.recaptchaClient = recaptchaClient;
    }

    /**
     * Verify a captcha.
     *
     * @param response The response of the captcha.
     * @param remoteIp The remote ip of user.
     * @return The result.
     */
    @Override
    public boolean verifyCaptcha(String response, String remoteIp) {
        JSONObject resultJson = JSONObject.parseObject(recaptchaClient.verifyCaptcha(secret, response, remoteIp));
        return resultJson.getBooleanValue("success");
    }
}
