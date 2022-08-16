<!--suppress JSUnresolvedVariable -->
<template>
    <div class="server">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="getSreverInfo"/>
        </panel-title>
        <!--数据列表-->
        <el-row>
            <!--处理器-->
            <el-col :span="12" style="padding-right: 5px">
                <el-card>
                    <div slot="header"><span>CPU</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <thead>
                            <tr>
                                <th>属性</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>核心数</td>
                                <td>{{ server.processor ? server.processor.threadNum : '' }}</td>
                            </tr>
                            <tr>
                                <td> 用户使用率</td>
                                <td>{{ server.processor ? server.processor.userUsageRate + '%' : '' }}</td>
                            </tr>
                            <tr>
                                <td>系统使用率</td>
                                <td>{{ server.processor ? server.processor.systemUsageRate + '%' : '' }}</td>
                            </tr>
                            <tr>
                                <td>当前空闲率</td>
                                <td :class="{'text-danger': server.processor && server.processor.surplusRate < 15}">{{ server.processor ? server.processor.surplusRate + '%' : '' }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--内存-->
            <el-col :span="12" style="padding-left: 5px">
                <el-card>
                    <div slot="header"><span>内存</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <thead>
                            <tr>
                                <th>属性</th>
                                <th>内存</th>
                                <th>JVM</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>总内存</td>
                                <td>{{ server.memory ? server.memory.gross + 'G' : '' }}</td>
                                <td>{{ server.jvm ? server.jvm.gross + 'M' : '' }}</td>
                            </tr>
                            <tr>
                                <td>剩余内存</td>
                                <td>{{ server.memory ? server.memory.surplus + "G" : '' }}</td>
                                <td>{{ server.jvm ? server.jvm.surplus + 'M' : '' }}</td>
                            </tr>
                            <tr>
                                <td>已用内存</td>
                                <td>{{ server.memory ? server.memory.employ + "G" : '' }}</td>
                                <td>{{ server.jvm ? server.jvm.employ + 'M' : '' }}</td>
                            </tr>
                            <tr>
                                <td>使用率</td>
                                <td :class="{'text-danger': server.memory && server.memory.usageRate > 85}">{{ server.memory ? server.memory.usageRate + "%" : '' }}</td>
                                <td :class="{'text-danger': server.jvm && server.jvm.usageRate > 85}">{{ server.jvm ? server.jvm.usageRate + "%" : '' }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--服务器-->
            <el-col :span="24" style="padding-top: 10px">
                <el-card>
                    <div slot="header">
                        <span>服务器</span>
                    </div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>服务器名称</td>
                                <td>{{ server.computer ? server.computer.name : '' }}</td>
                                <td>操作系统</td>
                                <td>{{ server.computer ? server.computer.osName : '' }}</td>
                            </tr>
                            <tr>
                                <td>服务器IP</td>
                                <td>{{ server.computer ? server.computer.ip : '' }}</td>
                                <td>系统架构</td>
                                <td>{{ server.computer ? server.computer.osArchitecture : '' }}</td>
                            </tr>
                            <tr>
                                <td>项目路径</td>
                                <td colspan="3">{{ server.computer ? server.computer.projectPath : '' }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--虚拟机-->
            <el-col :span="24" style="padding-top: 10px">
                <el-card>
                    <div slot="header">
                        <span>虚拟机</span>
                    </div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>Java名称</td>
                                <td>{{ server.jvm ? server.jvm.name : '' }}</td>
                                <td>Java版本</td>
                                <td>{{ server.jvm ? server.jvm.version : '' }}</td>
                            </tr>
                            <tr>
                                <td>启动时间</td>
                                <td>{{ server.jvm ? server.jvm.startTime : '' }}</td>
                                <td>运行时长</td>
                                <td>{{ server.jvm ? server.jvm.runTime : '' }}</td>
                            </tr>
                            <tr>
                                <td colspan="1">安装路径</td>
                                <td colspan="3">{{ server.jvm ? server.jvm.home : '' }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--磁盘状态-->
            <el-col :span="24" style="padding-top: 10px">
                <el-card>
                    <div slot="header">
                        <span>磁盘状态</span>
                    </div>
                    <div class="el-table el-table-custom">
                        <table>
                            <thead>
                            <tr>
                                <th>盘符名称</th>
                                <th>盘符路径</th>
                                <th>盘符类型</th>
                                <th>总大小</th>
                                <th>可用大小</th>
                                <th>已用大小</th>
                                <th>已用百分比</th>
                            </tr>
                            </thead>
                            <tbody v-if="server.diskList">
                            <tr v-for="(item,index) in server.diskList" :key="index">
                                <td>{{ item.name }}</td>
                                <td>{{ item.path }}</td>
                                <td>{{ item.type }}</td>
                                <td>{{ item.gross }}G</td>
                                <td>{{ item.surplus }}G</td>
                                <td>{{ item.employ }}G</td>
                                <td :class="{'text-danger': item.usageRate > 80}">{{ item.usageRate }}%</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import {panelTitle} from '@assets/js/common/components';
import popup from '@assets/js/mixin/popup';
import {getSreverInfo} from '@assets/js/api/util';

export default {
    name: 'server',
    mixins: [popup],
    components: {
        panelTitle,
    },
    data() {
        return {
            // 服务器信息
            server: {},
        };
    },
    created() {
        this.getSreverInfo();
    },
    methods: {
        /* 获取服务器信息*/
        getSreverInfo() {
            let self = this;
            const loading = self.loadingOpen('.server');
            getSreverInfo().then((response) => {
                if (response.flag) {
                    self.server = response.data;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
    },
};
</script>
