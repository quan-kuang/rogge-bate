package com.loyer.common.core.utils.encrypt;

import com.loyer.common.dedicine.utils.Base64Util;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @author kuangq
 * @title Des3Util
 * @description DES3加解密工具类
 * @date 2020-06-21 13:50
 */
public class Des3Util {

    /**
     * @param cipherMode
     * @param password
     * @return javax.crypto.Cipher
     * @author kuangq
     * @description 获取Cipher
     * @date 2020-06-21 13:53
     */
    @SneakyThrows
    private static Cipher getCipher(int cipherMode, String password) {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        secureRandom.setSeed(StringUtil.decode(password));
        keyGenerator.init(secureRandom);
        Key key = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(cipherMode, key);
        return cipher;
    }

    /**
     * @param message
     * @param password
     * @return java.lang.String
     * @author kuangq
     * @description 加密
     * @date 2020-06-21 13:53
     */
    @SneakyThrows
    public static String encrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
        byte[] messageBytes = StringUtil.decode(message);
        byte[] encryptBytes = cipher.doFinal(messageBytes);
        return Base64Util.encode(encryptBytes);
    }

    /**
     * @param message
     * @param password
     * @return java.lang.String
     * @author kuangq
     * @description 解密
     * @date 2020-06-21 13:54
     */
    @SneakyThrows
    public static String decrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
        byte[] messageBytes = Base64Util.decode(message);
        byte[] decryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.encode(decryptBytes);
    }
}
