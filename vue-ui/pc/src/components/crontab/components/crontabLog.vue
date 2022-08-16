<template>
    <div class="crontab-log">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh('')"/>
            <el-button type="danger" icon="el-icon-delete" @click="deleteConfirm(dataList)" v-has-all-permissions="['crontab:delete','crontabLog:delete']">清空</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.crontab-log')" v-has-all-permissions="['crontab:export','crontabLog:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['crontabLog:select']">
            <el-select v-model="status" clearable @change="refresh">
                <el-option label="全部" value=""/>
                <el-option label="成功" value="true"/>
                <el-option label="失败" value="false"/>
            </el-select>
            <el-select v-model="crontabType" placeholder="请选择任务类型" clearable>
                <el-option v-for="(item, index) in typeList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
            </el-select>
            <el-input v-model="crontabName" placeholder="请输入任务名称" clearable prefix-icon="el-icon-search"/>
            <el-date-picker v-model="startTime" type="datetime" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" default-time="00:00:00" :picker-options="pickerOptions" placeholder="请选择开始时间"/>
            <el-date-picker v-model="endTime" type="datetime" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" default-time="23:59:59" :picker-options="pickerOptions" placeholder="请选择结束时间"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent" @contextmenu.prevent.native="setEsMode">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :row-class-name="setRowClass">
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="crontabName" label="任务名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="result" label="运行结果" align="center" show-overflow-tooltip/>
            <el-table-column prop="status" label="执行状态" align="center" width="90">
                <template slot-scope="scope">
                    <span>{{ format.decode(scope.row.status, true, '成功', false, '失败', '-') }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="errorMessage" label="异常信息" align="center" show-overflow-tooltip/>
            <el-table-column prop="beginTime" label="执行时间" align="center" :formatter="formatDate2"/>
            <el-table-column prop="elapsedTime" label="耗时(S)" align="center" width="80"/>
            <el-table-column label="操作" align="center" fixed="right" width="100" v-if="isHasAllPermissions(['crontab:delete','crontabLog:delete'])">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-close" @click="deleteConfirm(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="dataTotal>10"
                       :page-sizes='pageSizes' :total="dataTotal" :page-size="pageSize" :current-page="pageNum"
                       @size-change="sizeChange" @current-change="currentChange"/>
    </div>
</template>

<script>
import format from '@assets/js/util/format';
import common from '@assets/js/mixin/common';
import {deleteCrontabLog, selectCrontabLog} from '@assets/js/api/crontab';

export default {
    name: 'crontabLog',
    mixins: [common],
    data() {
        return {
            format: format,
            // 任务名称
            crontabName: '',
            // 任务类型
            crontabType: '',
            // 操作状态
            status: '',
            // 开始时间
            startTime: '',
            // 结束时间
            endTime: '',
            // 任务类型
            typeList: [],
            // 限制选择时间不能大于今天不能小于去年
            pickerOptions: {
                disabledDate: (date) => {
                    const now = new Date();
                    const lastYear = new Date().setFullYear(now.getFullYear() - 1);
                    return lastYear > date || date > new Date();
                },
            },
            // ES查询方式，默认DSL，否则API
            mode: true,
        };
    },
    created() {
        let self = this;
        // 加载定时任务字典信息
        self.selectDict('.crontab', 'crontab-type').then((result) => {
            self.typeList = result;
        });
        self.selectEvent();
    },
    methods: {
        /* 刷新*/
        refresh(value) {
            this.pageNum = 1;
            this.pageSize = 10;
            this.crontabName = '';
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
                crontabName: self.crontabName,
                crontabType: self.crontabType,
                status: self.status.toBool(),
                startTime: self.startTime,
                endTime: self.endTime,
                params: {
                    mode: self.mode ? 'DSL' : 'API',
                },
            };
            const loading = self.loadingOpen('.crontab-log');
            selectCrontabLog(data).then((response) => {
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
        /* 更改分页大小*/
        sizeChange(pageSize) {
            if (this.pageNum * pageSize >= this.dataTotal) {
                this.pageNum = Math.ceil(this.dataTotal / pageSize);
            }
            this.pageSize = pageSize;
            this.selectEvent();
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
        /* 删除确认*/
        deleteConfirm(rows) {
            if (!rows || rows.length === 0) {
                return;
            }
            this.selected = [rows];
            const deleteHint = Object.prototype.toString.call(rows) === '[object Array]' ? '确认清空当前页？' : '确认删除选中记录？';
            this.confirm(deleteHint, this.deleteEvent, null);
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let uuids = [];
            for (let item of self.selected) {
                uuids.push(item.uuid);
            }
            const loading = self.loadingOpen('.crontab-log');
            deleteCrontabLog(uuids).then((response) => {
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
            this.mode = !this.mode;
            this.messageSuccess(this.mode ? 'DSL查询' : 'API查询');
        },
    },
};
</script>

<style lang="scss">
    .crontab-log {
        .el-table .row-1 {
            background: #FDE9E6;
        }

        .el-table .row-2 {
            background: #FDF5E6;
        }

        .search-box {
            .el-input {
                width: 200px;
            }

            .el-select {
                width: 200px;

                .el-input__inner {
                    width: 200px;
                }

                padding-right: 20px;
            }
        }
    }
</style>
