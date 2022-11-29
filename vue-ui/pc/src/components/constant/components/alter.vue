<template>
    <div class="add">
        <panel-title title="新增常量信息表"/>
        <el-form ref="form" label-width="100px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="键名" prop="key">
                        <el-input v-model="form.key" placeholder="请输入键名" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="键值" prop="value">
                        <el-input v-model="form.value" placeholder="请输入键值" maxlength="18" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="16">
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark" maxlength="180"/>
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
import {saveConstant} from '@assets/js/api/constant';

export default {
    name: 'add',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {},
        };
    },
    activated() {
        this.params.isFlush = false;
        this.form = this.params.item;
    },
    methods: {
        /* 保存任务信息*/
        saveConstant() {
            let self = this;
            const loading = self.loadingOpen('.add');
            saveConstant(self.form).then((response) => {
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
