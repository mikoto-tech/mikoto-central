package net.mikoto.central.controller;

import cn.dev33.satoken.stp.StpUtil;
import net.mikoto.central.dao.UserRepository;
import net.mikoto.pixiv.core.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/23
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Controller
public class OAuth2ServerController {
    @Value("${mikoto.pixiv.site-key}")
    private String siteKey;
    @Value("${mikoto.pixiv.rsa.public}")
    private String publicKey;
    private final UserRepository userRepository;

    @Autowired
    public OAuth2ServerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        if (StpUtil.isLogin()) {
            return "redirect:/profile";
        } else {
            model.addAttribute("siteKey", siteKey);
            model.addAttribute("publicKey", publicKey);
            return "register";
        }
    }

    @GetMapping("/login")
    public String login(@NotNull Model model) {
        if (StpUtil.isLogin()) {
            return "redirect:/profile";
        } else {
            model.addAttribute("siteKey", siteKey);
            model.addAttribute("publicKey", publicKey);
            return "login";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        if (StpUtil.isLogin()) {
            User user = userRepository.getUserByUserId(StpUtil.getLoginIdAsInt());
            model.addAttribute("user", user);
            return "profile";
        } else {
            model.addAttribute("siteKey", siteKey);
            model.addAttribute("publicKey", publicKey);
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
            return "redirect:/";
        } else {
            return "redirect:/login";
        }
    }
}
