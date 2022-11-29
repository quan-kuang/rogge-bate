package com.loyer.common.core.utils.encrypt;

import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密（两次密匙加密）
 *
 * @author kuangq
 * @date 2019-12-09 11:19
 */
public class Aes2Util {

    /**
     * 获取Cipher
     *
     * @author kuangq
     * @date 2019-12-09 14:07
     */
    @SneakyThrows
    private static Cipher getCipher(int cipherMode, String password) {
        IvParameterSpec zeroIv = new IvParameterSpec(StringUtil.decode(SystemConst.SECRET_KEY_AES));
        SecretKeySpec key = new SecretKeySpec(StringUtil.decode(password), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherMode, key, zeroIv);
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