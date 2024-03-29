package net.mikoto.central.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import net.mikoto.central.repository.ClientRepository;
import net.mikoto.central.service.CaptchaService;
import net.mikoto.central.service.UserService;
import net.mikoto.oauth2.model.Client;
import net.mikoto.oauth2.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import static net.mikoto.central.util.RsaUtil.decrypt;
import static net.mikoto.central.util.RsaUtil.getPrivateKey;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/8/1
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@RestController
public class SaOAuth2ServerRestController {
    private static final String RE_CAPTCHA_RESPONSE = "reCaptchaResponse";
    private static final String USER_NAME = "userName";
    private static final String USER_PASSWORD = "userPassword";

    @Value("${mikoto.pixiv.site-key}")
    private String siteKey;
    @Value("    ${mikoto.pixiv.rsa.public}")
    private String publicKey;
    @Value("${mikoto.pixiv.rsa.private}")
    private String privateKey;
    @Value("${mikoto.pixiv.secret}")
    private String secret;

    private final CaptchaService captchaService;
    private final UserService userService;
    private final ClientRepository clientRepository;


    @Autowired
    public SaOAuth2ServerRestController(CaptchaService captchaService, UserService userService, ClientRepository clientRepository) {
        this.captchaService = captchaService;
        this.userService = userService;
        this.clientRepository = clientRepository;
    }

    @RequestMapping(
            value = "/oauth2/doRegister",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8"
    )
    public JSONObject register(@RequestBody String jsonText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeyException {
        JSONObject outputJson = new JSONObject();
        JSONObject inputJson = JSONObject.parseObject(jsonText);

        if (captchaService.verify(secret, inputJson.getString(RE_CAPTCHA_RESPONSE))) {
            userService.register(
                    decrypt(inputJson.getString(USER_NAME), getPrivateKey(privateKey)),
                    decrypt(inputJson.getString(USER_PASSWORD), getPrivateKey(privateKey))
            );
            outputJson.fluentPut("success", true);
            outputJson.fluentPut("msg", "");
        } else {
            outputJson.fluentPut("success", false);
            outputJson.fluentPut("msg", "Cannot verify your reCaptcha response.");
        }

        return outputJson;
    }

    @RequestMapping("/oauth2/*")
    public Object request() {
        return SaOAuth2Handle.serverRequest();
    }

    @Autowired
    public void setSaOauth2Config(@NotNull SaOAuth2Config config) {
        config
                .setNotLoginView(() -> {
                    ModelAndView modelAndView = new ModelAndView("login");
                    modelAndView.addObject("publicKey", publicKey);
                    modelAndView.addObject("siteKey", siteKey);
                    return modelAndView;
                })

                .setDoLoginHandle((encryptedUserName, encryptedUserRawPassword) -> {
                    try {
                        String reCaptchaResponse = SaHolder.getRequest().getParam("reCaptchaResponse");

                        if (captchaService.verify(secret, reCaptchaResponse)) {
                            String userName = decrypt(encryptedUserName, getPrivateKey(privateKey));
                            String userRawPassword = decrypt(encryptedUserRawPassword, getPrivateKey(privateKey));
                            User user = userService.get(userName);

                            if (user == null) {
                                return SaResult.error("No such a user!");
                            }

                            if (userService.verify(user, userRawPassword)) {
                                StpUtil.login(user.getUserId());
                                return SaResult.ok("Login success!").setData(StpUtil.getTokenValue());
                            } else {
                                return SaResult.error("Wrong password");
                            }
                        } else {
                            return SaResult.error("Verify your recaptcha response failed.");
                        }
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                             IllegalBlockSizeException | BadPaddingException | IOException |
                             InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                })

                .setConfirmView((clientId, scope) -> {
                    ModelAndView modelAndView = new ModelAndView("confirm");
                    Client client = clientRepository.getById(Integer.valueOf(clientId));
                    modelAndView.addObject("clientName", client.getClientName());
                    modelAndView.addObject("clientId", clientId);
                    modelAndView.addObject("scope", scope);
                    return modelAndView;
                })
        ;
    }

    @RequestMapping(
            "/oauth2/check"
    )
    public JSONObject checkScope(String token, @NotNull String scope) {
        JSONObject outputJsonObject = new JSONObject();
        String[] scopes = scope.split(";");
        Set<String> failedScopes = new HashSet<>();

        try {
            SaOAuth2Util.checkScope(token, scopes);
        } catch (SaOAuth2Exception e) {
            failedScopes.add(e.getMessage().replace("This Access-Token don't have scope：", ""));
        }

        outputJsonObject.fluentPut("failedScopes", failedScopes);

        return outputJsonObject;
    }
}
