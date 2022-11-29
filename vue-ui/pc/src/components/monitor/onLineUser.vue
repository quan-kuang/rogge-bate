<template>
    <div class="on-line-user">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.on-line-user')" v-has-all-permissions="['onLineUser:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['onLineUser:select']">
            <el-input v-model="userFilter" placeholder="请输入用户账号/名称" clearable prefix-icon="el-icon-search"/>
            <el-input v-model="loginFilter" placeholder="请输入登录IP/位置" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList">
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="account" label="账号" align="center"/>
            <el-table-column prop="name" label="名称" align="center"/>
            <el-table-column prop="phone" label="手机号" align="center"/>
            <el-table-column prop="deptName" label="部门" align="center"/>
            <el-table-column prop="ip" label="IP" align="center" show-overflow-tooltip/>
            <el-table-column prop="position" label="位置" align="center" show-overflow-tooltip/>
            <el-table-column prop="browser" label="浏览器" align="center" show-overflow-tooltip/>
            <el-table-column prop="operateSystem" label="操作系统" align="center" show-overflow-tooltip/>
            <el-table-column prop="loginTime" label="登录时间" align="center" width="180" :formatter="formatDate"/>
            <el-table-column label="操作" align="center" fixed="right" v-if="isHasAllPermissions(['onLineUser:delete'])">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-delete" @click="quitConfirm(scope.row)">强退</el-button>
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
import common from '@assets/js/mixin/common';
import dept from '@components/dept/js/mixin';
import {deleteOnLineUser, selectOnLineUser} from '@assets/js/api/onLineUser';

export default {
    name: 'onLineUser',
    mixins: [common, dept],
    data() {
        return {
            // 用户账号、名称过滤
            userFilter: '',
            // 登录IP、位置过滤
            loginFilter: '',
        };
    },
    created() {
        this.selectEvent();
    },
    methods: {
        /* 刷新*/
        refresh() {
            this.pageNum = 1;
            this.pageSize = 10;
            this.userFilter = '';
            this.loginFilter = '';
            this.selected = [];
            this.selectEvent();
        },
        /* 查询在线用户信息*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                params: {
                    userFilter: self.userFilter,
                    loginFilter: self.loginFilter,
                },
            };
            const loading = self.loadingOpen('.on-line-user');
            selectOnLineUser(data).then((response) => {
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
        /* 强退确认事件*/
        quitConfirm(row) {
            this.selected = [row];
            this.confirm('确认退出该用户?', this.deleteEvent, null);
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let uuids = [];
            for (let item of self.selected) {
                uuids.push(item.uuid);
            }
            const loading = self.loadingOpen('.on-line-user');
            deleteOnLineUser(uuids).then((response) => {
                if (response.flag) {
                    self.refresh();
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
