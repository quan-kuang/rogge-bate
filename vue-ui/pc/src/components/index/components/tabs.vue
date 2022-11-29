<template>
    <div class="tabs" @wheel="handleScroll">
        <!--活动导航条-->
        <el-tabs v-model="activeName" type="card" @tab-click="tabClick" @tab-remove="tabRemove" @contextmenu.prevent.native="openContextMenu($event)">
            <el-tab-pane v-for="(item) in tabs" :key="item.name" :name="item.name" :label="item.label" :closable="tabs.length>1"/>
        </el-tabs>
        <!--右键关闭菜单-->
        <ul v-show="isShow" :style="{left:left+'px',top:top+'px'}" class="context-menu">
            <template v-if="visible">
                <li @click="tabRemove(currentTabName)" v-show="visibleCurrentAndOther">关闭当前</li>
                <li @click="closeTabs('left')" v-show="visibleLeft">关闭左边</li>
                <li @click="closeTabs('right')" v-show="visibleRight">关闭右边</li>
                <li @click="closeTabs('other')" v-show="visibleCurrentAndOther">关闭其他</li>
            </template>
            <template v-else>
                <li @click="refresh">页面刷新</li>
            </template>
        </ul>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import {mapActions, mapMutations, mapState} from 'vuex';

export default {
    name: 'tabs',
    inject: ['reload'],
    mixins: [popup],
    computed: {
        activeName: {
            get() {
                return this.$store.state.navigate.activeName;
            },
            set(value) {
                this.$store.state.navigate.activeName = value;
            },
        },
        ...mapState({
            tabs: (state) => state.navigate.tabs,
        }),
    },
    data() {
        return {
            top: 0,
            left: 0,
            isShow: false,
            visible: true,
            visibleCurrentAndOther: true,
            visibleLeft: true,
            visibleRight: true,
            currentTabName: '',
        };
    },
    methods: {
        ...mapActions(['refreshUser']),
        ...mapMutations(['setActive', 'setIsActiveName', 'delTabs', 'setTabs']),
        /* 点击标签导航*/
        tabClick(object) {
            let self = this;
            // 获取路由地址
            const path = self.tabs[Number(object.index)].path;
            // 设置左侧导航菜单选中
            self.setActive(path);
            // 跳转路由
            self.$router.push(path);
        },
        /* 移除标签*/
        tabRemove(name) {
            let self = this;
            // 遍历出选中标签索引
            const currentTab = self.selectCurrentTab(name);
            // 校验关闭的是当前活动标签
            if (self.activeName === name) {
                // 设置下一个活动标签
                let nextTab = self.tabs[currentTab.index + 1] || self.tabs[currentTab.index - 1];
                self.setIsActiveName(nextTab.name);
                // 设置左侧活动导航联动
                self.setActive(nextTab.path);
                //  跳转到该路由
                self.$router.push(nextTab.path);
            }
            // 删除选中标签
            self.delTabs({index: currentTab.index, count: 1});
        },
        /* 遍历出选中标签索引*/
        selectCurrentTab(name) {
            let self = this;
            for (let index = 0; index < self.tabs.length; index++) {
                // 遍历出选中标签
                if (self.tabs[index].name === name) {
                    return {index: index, tab: self.tabs[index]};
                }
            }
            return null;
        },
        /* 打开右键菜单*/
        openContextMenu(event) {
            let self = this;
            // 设置右键菜单显示位置
            self.top = event.clientY + 10;
            self.left = event.clientX;
            const id = event.target.id;
            // 根据ID判断是否选中标签
            if (!id) {
                // 点击空白处时右键菜单展开刷新按钮
                self.visible = false;
            } else {
                // 点击导航标签时右键菜单展开关闭按钮
                self.visible = true;
                // 截取元素id作为标签name，
                const name = id.split('-')[1];
                // 设置当前选中标签名称
                self.currentTabName = name;
                // 遍历出选中的标签索引
                const currentTab = self.selectCurrentTab(name);
                // 只有一个隐藏关闭当前和其他
                self.visibleCurrentAndOther = self.tabs.length > 1;
                // 第一个隐藏关闭左边
                self.visibleLeft = currentTab.index > 0;
                // 最后一个隐藏关闭右边
                self.visibleRight = currentTab.index < self.tabs.length - 1;
            }
            // 显示右键菜单
            self.isShow = true;
        },
        /* 关闭导航标签*/
        closeTabs(type) {
            let self = this;
            // 遍历出选中标签索引
            const currentTab = self.selectCurrentTab(self.currentTabName);
            // 判断删除类型
            if (type === 'left') {
                self.delTabs({index: 0, count: currentTab.index});
            } else if (type === 'right') {
                self.delTabs({index: currentTab.index + 1, count: self.tabs.length - currentTab.index - 1});
            } else if (type === 'other') {
                self.setTabs([self.tabs[currentTab.index]]);
            }
            // 若当前活动标签也被删除设置选中导航
            if (!self.tabs.some((tab) => tab.name === self.activeName)) {
                self.setIsActiveName(currentTab.tab.name);
                // 设置左侧活动导航联动
                self.setActive(currentTab.tab.path);
                //  跳转到该路由
                self.$router.push(currentTab.tab.path);
            }
        },
        /* 关闭右键菜单*/
        closeContextMenu() {
            this.isShow = false;
        },
        /* 页面刷新*/
        async refresh() {
            let self = this;
            const loading = self.loadingOpen();
            await self.refreshUser({router: self.$router});
            self.messageSuccess('刷新成功');
            self.reload();
            loading && loading.close();
        },
        /* 鼠标滚轮事件*/
        handleScroll(e) {
            // 获取父级容器的宽度
            const parentWidth = document.getElementsByClassName('el-tabs__nav-scroll')[0].offsetWidth;
            // 获取操作对象
            const object = document.getElementsByClassName('el-tabs__nav')[0];
            // 获取所有导航标签对象
            const tabs = object.childNodes;
            let tabsWidth = 0;
            // 获取合计标签的总宽度
            for (let item of tabs) {
                tabsWidth += item.offsetWidth;
            }
            const transform = object.style.transform;
            // 截取transform里面translateX值
            const xAxis = Number(transform.replace(/[^-.0-9]/ig, ''));
            // 获取预计滚动值（每次滚动50px）
            let moveDistance = xAxis.cusSub(e.deltaY / Math.abs(e.deltaY) * 50);
            // 如果是左侧移动并且已经到达尽头直接返回（最大值为起止位置0px）
            if (e.deltaY < 0 && xAxis >= 0) {
                return;
            }
            // 如果是右侧移动并且已经到达尽头直接返回（最大值为tabs的合计宽度）
            tabsWidth = tabsWidth * -1;
            if (e.deltaY > 0 && tabsWidth > -parentWidth + xAxis) {
                return;
            }
            // 达到最大左侧滚动值
            if (moveDistance > 0) {
                moveDistance = 0;
            }
            // 达到最大右侧滚动值
            if (moveDistance < tabsWidth) {
                moveDistance = tabsWidth;
            }
            object.style.transform = `translateX(${moveDistance}px)`;
        },
    },
    watch: {
        /* 处理打开右键菜单无操作，一直显示的问题*/
        isShow(value) {
            if (value) {
                document.body.addEventListener('click', this.closeContextMenu);
            } else {
                document.body.removeEventListener('click', this.closeContextMenu);
            }
        },
    },
};
</script>

<style lang="scss">
    .tabs {
        //阴影效果
        box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.1);

        //单个标签div
        .el-tabs__item {
            max-height: 35px;
            line-height: 35px;
            font-size: 13px;
            padding-right: 10px;
            overflow: hidden;
            margin-top: 1px;
            margin-left: 1px;
            user-select: none;
            /*max-width: 120px;*/
            /*text-overflow: ellipsis;*/
        }

        //导航标签鼠标悬浮
        .el-tabs__item:hover {
            color: var(--tabs-item-hover);
        }

        //card样式下活动中的标签
        .el-tabs__item.is-active {
            color: #ffffff;
            background-color: var(--tabs-item-active);
        }

        //card样式下的高度
        .el-tabs--card > .el-tabs__header .el-tabs__nav {
            border: none;
            height: 35px;
        }

        //border-card样式下的高度
        .el-tabs--border-card > .el-tabs__header {
            border-bottom: none;
            height: 35px;
        }

        //去除下面多余内容元素
        .el-tabs__content {
            display: none;
        }

        //超出滑动按钮
        .el-tabs__nav-next, .el-tabs__nav-prev {
            line-height: 35px;
        }

        // 右键关闭菜单
        .context-menu {
            position: absolute;
            list-style-type: none;
            background: #fff;
            margin: 0;
            z-index: 666;
            font-size: 12px;
            padding: 5px 0;
            border-radius: 5px;
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.2);

            li {
                height: 25px;
                line-height: 25px;
                padding: 2px 13px;
                cursor: pointer;

                &:hover {
                    background: #eee;
                }
            }
        }
    }
</style>
