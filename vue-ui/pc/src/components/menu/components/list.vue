<template>
    <div class="menu">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['menu:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['menu:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['menu:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataList.length>0" @click="exportExcel($route.meta.title,'.menu')" v-has-all-permissions="['menu:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectMenu" v-has-all-permissions="['menu:select']">
            <el-input v-model="queryText" placeholder="请输入菜单名称/路由地址/组件路径" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectMenu">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :default-expand-all="false" row-key="uuid" :tree-props="{children: 'children', hasChildren: 'hasChildren'}" @cell-dblclick="copy" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="title" label="菜单名称" align="center" width="170" show-overflow-tooltip/>
            <el-table-column label="图标" align="center">
                <template slot-scope="scope">
                    <i :class="scope.row.icon"/>
                </template>
            </el-table-column>
            <el-table-column prop="path" label="路由地址" align="center" width="170" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.path }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="url" label="组件路径" align="center" width="170" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.url }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="permission" label="权限标识" align="center" width="120" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.permission }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="redirect" label="是否隐藏" align="center">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.hide" @change="changeStatus(scope.row)" :disabled="!isHasAllPermissions(['menu:update'])"/>
                </template>
            </el-table-column>
            <el-table-column label="启用状态" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.type==='2'">-</span>
                    <el-switch v-else v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="!isHasAllPermissions(['menu:update'])"/>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" align="center" width="150" :formatter="formatDate2"/>
            <el-table-column label="操作" align="center" fixed="right" width="230">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="success" @click="setSelected(scope.row,ADD)" :disabled="scope.row.type==='2'" v-has-all-permissions="['menu:insert']">新增</el-button>
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" v-has-all-permissions="['menu:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['menu:delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import mixin from '../js/mixin';
import format from '@assets/js/util/format';
import global from '@assets/js/util/global';
import copy from '@assets/js/mixin/copy';
import {deleteMenu, selectCascade, selectMenu, updateMenu} from '@assets/js/api/menu';

export default {
    name: 'list',
    mixins: [mixin, copy],
    props: ['params'],
    created() {
        this.selectMenu();
    },
    activated() {
        this.params.isFlush && this.refresh();
    },
    methods: {
        /* 刷新*/
        refresh() {
            this.queryText = '';
            this.selectMenu();
        },
        /* 查询菜单列表*/
        selectMenu() {
            let self = this;
            const data = {
                params: {queryText: self.queryText},
            };
            const loading = self.loadingOpen('.menu');
            selectMenu(data).then((response) => {
                if (response.flag) {
                    self.dataList = format.list(response.data);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },

        /* 删除确认*/
        deleteConfirm(row) {
            let self = this;
            self.confirm(`确认删除选中${row.type === '0' ? '目录' : '菜单'}吗？`, self.deleteEvent, null);
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let menuIds = [];
            for (let item of self.selected) {
                menuIds.push(item.uuid);
            }
            if (menuIds.length === 0) {
                return;
            }
            self.selectCascade(menuIds).then((result) => {
                if (!!result && result.length > menuIds.length) {
                    self.$confirm(`选项中包含下级菜单，是否全部删除【${result.length}】项？`, '警告', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }).then(() => {
                        self.deleteMenu(result);
                    }).catch(() => {
                        self.messageInfo('已取消删除');
                    });
                } else {
                    self.deleteMenu(menuIds);
                }
            });
        },
        /* 删除前查询是否包含下级菜单*/
        selectCascade(data) {
            let self = this;
            return new Promise((resolve) => {
                const loading = self.loadingOpen('.menu');
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
        /* 删除菜单*/
        deleteMenu(data) {
            let self = this;
            const loading = self.loadingOpen('.menu');
            deleteMenu(data).then((response) => {
                if (response.flag) {
                    self.selectMenu();
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 页面跳转*/
        nextStep(index) {
            let self = this;
            const item = self.selected.length > 0 ? global.deepClone(self.selected[self.selected.length - 1]) : null;
            if (index === 2 && !!item && item.type === '2') {
                self.messageError(`按钮【${item.title}】无法新增子级菜单`);
                return;
            }
            self.params.item = item;
            self.$emit('nextStep', index);
        },
        /* 修改启用状态*/
        changeStatus(row) {
            let self = this;
            const data = {
                uuid: row.uuid,
                hide: row.hide,
                status: row.status,
            };
            const loading = self.loadingOpen('.menu');
            updateMenu(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 复制按钮*/
        copy(row, column, cell, event) {
            const className = event.target.className;
            if (className === 'el-link--inner') {
                const msg = row[column.property];
                this.hbdtwx(event, msg, column.label);
            }
        },
    },
};
</script>
