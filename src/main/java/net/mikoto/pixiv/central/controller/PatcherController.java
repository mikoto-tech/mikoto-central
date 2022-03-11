package net.mikoto.pixiv.central.controller;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.central.service.PatcherService;
import net.mikoto.pixiv.central.service.impl.PatcherServiceImpl;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;

/**
 * @author mikoto
 * @date 2022/3/6 3:24
 */
@RestController
public class PatcherController {
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    PatcherService patcherService = new PatcherServiceImpl();

    @RequestMapping(
            value = "/patcher/login",
            method = RequestMethod.GET
    )
    public JSONObject loginPatcher(@NotNull HttpServletResponse response,
                                   @RequestParam @NotNull String address,
                                   @RequestParam @NotNull String userKey) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IOException {
        // SetHeader
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        return patcherService.login(
                OK_HTTP_CLIENT,
                MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL"),
                MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_KEY"),
                address,
                userKey
        );
    }
}
