<template>
    <div class="dict">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['dict:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['dict:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['dict:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataList.length>0" @click="exportExcel($route.meta.title,'.dict')" v-has-all-permissions="['dict:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectDict" v-has-all-permissions="['dict:select']">
            <el-input v-model="queryText" placeholder="请输入字典主键/名称/键值" clearable prefix-icon="el-icon-search"/>
            <el-input v-model="parentId" placeholder="请输入上级字典" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectDict">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :default-expand-all="false" row-key="uuid" :tree-props="{children: 'children', hasChildren: 'hasChildren'}" @cell-dblclick="copy" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="text" label="字典名称" align="center" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-tooltip placement="top" effect="light" :disabled="!scope.row.remark" :content="scope.row.remark">
                        <el-link :underline="false">{{ scope.row.text }}</el-link>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column prop="value" label="字典键值" align="center" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.value }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="parentId" label="上级字典" align="center" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.parentId }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="parentName" label="上级名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="uuid" label="字典主键" align="center" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false">{{ scope.row.uuid }}</el-link>
                </template>
            </el-table-column>
            <el-table-column label="启用状态" align="center" width="120">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.status" @change="changeStatus(scope.row)" :disabled="!isHasAllPermissions(['dict:update'])"/>
                </template>
            </el-table-column>
            <el-table-column prop="creatorName" label="创建人" align="center" width="150"/>
            <el-table-column prop="createTime" label="创建时间" align="center" width="180" :formatter="formatDate2"/>
            <el-table-column label="操作" align="center" fixed="right" width="230">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="success" @click="setSelected(scope.row,ADD)" :disabled="scope.row.type==='2'" v-has-all-permissions="['dict:insert']">新增</el-button>
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" v-has-all-permissions="['dict:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['dict:delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import mixin from '../js/mixin';
import copy from '@assets/js/mixin/copy';
import {deleteDict, saveDict, selectCascade} from '@assets/js/api/dict';
import common from '@assets/js/mixin/common';

export default {
    name: 'list',
    mixins: [common, mixin, copy],
    props: ['params'],
    data() {
        return {
            // 上级字典
            parentId: '',
            deleteHint: '确认删除选中字典吗？',
        };
    },
    created() {
        this.selectDict();
    },
    activated() {
        this.params.isFlush && this.refresh();
    },
    methods: {
        /* 刷新*/
        refresh() {
            this.queryText = '';
            this.parentId = '';
            this.selectDict();
        },
        /* 查询字典列表*/
        async selectDict() {
            let self = this;
            const data = {
                parentId: self.parentId,
                params: {queryText: self.queryText},
            };
            await self.initDict(data, '.dict').then((result) => {
                self.dataList = result;
            });
        },
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let dictIds = [];
            for (let item of self.selected) {
                dictIds.push(item.uuid);
            }
            if (dictIds.length === 0) {
                return;
            }
            self.selectCascade(dictIds).then((result) => {
                if (!!result && result.length > dictIds.length) {
                    self.$confirm(`选项中包含下级字典，是否全部删除【${result.length}】项？`, '警告', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }).then(() => {
                        self.deleteDict(result);
                    }).catch(() => {
                        self.messageInfo('已取消删除');
                    });
                } else {
                    self.deleteDict(dictIds);
                }
            });
        },
        /* 删除前查询是否包含下级字典*/
        selectCascade(data) {
            let self = this;
            return new Promise((resolve, reject) => {
                const loading = self.loadingOpen('.dict');
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
        /* 删除字典*/
        deleteDict(data) {
            let self = this;
            const loading = self.loadingOpen('.dict');
            deleteDict(data).then((response) => {
                if (response.flag) {
                    self.selectDict();
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
            const loading = self.loadingOpen('.dict');
            saveDict(row).then((response) => {
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
