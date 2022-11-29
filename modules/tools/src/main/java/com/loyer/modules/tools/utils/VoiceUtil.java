package com.loyer.modules.tools.utils;

import com.loyer.common.dedicine.entity.ApiResult;

import java.io.File;

/**
 * 音频工具类
 *
 * @author kuangq
 * @date 2020-07-17 20:01
 */
public class VoiceUtil {

    /**
     * arm文件转mp3
     *
     * @author kuangq
     * @date 2020-07-19 9:29
     */
    public static ApiResult armToMp3(String amrPath, String mp3Path) {
        //获取arm文件
        File source = new File(amrPath);
        //创建mp3文件
        File target = new File(mp3Path);
        //文件类型转换
        it.sauronsoftware.jave.AudioUtils.amrToMp3(source, target);
        //noinspection ResultOfMethodCallIgnored 删除临时附件
        source.delete();
        return ApiResult.success();
    }
}