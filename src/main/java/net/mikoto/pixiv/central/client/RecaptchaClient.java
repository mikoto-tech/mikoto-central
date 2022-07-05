package net.mikoto.pixiv.central.client;

import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * Created at 2022/7/5, 下午5:07
 * For pixiv-central
 */
@Component
public interface RecaptchaClient {
    @Post("https://www.google.com/recaptcha/api/siteverify")
    String verifyCaptcha(@JSONBody("secret") String secret, @JSONBody("response") String response, @JSONBody("remoteip") String remoteIp);
}
