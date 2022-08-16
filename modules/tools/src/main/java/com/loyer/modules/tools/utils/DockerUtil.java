package com.loyer.modules.tools.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.tools.entity.DockerEntity;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author kuangq
 * @title DockerUtil
 * @description docker操作工具类
 * @date 2021-11-19 17:22
 */
public class DockerUtil {

    private static final Logger logger = LoggerFactory.getLogger(DockerUtil.class);

    //docker服务默认端口号
    private static final int DEFAULT_PORT = 2375;

    /**
     * @param ip
     * @param port
     * @return com.github.dockerjava.api.DockerClient
     * @author kuangq
     * @description 获取docker客户端链接
     * @date 2021-11-19 17:29
     */
    private static DockerClient getDockerClient(String ip, int port) {
        String host = String.format("tcp://%s:%s", ip, port);
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).build();
        DockerHttpClient dockerHttpClient = (new JerseyDockerHttpClient.Builder()).dockerHost(dockerClientConfig.getDockerHost()).build();
        return DockerClientBuilder.getInstance().withDockerHttpClient(dockerHttpClient).build();
    }

    /**
     * @param dockerEntity
     * @return com.github.dockerjava.api.DockerClient
     * @author kuangq
     * @description 获取docker客户端链接
     * @date 2021-11-19 17:23
     */
    public static DockerClient getDockerClient(DockerEntity dockerEntity) {
        return getDockerClient(dockerEntity.getIp(), dockerEntity.getPort() == null ? DEFAULT_PORT : dockerEntity.getPort());
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 启动容器
     * @date 2021-11-19 18:04
     */
    public static void startContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, startContainerCmd);
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 停止容器
     * @date 2021-11-19 18:04
     */
    public static void stopContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, stopContainerCmd);
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 重启容器
     * @date 2021-11-19 18:04
     */
    public static void restartContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        RestartContainerCmd restartContainerCmd = dockerClient.restartContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, restartContainerCmd);
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 删除容器
     * @date 2021-11-19 18:04
     */
    public static void removeContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, removeContainerCmd);
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 复制容器内部文件
     * @date 2022-03-28 14:15
     */
    @SneakyThrows
    public static void copyFileByContainer(DockerEntity dockerEntity) {
        try (DockerClient dockerClient = getDockerClient(dockerEntity)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(dockerEntity.getLocalPath());
                 InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(dockerEntity.getContainerId(), dockerEntity.getRemotePath()).exec()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > -1) {
                    fileOutputStream.write(buffer, 0, length);
                }
            }
        }
    }

    /**
     * @param dockerEntity
     * @return void
     * @author kuangq
     * @description 拷贝文件到容器
     * @date 2022-03-28 18:12
     */
    @SneakyThrows
    public static void copyFileToContainer(DockerEntity dockerEntity) {
        try (DockerClient dockerClient = getDockerClient(dockerEntity); InputStream inputStream = new FileInputStream(dockerEntity.getLocalPath())) {
            dockerClient.copyArchiveToContainerCmd(dockerEntity.getContainerId()).withRemotePath(dockerEntity.getRemotePath()).withTarInputStream(inputStream).exec();
        }
    }

    /**
     * @param dockerEntity
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 容器执行cmd命令
     * @date 2022-04-06 10:44
     */
    @SneakyThrows
    public static ApiResult execCommand(DockerEntity dockerEntity) {
        try (DockerClient dockerClient = getDockerClient(dockerEntity)) {
            String cmdId = dockerClient.execCreateCmd(dockerEntity.getContainerId()).withCmd(dockerEntity.getCommand().split(SpecialCharsConst.SPACE)).withAttachStdout(true).withAttachStderr(true).exec().getId();
            try (OutputStream outputStream = new ByteArrayOutputStream();
                 OutputStream errorStream = new ByteArrayOutputStream()) {
                dockerClient.execStartCmd(cmdId).exec(new ExecStartResultCallback(outputStream, errorStream)).awaitCompletion();
                if (StringUtils.isNotBlank(errorStream.toString())) {
                    return ApiResult.failure(errorStream.toString().replaceAll(SpecialCharsConst.LINE_FEED, SpecialCharsConst.SPACE));
                }
                if (StringUtils.isBlank(outputStream.toString())) {
                    return ApiResult.success();
                }
                return ApiResult.success(outputStream.toString().split(SpecialCharsConst.LINE_FEED));
            }
        }
    }

    /**
     * @param syncDockerCmd
     * @return void
     * @author kuangq
     * @description 执行cmd
     * @date 2021-11-19 18:09
     */
    private static void execute(DockerClient dockerClient, SyncDockerCmd syncDockerCmd) {
        try {
            syncDockerCmd.exec();
        } catch (Exception e) {
            throw new BusinessException(HintEnum.HINT_1035, e);
        } finally {
            try {
                if (dockerClient != null) {
                    dockerClient.close();
                }
            } catch (Exception e) {
                logger.error("【dockerClient关闭异常】：{}", e.getMessage());
            }
        }
    }
}