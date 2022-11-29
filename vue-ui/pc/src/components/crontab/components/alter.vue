<template>
    <div class="alter">
        <panel-title title="修改任务"/>
        <el-form ref="form" label-width="100px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="任务名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入任务名称" maxlength="18" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="任务类型" prop="type">
                        <el-select v-model="form.type" placeholder="请选择任务类型" clearable>
                            <el-option v-for="(item, index) in params.typeList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="16">
                    <el-form-item label="调用目标" prop="invokeTarget">
                        <el-input v-model="form.invokeTarget" placeholder="请输入调用目标" maxlength="360" clearable>
                            <el-popover slot="suffix" placement="left-start" trigger="click">
                                <hint/>
                                <i class="el-icon-info icon" slot="reference"/>
                            </el-popover>
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="corn表达式" prop="expression">
                        <el-input v-model="form.expression" placeholder="请输入corn表达式" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="并发执行" prop="concurrent" :required="true">
                        <el-radio-group v-model="form.concurrent">
                            <el-radio-button label="true">允许</el-radio-button>
                            <el-radio-button label="false">禁止</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="启用状态" prop="status" :required="true">
                        <el-switch v-model="form.status"/>
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
import {saveCrontab} from '@assets/js/api/crontab';

export default {
    name: 'alter',
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
        saveCrontab() {
            let self = this;
            const loading = self.loadingOpen('.alter');
            saveCrontab(self.form).then((response) => {
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
