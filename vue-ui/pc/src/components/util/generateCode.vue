<template>
    <div class="generate-code">
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh"/>
            <el-button type="primary" icon="el-icon-view" :disabled="selected.length!==1" @click="showDetails(null)">详情</el-button>
            <el-button type="success" icon="el-icon-download" :disabled="selected.length===0" @click="generateCode(null)" v-has-all-permissions="['generateCode:download']">下载</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.generate-code')" v-has-all-permissions="['generateCode:export']">导出</el-button>
        </panel-title>
        <div class="search-box" @keyup.enter="selectEvent">
            <el-select v-model="schemaName" placeholder="请选择模式" clearable>
                <el-option v-for="(item,index) in schemaNameList" :key="index" :label="item" :value="item"/>
            </el-select>
            <el-input v-model="queryText" placeholder="请输入表名/表名称" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="schemaName" label="模式名" align="center" show-overflow-tooltip width="120"/>
            <el-table-column prop="tableOwner" label="所有者" align="center" show-overflow-tooltip width="120"/>
            <el-table-column prop="tableName" label="表名" align="center" show-overflow-tooltip/>
            <el-table-column prop="tableText" label="表名称" align="center" show-overflow-tooltip/>
            <el-table-column prop="className" label="类名" align="center" show-overflow-tooltip/>
            <el-table-column prop="primaryKey" label="主键字段" align="center" width="120"/>
            <el-table-column prop="tableType" label="表类型" align="center" show-overflow-tooltip :formatter="formatType"/>
            <el-table-column label="操作" align="center" fixed="right" width="230">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="info" @click="showDetails(scope.row)">详情</el-button>
                    <el-button size="mini" plain type="success" @click="generateCode(scope.row)">下载</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="dataTotal>10"
                       :page-sizes='pageSizes' :total="dataTotal" :page-size="pageSize" :current-page="pageNum"
                       @size-change="sizeChange" @current-change="currentChange"/>
        <el-dialog :title="viewData.tableText || viewData.tableName" :visible.sync="isDialog" :append-to-body="true" :show-close="true" width="80%">
            <el-table :data="viewData.fieldExplainList">
                <el-table-column label="序号" align="center">
                    <template slot-scope="scope">
                        <span>{{ scope.$index + 1 }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="fieldText" label="字段名称" align="center" width="150" show-overflow-tooltip/>
                <el-table-column prop="fieldName" label="字段名" align="center"/>
                <el-table-column prop="fieldType" label="字段类型" align="center"/>
                <el-table-column prop="javaName" label="java字段名" align="center"/>
                <el-table-column prop="javaType" label="java字段类型" align="center"/>
                <el-table-column prop="isNotNull" label="不能为空" align="center" :formatter="formatIsNotNull"/>
                <el-table-column prop="defaultValue" label="默认值" align="center" width="150" show-overflow-tooltip/>
            </el-table>
        </el-dialog>
    </div>
</template>

<script>
import format from '@assets/js/util/format';
import {download} from '@assets/js/util/download';
import {selectSchemaName, selectTableExplain} from '@assets/js/api/util';
import common from '@assets/js/mixin/common';

export default {
    name: 'generateCode',
    mixins: [common],
    data() {
        return {
            // 展示的详情数据
            viewData: {},
            // 详情弹窗
            isDialog: false,
            // pgsql数据库模式列表
            schemaNameList: [],
            // 默认模式
            schemaName: 'public',
        };
    },
    created() {
        this.selectSchemaName();
        this.selectEvent();
    },
    methods: {
        /* 查询数据库模式名*/
        selectSchemaName() {
            let self = this;
            const loading = self.loadingOpen('.generate-code');
            selectSchemaName().then((response) => {
                if (response.flag) {
                    self.schemaNameList = response.data;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 刷新*/
        refresh() {
            this.schemaName = 'public';
            this.queryText = '';
            this.pageNum = 1;
            this.pageSize = 10;
            this.selectEvent();
        },
        /* 查询表说明信息*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                schemaName: self.schemaName,
                params: {queryText: self.queryText},
            };
            const loading = self.loadingOpen('.generate-code');
            selectTableExplain(data).then((response) => {
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
        /* 格式化表类型*/
        formatType(row) {
            return format.decode(row.tableType, 'r', '普通表', 'i', '索引', 's', '序列', 'v', '视图', 'c', '复合类型', 't', 'TOAST表', '未知类型');
        },
        /* 格式化布尔值*/
        formatIsNotNull(row) {
            return row.isNotNull ? 'Y' : '';
        },
        /* 显示详情*/
        showDetails(row) {
            let self = this;
            self.isDialog = true;
            if (!row) {
                self.viewData = self.selected[0];
                return;
            }
            // 先清除所有复选状态
            self.$refs.table.clearSelection();
            // 设置选中行
            self.$refs.table.toggleRowSelection(row);
            self.viewData = row;
        },
        /* 代码生成并下载*/
        async generateCode(row) {
            let self = this;
            const tableOids = [];
            if (!row) {
                for (let table of self.selected) {
                    tableOids.push(table.oid);
                }
            } else {
                tableOids.push(row.oid);
            }
            const url = 'system/util/generateCode';
            const data = {tableOids: tableOids};
            const loading = self.loadingOpen('.generate-code');
            await download('get', url, data, 'zip').finally(() => {
                loading && loading.close();
            });
        },
    },
};
</script>

<style scoped lang="scss">
    .el-pagination {
        margin-top: 15px;
        text-align: center;
    }
</style>
