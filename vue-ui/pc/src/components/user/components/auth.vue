<template>
    <div class="auth">
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-form-item label="人像照片" required style="margin-bottom: 150px">
                <el-upload class="upload-demo" ref="upload" accept="image/*" list-type="picture-card" :drag="true"
                           :action="uploadUrl" :data="fileData" :limit="fileLimit" :headers="headers"
                           :before-upload="beforeUploadPhoto" :before-remove="beforeRemove" :on-preview="onPreview" :on-success="onSuccess" :on-exceed="onExceed">
                    <el-tooltip class="item" effect="dark" content="将文件拖到此处，或点击上传" placement="top" :hide-after="1000" :enterable="false">
                        <i class="el-icon-upload"/>
                    </el-tooltip>
                </el-upload>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名" maxlength="18" clearable/>
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard" class="required">
                <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" clearable/>
            </el-form-item>
            <el-form-item>
                <el-upload class="ocr-upload" accept="image/*" action="#" :multiple="false" :show-file-list="false" :http-request="ocrIdCard" :before-upload="beforeUpload">
                    <el-button type="primary">证件识别</el-button>
                </el-upload>
                <el-button type="success" @click="submit">立即认证</el-button>
            </el-form-item>
        </el-form>
        <!--隐藏域图片-->
        <el-dialog :visible.sync="isImage" :modal="false">
            <el-image width="100%;" :src="imageSrc" alt=""/>
        </el-dialog>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import rules from '@components/user/js/rules';
import file from '../js/file';
import {mapActions, mapState} from 'vuex';
import {auth} from '@assets/js/api/user';
import {ocrIdCard} from '@assets/js/api/baidu';
import constant from '@assets/js/common/constant';

export default {
    name: 'auth',
    mixins: [popup, file, rules],
    computed: {
        ...mapState({
            user: (state) => state.user.user,
        }),
    },
    data() {
        return {
            fileData: {
                code: '其他', // 附件类型
                isSave: false, // 是否物理保存
                getBase64: true, // 是否获取base64
                targetSize: 50, // 压缩目标（0为默认）
            },
            form: {
                uuid: '',
                name: '',
                idCard: '',
                params: {
                    base64: '',
                },
            },
        };
    },
    created() {
        this.form.uuid = this.user.uuid;
    },
    methods: {
        ...mapActions(['refreshUser']),
        /* 身份证OCR识别*/
        ocrIdCard(response) {
            let self = this;
            // 获取上传图片的base64
            const fileReader = new FileReader();
            fileReader.readAsDataURL(response.file);
            fileReader.onload = (event) => {
                let base64 = fileReader.result;
                base64 = base64.substring(base64.indexOf('base64,') + 7);
                const loading = self.loadingOpen('.auth');
                const data = {
                    image: base64,
                    imageType: 'base64',
                };
                ocrIdCard(data).then((response) => {
                    if (response.flag) {
                        const data = response.data;
                        if (!data.words_result.idCard) {
                            self.messageError('未识别到身份信息');
                        } else {
                            self.messageSuccess('识别成功');
                            self.form.name = data.words_result.name.words;
                            self.form.idCard = data.words_result.idCard.words;
                        }
                    } else {
                        self.messageError(response.msg);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            };
        },
        /* 提交认证*/
        submit() {
            let self = this;
            if (self.uploadFiles.length === 0) {
                self.messageError('请先上传人像照片');
                return;
            }
            self.$refs.form.validate((valid) => {
                valid && self.auth();
            });
        },
        /* 实人认证*/
        auth() {
            let self = this;
            if (self.form.uuid === constant.admin) {
                self.messageError('不能操作管理员角色');
                return;
            }
            self.form.params.base64 = self.uploadFiles[0].base64;
            const loading = self.loadingOpen('.auth');
            auth(self.form).then((response) => {
                if (response.flag) {
                    self.confirm('认证成功', function () {
                        // 刷新用户信息
                        self.refreshUser({router: self.$router});
                        // 回调地址
                        const redirectUrl = self.$route.query.redirectUrl;
                        if (redirectUrl) {
                            self.$router.push(decodeURIComponent(redirectUrl));
                        }
                    }, null);
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

<style scoped lang="scss">
    @import '../../../assets/css/upload.css';

    .auth {
        .el-input {
            width: 240px;
        }

        .ocr-upload {
            float: left;
            margin-right: 10px;
        }

        .upload-demo {
            position: absolute;
        }
    }
</style>
