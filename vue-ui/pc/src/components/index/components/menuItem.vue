<template>
    <div v-if="!menu.hide">
        <!--根据children和type判断是否为菜单目录，是则继续遍历子菜单-->
        <el-submenu :index="getPath(menu.path)" :disabled="isDisabled(menu.status)" v-if="isDirectory(menu)">
            <!--设置菜单目录的标题和图标-->
            <template slot="title">
                <i :class="getIcon(menu)"/>
                <span>{{ getTitle(menu) }}</span>
            </template>
            <!--递归子级菜单-->
            <menu-item v-for="(children, index) in menu.children" :key="index" :menu="children" :basePath="getPath(menu.path)"/>
        </el-submenu>
        <!--菜单直接设置跳转路由-->
        <el-menu-item :index="getPath(menu.path)" :disabled="isDisabled(menu.status)" v-else>
            <!--设置菜单的标题和图标-->
            <i :class="getIcon(menu)"/>
            <span slot="title" :title="getTitle(menu)">{{ getTitle(menu) }}</span>
        </el-menu-item>
    </div>
</template>

<script>
import global from '@assets/js/util/global';
import menuItem from './menuItem';

export default {
    name: 'menuItem',
    components: {menuItem},
    props: {
        menu: {
            type: Object,
            required: true,
        },
        basePath: {
            type: String,
            default: '',
        },
    },
    methods: {
        /* 判断是否为目录*/
        isDirectory(item) {
            return item.type === '0' || !!item.children && item.children[0].type !== '2' && item.type !== 'index';
        },
        /* 获取路由地址*/
        getPath(path) {
            return this.basePath + '/' + path;
        },
        /* 判断是否禁用（管理员依旧开放）*/
        isDisabled(status) {
            return !this.isAdmin && String(status) === 'false';
        },
        /* 获取菜单名称*/
        getTitle(item) {
            if (!!item.title && item.title.trim() !== '') {
                return item.title;
            }
            return !item.level ? '未定义' : global.decode(item.level, 1, '一级', 2, '二级', 3, '三级', '未知');
        },
        /* 设置菜单图标*/
        getIcon(item) {
            if (item.type === '0' && (!item || !item.icon || item.icon === '')) {
                return 'el-icon-eleme';
            }
            return item.icon;
        },
    },
};
</script>

<style scoped lang="scss">
    //第三方库的icon图标
    .iconfont {
        margin-right: 10px;
    }
</style>
