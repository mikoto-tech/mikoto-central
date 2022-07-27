package net.mikoto.pixiv.central.service;

import java.io.IOException;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/21
 */
public interface CaptchaService {
    /**
     * Verify the response.
     *
     * @param secret            The secret.
     * @param reCaptchaResponse The response.
     * @return
     */
    boolean verify(String secret, String reCaptchaResponse) throws IOException;
}
