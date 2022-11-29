package com.loyer.modules.tools.utils;

import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.utils.common.CalculateUtil;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.VideoInfo;
import ws.schild.jave.info.VideoSize;
import ws.schild.jave.process.ProcessLocator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 视频工具类
 *
 * @author kuangq
 * @date 2020-11-04 10:19
 */
public class VideoUtil {

    private static final Logger logger = LoggerFactory.getLogger(VideoUtil.class);

    //媒体压缩大小阀值
    private static final int MAX_MEDIA_SIZE = 2;
    //媒体压缩每帧大小阀值
    private static final double MAX_FRAME_SIZE = 0.2;
    //压缩后媒体的最大比特率
    private static final int MAX_BIT_RATE = 100 * 1024;
    //压缩后媒体的最大采样率
    private static final int MAX_SAMPLING_RATE = 44100;
    //压缩后媒体的最大帧率
    private static final int MAX_FRAME_RATE = 20;
    //压缩后媒体的最大宽度
    private static final int MAX_WIDTH = 1280;

    /**
     * 视频压缩
     *
     * @author kuangq
     * @date 2020-11-04 14:53
     */
    @SneakyThrows
    public static ApiResult compressVideo(String sourcePath, String targetPath) {
        long startTime = System.currentTimeMillis();
        //获取源文件
        File source = new File(sourcePath);
        //获取媒体对象
        CustomFFMPEGLocator customFFMPEGLocator = new CustomFFMPEGLocator();
        MultimediaObject multimediaObject = new MultimediaObject(source, customFFMPEGLocator);
        //获取媒体大小，单位MB,
        double mediaSize = CalculateUtil.round(CalculateUtil.actuarial(source.length(), 1024 * 1024, CalculateType.DIVIDE), 2);
        //获取媒体播放时长，单位S
        double mediaDuration = CalculateUtil.round(CalculateUtil.actuarial(multimediaObject.getInfo().getDuration(), 1000, CalculateType.DIVIDE), 2);
        //获取媒体每帧大小，单位MB/S
        double frameSize = new BigDecimal(String.format("%.2f", mediaSize / mediaDuration)).doubleValue();
        //判断视频总大小和每帧大小超过阀值后进行压缩
        logger.info("视频大小({}MB)，视频时长({}S)，每帧大小({}MB/S)", mediaSize, mediaDuration, frameSize);
        if (mediaSize <= MAX_MEDIA_SIZE && frameSize <= MAX_FRAME_SIZE) {
            return ApiResult.success("无需压缩");
        }
        //TODO 获取音频对象
        AudioInfo audioInfo = multimediaObject.getInfo().getAudio();
        //创建音频属性
        AudioAttributes audioAttributes = new AudioAttributes();
        //设置音频编码格式
        audioAttributes.setCodec("aac");
        //设置声道数（1=单声道，2=双声道，0=默认）
        audioAttributes.setChannels(audioInfo.getChannels());
        //设置音量值，默认值为0，如果为256，则音量值不会改变
        audioAttributes.setVolume(256);
        //设置比特率，单位byte，比特率越高，音质越好
        if (audioInfo.getBitRate() > MAX_BIT_RATE) {
            audioAttributes.setBitRate(MAX_BIT_RATE);
        }
        //设置采样率，单位hz，采样率越高，声音还原度越好/文件越大
        if (audioInfo.getSamplingRate() > MAX_SAMPLING_RATE) {
            audioAttributes.setSamplingRate(MAX_SAMPLING_RATE);
        }
        //TODO 获取视频属性
        VideoInfo videoInfo = multimediaObject.getInfo().getVideo();
        //创建视频属性
        VideoAttributes videoAttributes = new VideoAttributes();
        //设置视频编码格式
        videoAttributes.setCodec("h264");
        //设置比特率，单位byte，比特率越高，清晰度越好
        if (videoInfo.getBitRate() > MAX_BIT_RATE) {
            videoAttributes.setBitRate(MAX_BIT_RATE);
        }
        //设置视频帧率，单位为每秒显示帧数，简称FPS或HZ，帧率越低，效果越差
        if (videoInfo.getFrameRate() > MAX_FRAME_RATE) {
            videoAttributes.setFrameRate(MAX_FRAME_RATE);
        }
        //设置视频分辨率
        int width = videoInfo.getSize().getWidth();
        int height = videoInfo.getSize().getHeight();
        if (width > MAX_WIDTH) {
            float diff = (float) height / width;
            int maxHeight = (int) (diff * MAX_WIDTH);
            videoAttributes.setSize(new VideoSize(MAX_WIDTH, maxHeight));
        }
        //TODO 编码压缩
        File target = new File(targetPath);
        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat("mp4");
        encodingAttributes.setAudioAttributes(audioAttributes);
        encodingAttributes.setVideoAttributes(videoAttributes);
        Encoder encoder = new Encoder(customFFMPEGLocator);
        encoder.encode(multimediaObject, target, encodingAttributes);
        logger.info("视频压缩耗时：{}s", DateUtil.getTdoa(startTime));
        //noinspection ResultOfMethodCallIgnored 删除源文件
        source.delete();
        return ApiResult.success();
    }

    public static class CustomFFMPEGLocator implements ProcessLocator {

        //jar包源码地址：https://github.com/a-schild/jave2
        private final String ffmpegPath;

        public CustomFFMPEGLocator() {
            String os = System.getProperty("os.name").toLowerCase();
            boolean isWindows = os.contains("windows");
            String parent = isWindows ? "E:/Temp/" : "/home/admin/tool/";
            String suffix = isWindows ? ".exe" : "";
            this.ffmpegPath = parent + "ffmpeg" + suffix;
            if (!isWindows) {
                try {
                    Runtime.getRuntime().exec(new String[]{"/bin/chmod", "755", this.ffmpegPath});
                } catch (IOException e) {
                    logger.error("Error setting executable via chmod：{}", e.getMessage());
                }
            }
        }

        @Override
        public String getExecutablePath() {
            return this.ffmpegPath;
        }
    }
}