package net.mikoto.pixiv.central.service;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author mikoto
 * @date 2022/2/19 9:05
 */
public interface PatcherService {
    /**
     * Get the token.
     *
     * @param okHttpClient The ok http client.
     * @param url          The url of pixiv-database
     * @param userKey      The key of user.
     * @param address      The address of token.
     * @param key          The key of pixiv-database.
     * @return Token and pixiv-forward and pixiv-database server.
     * @throws NoSuchAlgorithmException An exception.
     * @throws InvalidKeySpecException  An exception.
     * @throws SignatureException       An exception.
     * @throws InvalidKeyException      An exception.
     * @throws IOException              An exception.
     */
    JSONObject login(OkHttpClient okHttpClient, String url, String key, String address, String userKey) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IOException;
}
