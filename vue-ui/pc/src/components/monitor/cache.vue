<template>
    <div class="cache">
        <!-- 面板标签-->
        <panel-title>
            <el-button type="info" icon="el-icon-s-platform" @click="openWebssh" v-has-all-permissions="['cache:webssh']">控制台</el-button>
        </panel-title>
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
                        <el-button type="primary" icon="el-icon-search" @click="selectCacheInfo" @contextmenu.prevent.native="setFormatTree" v-has-all-permissions="['cache:select']">查询</el-button>
                        <el-button type="info" icon="el-icon-sort" @click="foldAll" v-show="formatTree">折叠</el-button>
                        <el-button type="success" icon="el-icon-circle-plus-outline" @click="openCreateKeyDialog" v-has-all-permissions="['cache:save']">新增</el-button>
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
                        <el-table-column label="操作" width="80" v-if="isHasAllPermissions(['cache:delete'])">
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
                        <el-table-column label="操作" width="80" v-if="isHasAllPermissions(['cache:delete'])">
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
                        <el-button type="primary" icon="el-icon-search" @click="filterCacheInfoDetails" v-has-all-permissions="['cache:select']">查询</el-button>
                    </el-row>
                    <el-table :data="details.table" :height="tableMaxHeight-41" :highlight-current-row="true" @row-click="rowClickDetails" @row-contextmenu="copy">
                        <el-table-column label="INDEX" prop="key" width="80"/>
                        <el-table-column :label="details.type === 'HASH' ? 'FIELD':  'VALUE'" prop="value" show-overflow-tooltip/>
                        <el-table-column label="操作" width="80" v-if="isHasAllPermissions(['cache:delete'])">
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
                                <el-input :maxlength="12" v-model="details.expire" @input="handleInput" :readonly="!isHasAllPermissions(['cache:save'])" clearable/>
                            </el-col>
                        </el-row>
                        <el-row class="group-item">
                            <el-input ref="textarea" type="textarea" autosize v-model="details.content" :readonly="!isHasAllPermissions(['cache:save'])"/>
                        </el-row>
                        <div style="position: absolute;right: 30px;bottom: 30px;">
                            <el-button type="success" icon="el-icon-edit" @click="saveCacheInfo" :disabled="!details.type" v-has-all-permissions="['cache:save']">保存</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <!--新增弹窗-->
        <el-dialog title="新增缓存" :visible.sync="createKeyDialog" :close-on-click-modal="false" width="35%">
            <el-form ref="form" :model="addForm" :rules="rules" label-width="60px">
                <el-row>
                    <el-col :span="11">
                        <el-form-item label="类型" prop="type">
                            <el-select v-model="addForm.type" clearable>
                                <el-option v-for="(item, index) of typeAry" :key="index" :value="item" :label="item" :disabled="item==='ZSET'"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="10">
                        <el-form-item label="过期" prop="expire" class="required">
                            <el-input v-model="addForm.expire " type="number" :min="-1" style="width: 91.6%" clearable/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="键名" prop="key" class="required">
                    <el-col :span="20">
                        <el-input v-model="addForm.key" style="width: 100%" clearable/>
                    </el-col>
                </el-form-item>
                <el-form-item label="键值" v-if="addForm.type==='STRING'" class="required" prop="value">
                    <el-col :span="20">
                        <el-input class="addFormValue" ref="addFormValue" v-model="addForm.value" type="textarea" autosize clearable style="width: 100%"/>
                    </el-col>
                </el-form-item>
                <el-form-item label="键值" v-else-if="addForm.type==='SET'" class="required" prop="value">
                    <el-row class="form-item-th">
                        <el-col :span="21">
                            <span>Member</span>
                        </el-col>
                        <el-col :span="3">
                            <span>Action</span>
                        </el-col>
                    </el-row>
                    <div style="max-height: 320px;overflow-y: auto;">
                        <el-row :class="index>0?'form-item-tr':'form-item-tt'" v-for="(item, index) in addForm.set" :key="index">
                            <el-col :span="21">
                                <el-input v-model="addForm.set[index]" style="width: 95.1%" clearable class="set-value cache-value"/>
                            </el-col>
                            <el-col :span="3">
                                <el-button size="mini" type="text" :icon="getIcon(index,'set')" @click="operateRow(index,'set')"/>
                            </el-col>
                        </el-row>
                    </div>
                </el-form-item>
                <el-form-item label="键值" v-else-if="addForm.type==='LIST'" class="required" prop="value">
                    <el-row class="form-item-th">
                        <el-col :span="21">
                            <span>Value</span>
                        </el-col>
                        <el-col :span="3">
                            <span>Action</span>
                        </el-col>
                    </el-row>
                    <div style="max-height: 320px;overflow-y: auto;">
                        <el-row :class="index>0?'form-item-tr':'form-item-tt'" v-for="(item, index) in addForm.list" :key="index">
                            <el-col :span="21">
                                <el-input v-model="addForm.list[index]" style="width: 95.1%" clearable class="list-value cache-value"/>
                            </el-col>
                            <el-col :span="3">
                                <el-button size="mini" type="text" :icon="getIcon(index,'list')" @click="operateRow(index,'list')"/>
                            </el-col>
                        </el-row>
                    </div>
                </el-form-item>
                <el-form-item label="键值" v-else-if="addForm.type==='HASH'" class="required" prop="value">
                    <el-row class="form-item-th">
                        <el-col :span="11">
                            <span>Field</span>
                        </el-col>
                        <el-col :span="10">
                            <span>Value</span>
                        </el-col>
                        <el-col :span="3">
                            <span>Action</span>
                        </el-col>
                    </el-row>
                    <div style="max-height: 320px;overflow-y: auto;">
                        <el-row :class="index>0?'form-item-tr':'form-item-tt'" v-for="(item, index) in addForm.hash" :key="index">
                            <el-col :span="11">
                                <el-input v-model="addForm.hash[index].field" clearable class="field-value cache-value"/>
                            </el-col>
                            <el-col :span="10">
                                <el-input v-model="addForm.hash[index].value" clearable class="value-value cache-value"/>
                            </el-col>
                            <el-col :span="3">
                                <el-button size="mini" type="text" :icon="getIcon(index,'hash')" @click="operateRow(index,'hash')"/>
                            </el-col>
                        </el-row>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="insertCacheInfo">保存</el-button>
                    <el-button type="info" @click="resetForm">重置</el-button>
                    <el-button @click="createKeyDialog=false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import copy from '@assets/js/mixin/copy';
import common from '@assets/js/mixin/common';
import storage from '@assets/js/config/storage';
import constant from '@assets/js/common/constant';
import {
    deleteCacheInfo,
    getRedis,
    insertCacheInfo,
    saveCacheInfo,
    selectCacheInfo,
    selectCacheInfoDetails,
} from '@assets/js/api/cache';

export default {
    name: 'cache',
    mixins: [common, copy],
    data() {
        const checkExpire = (rule, value, callback) => {
            if (!value) {
                return callback(new Error('请输入过期时间'));
            }
            this.addForm.expire = Math.max(value, -1);
            return callback();
        };
        const checkBlank = (rule, value, callback) => {
            if (!value || !value.trim()) {
                return callback(new Error('键名不能为空'));
            }
            return callback();
        };
        const checkValue = (rule, value, callback) => {
            if (this.addForm.type === 'STRING') {
                if (!value || !value.trim()) {
                    return callback(new Error('键值不能为空'));
                }
            } else if (this.addForm.type === 'SET') {
                this.checkInput('set-value', 'member');
            } else if (this.addForm.type === 'LIST') {
                this.checkInput('list-value', 'value');
            } else if (this.addForm.type === 'HASH') {
                this.checkInput('field-value', 'field');
                this.checkInput('value-value', 'value');
            }
            return callback();
        };
        return {
            key: '',
            field: '',
            details: {},
            detailsList: [],
            cacheInfoList: [],
            cacheInfoTree: [],
            treeIndexMap: {},
            formatTree: false,
            createKeyDialog: false,
            addForm: {
                type: 'STRING',
                expire: -1,
                key: '',
                value: '',
                set: [''],
                list: [''],
                hash: [{field: '', value: ''}],
            },
            typeAry: ['STRING', 'HASH', 'LIST', 'SET', 'ZSET'],
            isValid: true,
            rules: {
                type: [
                    {required: true, message: '请选择数据类型', trigger: 'change'},
                ],
                expire: [
                    {validator: checkExpire, trigger: 'change'},
                ],
                key: [
                    {validator: checkBlank, trigger: 'change'},
                ],
                value: [
                    {validator: checkValue, trigger: 'change'},
                ],
            },
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
                } else {
                    self.messageError(response.msg);
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
                this.details.content = this.formatValue(this.details.value);
            } else if (this.details.type === 'HASH') {
                this.details.field = row.value;
                this.details.content = this.formatValue(this.details.value[row.value]);
            } else {
                this.details.field = row.key - 1;
                this.details.content = this.formatValue(this.details.value[row.key - 1]);
            }
            this.details.index = row.key - 1;
            this.details.oldValue = global.deepClone(this.details.content);
            this.details = Object.assign({}, this.details);
        },
        /* 序列化值*/
        formatValue(value) {
            if (typeof value === 'object') {
                return JSON.stringify(value);
            }
            return value;
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
                        self.detailsList[self.details.index] = {
                            key: self.details.index + 1,
                            value: self.details.content,
                        };
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
        /* 打开控制台*/
        openWebssh() {
            let self = this;
            getRedis('host').then((response) => {
                if (response.flag) {
                    const token = format.null(storage.getItem('token'));
                    const href = `${constant.domainName}ssh/host/${response.data}?token=${token}`;
                    window.open(href, '_blank');
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        /* 打开新增Key弹窗*/
        openCreateKeyDialog() {
            this.createKeyDialog = !this.createKeyDialog;
            if (this.createKeyDialog) {
                this.$nextTick(() => {
                    this.$refs.addFormValue.$el.firstChild.style.setProperty('max-height', '320px');
                });
            }
        },
        /* 新增key*/
        operateRow(index, type) {
            if (this.addForm[type].length - 1 === index) {
                this.addForm[type].push(type === 'hash' ? {field: '', value: ''} : '');
            } else {
                this.addForm[type].splice(index, 1);
            }
        },
        /* 获取新增删除图标*/
        getIcon(index, type) {
            return this.addForm[type].length - 1 === index ? 'iconfont icon-xinzeng1' : 'iconfont icon-shanchu2';
        },
        /* 保存新key*/
        insertCacheInfo() {
            let self = this;
            self.$refs.form.validate((valid) => {
                if (valid && self.isValid) {
                    const hash = {};
                    for (let item of self.addForm.hash) {
                        hash[item.field] = item.value;
                    }
                    const data = global.deepClone(self.addForm);
                    data.hash = hash;
                    insertCacheInfo(data).then((response) => {
                        self.message(response.flag ? 'success' : 'error', response.msg);
                    });
                }
            });
        },
        /* 重置*/
        resetForm() {
            this.addForm.key = '';
            this.addForm.value = '';
            this.addForm.set = [''];
            this.addForm.list = [''];
            this.addForm.hash = [{field: '', value: ''}];
        },
        /* 校验输入框*/
        checkInput(className, msg) {
            let isValid = true;
            for (let obj of document.getElementsByClassName(className)) {
                const value = obj.firstElementChild.value;
                const hasError = obj.firstElementChild.className.split(' ').includes('form-item-tr-error');
                if (!value || !value.trim()) {
                    isValid = false;
                    if (!hasError) {
                        $(obj.firstElementChild).addClass('form-item-tr-error');
                        const div = document.createElement('div');
                        div.setAttribute('class', 'form-item-tr-error-msg');
                        div.innerHTML = `【${msg}】不能为空`;
                        obj.parentElement.appendChild(div);
                    }
                } else {
                    if (hasError) {
                        const div = obj.parentNode.lastChild;
                        obj.parentElement.removeChild(div);
                        $(obj.firstElementChild).removeClass('form-item-tr-error');
                    }
                }
            }
            this.isValid = isValid;
        },
    },
    watch: {
        tableMaxHeight(value) {
            this.setTextareaMaxHeight(value);
        },
        'addForm.type'(value) {
            if (!this.createKeyDialog) {
                return;
            }
            this.isValid = true;
            this.$refs.form.clearValidate();
            if (value === 'STRING') {
                this.$nextTick(() => {
                    this.$refs.addFormValue.$el.firstChild.style.setProperty('max-height', '320px');
                });
            } else {
                for (let obj of document.getElementsByClassName('cache-value')) {
                    if (obj.firstElementChild.className.split(' ').includes('form-item-tr-error')) {
                        obj.parentElement.removeChild(obj.parentNode.lastChild);
                        $(obj.firstElementChild).removeClass('form-item-tr-error');
                    }
                }
            }
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

        .addFormValue .el-textarea__inner {
            min-height: 40px !important;
        }

        .form-item-tr-error {
            border-color: #F56C6C;
        }

        .form-item-tr-error-msg {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
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

        .form-item-th {
            color: #909399;
            font-weight: bold;
        }

        .form-item-tt {
            padding-top: 10px;
        }

        .form-item-tr {
            padding-top: 23px;
        }
    }
</style>
