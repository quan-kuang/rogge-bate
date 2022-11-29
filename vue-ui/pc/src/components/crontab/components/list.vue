<template>
    <div class="crontab">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['crontab:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['crontab:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['crontab:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.crontab')" v-has-all-permissions="['crontab:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['crontab:select']">
            <el-input v-model="name" placeholder="请输入任务名称" clearable prefix-icon="el-icon-search"/>
            <el-select v-model="type" placeholder="请选择任务类型" clearable>
                <el-option label="启用" value="true"/>
                <el-option label="停用" value="false"/>
            </el-select>
            <el-select v-model="status" placeholder="请选择启用状态" clearable>
                <el-option label="启用" value="true"/>
                <el-option label="停用" value="false"/>
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
            <el-button type="info" icon="el-icon-document" @click="openLog" v-has-all-permissions="['crontabLog:select']">日志</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="type" label="类型" align="center" :formatter="formatType" width="100"/>
            <el-table-column prop="invokeTarget" label="调用目标" align="center" show-overflow-tooltip/>
            <el-table-column prop="expression" label="corn表达式" align="center" show-overflow-tooltip/>
            <el-table-column prop="executeTime" label="下次执行时间" align="center" width="180">
                <template slot-scope="scope">
                    <el-popover placement="left" width="300" trigger="click">
                        <div class="el-table el-table-custom">
                            <table>
                                <thead>
                                <tr>
                                    <th class="is-leaf">
                                        <div class="cell">序号</div>
                                    </th>
                                    <th class="is-leaf">
                                        <div class="cell">下次执行时间</div>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr v-for="(item,index) in executeTimeList" :key="index">
                                    <td>
                                        <div class="cell">{{ index + 1 }}</div>
                                    </td>
                                    <td>
                                        <div class="cell">{{ item }}</div>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <span slot="reference" style="cursor: pointer" @click="getExecuteTime(scope.row.expression)"> {{ scope.row.executeTime }}</span>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column prop="concurrent" label="并发执行" align="center" width="80">
                <template slot-scope="scope">
                    <span>{{ scope.row.concurrent ? '允许' : '禁止' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="creatorName" label="创建人" align="center" width="100"/>
            <el-table-column prop="createTime" label="创建时间" align="center" width="150" :formatter="formatDate2"/>
            <el-table-column label="启用状态" align="center" width="100">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="!isHasAllPermissions(['crontab:update'])"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" fixed="right" width="230">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="info" @click="setSelected(scope.row,EXECUTE)" v-has-all-permissions="['crontab:execute']">执行</el-button>
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" v-has-all-permissions="['crontab:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['crontab:delete']" :disabled="scope.row.parentId==='root'">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="dataTotal>10"
                       :page-sizes='pageSizes' :total="dataTotal" :page-size="pageSize" :current-page="pageNum" @size-change="sizeChange" @current-change="currentChange"/>
    </div>
</template>

<script>
import {mapActions} from 'vuex';
import common from '@assets/js/mixin/common';
import global from '@assets/js/util/global';
import {deleteCrontab, executeCrontab, getExecuteTime, saveCrontab, selectCrontab} from '@assets/js/api/crontab';

export default {
    name: 'list',
    mixins: [common],
    props: ['params'],
    data() {
        return {
            // 定时任务名称
            name: '',
            // 定时任务类型
            type: '',
            // 启用状态
            status: null,
            // 下次执行时间
            executeTimeList: [],
            deleteHint: '确认删除选中定时任务吗？',
        };
    },
    created() {
        this.selectEvent();
    },
    activated() {
        this.params.isFlush && this.refresh();
    },
    methods: {
        ...mapActions(['openHideMenu']),
        /* 刷新*/
        refresh() {
            this.pageNum = 1;
            this.pageSize = 10;
            this.name = '';
            this.type = '';
            this.status = null;
            this.selectEvent();
        },
        /* 查询定时任务列表*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                name: self.name,
                type: self.type,
                status: self.status,
            };
            const loading = self.loadingOpen('.crontab');
            selectCrontab(data).then((response) => {
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
        /* 格式化任务类型*/
        formatType(row, column, cellValue) {
            if (!!cellValue) {
                const type = global.extractByList(this.params.typeList, 'value', cellValue);
                return !type ? '' : type.text;
            }
        },
        /* 删除定时任务*/
        deleteEvent() {
            let self = this;
            let crontabIds = [];
            for (let item of self.selected) {
                crontabIds.push(item.uuid);
            }
            const loading = self.loadingOpen('.crontab');
            deleteCrontab(crontabIds).then((response) => {
                if (response.flag) {
                    self.selectEvent();
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 设置选中行*/
        setSelected(row, type) {
            // 先清除所有复选状态
            this.$refs.table.clearSelection();
            // 设置选中行
            this.$refs.table.toggleRowSelection(row);
            if (type === this.EXECUTE) {
                this.executeConfirm();
            } else if (type === this.DELETE) {
                this.deleteConfirm();
            } else {
                this.nextStep(this.ALTER);
            }
        },
        /* 修改启用状态*/
        changeStatus(row) {
            let self = this;
            const loading = self.loadingOpen('.crontab');
            saveCrontab(row).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 执行确认*/
        executeConfirm() {
            this.confirm('确认立即执行一次该任务吗？', this.executeCrontab, null);
        },
        /* 执行定时任务*/
        executeCrontab() {
            let self = this;
            const data = self.selected[0];
            const loading = self.loadingOpen('.crontab');
            executeCrontab(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 打开日志*/
        openLog() {
            // name为标签菜单的主键，必须唯一，否则操作会出现多个同样的标签，因该路由属于动态加载，取值数据库中menu表的主键
            const name = '5e7a0b90e6974834d9ed4f6c474d79e7';
            // 处理actions多个入参问题
            const object = {router: this.$router, name: name, label: '调度日志', path: '/hide/crontabLog'};
            // 添加用户缓存信息
            this.openHideMenu(object);
        },
        /* 获取表达式执行时间*/
        getExecuteTime(expression) {
            let self = this;
            const params = {
                expression: expression,
                count: 10,
            };
            const loading = self.loadingOpen('.crontab');
            getExecuteTime(params).then((response) => {
                if (response.flag) {
                    self.executeTimeList = response.data;
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
