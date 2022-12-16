<template>
    <div class="cache">
        <!--面板标签-->
        <panel-title/>
        <!--主界面-->
        <el-row>
            <!--缓存列表-->
            <el-col :span="8" class="pr-10">
                <el-card>
                    <div slot="header"><span>列表</span></div>
                    <el-row style="height: 41px">
                        <el-col :span="12" class="pr-10">
                            <el-input v-model="key" placeholder="请输入key*" clearable prefix-icon="el-icon-search" @keyup.enter.native="selectCacheInfo"/>
                        </el-col>
                        <el-button type="primary" icon="el-icon-search" @click="selectCacheInfo" @contextmenu.prevent.native="setFormatTree">查询</el-button>
                        <el-button type="info" icon="el-icon-sort" @click="foldAll" v-show="formatTree">折叠</el-button>
                    </el-row>
                    <!--列表结构-->
                    <el-table :data="cacheInfoList" :highlight-current-row="true" :height="tableMaxHeight-41" @row-click="rowClickLine" @row-contextmenu="copy" v-show="!formatTree">
                        <el-table-column label="序号" width="80">
                            <template slot-scope="scope">
                                <span>{{ scope.$index + 1 }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="类型" width="100">
                            <template slot-scope="scope">
                                <el-tag style="height: auto;line-height: 24px;width: 64px;text-align: center;" :type="getDataTypeStyle(scope.row.type)">{{ scope.row.type }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="键名" show-overflow-tooltip>
                            <template slot-scope="scope">
                                <span>{{ scope.row.key }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button size="mini" type="text" @click="deleteKey(scope.row,$event)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!--树状结构-->
                    <el-table ref="table-key" :data="cacheInfoTree" :highlight-current-row="true" row-key="key" :height="tableMaxHeight-41" :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                              @row-click="rowClick" @row-contextmenu="copy" v-show="formatTree">
                        <el-table-column label="序号" type="" width="150" :formatter="formatIndex"/>
                        <el-table-column label="键名" prop="name" show-overflow-tooltip/>
                        <el-table-column label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button size="mini" type="text" @click="deleteKey(scope.row,$event)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <!--键值列表-->
            <el-col :span="8" class="pr-10">
                <el-card>
                    <div slot="header"><span>键值</span></div>
                    <el-row style="height: 41px">
                        <el-col :span="12" class="pr-10">
                            <el-input v-model="field" placeholder="请输入field/value" clearable prefix-icon="el-icon-search" @keyup.enter.native="filterCacheInfoDetails"/>
                        </el-col>
                        <el-button type="primary" icon="el-icon-search" @click="filterCacheInfoDetails">查询</el-button>
                    </el-row>
                    <el-table :data="details.table" :height="tableMaxHeight-41" :highlight-current-row="true" @row-click="rowClickDetails" @row-contextmenu="copy">
                        <el-table-column label="INDEX" prop="key" width="80"/>
                        <el-table-column :label="details.type === 'HASH' ? 'FIELD':  'VALUE'" prop="value" show-overflow-tooltip/>
                        <el-table-column label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button size="mini" type="text" @click="deleteField(scope.row,$event)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <!--详情列表-->
            <el-col :span="8">
                <el-card>
                    <div slot="header"><span>详情</span></div>
                    <div class="underline" :style="{height: tableMaxHeight+16+'px','margin-top':'-17px'}">
                        <el-row class="group-item underline">
                            <el-col :span="3">
                                <span>TYPE</span>
                            </el-col>
                            <el-col :span="21">
                                <el-tag v-if="details.type" :type="getDataTypeStyle(details.type)">{{ details.type }}</el-tag>
                                <span v-else>&emsp;</span>
                            </el-col>
                        </el-row>
                        <el-row class="group-item underline">
                            <el-col :span="3">
                                <span>KEY</span>
                            </el-col>
                            <el-col :span="21">
                                <span>{{ details.key || '&emsp;' }}</span>
                            </el-col>
                        </el-row>
                        <el-row class="group-item underline" v-show="details.type==='HASH'">
                            <el-col :span="3">
                                <span>FIELD</span>
                            </el-col>
                            <el-col :span="21">
                                <span>{{ details.field || '&emsp;' }}</span>
                            </el-col>
                        </el-row>
                        <el-row class="group-item underline">
                            <el-col :span="3">
                                <span>EXPIRE</span>
                            </el-col>
                            <el-col :span="21">
                                <span>{{ details.expireHum || '&emsp;' }}</span>
                            </el-col>
                        </el-row>
                        <el-row class="group-item underline">
                            <el-col :span="3">
                                <span>TTL</span>
                            </el-col>
                            <el-col :span="21">
                                <el-input :maxlength="12" v-model="details.expire" @input="handleInput" clearable/>
                            </el-col>
                        </el-row>
                        <el-row class="group-item">
                            <el-input ref="textarea" type="textarea" autosize v-model="details.content"/>
                        </el-row>
                        <div style="position: absolute;right: 30px;bottom: 30px;">
                            <el-button type="success" icon="el-icon-edit" @click="saveCacheInfo" :disabled="!details.type">保存</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import copy from '@assets/js/mixin/copy';
import common from '@assets/js/mixin/common';
import {deleteCacheInfo, saveCacheInfo, selectCacheInfo, selectCacheInfoDetails} from '@assets/js/api/cache';

export default {
    name: 'cache',
    mixins: [common, copy],
    data() {
        return {
            key: '',
            field: '',
            details: {},
            detailsList: [],
            cacheInfoList: [],
            cacheInfoTree: [],
            treeIndexMap: {},
            formatTree: false,
        };
    },
    created() {
        this.selectCacheInfo();
    },
    mounted() {
        this.setTextareaMaxHeight(this.tableMaxHeight);
    },
    methods: {
        /* 设置树形查询*/
        setFormatTree() {
            this.formatTree = !this.formatTree;
            this.selectCacheInfo();
        },
        /* 查询缓存列表*/
        selectCacheInfo() {
            let self = this;
            const loading = self.loadingOpen('.cache');
            const params = {
                formatTree: this.formatTree,
                key: self.key,
            };
            selectCacheInfo(params).then((response) => {
                if (response.flag) {
                    if (self.formatTree) {
                        const treeList = format.list(response.data, 'root', 'key', 'parent');
                        self.cacheInfoTree = format.sortList(treeList, 'name');
                        self.setIndexMap(self.cacheInfoTree);
                    } else {
                        self.cacheInfoList = response.data;
                    }
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 查询缓存详情*/
        selectCacheInfoDetails(key) {
            let self = this;
            const params = {key: key};
            const loading = self.loadingOpen('.cache');
            selectCacheInfoDetails(params).then((response) => {
                if (response.flag) {
                    const table = [];
                    const detailsList = [];
                    const details = response.data;
                    if (!details) {
                        self.details = {};
                        self.detailsList = [];
                        self.messageWaning('该key已被删除');
                        return;
                    }
                    if (details.type === 'STRING') {
                        detailsList.push({key: 1, value: details.value});
                        table.push({key: 1, value: self.disposeBigValue(details.value)});
                    } else if (details.type === 'HASH') {
                        let index = 0;
                        for (let key in details.value) {
                            detailsList.push({key: index + 1, value: key});
                            table.push({key: index + 1, value: key});
                            index++;
                        }
                    } else {
                        details.value.forEach((value, index) => {
                            detailsList.push({key: index + 1, value: value});
                            table.push({key: index + 1, value: self.disposeBigValue(value)});
                        });
                    }
                    details.table = table;
                    self.details = details;
                    self.detailsList = detailsList;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 过滤缓存内容*/
        filterCacheInfoDetails() {
            let self = this;
            const table = [];
            for (let item of self.detailsList) {
                if (typeof item.value === 'object') {
                    if (JSON.stringify(item.value).indexOf(self.field) === -1) {
                        continue;
                    }
                } else {
                    if (item.value.toString().indexOf(self.field) === -1) {
                        continue;
                    }
                }
                if (self.details.type === 'HASH') {
                    table.push(item);
                } else {
                    table.push({key: item.key, value: self.disposeBigValue(item.value)});
                }
            }
            self.details.table = table;
        },
        /* 选中事件*/
        nodeClick(node) {
            if (!node.children) {
                this.selectCacheInfoDetails(node.key);
            }
        },
        /* 点击行事件*/
        rowClick(row) {
            if (row.children) {
                this.details = {};
                row.expanded = !row.expanded;
                this.$refs['table-key']['toggleRowExpansion'](row, row.expanded);
            } else {
                this.selectCacheInfoDetails(row.key);
            }
        },
        /* 列表结构点击行事件*/
        rowClickLine(row) {
            this.selectCacheInfoDetails(row.key);
        },
        /* 键值点击行事件*/
        rowClickDetails(row) {
            if (this.details.type === 'STRING') {
                if (typeof this.details.value === 'object') {
                    this.details.content = JSON.stringify(this.details.value);
                } else {
                    this.details.content = this.details.value;
                }
            } else if (this.details.type === 'HASH') {
                this.details.field = row.value;
                this.details.content = this.details.value[row.value];
            } else {
                this.details.field = row.key - 1;
                this.details.content = this.details.value[row.key - 1];
            }
            this.details.index = row.key - 1;
            this.details.oldValue = global.deepClone(this.details.content);
            this.details = Object.assign({}, this.details);
        },
        /* 设置序号*/
        setIndexMap(tree, parent) {
            tree.forEach((item, index) => {
                let order = parent || index + 1;
                order = order.toString().includes('-') ? order + (index + 1) : order;
                this.treeIndexMap[item.key] = order;
                if (item.children) {
                    order += '-';
                    return this.setIndexMap(item.children, order);
                }
            });
        },
        /* 格式化序号*/
        formatIndex(row) {
            return this.treeIndexMap[row.key];
        },
        /* 折叠全部*/
        foldAll() {
            for (const row of this.cacheInfoTree) {
                row.expanded = false;
                this.$refs['table-key'].toggleRowExpansion(row, false);
            }
        },
        /* 处理大key*/
        disposeBigValue(value) {
            if (typeof value === 'object') {
                value = JSON.stringify(value);
            }
            return value.length > 100 ? value.substr(0, 100) + '......' : value;
        },
        /* 限制过期时间输入类型*/
        handleInput(value) {
            let expire = value.replace(/[^\-?\d]/g, '');
            if (expire === '-' || expire === '--') {
                this.details.expire = '-';
                return;
            }
            if (expire.endsWith('-')) {
                this.details.expire = expire.substring(0, expire.length - 1);
                return;
            }
            if (expire === '') {
                return;
            }
            this.details.expire = Math.max(expire, -1);
        },
        /* 设置文本输入框的最大高度*/
        setTextareaMaxHeight(height) {
            this.$refs.textarea.$el.firstChild.style.setProperty('max-height', `${height - 270}px`);
        },
        /* 删除key*/
        deleteKey(row, event) {
            event.preventDefault();
            event.stopPropagation();
            const data = {key: row.key + `${row.children ? '*' : ''}`};
            this.deleteCacheInfo(data);
        },
        /* 删除field*/
        deleteField(row, event) {
            event.preventDefault();
            event.stopPropagation();
            this.rowClickDetails(row);
            const data = {
                key: this.details.key,
                type: this.details.type,
                field: this.details.field,
                value: this.details.content,
            };
            this.deleteCacheInfo(data);
        },
        /* 删除缓存信息*/
        deleteCacheInfo(data) {
            let self = this;
            const loading = self.loadingOpen('.cache');
            deleteCacheInfo(data).then((response) => {
                if (response.flag) {
                    if (data.type && typeof self.details.value === 'object' && Object.keys(self.details.value).length > 1) {
                        self.selectCacheInfoDetails(data.key);
                    } else {
                        self.selectCacheInfo();
                        self.details = {};
                    }
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 保存缓存信息*/
        saveCacheInfo() {
            let self = this;
            const data = {
                key: self.details.key,
                type: self.details.type,
                field: self.details.field,
                value: self.details.oldValue,
                newValue: self.details.content,
                expire: self.details.expire,
                expireHum: self.details.expireHum,
            };
            const loading = self.loadingOpen('.cache');
            saveCacheInfo(data).then((response) => {
                if (response.flag) {
                    if (self.details.type === 'HASH') {
                        self.details.value[self.details.field] = self.details.content;
                    } else {
                        self.detailsList[self.details.index] = {key: self.details.index + 1, value: self.details.content};
                        self.filterCacheInfoDetails();
                    }
                    self.messageSuccess(response.msg);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 复制按钮*/
        copy(row, column, event) {
            event.preventDefault();
            event.stopPropagation();
            const label = column.label;
            if (['键名', 'FIELD'].includes(label)) {
                const msg = row[label === '键名' ? 'key' : 'value'];
                this.hbdtwx(event, msg, label);
            }
        },
        /* 获取数据类型样式*/
        getDataTypeStyle(type) {
            if (type === 'STRING') {
                return 'info';
            }
            if (type === 'HASH') {
                return 'success';
            }
            if (type === 'LIST') {
                return '';
            }
            if (type === 'SET') {
                return 'warning';
            }
            if (type === 'ZSET') {
                return 'danger';
            }
        },
    },
    watch: {
        tableMaxHeight(value) {
            this.setTextareaMaxHeight(value);
        },
    },
};
</script>

<style lang="scss">
    .cache {
        .group-item .el-input__inner {
            border: 0;
            padding-left: 0;
        }

        .group-item .el-textarea__inner {
            padding: 3px;
            margin-top: 10px;
            min-height: 40px !important;
        }
    }
</style>

<style scoped lang="scss">
    .cache {
        .group-item {
            height: 53px;
            line-height: 53px;
            font-size: 14px;
            font-weight: bold;
            padding-left: 10px;
            color: #909399;
        }

        .underline {
            border-bottom: 1px solid #EBEEF5;
        }
    }
</style>

