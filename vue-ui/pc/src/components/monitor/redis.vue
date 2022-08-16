<!--suppress JSUnresolvedVariable -->
<template>
    <div class="redis">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="getRedis"/>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="getJedis">
            <template v-if="isHasAllPermissions(['jedis:get'])">
                <el-input v-model.trim="host" placeholder="请输入ip:port" clearable/>
                <el-input v-model.trim="jedis.password" placeholder="请输入密码" clearable/>
                <el-button type="primary" icon="el-icon-search" @click="getJedis">查询</el-button>
            </template>
            <el-button type="warning" icon="el-icon-setting" @click="open" :disabled="Object.keys(config).length === 0" v-has-any-permissions="['jedis:set','redis:set']">设置</el-button>
        </div>
        <!--主界面-->
        <el-row>
            <!--概览-->
            <el-col :span="12" style="padding-right: 10px;">
                <el-card>
                    <div slot="header"><span>概览</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>版本</td>
                                <td>{{ info.redis_version }}</td>
                                <td>角色</td>
                                <td>{{ info.role }}</td>
                            </tr>
                            <tr>
                                <td>gcc版本</td>
                                <td>{{ info.gcc_version }}</td>
                                <td>模式</td>
                                <td>{{ info.redis_mode }}</td>
                            </tr>
                            <tr>
                                <td>从机数</td>
                                <td>
                                    <el-popover placement="right" width="500" trigger="click">
                                        <div v-if="slaveList.length===0" style="text-align: center">暂无数据</div>
                                        <div v-else class="el-table el-table-custom" style="max-height: 400px;overflow-y:auto;">
                                            <table>
                                                <thead class="fixed">
                                                <tr>
                                                    <th>序号</th>
                                                    <th>地址</th>
                                                    <th>状态</th>
                                                    <th>偏移量</th>
                                                    <th>滞后</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr v-for="(item,index) in slaveList" :key="index">
                                                    <td>{{ index + 1 }}</td>
                                                    <td>{{ item.ip }}:{{ item.port }}</td>
                                                    <td>{{ item.state }}</td>
                                                    <td>{{ item.offset }}</td>
                                                    <td>{{ formatBool(item.lag) }}</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                        <b slot="reference" style="cursor: pointer;color: #1991FA" @click="getSlaveList"> {{ info.connected_slaves }}</b>
                                    </el-popover>
                                </td>
                                <td>处理机制</td>
                                <td>{{ info.multiplexing_api }}</td>
                            </tr>
                            <tr>
                                <td>当前键总数</td>
                                <td>{{ db0.keys }}</td>
                                <td>会过期的键总数</td>
                                <td>{{ db0.expires }}</td>
                            </tr>
                            <tr>
                                <td>运行天数</td>
                                <td>{{ info.uptime_in_days }} {{ !info.uptime_in_days ? '' : '天' }}</td>
                                <td>平均存活时间</td>
                                <td>{{ formatSecond(db0.avg_ttl) }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--统计-->
            <el-col :span="12">
                <el-card>
                    <div slot="header"><span>统计</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>连接数</td>
                                <td>
                                    <el-popover placement="left-start" width="600" trigger="click">
                                        <div v-if="clientList.length===0" style="text-align: center">暂无数据</div>
                                        <div v-else class="el-table el-table-custom" style="max-height: 400px;overflow-y:auto;">
                                            <table>
                                                <thead class="fixed">
                                                <tr>
                                                    <th>序号</th>
                                                    <th>地址</th>
                                                    <th>链接时长</th>
                                                    <th>空闲时长</th>
                                                    <th>最后执行</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr v-for="(item,index) in clientList" :key="index">
                                                    <td>{{ index + 1 }}</td>
                                                    <td>{{ item.addressPort }}</td>
                                                    <td>{{ formatSecond(item.age) }}</td>
                                                    <td>{{ formatSecond(item.idle) }}</td>
                                                    <td>{{ item.lastCommand }}</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                        <b slot="reference" style="cursor: pointer;color: #1991FA" @click="getClientList"> {{ info.connected_clients }}</b>
                                    </el-popover>
                                </td>
                                <td>拒绝的连接数</td>
                                <td>{{ info.rejected_connections }}</td>
                            </tr>
                            <tr>
                                <td>命中数</td>
                                <td>{{ info.keyspace_hits }}</td>
                                <td>未命中数</td>
                                <td>{{ info.keyspace_misses }}</td>
                            </tr>
                            <tr>
                                <td>输入流量</td>
                                <td>{{ formatMemory(info.instantaneous_input_kbps) }}</td>
                                <td>输出流量</td>
                                <td>{{ formatMemory(info.instantaneous_output_kbps) }}</td>
                            </tr>
                            <tr>
                                <td>总输入流量</td>
                                <td>{{ formatMemory(info.total_net_input_bytes, 2) }}</td>
                                <td>总输出流量</td>
                                <td>{{ formatMemory(info.total_net_output_bytes, 2) }}</td>
                            </tr>
                            <tr>
                                <td>执行过的命令总数</td>
                                <td>{{ info.total_commands_processed }}</td>
                                <td>链接过的客户端总数</td>
                                <td>{{ info.total_connections_received }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--内存-->
            <el-col :span="12" style="padding-top: 10px;padding-right: 10px;">
                <el-card>
                    <div slot="header"><span>内存</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>淘汰策略</td>
                                <td>
                                    <el-tooltip placement="top" effect="light" :content="formatPolicy(!info.maxmemory_policy ? config['maxmemory-policy'] : info.maxmemory_policy)">
                                        <span style="cursor:pointer">  {{ !info.maxmemory_policy ? config['maxmemory-policy'] : info.maxmemory_policy }}</span>
                                    </el-tooltip>
                                </td>
                                <td>内存分配器</td>
                                <td>{{ info.mem_allocator }}</td>
                            </tr>
                            <tr>
                                <td>内存碎片率</td>
                                <td>{{ info.mem_fragmentation_ratio }}{{ !info.mem_fragmentation_ratio ? '' : '%' }}</td>
                                <td>最大分配内存</td>
                                <td>{{ !info.maxmemory_human ? (formatMemory(config.maxmemory, 2)) : info.maxmemory_human }}</td>
                            </tr>
                            <tr>
                                <td>内存使用量</td>
                                <td>{{ info.used_memory_human }}</td>
                                <td>内存使用峰值</td>
                                <td>{{ info.used_memory_peak_human }}</td>
                            </tr>
                            <tr>
                                <td>进程占用内存总量</td>
                                <td>{{ info.used_memory_rss_human }}</td>
                                <td>lua引擎内存使用量</td>
                                <td>{{ info.used_memory_lua_human }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--配置-->
            <el-col :span="12" style="padding-top: 10px;">
                <el-card>
                    <div slot="header"><span>配置</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>启动端口</td>
                                <td>{{ config.port }}</td>
                                <td>最大连接数</td>
                                <td>{{ config.maxclients }}</td>
                            </tr>
                            <tr>
                                <td>检测时间</td>
                                <td>{{ formatSecond(config['tcp-keepalive']) }}</td>
                                <td>日志级别</td>
                                <td>{{ config.loglevel }}</td>
                            </tr>
                            <tr>
                                <td>守护方式启动</td>
                                <td>{{ config.daemonize }}</td>
                                <td>混合备份模式</td>
                                <td>{{ config['aof-use-rdb-preamble'] }}</td>
                            </tr>
                            <tr>
                                <td>同步超时时间</td>
                                <td>{{ formatSecond(config['repl-timeout']) }}</td>
                                <td>链接闲置关闭时间</td>
                                <td>{{ formatSecond(config.timeout) }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--持久化-->
            <el-col :span="12" style="padding-top: 10px;padding-right: 10px;">
                <el-card>
                    <div slot="header"><span>持久化（RDB）</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>是否在加载持久化文件</td>
                                <td>{{ formatBool(info.loading) }}</td>
                                <td>是否在执行bgsave</td>
                                <td>{{ formatBool(info.rdb_bgsave_in_progress) }}</td>
                            </tr>
                            <tr>
                                <td>当前bgsave的耗时</td>
                                <td>{{ formatSecond(info.rdb_current_bgsave_time_sec) }}</td>
                                <td>上次bgsave的耗时</td>
                                <td>{{ formatSecond(info.rdb_last_bgsave_time_sec) }}</td>
                            </tr>
                            <tr>
                                <td>上次bgsave的状态</td>
                                <td>{{ info.rdb_last_bgsave_status }}</td>
                                <td>上次bgsave的时间</td>
                                <td>{{ formatDate(info.rdb_last_save_time) }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--持久化-->
            <el-col :span="12" style="padding-top: 10px;">
                <el-card>
                    <div slot="header"><span>持久化（AOF）</span></div>
                    <div class="el-table el-table-custom">
                        <table>
                            <tbody>
                            <tr>
                                <td>是否开启aof</td>
                                <td>{{ formatBool(info.aof_enabled) }}</td>
                                <td>是否在执行aof</td>
                                <td>{{ formatBool(info.aof_rewrite_in_progress) }}</td>
                            </tr>
                            <tr>
                                <td>当前aof的耗时</td>
                                <td>{{ formatSecond(info.aof_current_rewrite_time_sec) }}</td>
                                <td>上次aof的耗时</td>
                                <td>{{ formatSecond(info.aof_last_rewrite_time_sec) }}</td>
                            </tr>
                            <tr>
                                <td>上次aof重写操作的状态</td>
                                <td>{{ info.aof_last_bgrewrite_status }}</td>
                                <td>上次aof重写磁盘的状态</td>
                                <td>{{ info.aof_last_write_status }}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </el-card>
            </el-col>
            <!--占用内存-->
            <el-col :span="12" style="padding-top: 10px;padding-right: 10px;">
                <el-card>
                    <div slot="header"><span>占用内存</span></div>
                    <div class="chart" ref="memoryChart"/>
                </el-card>
            </el-col>
            <!--命令统计-->
            <el-col :span="12" style="padding-top: 10px">
                <el-card>
                    <div slot="header">
                        <span>命令统计</span>
                    </div>
                    <div class="chart" ref="commandStatsChart"/>
                </el-card>
            </el-col>
        </el-row>
        <!--配置列表-->
        <el-dialog class="add-file" title="配置信息" :visible.sync="isConfigDialog" :append-to-body="true" width="50%">
            <div class="el-table el-table-custom" style="height: 400px;overflow-y:auto;">
                <table>
                    <thead class="fixed">
                    <tr>
                        <th style="width: 5%">
                            <span :class="['el-checkbox__input', {'is-checked ': isCheckedAll}, {'is-disabled': Object.keys(configInfo).length === 0}]" @click="checkedAll()">
                                <span class="el-checkbox__inner"/>
                            </span>
                        </th>
                        <th style="width: 10%">序号</th>
                        <th style="width: 30%">属性</th>
                        <th style="width: 40%">值</th>
                        <th style="width: 15%">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(value, key, index)  in configInfo" :key="index" :style="{color:(excludeKeys.includes(key)?'rgb(202,205,212)':'')}">
                        <td>
                            <span :class="['el-checkbox__input checkbox-custom', {'is-checked ': isChecked.includes(key)}, {'is-disabled': excludeKeys.includes(key)}]" @click="checked(key)">
                                <span class="el-checkbox__inner"/>
                            </span>
                        </td>
                        <td>{{ index + 1 }}</td>
                        <td>{{ key }}</td>
                        <td>
                            <el-input v-if="isEdit[key] && !excludeKeys.includes(key)" v-model="configInfo[key]" placeholder="请输入属性值" clearable/>
                            <template v-else>{{ value }}</template>
                        </td>
                        <td>
                            <el-button size="mini" type="text" icon="el-icon-edit" @click="edit(key)" :disabled="excludeKeys.includes(key)">编辑</el-button>
                            <el-button size="mini" type="text" icon="el-icon-close" @click="cancel(key)">删除</el-button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div style="text-align: right;margin-top: 20px;">
                <el-button type="primary" @click="append">新增</el-button>
                <el-button type="info" @click="reset">重置</el-button>
                <el-button type="danger" @click="empty">清空</el-button>
                <el-button type="success" @click="setConfig">保存</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {panelTitle} from '@assets/js/common/components';
import popup from '@assets/js/mixin/popup';
import {mapGetters} from 'vuex';
import {getJedis, getRedis, setJedis, setRedis} from '@assets/js/api/cache';
import format from '@assets/js/util/format';
import verify from '@assets/js/util/verify';
import global from '@assets/js/util/global';

const echarts = require('echarts');

export default {
    name: 'redis',
    mixins: [popup],
    components: {
        panelTitle,
    },
    computed: {
        ...mapGetters(['isHasAllPermissions']),
    },
    data() {
        return {
            // 服务器信息
            info: {},
            // 数据库信息
            db0: {},
            // 配置信息
            config: {},
            // 客户端信息
            clientList: [],
            // 从机信息
            slaveList: [],
            // 链接地址
            host: '',
            // jedis链接对象
            jedis: {ip: '', port: '', password: ''},
            // 是否本机查询
            isLocal: true,
            // 是否可以编辑
            isEdit: {},
            // 是否选中
            isChecked: [],
            // 是否全选
            isCheckedAll: false,
            // 配置信息
            configInfo: {},
            // 配置信息窗口
            isConfigDialog: false,
            // 淘汰策略说明
            policyExplain: {
                'noeviction': '不删除key, 达到最大内存限时，继续操作直接返回错误信息',
                'allkeys-lru': '所有key通用，优先删除最近最少使用的key',
                'allkeys-random': '所有key通用，随机删除一部分key',
                'volatile-lru': '只限于设置了expire的key; 优先删除最近最少使用的key',
                'volatile-random': '只限于设置了expire的key， 随机删除一部分key',
                'volatile-ttl': '只限于设置了expire的key，优先删除剩余时间短的key',
            },
            // 排除无法修改的key
            excludeKeys: ['bind', 'logfile', 'slaveof', 'unixsocket', 'always-show-logo', 'appendfilename', 'cluster-enabled', 'daemonize', 'databases', 'io-threads', 'io-threads-do-reads', 'pidfile', 'port', 'rdbchecksum', 'supervised', 'syslog-enabled', 'syslog-facility', 'syslog-ident', 'tcp-backlog', 'unixsocketperm'],
        };
    },
    created() {
        this.getRedis();
    },
    methods: {
        /* 单位转换*/
        convert(value, number, unit) {
            if (!value) {
                return '';
            }
            return parseInt(value).cusDiv(Math.pow(number, unit)).cusToFixed(2);
        },
        /* 淘汰策略转换*/
        formatPolicy(key) {
            if (this.policyExplain.hasOwnProperty(key)) {
                return this.policyExplain[key];
            }
            return '未知';
        },
        /* 单位转换*/
        formatMemory(value, unit) {
            if (!value) {
                return '';
            }
            if (!unit) {
                return value + 'KB';
            }
            return this.convert(value, 1024, unit) + 'MB';
        },
        /* 时间秒转换*/
        formatSecond(value) {
            if (!value) {
                return '';
            }
            return value + 'S';
        },
        /* 布尔转换*/
        formatBool(value) {
            if (!value) {
                return '';
            }
            return Number(value) === 0 ? '否' : '是';
        },
        /* 时间戳转换*/
        formatDate(value) {
            if (!value) {
                return '';
            }
            return Number(value * 1000).toDate();
        },
        /* 字符串转map*/
        formatStr(value) {
            if (!value) {
                return {};
            }
            const result = {};
            const strAry = value.split(',');
            for (let str of strAry) {
                const item = str.split('=');
                let key = item[0];
                let value = item[1];
                if (['avg_ttl', 'usec', 'usec_per_call'].includes(key)) {
                    value = this.convert(value, 10, 3);
                }
                result[key] = value;
            }
            return result;
        },
        /* 获取redis信息*/
        getRedis() {
            this.isLocal = true;
            this.getInfo();
        },
        /* 获取基本信息*/
        getInfo() {
            let self = this;
            const loading = self.loadingOpen('.redis');
            const promise = self.isLocal ? getRedis('info') : getJedis('info', self.jedis);
            promise.then((response) => {
                if (response.flag) {
                    self.getConfig();
                    self.getCommandStats();
                    self.info = response.data;
                    self.db0 = self.formatStr(response.data.db0);
                } else {
                    self.info = {};
                    self.db0 = {};
                    self.$refs.memoryChart.childNodes.length > 0 && self.memoryChart.dispose();
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 获取配置信息*/
        getConfig() {
            let self = this;
            self.isEdit = {};
            self.isChecked = [];
            self.configInfo = {};
            const loading = self.loadingOpen('.redis');
            const promise = self.isLocal ? getRedis('config') : getJedis('config', self.jedis);
            promise.then((response) => {
                if (response.flag) {
                    self.config = format.sortObj(response.data);
                    self.drawMemory(response.data);
                } else {
                    self.config = {};
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 获取命令统计信息*/
        getCommandStats() {
            let self = this;
            const loading = self.loadingOpen('.redis');
            const params = {param: 'Commandstats'};
            const promise = self.isLocal ? getRedis('info', params) : getJedis('info', self.jedis, params);
            promise.then((response) => {
                if (response.flag) {
                    self.drawCommandStats(response.data);
                } else {
                    self.$refs.commandStatsChart.childNodes.length > 0 && this.commandStatsChart.dispose();
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 获取从机信息*/
        getSlaveList() {
            const slaves = this.info.connected_slaves || 0;
            const slaveList = [];
            for (let i = 0; i < slaves; i++) {
                slaveList.push(this.formatStr(this.info['slave' + i]));
            }
            this.slaveList = slaveList;
        },
        /* 获取客户端连接信息*/
        getClientList() {
            let self = this;
            self.clientList = [];
            const loading = self.loadingOpen('.redis');
            const promise = self.isLocal ? getRedis('clientList') : getJedis('clientList', self.jedis);
            promise.then((response) => {
                if (response.flag) {
                    self.clientList = global.arySort(response.data, ['addressPort', 'lastCommand']);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 绘制内存占用圆饼图*/
        drawMemory(data) {
            const option = {
                series: [{
                    // 类型
                    type: 'gauge',
                    // 仪表盘最大值
                    max: Math.ceil(this.convert(data.maxmemory, 1024, 2)),
                    // 半径
                    radius: 150,
                    // 仪表盘指针
                    pointer: {length: '80%', itemStyle: {color: 'inherit'}},
                    // 刻度标签
                    axisLabel: {color: 'inherit', distance: 40, fontSize: 15},
                    // 分隔线样式
                    splitLine: {distance: -30, length: 30, lineStyle: {color: '#fff', width: 2}},
                    // 刻度样式
                    axisTick: {distance: -30, length: 8, lineStyle: {color: '#fff', width: 1}},
                    // 轴线样式
                    axisLine: {lineStyle: {width: 30, color: [[0.3, 'rgb(145,199,174)'], [0.7, 'rgb(99,134,158)'], [1, 'rgb(194,53,49)']]}},
                    // 仪表盘详情
                    detail: {valueAnimation: true, formatter: '{value}M', color: 'inherit', width: 500},
                    // 数值
                    data: [{value: parseInt(this.info.used_memory).cusDiv(Math.pow(1024, 2)).cusToFixed(2)}],
                }],
            };
            if (this.$refs.memoryChart.childNodes.length > 0) {
                this.memoryChart.dispose();
            }
            this.memoryChart = echarts.init(this.$refs.memoryChart);
            this.memoryChart.setOption(option);
        },
        /* 绘制命令统计柱形图*/
        drawCommandStats(data) {
            const keys = [];
            const calls = [];
            const usec = [];
            const usec_per_call = [];
            for (let item in data) {
                const key = item.substr(8);
                if (['evalsha'].includes(key)) {
                    continue;
                }
                keys.push(key);
                const value = this.formatStr(data[item]);
                calls.push(value.calls);
                usec.push(value.usec);
                usec_per_call.push(value.usec_per_call);
            }
            const option = {
                tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
                legend: {data: ['次数', '总耗时', '平均耗时']},
                xAxis: {type: 'value'},
                yAxis: {type: 'category', data: keys},
                series: [{name: '次数', type: 'bar', stack: 'total', data: calls}, {name: '总耗时', type: 'bar', stack: 'total', data: usec}, {name: '平均耗时', type: 'bar', stack: 'total', data: usec_per_call}],
            };
            if (this.$refs.commandStatsChart.childNodes.length > 0) {
                this.commandStatsChart.dispose();
            }
            this.commandStatsChart = echarts.init(this.$refs.commandStatsChart);
            this.commandStatsChart.setOption(option);
        },
        /* 查询jedis信息*/
        getJedis() {
            if (verify.isBlank(this.host)) {
                this.messageError('请输入ip:port');
                return;
            }
            if (!verify.regularCheck(this.host, verify.host)) {
                this.messageError('ip:port格式有误');
                return;
            }
            this.isLocal = false;
            this.jedis.ip = this.host.split(':')[0];
            this.jedis.port = this.host.split(':')[1];
            this.getInfo();
        },
        /* 打开配置设置窗口*/
        open() {
            if (Object.keys(this.configInfo).length === 0) {
                this.configInfo = global.deepClone(this.config);
            }
            this.isConfigDialog = true;
        },
        /* config属性可编辑*/
        edit(key) {
            this.$set(this.isEdit, key, !this.isEdit[key]);
        },
        /* config删除指定属性*/
        cancel(key) {
            delete this.isEdit[key];
            delete this.configInfo[key];
            this.configInfo = Object.assign({}, this.configInfo);
            if (this.isChecked.includes(key)) {
                this.isChecked.splice(this.isChecked.indexOf(key), 1);
            }
            this.setCheckedAll();
        },
        /* 重置源config*/
        reset() {
            this.isEdit = {};
            this.isChecked = [];
            this.configInfo = global.deepClone(this.config);
        },
        /* 清空当前config*/
        empty() {
            this.isEdit = {};
            this.isChecked = [];
            this.configInfo = {};
            this.isCheckedAll = false;
        },
        /* config添加新属性*/
        append() {
            let self = this;
            self.$prompt('请输入键值', '', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputValidator: (value) => {
                    if (verify.isBlank(value)) {
                        return '键值不能为空';
                    }
                    if (self.configInfo.hasOwnProperty(value)) {
                        return '该属性已经存在';
                    }
                },
            }).then(({value}) => {
                const item = {[value]: ''};
                self.configInfo = Object.assign(item, self.configInfo);
                self.edit(value);
                self.isChecked.push(value);
                self.setCheckedAll();
            });
        },
        /* 设置config*/
        setConfig() {
            let self = this;
            const permissions = self.isLocal ? ['redis:set'] : ['jedis:set'];
            if (!self.isHasAllPermissions(permissions)) {
                self.messageError(`没有${permissions[0]}操作权限`);
                return;
            }
            // 只修改选中配置
            const config = {};
            for (const key of self.isChecked) {
                if (!self.excludeKeys.includes(key)) {
                    config[key] = self.configInfo[key];
                }
            }
            if (Object.keys(config).length === 0) {
                self.messageError('暂无选中的配置项');
                return;
            }
            const loading = self.loadingOpen('.redis');
            self.jedis.config = config;
            const promise = self.isLocal ? setRedis(config) : setJedis(self.jedis);
            promise.then((response) => {
                if (response.flag) {
                    let html = '';
                    const errorKeyInfo = {};
                    for (let key in response.data) {
                        const value = response.data[key];
                        if (!format.toBool(value)) {
                            errorKeyInfo[key] = '';
                            html += `<p> <b>${key}</b> ：${value}</p>`;
                        }
                    }
                    if (Object.keys(errorKeyInfo).length === 0) {
                        // 刷新
                        self.getInfo();
                        self.isConfigDialog = false;
                        self.messageSuccess(response.msg);
                        return;
                    }
                    // 弹窗提示
                    self.$alert(html, '提示', {
                        customClass: 'alertBox',
                        dangerouslyUseHTMLString: true,
                        showCancelButton: true,
                        confirmButtonText: '重设',
                        cancelButtonText: '取消',
                    }).then(() => {
                        self.isEdit = {};
                        self.configInfo = errorKeyInfo;
                        for (let key in errorKeyInfo) {
                            self.isEdit[key] = true;
                        }
                    }).catch(() => {
                        // 刷新
                        self.getInfo();
                        self.isConfigDialog = false;
                    });
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 全选反选*/
        checkedAll() {
            this.isCheckedAll = !this.isCheckedAll;
            if (this.isCheckedAll) {
                for (let key in this.configInfo) {
                    if (!this.excludeKeys.includes(key)) {
                        this.isChecked.push(key);
                    }
                }
            } else {
                this.isChecked = [];
            }
        },
        /* 选中操作*/
        checked(key) {
            if (this.isChecked.includes(key)) {
                this.isChecked.splice(this.isChecked.indexOf(key), 1);
            } else {
                this.isChecked.push(key);
            }
            this.setCheckedAll();
        },
        /* 设置全选样式*/
        setCheckedAll() {
            this.$nextTick(() => {
                const checkboxList = document.getElementsByClassName('checkbox-custom');
                for (let checkbox of checkboxList) {
                    if (!checkbox.classList.contains('is-checked') && !checkbox.classList.contains('is-disabled')) {
                        this.isCheckedAll = false;
                        return;
                    }
                }
                this.isCheckedAll = true;
            });
        },
    },
};
</script>

<style lang="scss">
    .alertBox {
        width: auto;
    }
</style>

<style lang="scss" scoped>
    .redis .chart {
        height: 300px;
    }

    .fixed {
        position: sticky;
        top: 0;
        z-index: 99;
    }

    //去掉表格最下面的那一条线
    .el-table::before {
        height: 0;
    }
</style>
