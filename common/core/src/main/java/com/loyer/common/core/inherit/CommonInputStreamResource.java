package com.loyer.common.core.inherit;

import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * 重写InputStreamResource方法，基于RestTemplate使用InputStreamResource发送文件
 *
 * @author kuangq
 * @date 2020-06-16 11:34
 */
@SuppressWarnings({"unused"})
public class CommonInputStreamResource extends InputStreamResource {

    private final String fileName;

    private final long length;

    @SneakyThrows
    public CommonInputStreamResource(InputStream inputStream, String fileName) {
        super(inputStream);
        this.fileName = fileName;
        this.length = inputStream.available();
    }

    @Override
    public String getFilename() {
        return fileName;
    }

    @Override
    public long contentLength() {
        return length;
    }
}