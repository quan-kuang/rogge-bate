package com.loyer.modules.tools.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.DockerEntity;
import com.loyer.modules.tools.inherit.ExecStartCmdCallback;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * docker操作工具类
 *
 * @author kuangq
 * @date 2021-11-19 17:22
 */
public class DockerUtil {

    //docker服务默认端口号
    private static final int DEFAULT_PORT = 2375;

    /**
     * 获取docker客户端链接
     *
     * @author kuangq
     * @date 2021-11-19 17:29
     */
    @SneakyThrows
    private static DockerClient getDockerClient(DockerEntity dockerEntity) {
        String host = String.format("tcp://%s:%s", dockerEntity.getIp(), dockerEntity.getPort() == null ? DEFAULT_PORT : dockerEntity.getPort());
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).build();
        DockerHttpClient dockerHttpClient = new JerseyDockerHttpClient.Builder().dockerHost(dockerClientConfig.getDockerHost()).build();
        return DockerClientBuilder.getInstance().withDockerHttpClient(dockerHttpClient).build();
    }

    /**
     * 启动容器
     *
     * @author kuangq
     * @date 2021-11-19 18:04
     */
    public static void startContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, startContainerCmd);
    }

    /**
     * 停止容器
     *
     * @author kuangq
     * @date 2021-11-19 18:04
     */
    public static void stopContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, stopContainerCmd);
    }

    /**
     * 重启容器
     *
     * @author kuangq
     * @date 2021-11-19 18:04
     */
    public static void restartContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        RestartContainerCmd restartContainerCmd = dockerClient.restartContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, restartContainerCmd);
    }

    /**
     * 删除容器
     *
     * @author kuangq
     * @date 2021-11-19 18:04
     */
    public static void removeContainer(DockerEntity dockerEntity) {
        DockerClient dockerClient = getDockerClient(dockerEntity);
        RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(dockerEntity.getContainerId());
        execute(dockerClient, removeContainerCmd);
    }

    /**
     * 复制容器内部文件
     *
     * @author kuangq
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
     * 拷贝文件到容器
     *
     * @author kuangq
     * @date 2022-03-28 18:12
     */
    @SneakyThrows
    public static void copyFileToContainer(DockerEntity dockerEntity) {
        try (DockerClient dockerClient = getDockerClient(dockerEntity); InputStream inputStream = new FileInputStream(dockerEntity.getLocalPath())) {
            dockerClient.copyArchiveToContainerCmd(dockerEntity.getContainerId()).withRemotePath(dockerEntity.getRemotePath()).withTarInputStream(inputStream).exec();
        }
    }

    /**
     * 容器执行cmd命令
     *
     * @author kuangq
     * @date 2022-04-06 10:44
     */
    @SneakyThrows
    public static ApiResult execCommand(DockerEntity dockerEntity) {
        try (DockerClient dockerClient = getDockerClient(dockerEntity)) {
            String cmdId = dockerClient.execCreateCmd(dockerEntity.getContainerId()).withCmd(dockerEntity.getCommand().split(SpecialCharsConst.SPACE)).withAttachStdout(true).withAttachStderr(true).exec().getId();
            List<String> stdoutList = new ArrayList<>();
            List<String> stderrList = new ArrayList<>();
            dockerClient.execStartCmd(cmdId).exec(new ExecStartCmdCallback(stdoutList, stderrList)).awaitCompletion();
            if (!stderrList.isEmpty()) {
                return ApiResult.failure(stderrList);
            }
            if (stdoutList.isEmpty()) {
                return ApiResult.success();
            }
            return ApiResult.success(stdoutList);
        } catch (Exception e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * 执行cmd
     *
     * @author kuangq
     * @date 2021-11-19 18:09
     */
    private static <T> void execute(DockerClient dockerClient, SyncDockerCmd<T> syncDockerCmd) {
        try {
            syncDockerCmd.exec();
        } finally {
            try {
                if (dockerClient != null) {
                    dockerClient.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
}