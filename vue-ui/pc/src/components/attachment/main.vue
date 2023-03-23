<template>
    <div class="attachment">
        <!--面板标签-->
        <panel-title>
            <el-button class="refresh-button" icon="el-icon-refresh" @click="refresh"/>
            <el-upload class="upload-button" :multiple="true" :show-file-list="false" :action="uploadUrl" :data="fileData" :limit="fileLimit" :headers="headers" :before-upload="beforeUpload" :on-success="onSuccess"
                    v-has-all-permissions="['attachment:insert']">
                <el-tooltip class="item" effect="dark" content="右键设置" placement="top">
                    <el-button type="success" icon="el-icon-circle-plus-outline" @contextmenu.prevent.native="isDialog = true">新增</el-button>
                </el-tooltip>
            </el-upload>
            <el-button type="warning" icon="el-icon-edit" :disabled="selected.length!==1" @click="changePrompt(selected[0])" v-has-all-permissions="['attachment:update']">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!selected.length>0" @click="deleteConfirm" v-has-all-permissions="['attachment:delete']">删除</el-button>
            <el-button type="primary" icon="el-icon-download" :disabled="!selected.length>0" @click="downloadFile" v-has-all-permissions="['attachment:download']">下载</el-button>
            <el-button type="info" icon="iconfont icon-shujudaochu" :disabled="!fileTotal>0" @click="exportExcel($route.meta.title,'.attachment')" v-has-all-permissions="['attachment:export']">导出</el-button>
        </panel-title>
        <!--查询框-->
        <div class="search-box" @keyup.enter="selectEvent" v-has-all-permissions="['attachment:select']">
            <el-input v-model="name" placeholder="请输入附件名称" clearable prefix-icon="el-icon-search"/>
            <el-select v-model="type" placeholder="请选择附件类型" clearable>
                <el-option v-for="(item, index) in typeList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
            </el-select>
            <el-select v-model="source" placeholder="请选择存储位置" clearable>
                <el-option v-for="(item, index) in sourceList" :key="index" :value="item.value" :label="item.text" v-show="item.status"/>
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="selectEvent">查询</el-button>
        </div>
        <!--数据列表-->
        <el-table ref="table" :data="fileList" :max-height="tableMaxHeight" :default-expand-all="false" @selection-change="selectionChange">
            <el-table-column type="selection" width="50"/>
            <el-table-column label="序号" align="center" show-overflow-tooltip width="80">
                <template slot-scope="scope">
                    <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="附件名称" align="center" width="150" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false" @click="changePrompt(scope.row)">{{ scope.row.name }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="type" label="附件类型" align="center" width="120" :formatter="formatType"/>
            <el-table-column prop="size" label="附件大小" align="center" width="120">
                <template slot-scope="scope">
                    <el-tooltip effect="dark" placement="top-start" :disabled="!scope.row.rawSize" :content="!scope.row.rawSize ? '-' : '原始大小：' + scope.row.rawSize.cusDiv(1024).cusToFixed(2) + 'K'">
                        <span>{{ scope.row.size.cusDiv(1024).cusToFixed(2) }}K</span>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column prop="source" label="存储位置" align="center" width="120" :formatter="formatSource"/>
            <el-table-column prop="path" label="文件地址" align="center" show-overflow-tooltip>
                <template slot-scope="scope">
                    <el-link :underline="false" target="_blank" :href="getFileUrl(scope.row)" @contextmenu.prevent.native="hbdtwx($event, getFileUrl(scope.row))">{{ scope.row.path }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="creatorName" label="创建人" align="center" width="120"/>
            <el-table-column prop="createTime" label="创建时间" align="center" width="160" :formatter="formatDate"/>
            <el-table-column label="操作" align="center" fixed="right" width="180">
                <template slot-scope="scope">
                    <el-button size="mini" plain type="danger" @click="setSelected(scope.row,DELETE)" v-has-all-permissions="['attachment:delete']">删除</el-button>
                    <el-button size="mini" plain type="primary" @click="setSelected(scope.row,DOWNLOAD)" v-has-all-permissions="['attachment:download']">下载</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页控件-->
        <el-pagination class="el-pagination-2" background layout='total, sizes, prev, pager, next, jumper' v-show="fileTotal>10"
                :page-sizes='pageSizes' :total="fileTotal" :page-size="pageSize" :current-page="pageNum"
                @size-change="sizeChange" @current-change="currentChange"/>
        <!--新增设置的右键窗口-->
        <el-dialog class="add-file" title="新增设置" :visible.sync="isDialog" :append-to-body="true" width="40%">
            <el-row :gutter="15">
                <el-form ref="form" :model="fileData" label-width="100px" label-position="left">
                    <el-col :span="24">
                        <el-form-item label="存储位置" prop="sources">
                            <el-checkbox-group v-model="fileData.sources">
                                <el-checkbox v-for="(item, index) in sourceList" :key="index" :label="item.value">{{ item.text }}</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="返回Base64" prop="getBase64">
                            <el-switch v-model="fileData.getBase64"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="保存Base64" prop="setBase64">
                            <el-switch v-model="fileData.setBase64"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="文件类型" prop="type">
                            <el-select v-model="fileData.type">
                                <el-option v-for="(item, index) in typeList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="图片压缩(KB)" prop="targetSize">
                            <el-input-number v-model="fileData.targetSize" :min="0" :max="10240" :step="100" controls-position="right" :step-strictly="false" style="width: auto"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item>
                            <el-button type="primary" @click="isDialog = false">保存设置</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item>
                            <el-button type="info" @click="cancel">恢复默认</el-button>
                        </el-form-item>
                    </el-col>
                </el-form>
            </el-row>
        </el-dialog>
    </div>
</template>

<script>
import file from './js/file';
import verify from '@assets/js/util/verify';
import copy from '@assets/js/mixin/copy';
import common from '@assets/js/mixin/common';
import {download} from '@assets/js/util/download';
import {deleteAttachment, saveAttachment, selectAttachment} from '@assets/js/api/attachment';

export default {
    name: 'attachment',
    mixins: [common, copy, file],
    data() {
        return {
            fileData: {
                // 文件类型
                type: 'other',
                // 存储位置（0:本地服务器、1:FTP文件服务器、2:FastDfs文件服务器）
                source: '0',
                // 多选随机保存
                sources: ['0'],
                // 前端是否返回base64
                getBase64: false,
                // 数据库是否保存base64
                setBase64: false,
                // 压缩目标单位KB（不传默认为0取默认压缩率）
                targetSize: 0,
            },
            // 附件名称
            name: '',
            // 右键新增设置
            isDialog: false,
            sourceList: [],
            typeList: [],
            type: '',
            source: '',
            deleteHint: '确认删除选中附件吗？',
        };
    },
    created() {
        this.init();
    },
    methods: {
        /* 初始化加载*/
        async init() {
            let self = this;
            await self.selectDict('.attachment', 'attachment-source').then((result) => {
                self.sourceList = result;
            });
            await self.selectDict('.attachment', 'attachment-type').then((result) => {
                self.typeList = result;
            });
            self.selectEvent();
        },
        /* 刷新*/
        refresh() {
            this.name = '';
            this.pageNum = 1;
            this.pageSize = 10;
            this.selectEvent();
        },
        /* 查询附件信息*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                name: self.name,
                type: self.type,
                source: self.source,
            };
            const loading = self.loadingOpen('.attachment');
            selectAttachment(data).then((response) => {
                if (response.flag) {
                    const data = response.data;
                    self.fileTotal = data.total;
                    self.fileList = data.list;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 格式化文件类型*/
        formatType(row) {
            return this.typeList.find((item) => {
                return item.value === row.type;
            }).text;
        },
        /* 格式化存储位置*/
        formatSource(row) {
            return this.sourceList.find((item) => {
                return item.value === row.source;
            }).text;
        },
        /* 修改文件名称*/
        changePrompt(row) {
            let self = this;
            // 判断操作权限
            if (!self.isHasAllPermissions(['attachment:update'])) {
                return;
            }
            self.$prompt('请输入新的附件名称', '', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputValidator: self.checkName,
            }).then(({value}) => {
                row.name = value;
                // 传入需要修改的参数（主键必传）
                const data = {uuid: row.uuid, name: row.name};
                self.saveAttachment(data);
            });
        },
        /* 重置文件名称校验*/
        checkName(value) {
            if (!value) {
                return '请输入新的文件名称';
            }
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return '不能包含空格';
            }
        },
        /* 修改附件信息*/
        saveAttachment(data) {
            let self = this;
            const loading = self.loadingOpen('.attachment');
            saveAttachment(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 删除附件*/
        deleteEvent() {
            let self = this;
            const loading = self.loadingOpen('.attachment');
            deleteAttachment(self.selected).then((response) => {
                if (response.flag) {
                    self.selectEvent();
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 下载附件*/
        async downloadFile() {
            let self = this;
            const url = 'system/attachment/download';
            const data = self.selected;
            const loading = self.loadingOpen('.attachment');
            await download('post', url, data, 'zip').finally(() => {
                loading && loading.close();
            });
        },
        /* 设置选中行*/
        setSelected(row, action) {
            // 先清除所有复选状态
            this.$refs.table.clearSelection();
            // 设置选中行
            this.$refs.table.toggleRowSelection(row);
            if (action === this.DELETE) {
                this.deleteConfirm(row);
            } else if (action === this.DOWNLOAD) {
                this.downloadFile();
            }
        },
        /* 取消新增设置*/
        cancel() {
            this.isDialog = false;
            this.fileData.source = '';
            this.$refs.form.resetFields();
        },
    },
};
</script>

<style lang="scss">
    //右键附件设置弹窗
    .add-file {
        .el-form {
            .el-input, .el-select, .el-input__inner {
                width: 150px;
            }
        }
    }
</style>

<style scoped lang="scss">
    //附件新增按钮
    .upload-button {
        margin-left: 10px;
        margin-right: 10px;

        .item {
            width: 89px;
            height: 36.4px;
        }
    }
</style>
