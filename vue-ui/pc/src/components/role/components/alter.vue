<template>
    <div class="alter">
        <panel-title title="修改角色"/>
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
                        <el-button type="primary" @click="goBack">返回</el-button>
                    </el-form-item>
                </el-aside>
                <el-main style="padding: 0;margin-left: 20px">
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="菜单权限" prop="menuIds" class="required">
                                <tree ref="menuTree" :data="menuTree" :defaultKeys="defaultMenuKeys"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" style="margin-left: 20px" v-show="form.permissionType=='1'">
                            <el-form-item label="数据权限" prop="deptIds" class="required">
                                <tree ref="deptTree" :data="params.deptList" :props="props" :checkStrictly="true" :defaultKeys="defaultDeptKeys"/>
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
import constant from '@assets/js/common/constant';
import {saveRole, selectRoleLink} from '@assets/js/api/role';

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
        this.selectRoleLink(this.form.uuid);
    },
    methods: {
        /* 保存事件*/
        submit() {
            if (this.form.uuid === constant.admin) {
                this.messageError('不能操作管理员角色');
                return;
            }
            this.saveEvent();
        },
        /* 保存角色信息*/
        saveRole() {
            let self = this;
            const loading = self.loadingOpen('.alter');
            saveRole(self.form).then((response) => {
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
        /* 查询角色关联菜单/部门*/
        selectRoleLink(roleId) {
            let self = this;
            const loading = self.loadingOpen('.alter');
            const data = [roleId];
            selectRoleLink(data).then((response) => {
                if (response.flag) {
                    // 设置菜单的选中及展开
                    self.defaultMenuKeys = response.data.menuIds;
                    self.$refs.menuTree.setCheckedKeys(self.defaultMenuKeys);
                    // 设置部门树的选中及展开
                    self.defaultDeptKeys = response.data.deptIds;
                    self.$refs.deptTree.setCheckedKeys(self.defaultDeptKeys);
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
