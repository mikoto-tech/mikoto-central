package net.mikoto.pixiv.central.controller;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.api.http.central.patcher.Login;
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

import static net.mikoto.pixiv.api.http.HttpApi.CENTRAL_PATCHER;
import static net.mikoto.pixiv.api.http.HttpApi.CENTRAL_PATCHER_LOGIN;
import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;

/**
 * @author mikoto
 * @date 2022/3/6 3:24
 */
@RestController
public class PatcherController implements Login {
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    PatcherService patcherService = new PatcherServiceImpl();

    @RequestMapping(
            value = CENTRAL_PATCHER + CENTRAL_PATCHER_LOGIN,
            method = RequestMethod.GET
    )
    @Override
    public JSONObject loginHttpApi(@NotNull HttpServletResponse response,
                                   @RequestParam String address,
                                   @RequestParam String userKey) {
        // SetHeader
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        JSONObject result = null;

        try {
            result = patcherService.login(
                    OK_HTTP_CLIENT,
                    MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL"),
                    MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_KEY"),
                    address,
                    userKey
            );
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
