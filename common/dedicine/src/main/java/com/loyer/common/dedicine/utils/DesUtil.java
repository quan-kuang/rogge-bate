package com.loyer.common.dedicine.utils;

import com.loyer.common.dedicine.constant.SystemConst;
import com.sun.crypto.provider.SunJCE;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;

/**
 * DES加解密
 *
 * @author kuangq
 * @date 2019-08-09 14:14
 */
public class DesUtil {

    /**
     * 获取Cipher
     *
     * @author kuangq
     * @date 2019-12-09 14:01
     */
    @SneakyThrows
    private static Cipher getCipher(int cipherMode) {
        Security.addProvider(new SunJCE());
        byte[] keyBytes = StringUtil.decode(SystemConst.SECRET_KEY);
        byte[] bytes = new byte[8];
        for (int i = 0; i < keyBytes.length && i < bytes.length; i++) {
            bytes[i] = keyBytes[i];
        }
        Key key = new SecretKeySpec(bytes, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(cipherMode, key);
        return cipher;
    }

    /**
     * DES加密
     *
     * @author kuangq
     * @date 2019-12-09 13:54
     */
    @SneakyThrows
    public static String encrypt(String message) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] messageBytes = StringUtil.decode(message);
        byte[] encryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.hexEncode(encryptBytes);
    }

    /**
     * DES解密
     *
     * @author kuangq
     * @date 2019-12-09 13:54
     */
    @SneakyThrows
    public static String decrypt(String message) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] messageBytes = StringUtil.hexDecode(message);
        byte[] decryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.encode(decryptBytes);
    }
}