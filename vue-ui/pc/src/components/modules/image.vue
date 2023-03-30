<template>
    <div class="images">
        <!-- 面板标签-->
        <panel-title>
            <el-button icon="el-icon-refresh" @click="selectEvent"/>
            <el-radio-group v-model="type" v-show="!isPreview">
                <el-radio-button label="faceDetect">人脸检测</el-radio-button>
                <el-radio-button label="faceVerify">活体检测</el-radio-button>
            </el-radio-group>
            <el-checkbox-group v-model="isPreview">
                <el-checkbox-button label="true">开启预览</el-checkbox-button>
            </el-checkbox-group>
        </panel-title>
        <!--数据列表-->
        <ul :style="'height:'+tableMaxHeight+'px;overflow:auto;'">
            <li v-for="(item, index) of fileList" :key="index">
                <el-tooltip :content="item.name" placement="top" effect="light">
                    <el-image lazy :class="{'mt': index>2, 'ml': index%3>0}" :src="getFileUrl(item)" :preview-src-list="previewSrcList" @dblclick.native="faceVerify($event)"/>
                </el-tooltip>
            </li>
        </ul>
        <!-- 分页控件 -->
        <el-pagination class="el-pagination-2" background layout='total, prev, pager, next, jumper' v-show="fileTotal>10"
                :page-sizes='pageSizes' :total="fileTotal" :page-size="pageSize" :current-page="pageNum"
                @size-change="sizeChange" @current-change="currentChange"/>
    </div>
</template>

<script>
import common from '@assets/js/mixin/common';
import file from '@components/attachment/js/file';
import {selectAttachment} from '@assets/js/api/attachment';
import {faceDetect, faceVerify} from '@assets/js/api/baidu';

export default {
    name: 'images',
    mixins: [common, file],
    data() {
        return {
            pageSize: 12,
            isPreview: false,
            previewSrcList: [],
            type: 'faceDetect',
        };
    },
    created() {
        this.selectEvent();
    },
    methods: {
        /* 查询附件信息*/
        selectEvent() {
            let self = this;
            const data = {
                pageNum: self.pageNum,
                pageSize: self.pageSize,
                mime: 'image',
            };
            const loading = self.loadingOpen('.images');
            selectAttachment(data).then((response) => {
                if (response.flag) {
                    const data = response.data;
                    self.fileTotal = data.total;
                    self.fileList = data.list;
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 人脸识别*/
        faceVerify(event) {
            let self = this;
            const data = {
                image: event.target.src,
            };
            if (!data.image) {
                return;
            }
            const loading = self.loadingOpen('.images');
            if (self.type === 'faceDetect') {
                faceDetect(data).then((response) => {
                    if (response.flag) {
                        const result = response.data.result;
                        const faceNum = result.face_num;
                        const faceItem = result.face_list[0];
                        let html = '<table>';
                        html += `<tr><td>数量：</td><td>${faceNum}</td><td>&emsp;&emsp;</td><td>性别：</td><td>${faceItem.gender.type}</td></tr>`;
                        html += `<tr><td>完整：</td><td>${faceItem.quality.completeness}</td><td>&emsp;&emsp;</td><td>脸型：</td><td>${faceItem.face_shape.type}</td></tr>`;
                        html += `<tr><td>年龄：</td><td>${faceItem.age}</td><td>&emsp;&emsp;</td><td>眼镜：</td><td>${faceItem.glasses.type}</td></tr>`;
                        html += `<tr><td>颜值：</td><td>${faceItem.beauty}</td><td>&emsp;&emsp;</td><td>表情：</td><td>${faceItem.expression.type}</td></tr>`;
                        html += '</table>';
                        self.alertBox(html);
                    } else {
                        self.messageError(response.msg);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            } else {
                faceVerify(data).then((response) => {
                    if (response.flag) {
                        const result = response.data.result;
                        const faceLiveness = result.face_liveness;
                        const faceItem = result.face_list[0];
                        let html = '<table>';
                        html += `<tr><td>活体：</td><td>${faceLiveness}</td><td>&emsp;&emsp;</td><td>性别：</td><td>${faceItem.gender.type}</td></tr>`;
                        html += `<tr><td>阀值：</td><td>${result.thresholds.frr_1e_2}</td><td>&emsp;&emsp;</td><td>脸型：</td><td>${faceItem.face_shape.type}</td></tr>`;
                        html += `<tr><td>年龄：</td><td>${faceItem.age}</td><td>&emsp;&emsp;</td><td>眼镜：</td><td>${faceItem.glasses.type}</td></tr>`;
                        html += `<tr><td>颜值：</td><td>${faceItem.beauty}</td><td>&emsp;&emsp;</td><td>表情：</td><td>${faceItem.expression.type}</td></tr>`;
                        html += '</table>';
                        self.alertBox(html);
                    } else {
                        self.messageError(response.msg);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            }
        },
        getPreviewSrcList() {
            const fileList = [];
            for (const item of this.fileList) {
                fileList.push(this.getFileUrl(item));
            }
            return fileList;
        },
    },
    watch: {
        isPreview(newVal) {
            this.previewSrcList = newVal ? this.getPreviewSrcList() : [];
        },
        fileList() {
            if (this.isPreview) {
                this.previewSrcList = this.getPreviewSrcList();
            }
        },
    },
};
</script>

<style scoped lang="scss">
    .images {
        ul {
            text-align: center;

            li {
                width: 30%;
                height: 250px;
                overflow: hidden;
                display: inline-block;

                .el-image {
                    width: 100%;
                    height: 100%;
                }

                .mt {
                    margin-top: 30px;
                }

                .ml {
                    margin-left: 30px;
                }
            }
        }

        .el-radio-group, .el-checkbox-group {
            margin-left: 10px;
        }
    }
</style>
