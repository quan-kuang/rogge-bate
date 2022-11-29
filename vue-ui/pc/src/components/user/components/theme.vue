<template>
    <div class="theme">
        <div>
            <span>是否显示水印：</span>
            <el-switch class="content" v-model="isShowWatermark" active-color="#13ce66" inactive-color="#ff4949" @change="setIsShowWatermark"/>
        </div>
        <div>
            <span>是否显示导航标签：</span>
            <el-switch class="content" v-model="isShowTabs" active-color="#13ce66" inactive-color="#ff4949" @change="setIsShowTabs"/>
        </div>
        <div>
            <span>是否显示面板标签：</span>
            <el-switch class="content" v-model="isShowPanelTitle" active-color="#13ce66" inactive-color="#ff4949" @change="setIsShowPanelTitle"/>
        </div>
    </div>
</template>

<script>
import {mapMutations} from 'vuex';

export default {
    name: 'theme',
    computed: {
        isShowWatermark: {
            get() {
                return this.$store.state.style.isShowWatermark;
            },
            set(value) {
                this.$store.state.style.isShowWatermark = value;
            },
        },
        isShowTabs: {
            get() {
                return this.$store.state.style.isShowTabs;
            },
            set(value) {
                this.$store.state.style.isShowTabs = value;
            },
        },
        isShowPanelTitle: {
            get() {
                return this.$store.state.style.isShowPanelTitle;
            },
            set(value) {
                this.$store.state.style.isShowPanelTitle = value;
            },
        },
    },
    methods: {
        ...mapMutations(['setIsShowWatermark', 'setIsShowTabs', 'setIsShowPanelTitle']),
    },
};
</script>

<style scoped lang="scss">
    .theme {
        color: rgba(0, 0, 0, .65);
        font-size: 14px;

        div {
            height: 50px;
            margin-left: 20px;
            //弹性布局设置居中
            display: flex;
            //实现垂直居中
            align-items: center;
            //实现水平居中
            justify-content: left;
            //定位内容的位置
            .content {
                position: absolute;
                left: 150px;
            }
        }
    }
</style>
