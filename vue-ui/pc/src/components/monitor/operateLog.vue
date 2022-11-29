<template>
    <div class="operate-log">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh('')"/>
            <el-button type="danger" icon="el-icon-delete" @click="deleteConfirm(dataList)" v-has-all-permissions="['operateLog:delete']">清空</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.operate-log')" v-has-all-permissions="['operateLog:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent">
            <el-select v-model="status" clearable @change="refresh">
                <el-option label="全部" value=""/>
                <el-option label="成功" value="true"/>
                <el-option label="失败" value="false"/>
            </el-select>
            <el-input v-model="queryText" placeholder="请输入IP/标题/方法名/操作人" clearable prefix-icon="el-icon-search"/>
            <el-date-picker v-model="startTime" type="datetime" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" default-time="00:00:00" :picker-options="pickerOptions" placeholder="请选择开始时间"/>
            <el-date-picker v-model="endTime" type="datetime" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" default-time="23:59:59" :picker-options="pickerOptions" placeholder="请选择结束时间"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
            <el-button :type="isElasticsearch?'success':'info'" @click="isElasticsearch=!isElasticsearch" @contextmenu.prevent.native="setEsMode">{{ isElasticsearch ? 'ES开启' : 'ES关闭' }}</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :row-class-name="setRowClass">
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="serverIp" label="服务端IP" align="center" show-overflow-tooltip/>
            <el-table-column prop="clientIp" label="客户端IP" align="center" show-overflow-tooltip/>
            <el-table-column prop="type" label="请求类型" align="center" width="80"/>
            <el-table-column prop="path" label="类名称" align="center" show-overflow-tooltip :formatter="formatCut"/>
            <el-table-column prop="method" label="方法名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="title" label="事件标题" align="center" show-overflow-tooltip/>
            <el-table-column prop="creatorName" label="操作人" align="center"/>
            <el-table-column prop="createDeptId" label="部门" align="center" :formatter="formatDept"/>
            <el-table-column prop="createTime" label="创建时间" align="center" width="180" :formatter="formatDate"/>
            <el-table-column prop="status" label="状态" align="center" width="90">
                <template slot-scope="scope">
                    <span>{{ format.decode(scope.row.status, true, '成功', false, '失败', '-') }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="elapsedTime" label="耗时(S)" align="center" width="90"/>
            <el-table-column label="操作" align="center" fixed="right" :width="isHasAllPermissions(['operateLog:delete'])?'120':'100'" v-if="isHasAllPermissions(['operateLog:details'])">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-view" @click="showDetails(scope.row)">详情</el-button>
                    <el-button size="mini" type="text" icon="el-icon-close" @click="deleteConfirm(scope.row)" v-has-all-permissions="['operateLog:delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页控件 -->
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="dataTotal>10"
                       :page-sizes='pageSizes' :total="dataTotal" :page-size="pageSize" :current-page="pageNum"
                       @size-change="sizeChange" @current-change="currentChange"/>
        <!-- 日志详情 -->
        <el-dialog title="日志详情" :visible.sync="isShowDetails">
            <el-form :model="details" label-width="80px" size="mini" label-suffix="">
                <el-form-item label="请求地址">{{ details.url }}</el-form-item>
                <el-form-item label="文件路径">{{ details.path }}</el-form-item>
                <el-form-item label="请求入参">
                    <codemirror :value="details.inArgs" class="code" :options="inOptions"/>
                </el-form-item>
                <el-form-item label="响应出参">
                    <codemirror :value="details.outArgs" class="code" :options="outOptions"/>
                </el-form-item>
                <el-form-item label="执行详情" v-if="details.consoleLog && details.consoleLog.length > 0">
                    <codemirror :value="details.consoleLog" class="code" :options="outOptions"/>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
import format from '@assets/js/util/format';
import dept from '@components/dept/js/mixin';
import global from '@assets/js/util/global';
// 核心组件和样式
import {codemirror} from 'vue-codemirror';
import 'codemirror/lib/codemirror.css';
// 引入主题
import 'codemirror/theme/monokai.css';
import 'codemirror/theme/blackboard.css';
// 语言模型
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/javascript/javascript';
// 语言提示的核心文件
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint.js';
import {deleteOperateLog, selectOperateArgs, selectOperateLog, selectOperateLogEs} from '@assets/js/api/log';
import common from '@assets/js/mixin/common';

export default {
    name: 'operateLog',
    mixins: [dept, common],
    components: {
        codemirror,
    },
    data() {
        return {
            format: format,
            // 开始时间
            startTime: '',
            // 结束时间
            endTime: '',
            // 操作状态
            status: '',
            // 部门列表
            deptList: [],
            // 详情内容
            details: {},
            // 是否显示详情
            isShowDetails: false,
            // ES查询方式，默认DSL，否则API
            mode: false,
            // 是否从ES查询
            isElasticsearch: true,
            // 限制选择时间不能大于今天不能小于去年
            pickerOptions: {
                disabledDate: (date) => {
                    const now = new Date();
                    const lastYear = new Date().setFullYear(now.getFullYear() - 1);
                    return lastYear > date || date > new Date();
                },
            },
            // 输入参数样式
            inOptions: {
                // 显示行号
                lineNumbers: false,
                // 自动缩进
                smartIndent: true,
                // 自动补全括号
                autoCloseBrackets: true,
                // 主题
                theme: 'monokai',
                // 语言模型
                mode: 'application/json',
                // 自动提示配置
                extraKeys: {'Ctrl': 'autocomplete'},
            },
            // 输出参数样式
            outOptions: {
                // 显示行号
                lineNumbers: false,
                // 自动缩进
                smartIndent: true,
                // 自动补全括号
                autoCloseBrackets: true,
                // 主题
                theme: 'blackboard',
                // 语言模型
                mode: 'text/x-java',
                // 自动提示配置
                extraKeys: {'Ctrl': 'autocomplete'},
            },
        };
    },
    created() {
        let self = this;
        // 加载部门信息
        self.initDept({}, '.operate-log').then((result) => {
            self.deptList = result;
        });
        self.selectEvent();
    },
    methods: {
        /* 刷新*/
        refresh(value) {
            this.pageNum = 1;
            this.pageSize = 10;
            this.queryText = '';
            this.startTime = '';
            this.endTime = '';
            this.status = value;
            this.selectEvent();
        },
        /* 查询操作日志*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                startTime: self.startTime,
                endTime: self.endTime,
                status: self.status.toBool(),
                params: {
                    queryText: self.queryText,
                    mode: self.mode ? 'DSL' : 'API',
                },
            };
            const loading = self.loadingOpen('.operate-log');
            const request = self.isElasticsearch ? selectOperateLogEs(data) : selectOperateLog(data);
            request.then((response) => {
                if (response.flag) {
                    self.dataTotal = response.data.total;
                    self.dataList = response.data.list;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 截取类名*/
        formatCut(row, column, cellValue) {
            const directory = cellValue.split('.');
            return directory[directory.length - 1];
        },
        /* 格式化部门*/
        formatDept(row, column, cellValue) {
            if (!!cellValue) {
                const dept = global.extractByTree(this.deptList, 'uuid', cellValue);
                return !dept ? '' : dept.name;
            }
        },
        /* 设置行背景颜色*/
        setRowClass({row}) {
            if (String(this.status.toBool()) !== 'null') {
                return '';
            } else if (String(row.status) === 'false') {
                return 'row-1';
            } else if (!row.status) {
                return 'row-2';
            }
            return '';
        },
        /* 日志详情*/
        showDetails(row) {
            let self = this;
            const data = {
                uuid: row.uuid,
                isEs: self.isElasticsearch,
            };
            const loading = self.loadingOpen('.operate-log');
            const request = selectOperateArgs(data);
            request.then((response) => {
                if (response.flag) {
                    self.isShowDetails = true;
                    self.details = row;
                    self.details.inArgs = self.formatArgs(response.data.inArgs);
                    self.details.outArgs = self.formatArgs(response.data.outArgs);
                    self.details.consoleLog = self.formatArgs(response.data.consoleLog);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 格式化出入参*/
        formatArgs(args) {
            if (!args) {
                return '';
            }
            try {
                return JSON.stringify(JSON.parse(args), null, '\t');
            } catch (e) {
                return args;
            }
        },
        /* 删除确认*/
        deleteConfirm(rows) {
            if (!rows || rows.length === 0) {
                return;
            }
            if (Object.prototype.toString.call(rows) === '[object Array]') {
                this.selected = rows;
                this.confirm('确认清空当前页?', this.deleteEvent, null);
            } else {
                this.selected = [rows];
                this.confirm('确认删除选中记录?', this.deleteEvent, null);
            }
        },
        /* 删除事件*/
        deleteEvent() {
            let self = this;
            let uuids = [];
            for (let item of self.selected) {
                uuids.push(item.uuid);
            }
            const loading = self.loadingOpen('.operate-log');
            deleteOperateLog(uuids).then((response) => {
                if (response.flag) {
                    self.refresh('');
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 设置ES查询方式*/
        setEsMode() {
            if (this.isElasticsearch) {
                this.mode = !this.mode;
                this.messageSuccess(this.mode ? 'DSL查询' : 'API查询');
            }
        },
    },
    watch: {
        isElasticsearch(newValue) {
            this.messageInfo(newValue ? '开启ES查询，将从ES中获取数据' : '关闭ES查询，将从DB中获取数据');
        },
    },
};
</script>

<style lang="scss">
    .operate-log {
        .el-table .row-1 {
            background: #FDE9E6;
        }

        .el-table .row-2 {
            background: #FDF5E6;
        }
    }

    .CodeMirror {
        height: auto;
    }

    .CodeMirror-scroll {
        max-height: 300px;
        overflow-y: hidden;
        overflow-x: auto;
    }
</style>
