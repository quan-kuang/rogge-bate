package com.loyer.common.core.inherit;

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

    private String fileName;

    private int length;

    public CommonInputStreamResource(InputStream inputStream) {
        super(inputStream);
    }

    public CommonInputStreamResource(InputStream inputStream, String fileName) {
        super(inputStream);
        this.fileName = fileName;
    }

    public CommonInputStreamResource(InputStream inputStream, int length) {
        super(inputStream);
        this.length = length;
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