<template>
    <div>
        <panel-title/>
        <el-row :gutter="24">
            <el-col :span="8">
                <el-select v-model="packageName" filterable placeholder="请选择包" @change="getClassName">
                    <el-option v-for="(item,index)  in packageNameList" :key="index" :label="item.label" :value="item.value"/>
                </el-select>
            </el-col>
            <el-col :span="8">
                <el-select v-model="className" filterable placeholder="请选择类" @change="getMethod">
                    <el-option v-for="(item,index) of classNameList" :key="index" :label="substrClassName(item)" :value="item"/>
                </el-select>
            </el-col>
            <el-col :span="8">
                <el-select v-model="methodIndex" filterable placeholder="请选择方法" @change="setMethod">
                    <el-option v-for="(item,index) in methodList" :key="index" :label="item.name" :value="index"/>
                </el-select>
            </el-col>
        </el-row>
        <template v-if="!!method.parameterTypes && method.parameterTypes.length>0">
            <el-input v-for="(item,index) of method.parameterTypes" autosize class="distance" :key="index" type="textarea" :placeholder="item" v-model="uriVariables[index]"/>
            <el-button type="success" class="distance" @click="invoke">invoke</el-button>
        </template>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import {getClassName, getMethod, invoke} from '@assets/js/api/util';
import {panelTitle} from '@assets/js/common/components';

export default {
    name: 'reflect',
    components: {
        panelTitle,
    },
    mixins: [popup],
    data() {
        return {
            isRecursion: false,
            packageName: '',
            packageNameList: [{
                value: 'com.loyer.common.dedicine.utils',
                label: 'encrypt',
            }, {
                value: 'com.loyer.common.core.utils.encrypt',
                label: 'encrypt',
            }],
            className: '',
            classNameList: [],
            method: {},
            methodIndex: '',
            methodList: [],
            uriVariables: [],
        };
    },
    methods: {
        /* 根据包名获取下属类*/
        getClassName() {
            let self = this;
            const data = {
                packageName: self.packageName,
                isRecursion: self.isRecursion,
            };
            getClassName(data).then((response) => {
                if (response.flag) {
                    self.uriVariables = [];
                    self.method = {};
                    self.methodIndex = '';
                    self.methodList = [];
                    self.className = '';
                    self.classNameList = response.data;
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        /* 截取类名称*/
        substrClassName(className) {
            const classNameAry = className.split('.');
            return classNameAry[classNameAry.length - 1];
        },
        /* 获取方法对象*/
        getMethod() {
            let self = this;
            const data = {
                className: self.className,
            };
            getMethod(data).then((response) => {
                if (response.flag) {
                    self.uriVariables = [];
                    self.method = {};
                    self.methodIndex = '';
                    self.methodList = response.data;
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        /* 设置准备调用的方法对象*/
        setMethod(index) {
            this.uriVariables = [];
            this.method = this.methodList[index];
        },
        /* 调用*/
        invoke() {
            let self = this;
            const data = {
                className: self.className,
                methodEntity: self.method,
                uriVariables: self.uriVariables,
            };
            invoke(data).then((response) => {
                if (response.flag) {
                    self.alertBox(JSON.stringify(response.data));
                } else {
                    self.messageError(response.msg);
                }
            });
        },
    },
};
</script>

<style scoped lang="scss">
    .distance {
        margin-top: 20px;
    }
</style>
