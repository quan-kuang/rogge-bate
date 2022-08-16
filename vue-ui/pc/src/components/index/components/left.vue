<template>
    <div class="left">
        <logo/>
        <el-scrollbar>
            <el-menu background-color="var(--menu-base)" text-color="var(--menu-base-text)" active-text-color="var(--menu-base-text-active)"
                     :router="true" :collapse="isCollapse" :default-active="active" :unique-opened="false" :collapse-transition="false"
                     @open="handleOpen" @close="handleClose" @select="handleSelect">
                <!--遍历菜单、数据结构等同树状路由-->
                <menu-item v-for="(menu, index) in menus" :key="index" :menu="menu"/>
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script>
import {mapMutations, mapState} from 'vuex';
import logo from './logo';
import menuItem from './menuItem';

export default {
    name: 'left',
    components: {logo, menuItem},
    computed: {
        active: {
            get() {
                return this.$store.state.navigate.active;
            },
            set(value) {
                this.$store.state.navigate.active = value;
            },
        },
        ...mapState({
            menus: (state) => state.user.menus,
            isAdmin: (state) => state.user.isAdmin,
            isCollapse: (state) => state.navigate.isCollapse,
            tabs: (state) => state.navigate.tabs,
        }),
    },
    created() {
        this.setTabs(this.storage.getItem('tabs'));
        this.handleSelect(this.$route.path);
    },
    mounted() {
        this.setIsCollapse();
    },
    methods: {
        ...mapMutations(['addTabs', 'setTabs', 'setIsActiveName']),
        /* sub-menu展开的回调*/
        handleOpen(index, indexPath) {
            // TODO
        },
        /* sub-menu收起的回调*/
        handleClose(index, indexPath) {
            // TODO
        },
        /* 菜单激活回调*/
        handleSelect(index) {
            let self = this;
            // 延迟等待获取跳转后的路由
            setTimeout(() => {
                // 获取路由meta对象
                const menu = self.$route.meta;
                // 遍历检测是否已经添加该菜单标签
                const isExist = self.tabs.some((tab) => {
                    return tab.name === menu.uuid;
                });
                // 不存则添加标签导航
                if (!isExist) {
                    const tab = {name: menu.uuid, label: menu.title, path: index};
                    self.addTabs(tab);
                }
                // 设置活动导航
                self.setIsActiveName(menu.uuid);
            }, 100);
        },
        /* 折叠导航菜单*/
        setIsCollapse() {
            const left = document.getElementsByClassName('left')[0];
            left.style.width = this.isCollapse ? '64px' : '200px';
            const right = document.getElementsByClassName('right')[0];
            right.style.marginLeft = this.isCollapse ? '-136px' : '0px';
        },
    },
    watch: {
        /* 监听是否折叠导航菜单*/
        isCollapse() {
            this.setIsCollapse();
        },
    },
};
</script>

<style scoped lang="scss">
    .left {
        //左侧导航置顶，折叠动画
        position: fixed;
        overflow: hidden;
        width: 200px;
        height: 100%;
        z-index: 777;
        transition: width 0.2s;
        -webkit-transition: width 0.2s;
        background-color: var(--menu-base);

        //滚动窗口
        .el-scrollbar {
            height: calc(100% - 60px);
        }

        //菜单设置
        .el-menu {
            border: 0;
            max-width: 200px;
        }

        //导航元素块
        .el-submenu ul .el-menu-item {
            max-width: 100px;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    }
</style>

<style lang="less">
    .left {
        //滚动窗口
        .el-scrollbar .el-scrollbar__wrap {
            overflow-x: hidden;
        }

        //悬浮菜单节点背景色
        .el-submenu__title:hover {
            background-color: var(--menu-title-hover) !important;
        }

        //悬浮菜单背景色
        .el-menu-item:hover {
            background-color: var(--menu-item-hover) !important;
        }

        //选中菜单颜色
        .el-menu-item.is-active {
            background-color: var(--menu-item-active) !important;
        }

        //折叠菜单后隐藏文字
        .el-menu--collapse .el-submenu__title span {
            display: none;
        }

        //折叠菜单后隐藏符号>
        .el-menu--collapse .el-submenu__icon-arrow {
            display: none;
        }

        //折叠小箭头关闭
        .el-submenu > .el-submenu__title .el-submenu__icon-arrow {
            -webkit-transform: rotateZ(-90deg);
            -ms-transform: rotate(-90deg);
            transform: rotateZ(-90deg);
        }

        //折叠小箭头展开
        .el-submenu.is-opened > .el-submenu__title .el-submenu__icon-arrow {
            -webkit-transform: rotateZ(0deg);
            -ms-transform: rotate(0deg);
            transform: rotateZ(0deg);
        }
    }

    //折叠后的隐藏菜单
    .el-menu--vertical .el-menu {
        //背景色
        background-color: var(--menu-base) !important;

        //悬浮菜单节点背景色
        .el-submenu__title:hover {
            background-color: var(--menu-title-hover) !important;
        }

        //悬浮菜单背景色
        .el-menu-item:hover {
            background-color: var(--menu-item-hover) !important;
        }

        //选中菜单颜色
        .el-menu-item.is-active {
            background-color: var(--menu-item-active) !important;
        }

        //菜单字体颜色
        .el-menu-item, .el-submenu__title {
            span {
                color: var(--menu-base-text);
            }
        }

        //选中菜单字体颜色
        .el-menu-item.is-active span {
            color: var(--menu-base-text-active);
        }
    }
</style>
