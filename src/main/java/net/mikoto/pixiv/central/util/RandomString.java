package net.mikoto.pixiv.central.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Get random string.
 *
 * @author yanglin
 * @modify mikoto
 */
public class RandomString {
    /**
     * Get random string
     *
     * @param length The length of the string.
     * @return The string.
     */
    public static @NotNull String getRandomString(int length) {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuilder.append(source.charAt(number));
        }
        return stringBuilder.toString();
    }

}