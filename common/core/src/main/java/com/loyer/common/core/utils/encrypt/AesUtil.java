package com.loyer.common.core.utils.encrypt;

import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES加解密
 *
 * @author kuangq
 * @date 2020-02-20 16:48
 */
public class AesUtil {

    /**
     * 获取Cipher
     *
     * @author kuangq
     * @date 2020-02-20 17:02
     */
    @SneakyThrows
    private static Cipher getCipher(int cipherMode, String password) {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        secureRandom.setSeed(StringUtil.decode(password));
        keyGenerator.init(128, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytes = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(bytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMode, key);
        return cipher;
    }

    /**
     * AES加密
     *
     * @author kuangq
     * @date 2019-12-09 14:27
     */
    @SneakyThrows
    public static String encrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
        byte[] messageBytes = StringUtil.decode(message);
        byte[] encryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.hexEncode(encryptBytes);
    }

    /**
     * AES解密
     *
     * @author kuangq
     * @date 2019-12-09 14:27
     */
    @SneakyThrows
    public static String decrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
        byte[] messageBytes = StringUtil.hexDecode(message);
        byte[] decryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.encode(decryptBytes);
    }
}