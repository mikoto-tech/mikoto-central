package net.mikoto.central.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/21
 */
public class RsaController {
    @Value("${mikoto.pixiv.rsa.public}")
    private String publicKey;

    @RequestMapping("/publicKey")
    public String getPublicKey() {
        return publicKey;
    }
}
