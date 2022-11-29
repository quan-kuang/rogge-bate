package com.loyer.modules.tools.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 集群节点
 *
 * @author kuangq
 * @date 2022-01-05 10:28
 */
@Data
@NoArgsConstructor
public class ClusterNode {

    //节点id
    private String id;

    //ip地址
    private String ip;

    //端口号
    private Integer port;

    //密码
    private String password;

    //状态
    private String status;

    //存在
    private Boolean isExist;

    //容器id
    private String containerId;

    //链接状态
    private Boolean isConnect;

    public ClusterNode(String ip, Integer port, String status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
    }
}