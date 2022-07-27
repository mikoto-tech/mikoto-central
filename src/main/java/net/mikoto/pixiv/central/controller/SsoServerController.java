package net.mikoto.pixiv.central.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/23
 */
@Controller
public class SsoServerController {
    @Value("${mikoto.pixiv.site-key}")
    private String siteKey;
    @Value("${mikoto.pixiv.rsa.public}")
    private String publicKey;

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("siteKey", siteKey);
        model.addAttribute("publicKey", publicKey);
        return "register";
    }

    @GetMapping("/login")
    public String login(@NotNull Model model) {
        model.addAttribute("siteKey", siteKey);
        model.addAttribute("publicKey", publicKey);
        return "login";
    }
}
