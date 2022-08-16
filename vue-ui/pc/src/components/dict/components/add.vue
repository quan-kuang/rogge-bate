<template>
    <div class="add">
        <panel-title title="新增字典"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="上级字典" prop="parentId">
                        <el-select ref="parent" v-model="form.parentId" placeholder="请选择上级字典" clearable @clear="clear">
                            <el-option style="background-color: white" :value="option.uuid" :label="option.text">
                                <tree-select ref="treeSelect" :props="props" :data="dictList" :defaultKeys="defaultKeys" @nodeClick="nodeClick"/>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="字典键值" prop="value">
                        <el-input v-model="form.value" placeholder="请输入字典键值" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="字典名称" prop="text">
                        <el-input v-model="form.text" placeholder="请输入字典名称" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="字典主键" prop="uuid">
                        <el-input v-model="form.uuid" placeholder="请输入字典主键" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="字典排序" prop="sort">
                        <el-input v-model="form.sort" placeholder="请输入排序" maxlength="2" type="number" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="启用状态" prop="status" :required="true">
                        <el-switch v-model="form.status"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="20">
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark" maxlength="36"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item>
                <el-button type="success" @click="submit">保存</el-button>
                <el-button type="warning" @click="reset">重置</el-button>
                <el-button type="primary" @click="goBack">返回</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import mixin from '../js/mixin';
import global from '@assets/js/util/global';
import {saveDict} from '@assets/js/api/dict';

export default {
    name: 'add',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {
                uuid: null,
                value: '',
                text: '',
                parentId: '',
                sort: null,
                status: true,
                remark: '',
            },
        };
    },
    async activated() {
        await this.selectDict();
        this.params.isFlush = false;
        const item = this.params.item;
        if (!item) {
            this.option = global.deepClone({uuid: 'root', text: '根节点'});
            this.form.parentId = 'root';
            this.defaultKeys = [];
            this.$refs.treeSelect.setCurrentKey(null);
        } else {
            this.option.uuid = item.uuid;
            this.option.text = item.text;
            this.form.parentId = item.uuid;
            this.defaultKeys = [item.uuid];
            this.$refs.treeSelect.setCurrentKey(item.uuid);
        }
    },
    methods: {
        /* 查询字典列表*/
        async selectDict() {
            let self = this;
            const data = {
                parentId: 'root',
                status: true,
            };
            await self.initDict(data, '.add').then((result) => {
                self.dictList = result;
            });
        },
        /* 清空表单输入框*/
        reset() {
            this.$refs.form.resetFields();
            this.$refs.treeSelect.setCurrentKey([]);
        },
        /* 保存字典信息*/
        saveDict() {
            let self = this;
            const data = global.deepClone(self.form);
            data.parentName = self.$refs.parent.selected.label;
            const loading = self.loadingOpen('.add');
            saveDict(data).then((response) => {
                if (response.flag) {
                    self.params.isFlush = true;
                    self.confirm(`${response.msg}，继续添加？`
                        , function () {
                            self.reset();
                            self.form.parentId = data.parentId;
                        }, function () {
                            self.reset();
                            self.goBack();
                        });
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
