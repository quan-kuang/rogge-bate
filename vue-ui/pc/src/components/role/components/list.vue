<template>
    <div class="role">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['role:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['role:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['role:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.role')" v-has-all-permissions="['role:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['role:select']">
            <el-input v-model="name" placeholder="请输入名称" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="name" label="名称" align="center"/>
            <el-table-column prop="permissionType" label="权限类型" align="center" :formatter="formatPermission"/>
            <el-table-column prop="remark" label="描述" align="center"/>
            <el-table-column prop="creatorName" label="创建人" align="center"/>
            <el-table-column prop="createTime" label="创建时间" align="center" :formatter="formatDate2" width="200"/>
            <el-table-column prop="updateTime" label="修改时间" align="center" :formatter="formatDate2" width="200"/>
            <el-table-column label="启用状态" align="center" width="100">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="scope.row.uuid === constant.admin"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" fixed="right" width="180">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" :disabled="scope.row.uuid === constant.admin" v-has-all-permissions="['role:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" :disabled="scope.row.uuid === constant.admin" v-has-all-permissions="['role:delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-1" layout="total, prev, pager, next" v-show="dataTotal>10" :total="dataTotal" :page-size="pageSize" :current-page="pageNum" @current-change="currentChange"/>
    </div>
</template>

<script>
import format from '@assets/js/util/format';
import global from '@assets/js/util/global';
import constant from '@assets/js/common/constant';
import {deleteRole, selectRole, updateRole} from '@assets/js/api/role';
import common from '@assets/js/mixin/common';

export default {
    name: 'role',
    mixins: [common],
    props: ['params'],
    data() {
        return {
            constant: constant,
            name: '',
            deleteHint: '确认删除选中角色吗？',
        };
    },
    created() {
        this.selectEvent();
    },
    activated() {
        this.params.isFlush && this.refresh();
    },
    methods: {
        /* 刷新*/
        refresh() {
            this.pageNum = 1;
            this.pageSize = 10;
            this.name = '';
            this.selectEvent();
        },
        /* 查询角色列表*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                name: self.name,
            };
            const loading = self.loadingOpen('.role');
            selectRole(data).then((response) => {
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
        /* 格式化性别*/
        formatSex(row, column, cellValue) {
            return format.sex(cellValue);
        },
        /* 格式化权限类型*/
        formatPermission(row, column, cellValue) {
            const dict = global.extractByList(this.params.permissionTypeList, 'value', cellValue);
            return !dict ? '' : dict.text;
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let userIds = [];
            for (let item of self.selected) {
                if (item.uuid === 'admin') {
                    self.$refs.table.toggleRowSelection(item, false);
                    self.notify('info', '不能删除管理员角色，已取消选择');
                } else {
                    userIds.push(item.uuid);
                }
            }
            if (userIds.length === 0) {
                return;
            }
            const loading = self.loadingOpen('.role');
            deleteRole(userIds).then((response) => {
                if (response.flag) {
                    self.selectEvent();
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 修改启用状态*/
        changeStatus(row) {
            let self = this;
            const data = {
                uuid: row.uuid,
                status: row.status,
            };
            const loading = self.loadingOpen('.role');
            updateRole(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
    },
};
</script>
