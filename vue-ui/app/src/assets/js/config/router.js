import Vue from 'vue';
import Router from 'vue-router';
import storage from '@assets/js/config/storage';
import store from '@assets/js/vuex/store';

/* 处理重复点菜单报错问题*/
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch((err) => err);
};

Vue.use(Router);

const routers = [
    {
        path: '/',
        meta: {
            requireLogin: true,
        },
        component: () => import('@components/index/main'),
    }, {
        path: '/login',
        component: () => import('@components/login/main'),
    }, {
        path: '/wechat/:openId',
        meta: {
            requireLogin: true,
        },
        component: () => import('@components/wechat/main'),
    }, {
        path: '*', // 页面不存在时回调页面
        name: '404',
        redirect: '/',
        hidden: true,
    },
];

const router = new Router({
    base: '/app', // tomcat发布设置/app/、nginx设置/
    mode: 'history',
    routes: routers,
});

router.beforeEach((to, from, next) => {
    const user = storage.getItem('user');
    // 已登录并且是刷新操作重新赋值state
    if (!!user && store.state.user.refresh) {
        // 处理actions多个入参问题
        const object = {user: user};
        // 添加用户缓存信息
        store.dispatch('refreshUser', object);
        next({...to, replace: true});
        return;
    }
    // 校验路由是否需要登录权限
    if (to.matched.some((record) => record.meta.requireLogin)) {
        if (!user) {
            next({
                path: '/login',
                query: {
                    redirectUrl: encodeURIComponent(to.fullPath),
                },
            });
        } else {
            next();
        }
    } else {
        next();
    }
});

router.afterEach(function (to, from) {
// TODO
});

export default router;
