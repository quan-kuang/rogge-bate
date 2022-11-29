<template>
    <div class="add">
        <panel-title title="新增用户"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="账号" prop="account">
                        <el-input v-model="form.account" placeholder="请输入账号" maxlength="18" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="form.name" placeholder="请输入姓名" maxlength="9" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="手机号" prop="phone">
                        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="身份证" prop="idCard">
                        <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="性别" prop="sex">
                        <el-select v-model="form.sex" placeholder="请选择性别" clearable>
                            <el-option v-for="(item, index) in params.genderList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="出生日期" prop="birthday">
                        <el-date-picker type="date" placeholder="请选择出生日期" v-model="form.birthday" value-format="yyyy-MM-dd" :editable="false"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="form.email" maxlength="36" placeholder="请输入邮箱" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="角色" prop="roleIds">
                        <el-select v-model="form.roleIds" placeholder="请选择角色" multiple filterable clearable>
                            <el-option v-for="(item,index) in params.roleList" :key="index" :label="item.name" :value="item.uuid" :disabled="!item.status"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="部门" prop="deptId">
                        <el-select ref="dept" v-model="form.deptId" placeholder="请选择部门" filterable :filter-method="filterMethod" clearable @clear="clear">
                            <el-option style="background-color: white" :value="option.uuid" :label="option.name">
                                <tree-select ref="treeSelect" :props="props" :data="params.deptList" @nodeClick="nodeClick"/>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="form.password" placeholder="请输入密码" maxlength="18" type="password" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="确认密码" prop="confirmPassword">
                        <el-input v-model="form.confirmPassword" placeholder="请再次输入密码" maxlength="18" type="password" clearable/>
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
import global from '@assets/js/util/global';
import cipher from '@assets/js/util/cipher';
import mixin from '../js/mixin';
import {saveUser} from '@assets/js/api/user';

export default {
    name: 'add',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {
                uuid: null,
                account: '',
                name: '',
                phone: '',
                idCard: '',
                sex: '',
                birthday: '',
                email: '',
                deptId: '',
                status: true,
                remark: '',
                password: '',
                confirmPassword: '',
                roleIds: [],
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
        },
        /* 保存事件*/
        submit() {
            let self = this;
            self.$refs.form.validate((valid) => {
                valid && self.saveUser();
            });
        },
        /* 保存用户信息*/
        saveUser() {
            let self = this;
            const data = global.deepClone(self.form);
            data.password = cipher.encryptRSA(data.password);
            const loading = self.loadingOpen('.add');
            saveUser(data).then((response) => {
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
