<template>
    <div class="panel-title" v-show="isShowPanelTitle">
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item class="home" @click.native="goHome">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item,index) in $route.matched" :key="index">{{ item.meta.title }}</el-breadcrumb-item>
            <el-breadcrumb-item v-if="title">{{ title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="button-box">
            <slot/>
        </div>
    </div>
</template>

<script>
import {mapMutations, mapState} from 'vuex';

export default {
    name: 'panelTitle',
    props: {
        title: {
            type: String,
        },
    },
    computed: {
        ...mapState({
            tabs: (state) => state.navigate.tabs,
            isShowPanelTitle: (state) => state.style.isShowPanelTitle,
        }),
    },
    methods: {
        ...mapMutations(['setActive', 'setIsActiveName']),
        goHome() {
            // 获取首页路由
            const tab = this.tabs[0];
            // 设置活动导航标签
            this.setIsActiveName(tab.name);
            // 设置左侧导航菜单选中
            this.setActive(tab.path);
            // 跳转路由
            this.$router.push(tab.path);
        },
    },
};
</script>

<style lang="scss">
    .panel-title {
        .icon-yunduanxiazai, .icon-shujudaochu {
            font-size: 14px;
            margin-right: 5px;
        }
    }
</style>

<style scoped lang="scss">
    .panel-title {
        display: flex;
        font-weight: bold;
        margin-bottom: 10px;
        border-bottom: 1px dotted rgba(0, 0, 0, 0.2);

        .el-breadcrumb {
            line-height: 50px;

            .home {
                cursor: pointer;

                :hover {
                    color: #20a0ff;
                }
            }
        }

        .button-box {
            margin-left: auto;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }
</style>
