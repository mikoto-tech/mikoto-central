package net.mikoto.pixiv.central.service;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;

/**
 * @author mikoto
 * @date 2022/2/19 1:04
 */
public interface UserService {
    /**
     * Register a user.
     *
     * @param okHttpClient     The ok http client.
     * @param url              The url of pixiv database.
     * @param userName         The name of the user.
     * @param userPassword     The password of the user(raw).
     * @param key              The key of pixiv database.
     * @param profile          normal profile of user.
     * @param simpleDateFormat A date format.
     * @return Is success.
     * @throws IOException An exception.
     */
    boolean register(OkHttpClient okHttpClient, String url, String key, String profile, String userName, String userPassword, SimpleDateFormat simpleDateFormat) throws IOException;

    /**
     * Login a user
     *
     * @param okHttpClient The ok http client.
     * @param url          The url of pixiv database.
     * @param userName     The name of the user.
     * @param userPassword The password of the user(raw).
     * @param key          The key of pixiv database.
     * @return Token
     * @throws IOException              An exception.
     * @throws NoSuchAlgorithmException An exception.
     * @throws InvalidKeySpecException  An exception.
     * @throws SignatureException       An exception.
     * @throws InvalidKeyException      An exception.
     * @throws IllegalAccessException   An exception.
     */
    String login(OkHttpClient okHttpClient, String url, String key, String userName, String userPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, IllegalAccessException;
}
