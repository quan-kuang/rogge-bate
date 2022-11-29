package com.loyer.common.core.utils.encrypt;

import com.loyer.common.core.entity.RsaKeyPair;
import com.loyer.common.dedicine.utils.Base64Util;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RAS非对称加密
 *
 * @author kuangq
 * @date 2021-11-12 14:11
 */
public class RsaUtil {

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmdctCQ9ttXtSqby35XGHBXU38Pdvu7qxjxRExa76kaD5ffa5M7Xgq3rTTPPnGYqBm7lCP7wY/aaGT1fqVUn0XX8EQVsoGCRi3Iz+coP4miHBn6SmrXDATn1IcAYtEXws7xGgXgSs+zDobz191052g7rgkdfl3lcE4JLA08KpHPQIDAQAB";

    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKZ1y0JD221e1KpvLflcYcFdTfw92+7urGPFETFrvqRoPl99rkzteCretNM8+cZioGbuUI/vBj9poZPV+pVSfRdfwRBWygYJGLcjP5yg/iaIcGfpKatcMBOfUhwBi0RfCzvEaBeBKz7MOhvPX3XTnaDuuCR1+XeVwTgksDTwqkc9AgMBAAECgYA738DVu/ywUVzjVQxPVlhfKgWEBmgWgEkHtfmiMiQnlMtio51PVFdV7DdUh+k733vcqjnWG96+bmtM9rmkfkA503iNrdVPBjzY8x0DsaOmG6D33GvN6XTIo89oWRCcy/uCYv+o9OpxgVuXrifAlL8P+uxp7BflRnuz68YmOMa9DQJBAOl8YB8tUejFUCHq4E+FciGX+APrIzIVSt01AoCHYW6x6n7GcAypqa8S817DafH74pR0UxsXG7Hzp6QJ7NBXfusCQQC2guGakUfTWjLYefyzCzfV/BLreHoC3wVv0+yMvolqFbi3vFkjY64+7Rb63K0qu8ndPd5F1DGTXTQn4U4kuNh3AkBrsg/QMaJHYMO+cU03wNDNCADBJfNdBY87i1j1GfzqByynfzZt0NBQzcft3OsAT/PKEAHJTBZdNYsM0fsmekUzAkB1dbJASfPR4CENFSU/DZ83xm1ewkC0DFhxahC5W/8QGT6ycTNlBUglE21Qsil4cTcvfhvJERF+5+MjL2udUqFtAkBrM1OjM/5yCgrK6NlZxm497ET0zFGmHdXlV7gP/Gtmcr7BCvGC/zqW42DK4ePlvmmn9ftq7rsU9ibfRdXPiCCu";

    /**
     * 获取密匙对，密钥大小为1024/2048位
     *
     * @author kuangq
     * @date 2021-11-12 20:42
     */
    @SneakyThrows
    public static RsaKeyPair getRsaKeyPair(int keySize) {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(keySize, new SecureRandom());
        return new RsaKeyPair(keyPairGen.generateKeyPair());
    }

    /**
     * 获取Cipher
     *
     * @author kuangq
     * @date 2021-11-12 16:02
     */
    @SneakyThrows
    private static Cipher getCipher(int cipherMode, String password) {
        byte[] encodedKey = Base64.getDecoder().decode(password);
        EncodedKeySpec encodedKeySpec = Cipher.DECRYPT_MODE == cipherMode ? new PKCS8EncodedKeySpec(encodedKey) : new X509EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key key = Cipher.DECRYPT_MODE == cipherMode ? keyFactory.generatePrivate(encodedKeySpec) : keyFactory.generatePublic(encodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(cipherMode, key);
        return cipher;
    }

    /**
     * 公匙加密
     *
     * @author kuangq
     * @date 2021-11-12 16:02
     */
    @SneakyThrows
    public static String encrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
        byte[] messageBytes = StringUtil.decode(message);
        byte[] encryptBytes = cipher.doFinal(messageBytes);
        return Base64Util.encode(encryptBytes);
    }

    /**
     * 私匙解密
     *
     * @author kuangq
     * @date 2021-11-12 16:02
     */
    @SneakyThrows
    public static String decrypt(String message, String password) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
        byte[] messageBytes = Base64Util.decode(message);
        byte[] decryptBytes = cipher.doFinal(messageBytes);
        return StringUtil.encode(decryptBytes);
    }

    /**
     * 加密
     *
     * @author kuangq
     * @date 2021-11-12 16:02
     */
    public static String encrypt(String message) {
        return encrypt(message, PUBLIC_KEY);
    }

    /**
     * 解密
     *
     * @author kuangq
     * @date 2021-11-12 16:02
     */
    public static String decrypt(String message) {
        return decrypt(message, PRIVATE_KEY);
    }
}