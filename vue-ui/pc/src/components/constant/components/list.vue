<template>
    <div class="constant">
        <!--面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="success" icon="el-icon-circle-plus-outline" @click="nextStep(ADD)" v-has-all-permissions="['constant:insert']">新增</el-button>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="nextStep(ALTER)" v-has-all-permissions="['constant:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['constant:delete']">删除</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataList.length>0" @click="exportExcel($route.meta.title,'.constant')" v-has-all-permissions="['constant:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['constant:select']">
            <el-input v-model="queryText" placeholder="请输入键名/名称" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="id" label="编码" align="center" show-overflow-tooltip/>
            <el-table-column prop="key" label="键名" align="center" show-overflow-tooltip/>
            <el-table-column prop="value" label="键值" align="center" show-overflow-tooltip/>
            <el-table-column prop="name" label="名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="remark" label="备注" align="center" show-overflow-tooltip/>
            <el-table-column prop="createTime" label="创建时间" align="center" width="180" :formatter="formatDate2"/>
            <el-table-column prop="updateTime" label="修改时间" align="center" width="180" :formatter="formatDate2"/>
            <el-table-column label="操作" align="center" fixed="right" width="180">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="warning" @click="setSelected(scope.row,ALTER)" v-has-all-permissions="['constant:update']">编辑</el-button>
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['constant:delete']">删除</el-button>
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
import {deleteConstant, selectConstant} from '@assets/js/api/constant';

export default {
    name: 'constant',
    mixins: [common],
    props: ['params'],
    data() {
        return {
            queryText: '',
            deleteHint: '确认删除选中信息吗？',
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
            this.queryText = '';
            this.selectEvent();
        },
        /* 查询常量信息表*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                queryText: self.queryText,
            };
            const loading = self.loadingOpen('.constant');
            selectConstant(data).then((response) => {
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
        /* 删除操作*/
        deleteEvent() {
            let self = this;
            let ids = [];
            for (let item of self.selected) {
                ids.push(item.id);
            }
            if (ids.length === 0) {
                return;
            }
            const loading = self.loadingOpen('.constant');
            deleteConstant(ids).then((response) => {
                if (response.flag) {
                    self.selectEvent();
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
