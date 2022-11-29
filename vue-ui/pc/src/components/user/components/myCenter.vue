<template>
    <div class="myCenter">
        <el-row :gutter="20">
            <!--左侧卡片-->
            <el-col :span="8" :xs="24">
                <el-card>
                    <div slot="header">
                        <span>个人信息</span>
                    </div>
                    <div style="text-align: center">
                        <avatar :user="user"/>
                    </div>
                    <div class="personal-info">
                        <ul>
                            <li>用户名称<span>{{ user.name }}</span></li>
                            <li>手机号码<span>{{ user.phone }}</span></li>
                            <li>用户邮箱<span>{{ user.email }}</span></li>
                            <li>出生日期<span>{{ user.birthday }}</span></li>
                            <li>所属部门<span>{{ user.deptName }}</span></li>
                            <li>创建日期<span>{{ user.createTime | formatDate }}</span></li>
                            <li>最近登录<span>{{ user.loginTime | formatDate }}</span></li>
                        </ul>
                    </div>
                </el-card>
            </el-col>
            <!--右侧卡片-->
            <el-col :span="16" :xs="24">
                <el-card>
                    <div slot="header">
                        <span>基本资料</span>
                    </div>
                    <el-tabs v-model="activeTab">
                        <el-tab-pane label="基本资料" name="userInfo">
                            <el-form ref="userInfo" label-width="80px" :model="form" :rules="rules" lstatus-icon>
                                <el-form-item label="姓名" prop="name">
                                    <el-input v-model="form.name" maxlength="9" clearable/>
                                </el-form-item>
                                <el-form-item label="手机号" prop="phone">
                                    <el-input v-model="form.phone" maxlength="11" clearable/>
                                </el-form-item>
                                <el-form-item label="身份证" prop="idCard">
                                    <el-input v-model="form.idCard" maxlength="18" clearable/>
                                </el-form-item>
                                <el-form-item label="性别" prop="sex">
                                    <el-radio-group v-model="form.sex">
                                        <el-radio label="1">男</el-radio>
                                        <el-radio label="0">女</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item label="出生日期" prop="birthday">
                                    <el-date-picker type="date" v-model="form.birthday" value-format="yyyy-MM-dd" :editable="false" style="width: 100%"/>
                                </el-form-item>
                                <el-form-item label="邮箱" prop="email">
                                    <el-input v-model="form.email" maxlength="36" clearable/>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="submit('userInfo')">保存</el-button>
                                    <el-button @click="cancel">取消</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>
                        <el-tab-pane label="修改密码" name="pwdInfo">
                            <el-form ref="pwdInfo" label-width="80px" :model="form" :rules="rules" lstatus-icon>
                                <el-form-item label="旧密码" prop="oldPassword">
                                    <el-input v-model="form.oldPassword" maxlength="18" type="password" clearable/>
                                </el-form-item>
                                <el-form-item label="新密码" prop="password">
                                    <el-input v-model="form.password" maxlength="18" type="password" clearable/>
                                </el-form-item>
                                <el-form-item label="确认密码" prop="confirmPassword">
                                    <el-input v-model="form.confirmPassword" maxlength="18" type="password" clearable/>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="submit('pwdInfo')">保存</el-button>
                                    <el-button @click="reset">取消</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>
                    </el-tabs>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import avatar from './avatar';
import global from '@assets/js/util/global';
import {mapMutations, mapState} from 'vuex';
import rules from '../js/rules';
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import cipher from '@assets/js/util/cipher';
import {updateUser} from '@assets/js/api/user';

export default {
    name: 'myCenter',
    components: {avatar},
    mixins: [popup, rules],
    computed: {
        ...mapState({
            user: (state) => state.user.user,
        }),
    },
    data() {
        return {
            activeTab: 'userInfo',
            form: {},
        };
    },
    filters: {
        formatDate(value) {
            return value.toDate();
        },
    },
    created() {
        this.cancel();
    },
    methods: {
        ...mapMutations(['setUser']),
        /* 保存事件*/
        submit(ref) {
            let self = this;
            if (self.form.uuid === constant.admin) {
                self.messageError('不能操作管理员用户');
                return;
            }
            self.$refs[ref].validate((valid) => {
                if (valid) {
                    if (ref === 'userInfo') {
                        self.updateUser();
                    } else if (ref === 'pwdInfo') {
                        self.changePassword();
                    }
                }
            });
        },
        /* 修改用户信息*/
        updateUser() {
            let self = this;
            const data = {
                uuid: self.form.uuid,
                name: self.form.name,
                phone: self.form.phone,
                idCard: self.form.idCard,
                sex: self.form.sex,
                birthday: self.form.birthday,
                email: self.form.email,
            };
            const loading = self.loadingOpen('.myCenter');
            updateUser(data).then((response) => {
                if (response.flag) {
                    self.setUser(global.deepClone(self.form));
                    self.messageSuccess(response.msg);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 修改密码*/
        changePassword() {
            let self = this;
            if (self.form.password === self.form.oldPassword) {
                self.messageError('新密码不能同旧密码一致');
                return;
            }
            const data = {
                uuid: self.form.uuid,
                account: self.form.account,
                password: cipher.encryptRSA(self.form.password),
                params: {oldPassword: cipher.encryptRSA(self.form.oldPassword)},
            };
            const loading = self.loadingOpen('.myCenter');
            updateUser(data).then((response) => {
                if (response.flag) {
                    self.messageSuccess(response.msg + '，请重新登录');
                    self.storage.removeItem('token');
                    self.$router.push('/login');
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 取消事件*/
        cancel() {
            this.form = global.deepClone(this.user);
            this.form.password = '';
        },
        /* 取消密码修改*/
        reset() {
            this.$refs.pwdInfo.resetFields();
        },
    },
};
</script>

<style lang="scss">
    //个人信息
    .personal-info {
        ul {
            padding-left: 0px;
            list-style: none;
        }

        li {
            border-bottom: 1px solid #e7eaec;
            padding: 10px 0px;
            font-size: 13px;

            span {
                float: right !important;
            }
        }
    }
</style>
