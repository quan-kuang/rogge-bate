<template>
    <div class="alter">
        <panel-title title="修改部门"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="上级部门" prop="parentId">
                        <el-select ref="dept" v-model="form.parentId" placeholder="请选择上级部门" :disabled="form.parentId==='root'" filterable :filter-method="filterMethod" clearable @clear="clear">
                            <el-option style="background-color: white" :value="option.uuid" :label="option.name">
                                <tree-select ref="treeSelect" :props="props" :data="params.deptList" :defaultKeys="defaultKeys" @nodeClick="nodeClick"/>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="部门名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入部门名称" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="部门排序" prop="sort">
                        <el-input v-model="form.sort" placeholder="请输入排序" maxlength="2" type="number" clearable :disabled="form.parentId==='root'"/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="启用状态" prop="status" :required="true">
                        <el-switch v-model="form.status"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="13">
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
import {saveDept} from '@assets/js/api/dept';

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
        const item = this.params.item;
        this.option.uuid = item.parentId;
        this.option.name = item.parentName;
        this.defaultKeys = [item.parentId];
        this.$refs.treeSelect.setCurrentKey(item.parentId);
        this.form = item;
    },
    methods: {
        /* 保存部门信息*/
        saveDept() {
            let self = this;
            const data = global.deepClone(self.form);
            data.parentName = self.$refs.dept.selected.label;
            const loading = self.loadingOpen('.alter');
            saveDept(data).then((response) => {
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
