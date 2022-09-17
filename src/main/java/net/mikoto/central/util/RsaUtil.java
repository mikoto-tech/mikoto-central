package net.mikoto.central.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author mikoto
 * @date 2022/4/23 2:23
 */
public final class RsaUtil {
    /**
     * RSA max encrypt block.
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA max decrypt block.
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * Get a key pair.
     *
     * @return The key pair.
     */
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    /**
     * Get the private key.
     *
     * @param privateKey The private key string.
     * @return Private key.
     */
    public static PrivateKey getPrivateKey(@NotNull String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Get the public key.
     *
     * @param publicKey The public key string.
     * @return Public key.
     */
    public static PublicKey getPublicKey(@NotNull String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA encrypt.
     *
     * @param data      The data need to encrypt.
     * @param publicKey The public key.
     * @return The encrypted data.
     */
    @Contract("_, _ -> new")
    public static @NotNull String encrypt(@NotNull String data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // Encrypt data segments.
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // The encrypted content is encoded using base64 and converted into a string using UTF-8 as the standard.
        // encrypted string.
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA decrypt.
     *
     * @param data       The data need to decrypt.
     * @param privateKey The private key.
     * @return Data
     */
    public static String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // Decrypt data segments.
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        out.close();
        // Decrypted content.
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * RSA sign
     *
     * @param data       The data need to sign.
     * @param privateKey The private key.
     * @return The sign.
     */
    @Contract("_, _ -> new")
    public static @NotNull String sign(@NotNull String data, @NotNull PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * RSA verify sign.
     *
     * @param rawData   The raw data.
     * @param publicKey The public key.
     * @param sign      The sign.
     * @return Is success.
     */
    public static boolean verify(@NotNull String rawData, @NotNull PublicKey publicKey, @NotNull String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(rawData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }
}
