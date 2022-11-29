<template>
    <div class="roof">
        <i :class="isCollapse?'iconfont icon-zhankai':'iconfont icon-shousuo'" @click="setIsCollapse"/>
        <div class="right-menu">
            <i class="iconfont icon-fangdajing1" @click="showSearchInput"/>
            <el-autocomplete class="search-input" value-key="title" v-model="menuName" placeholder="请输入菜单名称" :maxlength='10'
                             :trigger-on-focus="false" :highlight-first-item="true" :select-when-unmatched="true" :fetch-suggestions="fuzzySearch" @select="handleSelect"/>
            <i class="iconfont icon-github" @click="gotoGitee"/>
            <el-dropdown trigger="click" @command="onCommand">
                <div class="avatar">
                    <el-tooltip :content="user.name" placement="left">
                        <el-avatar :src="user.avatar | imageSrc"/>
                    </el-tooltip>
                </div>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item class="item" icon="iconfont icon-user" command="1">个人中心</el-dropdown-item>
                    <el-dropdown-item class="item" icon="iconfont icon-zhuti" command="2" :divided="true">主题设置</el-dropdown-item>
                    <el-dropdown-item class="item" icon="iconfont icon-shirenrenzheng" command="3" :divided="true">实名认证</el-dropdown-item>
                    <el-dropdown-item class="item" icon="iconfont icon-iconfontguanji" command="4" :divided="true">安全退出</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <el-drawer :title="title" :visible.sync="isDrawer" :append-to-body="true" :show-close="false" :size="drawerSize">
            <component :key="active" :is="active"/>
        </el-drawer>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import format from '@assets/js/util/format';
import {mapActions, mapMutations, mapState} from 'vuex';
import store from '@assets/js/vuex/store';

export default {
    name: 'roof',
    components: {
        theme: () => import('../../user/components/theme'),
        auth: () => import('../../user/components/auth'),
    },
    mixins: [popup],
    computed: {
        ...mapState({
            user: (state) => state.user.user,
            isCollapse: (state) => state.navigate.isCollapse,
            tabs: (state) => state.navigate.tabs,
            menus: (state) => format.tree(state.user.menus),
        }),
    },
    data() {
        return {
            currentTime: '',
            isDrawer: false,
            title: '',
            active: '',
            drawerSize: '',
            menuName: '',
            isShowSearchInput: false,
        };
    },
    filters: {
        imageSrc(src) {
            return constant.viewFtpUrl + src;
        },
    },
    methods: {
        ...mapMutations(['setIsCollapse', 'addTabs', 'setIsActiveName', 'setActive']),
        ...mapActions(['openHideMenu']),
        /* 显示隐藏输入框*/
        showSearchInput() {
            const searchInput = document.getElementsByClassName('search-input')[0];
            searchInput.style.width = this.isShowSearchInput ? '0px' : '160px';
            this.isShowSearchInput = !this.isShowSearchInput;
            this.isShowSearchInput && document.getElementsByClassName('el-input__inner')[0].focus();
        },
        /* 模糊查询*/
        fuzzySearch(queryString, callback) {
            callback(!queryString ? this.menus : this.menus.filter((menu) => {
                return menu.title.indexOf(queryString) > -1;
            }));
        },
        /* 选中查询*/
        handleSelect(menu) {
            let self = this;
            const {uuid, title, path, value} = menu;
            if (!path) {
                self.menuName = '';
                self.messageWaning(`【${value}】不存在`);
                return;
            }
            // 遍历检测是否已经添加该菜单标签
            const isExist = self.tabs.some((tab) => {
                return tab.name === uuid;
            });
            // 不存则添加标签导航
            if (!isExist) {
                self.addTabs({name: uuid, label: title, path: path});
            }
            // 设置左侧导航菜单选中
            self.setActive(path);
            // 设置活动导航
            self.setIsActiveName(uuid);
            // 跳转路由
            self.$router.push(path);
            // 收回输入框
            self.showSearchInput();
            self.menuName = '';
        },
        /* 跳转源码地址*/
        gotoGitee() {
            window.open('https://gitee.com/rogbet/rogge-bate');
        },
        /* 点击下拉框*/
        onCommand(command) {
            let self = this;
            if (command === '1') {
                // 处理actions多个入参问题
                const object = {router: self.$router, name: 'myCenter', label: '个人中心', path: '/common/myCenter'};
                // 添加用户缓存信息
                self.openHideMenu(object);
            } else if (command === '2') {
                self.title = '主题设置';
                self.active = 'theme';
                self.isDrawer = true;
                self.drawerSize = '18%';
            } else if (command === '3') {
                self.title = '实名认证';
                self.active = 'auth';
                self.isDrawer = true;
                self.drawerSize = '24%';
            } else if (command === '4') {
                self.logout();
            }
        },
        /* 退出*/
        logout() {
            let self = this;
            self.confirm('确定要退出吗？', () => {
                // 后端接口退出
                self.request.get('system/logout').then((response) => {
                    self.message(response.flag ? 'success' : 'error', response.msg);
                }).finally(() => {
                    // 恢复默认主题样式
                    store.dispatch('resetStyle');
                    // 回跳登录页
                    self.$router.push('/login');
                    // 清空缓存
                    self.storage.clear();
                });
            }, null);
        },
    },
};
</script>

<style lang="scss">
    .el-tooltip, .el-drawer, .el-drawer .el-drawer__header span {
        border: none;
        outline: none; // 处理点击出现边框
    }

    .el-drawer .el-drawer__header span {
        font-weight: bold;
        color: black;
    }

    .right-menu .el-input__inner {;
        color: white;
        width: 95%;
        padding: 0;
        border: none;
        border-radius: 0;
        border-bottom: white 1px solid;
        background-color: transparent;
    }
</style>

<style scoped lang="scss">
    .roof {
        width: 100%;
        background-color: var(--header-base);

        //图标
        i {
            color: white;
            line-height: 60px;
            margin-right: 10px;
            cursor: pointer;
        }

        //折叠图标
        .icon-zhankai, .icon-shousuo {
            font-size: 23px;
            line-height: 60px;
        }

        //右侧元素
        .right-menu {
            float: right;
            display: flex;
            justify-content: center;
            align-items: center;

            //搜索输入框
            .search-input {
                width: 0;
                transition: width 0.5s;
                -webkit-transition: width 0.5s;
            }

            //源码图标
            .icon-fangdajing1:hover, .icon-github:hover {
                font-size: 25px;
            }

            //用户头像
            .avatar {
                float: left;
                cursor: pointer;
                margin-right: 10px;
            }
        }
    }
</style>
