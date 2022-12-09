<template>
    <el-container>
        <el-aside>
            <left/>
        </el-aside>
        <el-container class="right">
            <el-header>
                <roof/>
            </el-header>
            <el-header class="tabs" v-if="isShowTabs">
                <tabs/>
            </el-header>
            <el-main>
                <el-backtop target=".el-main"/>
                <right/>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
import '@assets/css/theme.css';
import roof from './components/roof';
import left from './components/left';
import right from './components/right';
import tabs from './components/tabs';
import {mapMutations, mapState} from 'vuex';
import watermark from '@assets/js/util/watermark';

export default {
    name: 'layout',
    components: {
        roof, left, right, tabs,
    },
    computed: {
        ...mapState({
            user: (state) => state.user.user,
            isShowWatermark: (state) => state.style.isShowWatermark,
            isShowTabs: (state) => state.style.isShowTabs,
        }),
    },
    mounted() {
        this.isShowWatermark && this.addWatermark();
        this.windowAdaptive();
    },
    methods: {
        ...mapMutations(['setTableMaxHeight']),
        addWatermark() {
            watermark.addWatermark({'content': this.user.account});
        },
        windowAdaptive() {
            window.onresize = () => {
                return (() => {
                    this.setTableMaxHeight();
                    watermark.deleteWatermark();
                    this.isShowWatermark && this.addWatermark();
                })();
            };
        },
    },
    watch: {
        /* 监听是否显示水印*/
        isShowWatermark(newValue) {
            if (newValue) {
                this.addWatermark();
            } else {
                watermark.deleteWatermark();
            }
        },
    },
};
</script>

<style scoped lang="scss">
    //高度撑满
    .el-container {
        height: 100%;
    }

    //左侧导航
    .el-aside {
        max-width: 200px;
    }

    //右侧主体
    .el-container.right {
        transition: margin-left 0.2s;
        -webkit-transition: margin-left 0.2s;
        background-color: var(--main-base);
    }

    //头部导航
    .el-header {
        padding: 0;
    }

    //标签导航
    .el-header.tabs {
        background-color: var(--tabs-base);
        height: 35px !important;
        line-height: 35px;
        vertical-align: middle;
    }
</style>
