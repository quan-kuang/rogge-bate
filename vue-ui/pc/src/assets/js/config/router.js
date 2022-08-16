import Vue from 'vue';
import Router from 'vue-router';
import {MessageBox} from 'element-ui';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import storage from '@assets/js/config/storage';
import store from '@assets/js/vuex/store';

/* 隐藏进度环*/
NProgress.configure({showSpinner: false});

/* 处理重复点菜单报错问题*/
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch((err) => err);
};

Vue.use(Router);

/* 公共路由*/
const routers = [
    {
        path: '/login',
        name: 'login',
        component: () => import('@components/login/main'),
    }, {
        path: '/',
        redirect: '/index',
    }, {
        path: '/500',
        component: () => import('@components/error/500'),
    },
];

/* 静态路由*/
const menus = [
    {
        level: 1, // 菜单等级
        title: '首页', // 菜单名称
        path: 'index', // 首页地址
        type: 'index', // 特殊处理首页路由在导航栏显示
        icon: 'iconfont icon-shouye1', // 菜单图标
        url: '/index/main', // 二级路由出口组件（等同于component会根据路径动态加载该组件）
        children: [{
            path: '',
            title: '首页', // 用于标签导航的label
            uuid: 'index', // 用于标签导航的name
            component: () => import('@components/home/main'), // 首页组件
        }],
    },
    {
        level: 1, // 菜单等级
        title: '公共目录', // 菜单名称
        path: 'common', // 公共目录
        icon: 'iconfont icon-Group-1', // 菜单图标
        url: '/index/main', // 二级路由出口组件（等同于component会根据路径动态加载该组件）
        hide: true, // 隐藏该目录
        children: [{
            path: 'myCenter',
            title: '个人中心', // 用于标签导航的label
            uuid: 'myCenter', // 用于标签导航的name
            component: () => import('@components/user/components/myCenter'), // 个人中心组件
        }],
    },
];

/* 特殊设置*/
const back = [
    {
        path: '*', // 页面不存在时回调页面
        name: '404',
        redirect: '/index',
        hidden: true,
    },
];

function createRouter() {
    return new Router({
        base: '/', // tomcat发布设置/app/、nginx设置/
        mode: 'history',
        routes: routers,
        menus: menus,
        back: back,
    });
}

const router = createRouter();

/* 重写路由添加方法*/
router.$addRoutes = function (routes) {
    // 避免死循环，加入当前活动路由数和静态路由数的长度校验
    if (router.getRoutes().length > routers.length) {
        router.matcher = createRouter().matcher;
    }
    // 避免警告将addRoutes()改成addRoute()
    routes.forEach((route) => {
        router.addRoute(route);
    });
};

/* 进入路由前的钩子函数*/
router.beforeEach(function (to, from, next) {
    NProgress.start();
    const user = storage.getItem('user');
    // 校验是否为500页面
    if (to.fullPath === '/500') {
        next();
        return;
    }
    // 未登录且未加载动态路由则跳转登录页、避免死循环加入回调地址判定
    if (!user && to.fullPath !== '/login' && to.fullPath.indexOf('redirectUrl') < 0) {
        next({
            path: '/login',
            query: {
                redirectUrl: encodeURIComponent(to.fullPath),
            },
        });
    }
    // 设置主题样式
    store.dispatch('setStyle');
    // 已登录并且是刷新操作加载动态路由、根据缓存信息设置菜单折叠
    if (!!user && store.state.user.refresh) {
        // 设置默认折叠状态
        store.dispatch('setIsCollapse');
        // 处理actions多个入参问题
        const object = {user: user, router: router};
        // 添加用户缓存信息
        store.dispatch('refreshUser', object);
        next({...to, replace: true});
        return;
    }
    // 判断当前菜单路由是否被停用
    if (to.matched.some((record) => String(record.meta.status) === 'false')) {
        if (!!user && !store.state.user.isAdmin) {
            MessageBox.alert('<strong><i style="color: red">该页面已停用！</i></strong>', '提示', {
                dangerouslyUseHTMLString: true,
                callback: () => {
                    next('/');
                },
            });
            return;
        }
    }
    // 校验路由是否需要认证权限
    if (to.matched.some((record) => record.meta.requireAuth)) {
        if (!!user && !store.state.user.isAdmin && !user.auth) {
            MessageBox.confirm('该页面需要认证，立即前往？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                next({
                    path: '/hide/auth',
                    query: {
                        redirectUrl: encodeURIComponent(to.fullPath),
                    },
                });
            }).catch(() => {
                next('/');
            });
            return;
        }
    }
    // 校验路由是否需要操作权限（当前逻辑此校验失效）
    if (to.matched.some((record) => record.meta.permission)) {
        if (!!user && !store.state.user.isAdmin && !store.state.user.permissions.includes(to.meta.permission)) {
            MessageBox.confirm('尚无该页面权限！', '提示', {
                confirmButtonText: '返回首页',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                next('/');
            });
            return;
        }
    }
    next();
});

// noinspection JSUnusedLocalSymbols
router.afterEach(function (to, from) {
    NProgress.done();
});

export default router;
