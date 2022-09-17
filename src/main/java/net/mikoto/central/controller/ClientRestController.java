package net.mikoto.central.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import net.mikoto.central.repository.ClientRepository;
import net.mikoto.central.service.CaptchaService;
import net.mikoto.oauth2.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static net.mikoto.central.util.RsaUtil.decrypt;
import static net.mikoto.central.util.RsaUtil.getPrivateKey;

/**
 * @author mikoto
 * {@code @time} 2022/9/18
 * Create for mikoto-central
 */
@RestController
@RequestMapping("/client")
public class ClientRestController {
    private static final String RE_CAPTCHA_RESPONSE = "reCaptchaResponse";
    private static final String CLIENT_NAME = "clientName";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String ALLOW_URL = "allowUrl";
    private static final String CONTRACT_SCOPE = "contractScope";
    private final CaptchaService captchaService;
    private final ClientRepository clientRepository;
    @Value("${mikoto.pixiv.rsa.private}")
    private String privateKey;
    @Value("${mikoto.pixiv.secret}")
    private String secret;

    @Autowired
    public ClientRestController(CaptchaService captchaService, ClientRepository clientRepository) {
        this.captchaService = captchaService;
        this.clientRepository = clientRepository;
    }

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8"
    )
    public JSONObject register(String jsonText) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        JSONObject outputJson = new JSONObject();
        JSONObject inputJson = JSONObject.parseObject(jsonText);

        if (StpUtil.isLogin()) {
            if (captchaService.verify(secret, inputJson.getString(RE_CAPTCHA_RESPONSE))) {
                Client client = new Client();
                client.setClientName(inputJson.getString(CLIENT_NAME));
                client.setClientSecret(decrypt(inputJson.getString(CLIENT_SECRET), getPrivateKey(privateKey)));
                client.setAllowUrl(inputJson.getString(ALLOW_URL));
                client.setContractScope(inputJson.getString(CONTRACT_SCOPE));
                clientRepository.saveAndFlush(client);
                outputJson.fluentPut("success", true);
                outputJson.fluentPut("msg", "");
            } else {
                outputJson.fluentPut("success", false);
                outputJson.fluentPut("msg", "Cannot verify your reCaptcha response.");
            }
        } else {
            outputJson.fluentPut("success", false);
            outputJson.fluentPut("msg", "Not login");
        }

        return outputJson;
    }

    @RequestMapping(
            value = "/get",
            produces = "application/json;charset=UTF-8"
    )
    public JSONObject get(String clientName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        JSONObject outputJson = new JSONObject();

        if (captchaService.verify(secret, inputJson.getString(RE_CAPTCHA_RESPONSE))) {
            Client client = new Client();
            client.setClientName(inputJson.getString(CLIENT_NAME));
            client.setClientSecret(decrypt(inputJson.getString(CLIENT_SECRET), getPrivateKey(privateKey)));
            client.setAllowUrl(inputJson.getString(ALLOW_URL));
            client.setContractScope(inputJson.getString(CONTRACT_SCOPE));
            clientRepository.saveAndFlush(client);
            outputJson.fluentPut("success", true);
            outputJson.fluentPut("msg", "");
        } else {
            outputJson.fluentPut("success", false);
            outputJson.fluentPut("msg", "Cannot verify your reCaptcha response.");
        }

        return outputJson;
    }
}
