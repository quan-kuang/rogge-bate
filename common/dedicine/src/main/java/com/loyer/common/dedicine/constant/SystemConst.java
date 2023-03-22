package com.loyer.common.dedicine.constant;

import com.loyer.common.dedicine.utils.GeneralUtil;

/**
 * 系统常量
 *
 * @author kuangq
 * @date 2019-11-21 12:04
 */
public interface SystemConst {

    //系统版本号
    String VERSION = "2.0";

    //私人密匙key值
    String LOYER = "loyer";

    //私人密匙value值
    String SECRET_KEY = "931851631";

    //认证token的key值
    String TOEKN_KEY = "token";

    //签名过期时间，五分钟
    Long SIGN_EXPIRE_TIME = 300000L;

    //AES加密密匙
    String SECRET_KEY_AES = "o1c2e3a4n5s6o7ft";

    //小写数组
    Character[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    //管理员账号
    String ADMIN = "admin";

    //windows操作系统
    String WINDOWS = "windows";

    //linux操作系统
    String LINUX = "linux";

    //mac操作系统
    String MAC = "mac";

    //FTP文件服务器地址
    String FTP_SERVER_PATH = "/home/admin/file/";

    //当前服务器的本地存储地址
    String LOCAL_FILE_PATH = (SystemConst.WINDOWS.equals(GeneralUtil.getOs()) ? "D:" : "") + FTP_SERVER_PATH;

    //vue项目地址
    String VUE_PROJECT_PATH = "/home/admin/project/vue/";

    //用户信息缓存时长
    Integer USER_EXPIRE_TIME = 3600 * 24;

    //拼图验证码允许偏差
    Integer ALLOW_DEVIATION = 3;

    //公众号的ID
    String WECHAT_ID = "gh_dba47d12d20a";

    //我的微信openid
    String MY_OPEN_ID = "or7WYt-vdrGynNLxFidKr2-Mmggc";

    //小宝的微信openid
    String BAO_OPEN_ID = "or7WYt5P62MmL3Un3ecWgENFqFTQ";
}