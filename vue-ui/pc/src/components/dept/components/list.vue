<template>
    <div class="dept">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['dept:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['dept:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['dept:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataList.length>0" @click="exportExcel($route.meta.title,'.dept')" v-has-all-permissions="['dept:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectDept" v-has-all-permissions="['dept:select']">
            <el-input v-model="name" placeholder="请输入部门名称" clearable prefix-icon="el-icon-search"/>
            <el-select v-model="status" placeholder="请选择启用状态" clearable>
                <el-option label="启用" value="true"/>
                <el-option label="停用" value="false"/>
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="selectDept">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :default-expand-all="false" row-key="uuid" :tree-props="{children: 'children', hasChildren: 'hasChildren'}" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="name" label="部门名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="parentName" label="上级部门" align="center" show-overflow-tooltip/>
            <el-table-column label="启用状态" align="center" width="100">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="!isHasAllPermissions(['dept:update'])"/>
                </template>
            </el-table-column>
            <el-table-column prop="creatorName" label="创建人" align="center"/>
            <el-table-column prop="createTime" label="创建时间" align="center" :formatter="formatDate2"/>
            <el-table-column prop="updateTime" label="修改时间" align="center" :formatter="formatDate2"/>
            <el-table-column label="操作" align="center" fixed="right" width="230">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="success" @click="setSelected(scope.row,ADD)" :disabled="scope.row.type==='2'" v-has-all-permissions="['dept:insert']">新增</el-button>
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" v-has-all-permissions="['dept:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['dept:delete']" :disabled="scope.row.parentId==='root'">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import mixin from '../js/mixin';
import global from '@assets/js/util/global';
import common from '@assets/js/mixin/common';
import {deleteDept, saveDept, selectCascade} from '@assets/js/api/dept';

export default {
    name: 'list',
    mixins: [common, mixin],
    props: ['params'],
    data() {
        return {
            // 部门名称
            name: '',
            // 启用状态
            status: null,
            deleteHint: '确认删除选中部门吗？',
        };
    },
    created() {
        this.selectDept();
    },
    activated() {
        this.params.isFlush && this.refresh();
    },
    methods: {
        /* 刷新*/
        refresh() {
            let self = this;
            self.name = '';
            self.status = null;
            self.selectDept();
            // 加载树形部门信息
            self.initDept({status: true}, '.dept').then((result) => {
                self.params.deptList = result;
                self.params.cloneDeptList = global.deepClone(result);
            });
        },
        /* 查询部门列表*/
        async selectDept() {
            let self = this;
            const data = {
                name: self.name,
                status: self.status,
            };
            await self.initDept(data, '.dept').then((result) => {
                self.dataList = result;
            });
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let deptIds = [];
            for (let item of self.selected) {
                deptIds.push(item.uuid);
            }
            if (deptIds.length === 0) {
                return;
            }
            self.selectCascade(deptIds).then((result) => {
                if (!!result && result.length > deptIds.length) {
                    self.$confirm(`选项中包含下级部门，是否全部删除【${result.length}】项？`, '警告', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }).then(() => {
                        self.deleteDept(result);
                    }).catch(() => {
                        self.messageInfo('已取消删除');
                    });
                } else {
                    self.deleteDept(deptIds);
                }
            });
        },
        /* 删除前查询是否包含下级部门*/
        selectCascade(data) {
            let self = this;
            return new Promise((resolve) => {
                const loading = self.loadingOpen('.dept');
                selectCascade(data).then((response) => {
                    if (response.flag) {
                        resolve(response.data);
                    } else {
                        resolve(null);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            });
        },
        /* 删除部门*/
        deleteDept(data) {
            let self = this;
            const loading = self.loadingOpen('.dept');
            deleteDept(data).then((response) => {
                if (response.flag) {
                    self.selectDept();
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
            const loading = self.loadingOpen('.dept');
            saveDept(row).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
    },
};
</script>
