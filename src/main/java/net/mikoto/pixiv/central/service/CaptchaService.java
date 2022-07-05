package net.mikoto.pixiv.central.service;

/**
 * @author mikoto
 * Created at 2022/7/5, 下午5:12
 * For pixiv-central
 */
public interface CaptchaService {
    /**
     * Verify a captcha.
     *
     * @param response The response of the captcha.
     * @param remoteIp The remote ip of user.
     * @return The result.
     */
    boolean verifyCaptcha(String response, String remoteIp);
}
