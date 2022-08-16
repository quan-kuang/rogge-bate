<template>
    <div class="avatar">
        <el-avatar shape="square" :size="150" :src="user.avatar | imageSrc" @click.native="visible=true"/>
        <el-dialog title="修改头像" :visible.sync="visible">
            <el-row>
                <el-col :xs="24" style="width: 300px;height: 300px">
                    <vue-cropper ref="cropper"
                                 :img="option.img"
                                 :outputSize="option.outputSize"
                                 :outputType="option.outputType"
                                 :canScale="option.canScale"
                                 :autoCrop="option.autoCrop"
                                 :autoCropWidth="option.autoCropWidth"
                                 :autoCropHeight="option.autoCropHeight"
                                 :fixedBox="option.fixedBox"
                                 :fixed="option.fixed"
                                 :fixedNumber="option.fixedNumber"
                                 :original="option.original"
                                 :centerBox="option.centerBox"
                                 :canMoveBox="option.canMoveBox"
                                 :full="option.full"
                                 :info="option.info"
                                 :infoTrue="option.infoTrue"
                                 @realTime="realTime"/>
                </el-col>
                <el-col :xs="24" style="width: 300px;height: 300px;margin-left: 50px;">
                    <div class="real-time-preview">
                        <img :src="previews.url" :style="previews.img"/>
                    </div>
                </el-col>
            </el-row>
            <br/>
            <el-row style="text-align: center">
                <el-col :lg="2">
                    <el-upload action="#" :http-request="httpRequest" :show-file-list="false" :before-upload="beforeUpload">
                        <el-button size="small" type="primary">上传 <i class="el-icon-upload"/></el-button>
                    </el-upload>
                </el-col>
                <el-col :lg="{span: 1, offset: 1}">
                    <el-button size="small" type="info" icon="el-icon-refresh-left" @click="rotateLeft()"/>
                </el-col>
                <el-col :lg="{span: 1, offset: 1}">
                    <el-button size="small" type="info" icon="el-icon-refresh-right" @click="rotateRight()"/>
                </el-col>
                <el-col :lg="{span: 2, offset: 1}">
                    <el-button size="small" type="success" @click="upload">提交 <i class="el-icon-check"/></el-button>
                </el-col>
            </el-row>
        </el-dialog>
    </div>
</template>

<script>
import {VueCropper} from 'vue-cropper';
import constant from '@assets/js/common/constant';
import global from '@assets/js/util/global';
import popup from '@assets/js/mixin/popup';
import file from '@components/user/js/file';
import {saveAttachment, upload} from '@assets/js/api/attachment';
import {updateUser} from '@assets/js/api/user';

export default {
    name: 'avatar',
    components: {VueCropper},
    mixins: [popup, file],
    props: {
        user: {
            type: Object,
        },
    },
    data() {
        return {
            visible: false,
            option: {
                img: '', // 裁剪图片的地址
                outputSize: 0.8, // 裁剪生成图片的质量
                outputType: 'jpg', // 裁剪生成图片的格式
                canScale: true, // 图片是否允许滚轮缩放
                autoCrop: true, // 是否默认生成截图框
                autoCropWidth: 200, // 默认生成截图框宽度
                autoCropHeight: 200, // 默认生成截图框高度
                fixedBox: true, // 固定截图框大小不允许改变
                fixed: true, // 是否开启截图框宽高固定比例
                fixedNumber: [1, 1], // 截图框的宽高比例
                original: false, // 上传图片按照原始比例渲染
                centerBox: false, // 截图框是否被限制在图片里面
                canMoveBox: true, // 截图框能否拖动
                full: false, // 是否输出原图比例的截图
                info: true, // 裁剪框的大小信息
                infoTrue: false, // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
            },
            previews: {},
            fileName: '',
        };
    },
    filters: {
        imageSrc(src) {
            return constant.viewFtpUrl + src;
        },
    },
    created() {
        this.getImgSrc();
    },
    methods: {
        /* 获取图片的base64*/
        getImgSrc(path) {
            let self = this;
            const filePath = constant.viewFtpUrl + self.user.avatar;
            const filePaths = filePath.split('/');
            self.fileName = filePaths[filePaths.length - 1];
            global.convertImgToBase64(filePath, (data) => {
                self.option.img = data;
            });
        },
        /* 实时预览*/
        realTime(data) {
            this.previews = data;
        },
        /* 向左旋转*/
        rotateLeft() {
            this.$refs.cropper.rotateLeft();
        },
        /* 向右旋转*/
        rotateRight() {
            this.$refs.cropper.rotateRight();
        },
        /* 图片缩放*/
        changeScale(num) {
            this.$refs.cropper.changeScale(num);
        },
        /* 覆盖默认的上传行为*/
        httpRequest(file) {
            let self = this;
            self.fileName = file.file.name;
            const reader = new FileReader();
            reader.readAsDataURL(file.file);
            reader.onload = () => {
                self.option.img = reader.result;
            };
        },
        /* 上传图片*/
        upload() {
            let self = this;
            self.$refs.cropper.getCropBlob((blob) => {
                if (!(blob instanceof Blob)) {
                    self.messageError('未采集到裁剪后的图片');
                    return;
                }
                const data = new FormData();
                data.append('file', blob, self.fileName);
                data.append('source', '1');
                const loading = self.loadingOpen('.avatar');
                upload(data).then((response) => {
                    if (response.flag) {
                        self.updateUser(response.data);
                    } else {
                        self.messageError(response.msg);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            });
        },
        /* 修改用户信息*/
        updateUser(attachment) {
            let self = this;
            let data = {
                uuid: self.user.uuid,
                avatar: attachment.path,
            };
            updateUser(data).then((response) => {
                if (response.flag) {
                    self.user.avatar = attachment.path;
                    self.saveAttachment(attachment.uuid);
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        /* 保存附件信息*/
        saveAttachment(uuid) {
            let self = this;
            let data = {
                uuid: uuid,
                foreignId: self.user.uuid,
                citeType: '0', // 0 头像
            };
            saveAttachment(data).then((response) => {
                if (response.flag) {
                    self.visible = false;
                    self.messageSuccess(response.msg);
                } else {
                    self.messageError(response.msg);
                }
            });
        },
    },
};
</script>

<style scoped lang="scss">
    .real-time-preview {
        position: absolute;
        top: 50%;
        transform: translate(50%, -50%);
        width: 200px;
        height: 200px;
        border-radius: 50%;
        box-shadow: 0 0 5px #ccc;
        overflow: hidden;
    }
</style>
