package net.mikoto.pixiv.central.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author https://www.cnblogs.com/jpfss/
 * @modify mikoto
 */
public class StringUtil {
    public static @NotNull String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
