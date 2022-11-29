<template>
    <div class="stocks">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="refresh('')"/>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!dataTotal>0" @click="exportExcel($route.meta.title,'.stocks')" v-has-all-permissions="['stocks:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="getList">
            <el-select v-model="query.plate" clearable @change="getList">
                <el-option label="全部" value=""/>
                <el-option label="科创板" value="KC"/>
                <el-option label="创业板" value="CY"/>
                <el-option label="沪深A股" value="HSA"/>
                <el-option label="沪市A股" value="HA"/>
                <el-option label="深市A股" value="SA"/>
                <el-option label="沪市B股" value="HB"/>
                <el-option label="深市B股" value="SB"/>
            </el-select>
            <el-select v-model="query.sortField" clearable @change="getList">
                <el-option label="默认" value=""/>
                <el-option label="涨幅" value="f3"/>
                <el-option label="股价" value="f2"/>
                <el-option label="换手率" value="f8"/>
            </el-select>
            <el-input v-model.trim="query.code" placeholder="请输入股票代码" clearable prefix-icon="el-icon-search"/>
            <el-input v-model.trim="query.name" placeholder="请输入股票名称" clearable prefix-icon="el-icon-search"/>
            <el-button type="primary" icon="el-icon-search" @click="getList">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="dataList" :max-height="tableMaxHeight" :cell-class-name="setCellClass">
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (query.pageNum - 1) * query.pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="f12" label="代码" align="center" show-overflow-tooltip/>
            <el-table-column prop="f14" label="名称" align="center"/>
            <el-table-column prop="f100" label="行业" align="center"/>
            <el-table-column prop="f2" label="最新" align="center"/>
            <el-table-column prop="f3" label="涨幅" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.f3 }}%</span><i :class="getIconName(scope.row)"/>
                </template>
            </el-table-column>
            <el-table-column prop="f4" label="涨跌" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.f4 }}</span><i :class="getIconName(scope.row)"/>
                </template>
            </el-table-column>
            <el-table-column prop="f15" label="最高" align="center"/>
            <el-table-column prop="f16" label="最低" align="center"/>
            <el-table-column prop="f17" label="开盘" align="center"/>
            <el-table-column prop="f18" label="昨收" align="center"/>
            <el-table-column prop="f10" label="量比" align="center"/>
            <el-table-column prop="f8" label="换手率" align="center" :formatter="formatRate"/>
            <el-table-column prop="f23" label="市净率" align="center" :formatter="formatRate"/>
            <el-table-column prop="f20" label="总市值" align="center" :formatter="formatMarketValue"/>
            <el-table-column prop="f124" label="时间" align="center" width="180" :formatter="formatDate"/>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="dataTotal>10"
                       :page-sizes='pageSizes' :total="dataTotal" :page-size="query.pageSize" :current-page="query.pageNum"
                       @size-change="sizeChange" @current-change="currentChange"/>
    </div>
</template>

<script>
import common from '@assets/js/mixin/common';
import {getList} from '@assets/js/api/stocks';

export default {
    name: 'stocks',
    mixins: [common],
    data() {
        return {
            query: {
                code: '',
                name: '',
                plate: '',
                sortField: '',
                pageNum: 1,
                pageSize: 10,
            },
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /* 刷新*/
        refresh() {
            for (let key in this.query) {
                this.query[key] = '';
            }
            this.query.pageNum = 1;
            this.query.pageSize = 10;
            this.getList();
        },
        /* 查询股票信息*/
        getList() {
            let self = this;
            const loading = self.loadingOpen('.stocks');
            getList(self.query).then((response) => {
                if (response.flag) {
                    if (!response.data) {
                        self.dataTotal = 0;
                        self.dataList = [];
                        return;
                    }
                    self.dataTotal = response.data.total;
                    self.dataList = response.data.diff;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 更改分页大小*/
        sizeChange(pageSize) {
            this.query.pageSize = pageSize;
            this.getList();
        },
        /* 更改当前页*/
        currentChange(pageNum) {
            this.query.pageNum = pageNum;
            this.getList();
        },
        /* 格式化换手/市净率*/
        formatRate(row, column) {
            return row[column.property] + '%';
        },
        /* 格式化总市值*/
        formatMarketValue(row) {
            return Number(row.f20).cusDiv(100000000).cusToFixed(2) + 'E';
        },
        /* 格式化时间*/
        formatDate(row, column, cellValue) {
            return Number(cellValue * 1000).toDate();
        },
        /* 设置行背景颜色*/
        setCellClass({row, column}) {
            if (['f3', 'f4'].includes(column.property)) {
                if (Number(row.f3) > 0) {
                    return 'cell-1';
                } else if (Number(row.f3) < 0) {
                    return 'cell-2';
                }
            }
        },
        /* 获取上涨下跌图标*/
        getIconName(row) {
            if (Number(row.f3) > 0) {
                return 'el-icon-top';
            } else if (Number(row.f3) < 0) {
                return 'el-icon-bottom';
            }
            return '';
        },
    },
};
</script>

<style lang="scss">
    .stocks {
        .el-table .cell-1 {
            color: orangered;
        }

        .el-table .cell-2 {
            color: darkgreen;
        }
    }
</style>
