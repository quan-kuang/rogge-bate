<template>
    <div>
        <!--登录表单-->
        <van-cell-group>
            <van-field label="账号" placeholder="请输入账号" maxlength="18" clearable v-model="account"
                       left-icon="contact" right-icon="question-o" @click-right-icon="$toast('请输入账号')"
                       @blur="checkAccount"/>
            <van-field label="密码" placeholder="请输入密码" maxlength="36" clearable v-model="password" :type="isPassword?'':'password'"
                       :left-icon="isPassword?'eye-o':'closed-eye'" @click-left-icon="showPassword"
                       right-icon="question-o" @click-right-icon="$toast('密码只能是数字、字母、下划线')"
                       @blur="checkPassword"/>
            <van-field v-model="captcha" clearable label="验证码" placeholder="请输入验证码" maxlength="4" left-icon="other-pay"
                       @blur="checkCaptcha">
                <img slot="right-icon" class="captcha" :src="captchaSrc" alt="看不清，换一张" @click="getCaptcha">
            </van-field>
        </van-cell-group>
        <!--操作按钮-->
        <div class="register-forget">
            <router-link to="/login">立即注册</router-link>
            <router-link to="/login">忘记密码</router-link>
        </div>
        <!--登录按钮-->
        <div class="button-box">
            <van-button type="primary" @click="login">登录</van-button>
        </div>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import cipher from '@assets/js/util/cipher';
import verify from '@assets/js/util/verify';
import {mapMutations} from 'vuex';
import {getCaptcha} from '@assets/js/api/util';
import {login} from '@assets/js/api/user';

export default {
    name: 'login',
    mixins: [popup],
    data() {
        return {
            account: 'ceshi', // 账号
            password: 'ceshi', // 密码
            captcha: '', // 验证码
            isPassword: false, // 是否显示密码
            nonceStr: '', // 验证码随机字符串
            captchaSrc: '', // 验证码图片
        };
    },
    created() {
        this.getCaptcha();
    },
    methods: {
        ...mapMutations(['setUser']),
        /* 显示明文密码*/
        showPassword() {
            this.isPassword = !this.isPassword;
        },
        /* 获取验证码图片*/
        getCaptcha() {
            let self = this;
            self.captcha = '';
            const data = {type: 'code'};
            getCaptcha(data).then((response) => {
                if (response.data.flag) {
                    const data = response.data.data;
                    self.captchaSrc = data.canvasSrc;
                    self.nonceStr = data.nonceStr;
                }
            });
        },
        /* 校验账号*/
        checkAccount() {
            const checkResult = verify.notEmptyCheck(this.account, '账号');
            if (!checkResult.rst) {
                this.toast(checkResult.msg);
                return false;
            }
            return true;
        },
        /* 校验密码*/
        checkPassword() {
            const checkResult = verify.notEmptyCheck(this.password, '密码');
            if (!checkResult.rst) {
                this.toast(checkResult.msg);
                return false;
            }
            return true;
        },
        /* 校验验证码*/
        checkCaptcha() {
            if (!this.captcha || !this.captcha.trim()) {
                this.toast('请输入验证码');
                return false;
            }
            return true;
        },
        /* 登录*/
        login() {
            let self = this;
            if (self.checkAccount() && self.checkPassword() && self.checkCaptcha()) {
                const data = {
                    user: {
                        account: self.account,
                        password: cipher.encryptMD5(self.password),
                    },
                    captcha: {
                        type: 'code',
                        nonceStr: self.nonceStr,
                        value: self.captcha,
                    },
                };
                const loading = self.loadingOpen('加载中...');
                login(data).then((response) => {
                    if (response.data.flag) {
                        // 获取登录用户对象
                        const loginUser = response.data.data;
                        // 缓存用户认证token
                        self.storage.setItem('token', loginUser.token);
                        // 添加用户缓存
                        self.setUser(loginUser.user);
                        // 回调地址
                        const redirectUrl = self.$route.query.redirectUrl;
                        if (redirectUrl) {
                            self.$router.push(decodeURIComponent(redirectUrl));
                        } else {
                            self.$router.push('/');
                        }
                    } else {
                        self.getCaptcha();
                        self.alert(response.data.msg);
                    }
                }).finally(() => {
                    loading.clear();
                });
            }
        },
    },
};
</script>

<style lang="scss" scoped>
    /*输入字体大小*/
    .van-cell-group .van-cell {
        font-size: 16px;
    }

    /*验证码位置*/
    .captcha {
        position: fixed;
        top: 90px;
        right: 15px;
    }

    /*按钮div*/
    .button-box {
        text-align: center;
    }

    /*按钮*/
    .button-box button {
        margin-top: 10px;
        width: 90%;
    }

    /*立即注册、忘记密码*/
    .register-forget {
        text-align: center;
        margin: 10px;

        a {
            display: inline-block;
            padding: 10px;
            color: #666;
        }
    }
</style>
