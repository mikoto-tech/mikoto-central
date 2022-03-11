package net.mikoto.pixiv.central.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;

/**
 * @author mikoto
 * @date 2022/2/20 13:07
 */
@RestController
public class WebController {
    @RequestMapping(
            value = "/web/publicKey",
            method = RequestMethod.GET
    )
    public String getPublicKey(@NotNull HttpServletResponse response) {
        // SetHeader
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        return MAIN_PROPERTIES.getProperty("RSA_PUBLIC_KEY");
    }
}
