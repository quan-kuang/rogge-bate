<template>
    <div class="alter">
        <panel-title title="修改字典"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="上级字典" class="required">
                        <el-select ref="parent" v-model="form.parentId" placeholder="请选择上级字典" clearable @clear="clear" :disabled="true">
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
                        <el-input v-model="form.uuid" placeholder="请输入字典主键" maxlength="36" clearable :disabled="true"/>
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
    name: 'alter',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {},
            cloneForm: {},
        };
    },
    async activated() {
        this.params.isFlush = false;
        const item = global.deepClone(this.params.item);
        this.option = item.level === 1 ? global.deepClone({uuid: 'root', text: '根节点'}) : {uuid: item.parentId, text: item.parentName};
        this.form.parentId = item.parentId;
        this.defaultKeys = [item.parentId];
        this.$refs.treeSelect.setCurrentKey(item.parentId);
        this.form = item;
        this.cloneForm = this.params.item;
    },
    deactivated() {
        this.cloneForm = null;
    },
    methods: {
        /* 修改字典信息*/
        saveDict() {
            let self = this;
            const loading = self.loadingOpen('.alter');
            saveDict(self.form).then((response) => {
                if (response.flag) {
                    self.params.isFlush = true;
                    self.confirm(`${response.msg}，立即返回？`, self.goBack, null);
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
