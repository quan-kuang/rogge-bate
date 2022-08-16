<template>
    <div class="add">
        <panel-title title="新增角色"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-container>
                <el-aside width="25%">
                    <el-form-item label="角色名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入角色名称" maxlength="9" clearable/>
                    </el-form-item>
                    <el-form-item label="权限类型" prop="permissionType">
                        <el-select v-model="form.permissionType" placeholder="请选择权限类型" filterable clearable>
                            <el-option v-for="(item,index) in params.permissionTypeList" :key="index" :label="item.text" :value="item.value" :disabled="!item.status"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="启用状态" prop="status" :required="true">
                        <el-switch v-model="form.status"/>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark" maxlength="36"/>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="success" @click="submit">保存</el-button>
                        <el-button type="warning" @click="reset">重置</el-button>
                        <el-button type="primary" @click="goBack">返回</el-button>
                    </el-form-item>
                </el-aside>
                <el-main style="padding: 0;margin-left: 20px">
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="菜单权限" prop="menuIds" class="required">
                                <tree ref="menuTree" :data="menuTree"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" style="margin-left: 20px" v-show="form.permissionType=='1'">
                            <el-form-item label="数据权限" prop="deptIds" class="required">
                                <tree ref="deptTree" :data="params.deptList" :props="props" :checkStrictly="true"/>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-main>
            </el-container>
        </el-form>
    </div>
</template>

<script>
import mixin from '../js/mixin';
import global from '@assets/js/util/global';
import {saveRole} from '@assets/js/api/role';

export default {
    name: 'add',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {
                name: '',
                permissionType: '',
                status: true,
                remark: '',
                menuIds: [],
            },
        };
    },
    activated() {
        this.params.isFlush = false;
    },
    methods: {
        /* 清空表单输入框*/
        reset() {
            this.$refs.form.resetFields();
            this.$refs.menuTree.isCheckAll = false;
            this.$refs.menuTree.checkAll();
            this.$refs.deptTree.isCheckAll = false;
            this.$refs.deptTree.checkAll();
        },
        /* 保存事件*/
        submit() {
            this.saveEvent();
        },
        /* 保存角色信息*/
        saveRole() {
            let self = this;
            const data = self.form;
            data.uuid = global.getUuid();
            const loading = self.loadingOpen('.add');
            saveRole(data).then((response) => {
                if (response.flag) {
                    self.params.isFlush = true;
                    self.confirm(`${response.msg}，继续添加？`
                        , function () {
                            self.reset();
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
