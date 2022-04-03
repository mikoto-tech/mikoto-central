package net.mikoto.pixiv.central.controller;

import com.alibaba.fastjson.JSONObject;
import net.mikoto.pixiv.api.http.central.user.Login;
import net.mikoto.pixiv.api.http.central.user.Register;
import net.mikoto.pixiv.central.service.CaptchaService;
import net.mikoto.pixiv.central.service.UserService;
import net.mikoto.pixiv.central.service.impl.CaptchaServiceImpl;
import net.mikoto.pixiv.central.service.impl.UserServiceImpl;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;

import static net.mikoto.pixiv.api.http.HttpApi.*;
import static net.mikoto.pixiv.central.constant.Properties.MAIN_PROPERTIES;
import static net.mikoto.pixiv.central.util.RsaUtil.decrypt;
import static net.mikoto.pixiv.central.util.RsaUtil.getPrivateKey;

/**
 * @author mikoto
 * @date 2022/2/3 4:27
 */
@RestController
public class UserController implements Login, Register {
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final UserService USER_SERVICE = new UserServiceImpl();
    private static final CaptchaService CAPTCHA_SERVICE = new CaptchaServiceImpl();
    private static final String CAPTCHA = "captcha";
    private static final String TOKEN = "token";

    @RequestMapping(
            value = CENTRAL_USER + CENTRAL_PATCHER_LOGIN,
            method = RequestMethod.GET
    )
    @Override
    public JSONObject loginHttpApi(@NotNull HttpServletResponse response,
                                   @RequestParam String reCaptchaResponse,
                                   @RequestParam String userName,
                                   @RequestParam String userPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IllegalAccessException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // SetHeader
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // InitVariable
        JSONObject outputJsonObject = new JSONObject();

        if ("".equals(reCaptchaResponse) || "".equals(userName) || "".equals(userPassword)) {
            outputJsonObject.put(CAPTCHA, false);
            outputJsonObject.put(TOKEN, false);
            return outputJsonObject;
        }

        outputJsonObject.put(CAPTCHA, CAPTCHA_SERVICE.verifyCaptcha(OK_HTTP_CLIENT, reCaptchaResponse));

        if (outputJsonObject.getBoolean(CAPTCHA)) {
            // Decrypt userName & userPassword
            String decryptedUserName = decrypt(userName.replace("*", "=").replace("$", "+"), getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));
            String decryptedUserPassword = decrypt(userPassword.replace("*", "=").replace("$", "+"), getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));

            outputJsonObject.put(
                    TOKEN,
                    USER_SERVICE.login(
                            OK_HTTP_CLIENT,
                            MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL"),
                            MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_KEY"),
                            decryptedUserName,
                            decryptedUserPassword
                    )
            );
        } else {
            outputJsonObject.put(TOKEN, null);
        }

        response.addCookie(new Cookie(TOKEN, outputJsonObject.getString(TOKEN)));

        return outputJsonObject;
    }

    @RequestMapping(
            value = CENTRAL_USER + CENTRAL_USER_REGISTER,
            method = RequestMethod.GET
    )
    @Override
    public JSONObject registerHttpApi(@NotNull HttpServletResponse response,
                                      @RequestParam String reCaptchaResponse,
                                      @RequestParam String userName,
                                      @RequestParam String userPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // SetHeader
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // InitVariable
        JSONObject outputJsonObject = new JSONObject();

        if ("".equals(reCaptchaResponse) || "".equals(userName) || "".equals(userPassword)) {
            outputJsonObject.put(CAPTCHA, false);
            outputJsonObject.put("user", false);
            return outputJsonObject;
        }

        outputJsonObject.put(CAPTCHA, CAPTCHA_SERVICE.verifyCaptcha(OK_HTTP_CLIENT, reCaptchaResponse));

        if (outputJsonObject.getBoolean(CAPTCHA)) {
            // Decrypt userName & userPassword
            String decryptedUserName = decrypt(userName.replace("*", "=").replace("$", "+"), getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));
            String decryptedUserPassword = decrypt(userPassword.replace("*", "=").replace("$", "+"), getPrivateKey(MAIN_PROPERTIES.getProperty("RSA_PRIVATE_KEY")));

            outputJsonObject.put(
                    "user",
                    USER_SERVICE.register(
                            OK_HTTP_CLIENT,
                            MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_URL"),
                            MAIN_PROPERTIES.getProperty("PIXIV_DATABASE_KEY"),
                            MAIN_PROPERTIES.getProperty("NORMAL_USER_PROFILE_URL"),
                            decryptedUserName,
                            decryptedUserPassword,
                            SIMPLE_DATE_FORMAT
                    )
            );
        } else {
            outputJsonObject.put("user", null);
        }

        // output
        return outputJsonObject;
    }
}
