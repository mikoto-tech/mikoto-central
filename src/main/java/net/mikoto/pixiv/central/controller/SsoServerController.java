package net.mikoto.pixiv.central.controller;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoHandle;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import net.mikoto.pixiv.central.service.CaptchaService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author mikoto
 * Created at 2022/7/5, 下午4:34
 * For pixiv-central
 */
@RestController
public class SsoServerController {
    private final CaptchaService captchaService;
    @Value("${mikoto.pixiv.rsa.private}")
    private String privateKey;
    @Value("${mikoto.pixiv.rsa.public}")
    private String publicKey;

    public SsoServerController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @RequestMapping("/login")
    public String login() throws IOException {
        String login = IOUtils.toString(Objects.requireNonNull(SsoServerController.class.getClassLoader().getResourceAsStream("login.html")), StandardCharsets.UTF_8);
        login = login.replace("${publicKey}", publicKey);
        return login;
    }

    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoHandle.serverRequest();
    }

    /**
     * 配置SSO相关参数
     */
    @Autowired
    private void configSso(@NotNull SaSsoConfig sso) {
        // 配置：登录处理函数
        sso.setDoLoginHandle((name, pwd) -> {
            // 此处仅做模拟登录，真实环境应该查询数据进行登录
            if ("sa".equals(name) && "123456".equals(pwd)) {
                StpUtil.login(10001);
                return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
            }
            return SaResult.error("登录失败！");
        });
    }
}
