<template>
    <div class="login">
        <vue-particles clickMode="repulse" class="particles"/>
        <div class="box" @keyup.enter="submit">
            <div class="title">罗格贝特</div>
            <el-form ref="form" :model="form" :rules="rules">
                <el-form-item prop="user.account">
                    <el-input v-model.trim="form.user.account" :placeholder="format.decode(loginType,2,'请输入手机号','请输入账号')" :maxlength="format.decode(loginType,2,11,18)" prefix-icon="iconfont icon-yonghu1" clearable/>
                </el-form-item>
                <el-form-item prop="user.password">
                    <el-input v-model="form.user.password" class="password" prefix-icon="iconfont icon-mima" clearable
                              :disabled="loginType===2 && !sendStatus" :placeholder="format.decode(loginType,2,'请输入验证码','请输入密码')" :maxlength="format.decode(loginType,2,6,36)" :type="format.decode(loginType,2,'','password')">
                        <el-button slot="append" v-if="loginType===2" @click="sendCaptcha"><span class="sendCaptcha">{{ sendCaptchaHint }}</span></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="captcha.value" v-if="loginType===1">
                    <el-input v-model="form.captcha.value" placeholder="请输入验证码" maxlength="4" class="captcha" prefix-icon="iconfont icon-anquan" clearable>
                        <el-button slot="append"><img :src="captchaSrc" @click="getCodeCaptcha" alt=""/></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button size="large" :icon="loginIcon?'el-icon-circle-check': 'el-icon-remove'" class="login-button" :loading="isLoading"
                               @click="submit" @mouseover.native="loginIcon=true" @mouseout.native="loginIcon=false" @contextmenu.prevent.native="changeLoginType"/>
                </el-form-item>
            </el-form>
            <!--滑块验证-->
            <el-dialog title="请拖动滑块完成拼图" width="360px" :visible.sync="isShowSliderVerify" :close-on-click-modal="false">
                <slider-verify ref="sliderVerify" @success="onSuccess" @fail="onFail" @again="onAgain"/>
            </el-dialog>
        </div>
        <div class="icp" v-show="false">
            <el-link type="info" href="http://beian.miit.gov.cn/" target="_blank">陕ICP备20009750号{{ constant.domainName.includes('www.') ? '-1' : '' }}</el-link>
        </div>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import global from '@assets/js/util/global';
import cipher from '@assets/js/util/cipher';
import format from '@assets/js/util/format';
import rules from './js/rules';
import constant from '@assets/js/common/constant';
import {mapActions} from 'vuex';
import sliderVerify from './components/sliderVerify';
import {getCaptcha} from '@assets/js/api/util';
import {login, messageLogin} from '@assets/js/api/user';
import {sendCaptcha} from '@assets/js/api/news';
import verify from '@assets/js/util/verify';
import watermark from '@assets/js/util/watermark';

export default {
    name: 'login',
    components: {
        sliderVerify,
    },
    mixins: [popup, rules],
    data() {
        return {
            constant: constant,
            format: format,
            form: {
                user: {
                    // 账号
                    account: constant.environment ? 'ceshi' : 'admin',
                    // 密码
                    password: constant.environment ? 'ceshi' : 'Loyer134.',
                },
                // 验证码
                captcha: {
                    // 类型code/puzzle
                    type: 'puzzle',
                    // 随机字符串
                    nonceStr: '',
                    // 验证值
                    value: '',
                },
            },
            // 验证码图片
            captchaSrc: '',
            // 登录状态
            isLoading: false,
            // 是否显示滑块验证
            isShowSliderVerify: false,
            // 登录按钮的图标
            loginIcon: false,
            // 登录方式
            loginType: 0,
            // 发送验证码提示
            sendCaptchaHint: '发送验证码',
            // 发送状态
            sendStatus: false,
        };
    },
    created() {
        this.getCodeCaptcha();
        watermark.deleteWatermark();
    },
    methods: {
        ...mapActions(['refreshUser']),
        /* 获取验证码图片*/
        getCodeCaptcha() {
            let self = this;
            if (self.loginType !== 1) {
                return;
            }
            self.form.captcha.value = '';
            const data = {type: 'code'};
            getCaptcha(data).then((response) => {
                if (response.flag) {
                    const data = response.data;
                    self.captchaSrc = data.canvasSrc;
                    self.form.captcha.nonceStr = data.nonceStr;
                }
            });
        },
        /* 改变登录方式*/
        changeLoginType() {
            let self = this;
            if (self.loginType < 2) {
                self.loginType++;
            } else {
                self.loginType = 0;
            }
            self.getCodeCaptcha();
            self.form.user.account = '';
            self.form.user.password = '';
            self.form.captcha.type = format.decode(self.loginType, 0, 'puzzle', 1, 'code', '');
            self.$refs.form.clearValidate();
        },
        /* 发送短信验证码*/
        sendCaptcha() {
            let self = this;
            const phone = self.form.user.account;
            if (!phone) {
                self.messageError('请输入手机号');
                return;
            }
            if (!verify.regularCheck(phone, verify.regExpPhone)) {
                self.messageError('手机号格式错误');
                return;
            }
            const key = 'countDown' + phone;
            const currentTime = new Date();
            if (self.$cookies.isKey(key)) {
                const remainTime = Math.ceil((self.$cookies.get(key) - currentTime.getTime()).cusDiv(1000));
                self.messageInfo(`${remainTime} 秒后可重发`);
                self.sendStatus = true;
                return;
            }
            self.sendCaptchaHint = '发送中...';
            const data = {
                phoneNumbers: phone,
                templateParams: '6',
            };
            sendCaptcha(data).then((response) => {
                if (response.flag) {
                    self.sendStatus = true;
                    self.sendCaptchaHint = '已发送';
                    const countDown = 60;
                    const expireTime = new Date(currentTime.setSeconds(currentTime.getSeconds() + countDown));
                    self.$cookies.set(key, expireTime.getTime(), expireTime);
                    self.setInterval(countDown);
                } else {
                    self.sendCaptchaHint = '发送验证码';
                    self.messageError(response.msg);
                }
            });
        },
        /* 倒计时*/
        setInterval(countDown) {
            let self = this;
            const interval = setInterval(() => {
                self.sendCaptchaHint = `${countDown} 秒后可重发`;
                countDown--;
                if (countDown < 0) {
                    self.sendCaptchaHint = '发送验证码';
                    clearInterval(interval);
                }
            }, 1000);
        },
        /* 提交*/
        submit() {
            let self = this;
            self.$refs['form'].validate((flag) => {
                // 滑块验证码登录
                if (self.loginType === 0) {
                    self.isShowSliderVerify = flag;
                }
                // 文字验证码登录
                else if (self.loginType === 1) {
                    flag && self.login();
                }
                // 短信登录
                else if (self.loginType === 2) {
                    if (flag) {
                        if (!verify.regularCheck(self.form.user.account, verify.regExpPhone)) {
                            self.messageError('手机号格式错误');
                            return;
                        }
                        self.messageLogin();
                    }
                }
            });
        },
        /* 短信登录*/
        messageLogin() {
            let self = this;
            self.isLoading = true;
            const data = self.form.user;
            messageLogin(data).then((response) => {
                if (response.flag) {
                    self.messageSuccess('登录成功');
                    self.loginSuccess(response);
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                self.isLoading = false;
            });
        },
        /* 登录*/
        login() {
            let self = this;
            self.isLoading = true;
            const data = global.deepClone(self.form);
            data.user.password = cipher.encryptRSA(data.user.password);
            login(data).then((response) => {
                if (response.flag) {
                    if (self.loginType === 1) {
                        self.loginSuccess(response);
                    } else {
                        self.$refs.sliderVerify.verifySuccessEvent();
                        setTimeout(() => {
                            self.loginSuccess(response);
                        }, 500);
                    }
                } else {
                    if (self.loginType === 1) {
                        self.messageError(response.msg);
                        self.getCodeCaptcha();
                    } else {
                        const isCaptchaError = [1086, 1087].some((code) => code === response.code);
                        if (isCaptchaError) {
                            self.$refs.sliderVerify.verifyFailEvent();
                        } else {
                            self.messageError(response.msg);
                            self.isShowSliderVerify = false;
                            self.$refs.sliderVerify.refresh();
                        }
                    }
                }
            }).finally(() => {
                self.isLoading = false;
            });
        },
        /* 登录成功事件*/
        loginSuccess(response) {
            let self = this;
            // 获取登录用户对象
            const loginUser = response.data;
            // 判断是否有菜单权限
            if (loginUser.user.menuList.length === 0) {
                self.messageError('当前用户尚未配置菜单');
                return;
            }
            // 缓存用户认证token
            self.storage.setItem('token', loginUser.token);
            // 处理actions多个入参问题
            const object = {router: self.$router, user: loginUser.user};
            // 添加用户缓存信息
            self.refreshUser(object);
            // 回调地址
            const redirectUrl = self.$route.query.redirectUrl;
            if (redirectUrl) {
                self.$router.push(decodeURIComponent(redirectUrl));
            } else {
                self.$router.push('/');
            }
        },
        /* 滑动验证成功*/
        onSuccess(captcha) {
            Object.assign(this.form.captcha, captcha);
            this.login();
        },
        /* 滑动验证失败*/
        onFail() {
            this.messageError('验证失败，请控制拼图对齐缺口');
        },
        /* 滑动验证异常*/
        onAgain() {
            this.messageError('滑动操作异常，请重试');
            this.$refs.sliderVerify.refresh();
        },
    },
};
</script>

<style lang="scss">
    .login {
        .el-input__inner {
            opacity: 0.9;
        }

        .password {
            .el-input-group__append {
                width: 88px;
                text-align: center;
                border: none;
                background-color: gainsboro;

                &:active, &:hover {
                    border: none;
                    background: lightgray;

                    .sendCaptcha {
                        color: crimson;
                    }
                }
            }

            .sendCaptcha {
                color: #fe7300;
            }
        }

        .captcha {
            .el-input-group__append {
                border: none;
                background: none;
            }

            .el-input-group__prepend, .el-input__inner {
                border-top-right-radius: 4px;
                border-bottom-right-radius: 4px;
            }
        }
    }
</style>

<style scoped lang="scss">
    .login {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        overflow-y: hidden;
        background-image: url(../../assets/img/bgImg.jpg);
        background-repeat: no-repeat;
        background-position: center;
        background-size: cover;

        .el-form {
            .el-input {
                width: 300px;
            }
        }

        .particles {
            position: absolute;
            width: 100%;
            height: 100%;
        }

        .box {
            width: 300px;
            padding: 25px 15px;
            margin: 100px auto;
            background: rgba(0, 0, 0, 0);
            border-radius: 5px;

            .title {
                margin-bottom: 25px;
                text-align: center;
                font-size: larger;
                color: white;
                opacity: 0.8;
            }

            .captcha {
                button {
                    margin-left: 1px;
                    width: 100px;
                    height: 40px;
                }

                img {
                    height: 40px;
                    position: absolute;
                    right: 0;
                    top: 0;
                    border-radius: 4px;
                }
            }

            .login-button {
                width: 100%;
                padding: 6px 20px;
                font-size: 24px;
                color: #fff;
                border-color: #bfbfc5;
                background: #bfbfc5;

                &:active, &:hover {
                    border-color: #6bc5a4;
                    background: #6bc5a4;
                }
            }
        }

        .icp {
            position: absolute;
            right: 20px;
            bottom: 20px;

            .el-link.el-link--info {
                font-size: 13px;
                color: #909399;
            }
        }
    }
</style>
