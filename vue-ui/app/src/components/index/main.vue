<template>
    <div>
        <!--顶部-->
        <div class="header">
            <!--通知条-->
            <van-notice-bar left-icon="info-o" :scrollable="true" mode="closeable" color="#1989fa" background="#ecf9ff">
                {{ version }}
            </van-notice-bar>
            <!--轮播图-->
            <van-swipe class="swipe" :autoplay="3000" indicator-color="white">
                <van-swipe-item v-for="(item, index) in homeImages" :key="index">
                    <img v-lazy="getFileUrl(item)" alt=""/>
                </van-swipe-item>
            </van-swipe>
        </div>
        <!--功能菜单-->
        <div class="middle">
            <van-swipe :autoplay="0" indicator-color="white">
                <van-swipe-item>
                    <van-grid>
                        <van-grid-item icon="gem-o" text="微信接口" to="/wechat/:openId"/>
                        <van-grid-item icon="gem-o" text="文字"/>
                        <van-grid-item icon="gem-o" text="文字"/>
                        <van-grid-item icon="gem-o" text="文字"/>
                        <van-grid-item icon="photo-o" text="文字"/>
                        <van-grid-item icon="photo-o" text="文字"/>
                        <van-grid-item icon="photo-o" text="文字"/>
                        <van-grid-item icon="photo-o" text="文字"/>
                    </van-grid>
                </van-swipe-item>
                <van-swipe-item>
                    <van-grid>
                        <van-grid-item v-for="value in 8" :key="value" icon="photo-o" text="文字"/>
                    </van-grid>
                </van-swipe-item>
            </van-swipe>
        </div>
        <!--底部导航条-->
        <div class="footer">
            <van-tabbar v-model="active" active-color="#07c160" inactive-color="#000">
                <van-tabbar-item icon="home-o">标签</van-tabbar-item>
                <van-tabbar-item icon="search">标签</van-tabbar-item>
                <van-tabbar-item icon="friends-o">标签</van-tabbar-item>
                <van-tabbar-item icon="setting-o" @click.native="logout">退出</van-tabbar-item>
            </van-tabbar>
        </div>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import {showVersion} from '@assets/js/api/demo';
import {selectAttachment} from '@assets/js/api/attachment';

export default {
    name: 'index',
    components: {},
    mixins: [popup],
    data() {
        return {
            version: '罗格贝特 ',
            active: 0,
            homeImages: [],
        };
    },
    created() {
        this.showVersion();
        this.selectHomeImages();
    },
    methods: {
        /* 查看系统版本号*/
        showVersion() {
            let self = this;
            showVersion().then((response) => {
                if (response.data.flag) {
                    self.version += response.data.data;
                } else {
                    self.alert(response.data.msg);
                }
            });
        },
        /* 查询首页图片*/
        selectHomeImages() {
            let self = this;
            const data = {
                type: 'image',
            };
            const loading = self.loadingOpen('加载中...');
            selectAttachment(data).then((response) => {
                if (response.data.flag) {
                    self.homeImages = response.data.data;
                } else {
                    self.alert(response.data.msg);
                }
            }).finally(() => {
                loading.clear();
            });
        },
        /* 获取附件URL*/
        getFileUrl(row) {
            switch (row.source) {
                case '0':
                    return constant.viewLocalUrl + row.path;
                case '1':
                    return constant.viewFtpUrl + row.path;
                case '2':
                    return constant.viewFastDfsUrl + row.path;
                default:
                    return '/';
            }
        },
        /* 退出*/
        logout() {
            let self = this;
            // 后端接口退出
            self.request.get('system/logout').then((response) => {
                self.toast(response.data.msg, response.data.flag ? 'T' : 'F');
            }).finally(() => {
                // 删除缓存用户认证token
                self.storage.removeItem('token');
                // 删除缓存用户信息
                self.storage.removeItem('user');
                // 回跳登录页
                self.$router.push('/login');
            });
        },
    },
};
</script>

<style scoped lang="scss">
    /*轮播图*/
    .swipe img {
        width: 100%;
        height: 180px;
    }

    /*菜单功能区*/
    .van-swipe-item .van-grid-item__text {
        font-size: 13px;
    }

    /*底部按钮*/
    .van-tabbar .van-tabbar-item {
        font-size: 13px;
    }
</style>
