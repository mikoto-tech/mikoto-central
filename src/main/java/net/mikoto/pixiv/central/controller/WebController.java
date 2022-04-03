package net.mikoto.pixiv.central.controller;

import net.mikoto.pixiv.api.http.central.web.PublicKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static net.mikoto.pixiv.api.http.HttpApi.CENTRAL_WEB;
import static net.mikoto.pixiv.api.http.HttpApi.CENTRAL_WEB_PUBLIC_KEY;
import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;

/**
 * @author mikoto
 * @date 2022/2/20 13:07
 */
@RestController
public class WebController implements PublicKey {
    @RequestMapping(
            value = CENTRAL_WEB + CENTRAL_WEB_PUBLIC_KEY,
            method = RequestMethod.GET
    )
    @Override
    public String publicKeyHttpApi(@NotNull HttpServletResponse response) {
        // SetHeader
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        return MAIN_PROPERTIES.getProperty("RSA_PUBLIC_KEY");
    }
}
