package net.mikoto.pixiv.central.controller;

import net.mikoto.pixiv.central.PixivCentralApplication;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author mikoto
 * @date 2022/2/3 4:27
 */
@RestController
public class UserRegisterController {
    private static final MediaType URLENCODED = MediaType.get("application/x-www-form-urlencoded; charset=UTF-8");

    @RequestMapping(
            value = "/user/register",
            method = RequestMethod.GET
    )
    public void userRegisterController(@NotNull HttpServletRequest request,
                                       @NotNull HttpServletResponse response,
                                       @RequestParam String server,
                                       @RequestParam String token) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String userAddr = request.getRemoteAddr();

        String verifyRequestString = "id=" +
                PixivCentralApplication.PROPERTIES.getProperty("ID") +
                "&secretkey=" +
                PixivCentralApplication.PROPERTIES.getProperty("SECRET_KEY") +
                "&scene=" +
                PixivCentralApplication.PROPERTIES.getProperty("REGISTER_SCENE") +
                "&token=" +
                token +
                "&ip=" +
                userAddr;
        System.out.println(verifyRequestString);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(verifyRequestString, URLENCODED);
        Request verifyRequest = new Request.Builder()
                .url(server)
                .post(body)
                .build();
        Response verifyResponse = client.newCall(verifyRequest).execute();
        out.println(Objects.requireNonNull(verifyResponse.body()).string());
    }
}
