<template>
    <div class="user">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['user:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['user:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['user:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.user')" v-has-all-permissions="['user:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['user:select']">
            <el-input v-model="queryText" placeholder="请输入账号/姓名" clearable prefix-icon="el-icon-search"/>
            <el-select v-model="roleId" placeholder="请选择角色" clearable>
                <el-option v-for="(item,index) in roleList" :key="index" :label="item.name" :value="item.uuid" :disabled="!item.status"/>
            </el-select>
            <el-select ref="dept" v-model="form.deptId" placeholder="请选择部门" filterable :filter-method="filterMethod" clearable @clear="clear">
                <el-option style="background-color: white" :value="option.uuid" :label="option.name">
                    <tree-select ref="treeSelect" :props="props" :data="params.deptList" @nodeClick="nodeClick"/>
                </el-option>
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column type="expand" width="50">
                <template slot-scope="scope">
                    <el-descriptions :column="4" border>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-yonghu1"/> 创建人</template>
                            {{ scope.row.creatorName }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-ziyuan"/> 身份证号</template>
                            {{ scope.row.idCard }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-chushengriqi"/> 出生日期</template>
                            {{ scope.row.birthday }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-youjian"/> 电子邮箱</template>
                            {{ scope.row.email }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-chuangjianshijian"/> 创建时间</template>
                            {{ formatDate2(null, null, scope.row.createTime) }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-gengxinshijian"/> 更新时间</template>
                            {{ formatDate2(null, null, scope.row.updateTime) }}
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template slot="label"><i class="iconfont icon-beizhu1"/> 备注</template>
                            {{ scope.row.remark }}
                        </el-descriptions-item>
                    </el-descriptions>
                </template>
            </el-table-column>
            <el-table-column prop="account" label="账号" align="center" :show-overflow-tooltip="true"/>
            <el-table-column prop="name" label="名称" align="center"/>
            <el-table-column prop="sex" label="性别" align="center" :formatter="formatSex" width="50"/>
            <el-table-column prop="phone" label="手机号" align="center"/>
            <el-table-column prop="roleIds" label="角色" align="center" :formatter="formatRoleIds" :show-overflow-tooltip="true"/>
            <el-table-column prop="deptName" label="部门" align="center" width="120" :show-overflow-tooltip="true"/>
            <el-table-column prop="loginTime" label="最后登录" align="center" :formatter="formatDate2" width="180"/>
            <el-table-column label="启用状态" align="center" width="100">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="scope.row.uuid === constant.admin"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="300">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="primary" @click="showPermissionScope(scope.row)">权限范围</el-button>
                    <el-button size="mini" plain type="info" @click="setSelected(scope.row,RESET)" :disabled="scope.row.uuid === constant.admin" v-has-all-permissions="['user:reset']">重置</el-button>
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" :disabled="scope.row.uuid === constant.admin" v-has-all-permissions="['user:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" :disabled="scope.row.uuid === constant.admin" v-has-all-permissions="['user:delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-1" layout="total, prev, pager, next" v-show="dataTotal>10" :total="dataTotal" :page-size="pageSize" :current-page="pageNum"
                       @current-change="currentChange"/>
        <!--权限弹窗-->
        <el-dialog :title="permissionScope" :visible.sync="isShowPermissionScope" :close-on-click-modal="true" width="30%">
            <tree ref="deptTree" treeStyle="background-color: white" :readonly="true" :checkStrictly="true"
                  :props="props" :data="params.deptList" :defaultKeys="permissionDeptSet"/>
        </el-dialog>
    </div>
</template>

<script>
import {tree, treeSelect} from '@assets/js/common/components';
import format from '@assets/js/util/format';
import global from '@assets/js/util/global';
import verify from '@assets/js/util/verify';
import constant from '@assets/js/common/constant';
import cipher from '@assets/js/util/cipher';
import mixin from '../js/mixin';
import {deleteUser, selectUser, selectUserById, updateUser} from '@assets/js/api/user';
import {selectRole} from '@assets/js/api/role';
import common from '@assets/js/mixin/common';

export default {
    name: 'user',
    components: {
        tree, treeSelect,
    },
    mixins: [mixin, common],
    props: ['params'],
    data() {
        return {
            constant: constant,
            roleId: '',
            roleList: [],
            form: {deptId: ''},
            permissionScope: '',
            permissionDeptSet: [],
            isShowPermissionScope: false,
            deleteHint: '确认删除选中用户吗？',
        };
    },
    created() {
        this.selectEvent();
    },
    activated() {
        this.selectRole();
        this.params.isFlush && this.refresh();
    },
    methods: {
        /* 刷新*/
        refresh() {
            this.pageNum = 1;
            this.pageSize = 10;
            this.queryText = '';
            this.roleId = '';
            this.form.deptId = '';
            this.selectEvent();
        },
        /* 查询用户列表*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                deptId: self.form.deptId,
                params: {queryText: self.queryText, roleId: self.roleId},
            };
            const loading = self.loadingOpen('.user');
            selectUser(data).then((response) => {
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
        /* 格式化角色ID*/
        formatRoleIds(row, column, cellValue) {
            let roleNames = [];
            for (let roleId of cellValue) {
                const role = global.extractByList(this.roleList, 'uuid', roleId);
                role && roleNames.push(role.name);
            }
            return roleNames.join(',');
        },
        /* 删除用户*/
        deleteEvent() {
            let self = this;
            let userIds = [];
            for (let item of self.selected) {
                if (item.uuid === 'admin') {
                    self.$refs.table.toggleRowSelection(item, false);
                    self.notify('info', '不能删除管理员账户，已取消选择');
                } else {
                    userIds.push(item.uuid);
                }
            }
            if (userIds.length === 0) {
                return;
            }
            const loading = self.loadingOpen('.user');
            deleteUser(userIds).then((response) => {
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
            const loading = self.loadingOpen('.user');
            updateUser(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 查询用户权限范围*/
        showPermissionScope(row) {
            let self = this;
            const loading = self.loadingOpen('.user');
            selectUserById(row.uuid).then((response) => {
                if (response.flag) {
                    const data = response.data;
                    self.isShowPermissionScope = true;
                    setTimeout(() => {
                        if (data.permissionScope === 0) {
                            self.permissionScope = '全部权限';
                            self.permissionDeptSet = self.params.deptList;
                            self.$refs.deptTree.isCheckAll = true;
                            self.$refs.deptTree.checkAll();
                            self.$refs.deptTree.allExpand(true);
                        } else if (data.permissionScope === 1) {
                            self.permissionScope = '部分权限';
                            self.permissionDeptSet = data.permissionDeptSet;
                            self.$refs.deptTree.setCheckedKeys(data.permissionDeptSet);
                        } else {
                            self.permissionScope = '仅本人权限';
                            self.permissionDeptSet = [];
                            self.$refs.deptTree.setCheckedKeys([]);
                            self.$refs.deptTree.allExpand(false);
                        }
                        self.$refs.deptTree.setDisable();
                    }, 1);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 设置选中行*/
        setSelected(row, action) {
            // 先清除所有复选状态
            this.$refs.table.clearSelection();
            // 设置选中行
            this.$refs.table.toggleRowSelection(row);
            if (action === this.RESET) {
                this.changePrompt(row);
            } else if (action === this.DELETE) {
                this.deleteConfirm();
            } else {
                this.nextStep(action);
            }
        },
        /* 修改密码提示*/
        changePrompt(row) {
            this.$prompt('请输入新的密码', '重置密码', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputValidator: this.checkPassword,
            }).then(({value}) => {
                this.changePassword(value, row);
            }).catch(() => {
                this.messageInfo('取消修改');
            });
        },
        /* 重置密码校验*/
        checkPassword(value) {
            if (!value) {
                return '请输入新密码';
            }
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return '不能包含空格';
            }
        },
        /* 修改密码*/
        changePassword(value, row) {
            let self = this;
            const data = {
                uuid: row.uuid,
                account: row.account,
                password: cipher.encryptRSA(value),
            };
            const loading = self.loadingOpen('.user');
            updateUser(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 查询角色列表*/
        selectRole() {
            let self = this;
            const data = {
                // status: true,
            };
            selectRole(data).then((response) => {
                if (response.flag) {
                    self.roleList = response.data;
                    self.params.roleList = self.roleList;
                } else {
                    self.messageError(response.msg);
                }
            });
        },
    },
};
</script>

<style>
    .table-expand label {
        width: 80px;
        color: #99a9bf;
    }

    .table-expand .el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        width: 50%;
    }
</style>
