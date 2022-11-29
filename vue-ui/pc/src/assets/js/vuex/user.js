import format from '@assets/js/util/format';
import router from '@assets/js/config/router';
import storage from '@assets/js/config/storage';
import constant from '@assets/js/common/constant';
import {selectUserBy} from '@assets/js/api/user';

/* 查询用户信息*/
async function selectUser() {
    if (!state.user) {
        return;
    }
    const data = {uuid: state.user.uuid};
    await selectUserBy(data).then((response) => {
        if (response.flag) {
            mutations.setUser(state, response.data);
        }
    });
}

/* 获取路由菜单用于左侧导航栏展示*/
function getMenuTree() {
    const user = state.user;
    // 如果用户信息不存在或者无菜单权限直接返回
    if (!user || user.menuList.length === 0) {
        return [];
    }
    // 将后台返回的菜单数据转换成树状结构
    let menuTree = format.list(user.menuList);
    // 添加router.js中定义的静态菜单（拼接顺序将首页放置最前）
    menuTree = router.options.menus.concat(menuTree);
    return menuTree;
}

/* 将树状菜单转换成vue路由对象*/
function formatRoutes(menuList) {
    const routes = [];
    for (const item of menuList) {
        let {level, uuid, url, path, name, title, status, redirect, permission, requireAuth, hide, children, component} = item;
        // 包含children对象的递归遍历子路由
        if (!!children && children instanceof Array) {
            // 按钮级别的菜单不加入路由当中
            children = children[0].type === '2' ? null : formatRoutes(children);
        }
        // 此处将扩展信息放入meta对象中，用于在router钩子函数中to对象里获取
        const meta = {
            uuid: uuid,
            title: title,
            status: status,
            permission: permission,
            requireAuth: requireAuth,
            hide: hide,
        };
        // 一级菜单前面要保证加上/
        if (level === 1 && path.substr(0, 1) !== '/') {
            path = '/' + path;
        }
        // 创建路由对象
        const route = {
            path: path,
            name: name,
            meta: meta,
            redirect: redirect,
            children: children,
            // 此处component若不为null则在router.js中定义了静态路由
            component: !component ? (resolve) => require(['@/components' + url], resolve) : component,
        };
        routes.push(route);
    }
    return routes;
}

/* 添加动态路由*/
function addRoutes(router) {
    const menus = state.menus;
    if (menus === 0) {
        return [];
    }
    // 将菜单树转换成vue路由对象
    let routes = formatRoutes(menus);
    // 拼接404等特殊路由
    routes = routes.concat(router.options.back);
    // 动态添加路由
    router.$addRoutes(routes);
    return routes;
}

/* 过滤路由转换成树状组件data结构*/
function filterMenus(menus, filter) {
    let menuTree = [];
    for (let item of menus) {
        // 特殊菜单隐藏
        if (item.hide || item.path === 'index') {
            continue;
        }
        let menu = {};
        menu.level = item.level;
        menu.type = item.type;
        menu.uuid = item.uuid;
        menu.label = item.title;
        menu.disabled = !item.status;
        // 递归子菜单
        if (!!item.children && item.children.length > 0) {
            menu.children = filterMenus(item.children, filter);
        }
        if (!filter) {
            menuTree.push(menu);
        } else if (item.type !== filter) {
            menuTree.push(menu);
        }
    }
    return menuTree;
}

/* 获取用户的操作权限*/
function getPermissions() {
    let permissions = [];
    // 遍历出用户操作权限权限
    for (const item of state.user.menuList) {
        if (!!item.permission) {
            permissions.push(item.permission);
        }
    }
    return permissions;
}

/* 判断是否拥有该权限*/
function isHasPermission(permissions, type) {
    // 校验管理员权限
    if (state.isAdmin) {
        return true;
    }
    // 校验入参为空
    if (!permissions) {
        return false;
    }
    // 校验是否传入数组
    if (!(permissions instanceof Array)) {
        permissions = [permissions];
    }
    // 校验是否具有传入的条件的任一权限
    if (type === 'some') {
        return permissions.some((permission) => {
            return state.permissions.includes(permission);
        });
    }
    // 校验是否具备传入的所有权限
    else if (type === 'every') {
        return permissions.every((permission) => {
            return state.permissions.includes(permission);
        });
    }
    return false;
}

/* 定义state，数据*/
const state = {
    // 页面刷新状态
    refresh: true,
    // 用户信息
    user: null,
    // 菜单信息
    menus: [],
    // 路由信息
    routes: [],
    // 操作权限
    permissions: [],
    // 是否为管理员
    isAdmin: false,
};

/* 定义getters，计算属性*/
const getters = {
    /* 菜单树*/
    menuTree: (state) => {
        return filterMenus(state.menus);
    },
    /* 上级菜单目录树*/
    menuDirTree: (state) => {
        return filterMenus(state.menus, '2');
    },
    /* 校验是否具有传入的条件的任一权限*/
    isHasAnyPermissions: (state) => (permissions) => {
        return isHasPermission(permissions, 'some');
    },
    /* 校验是否具备传入的所有权限*/
    isHasAllPermissions: (state) => (permissions) => {
        return isHasPermission(permissions, 'every');
    },
};

/* 定义mutations，修改数据，同步事务*/
const mutations = {
    /* 添加用户缓存*/
    setUser(state, user) {
        // 设置默认头像
        if (!user.avatar) {
            user.avatar = 'image/me.jpg';
        }
        state.user = user;
        // 判断是管理员用户或者拥有管理员角色权限
        state.isAdmin = state.user.uuid === constant.admin || !!state.user.roleIds && state.user.roleIds.includes(constant.admin);
        storage.setItem('user', user);
    },
    /* 设置导航菜单*/
    setMenus(state) {
        state.menus = getMenuTree();
    },
    /* 添加动态路由*/
    setRoutes(state, router) {
        state.routes = addRoutes(router);
        state.refresh = false;
    },
    /* 获取用户的操作权限*/
    setPermissions(state) {
        state.permissions = getPermissions();
    },
};

/* 定义actions，提交mutation，异步事务*/
const actions = {
    /* 刷新用户/菜单/路由/操作权限等缓存信息*/
    async refreshUser({commit, state}, {user, router}) {
        if (!user) {
            await selectUser();
        } else {
            commit('setUser', user);
        }
        commit('setMenus');
        commit('setRoutes', router);
        commit('setPermissions');
    },
};

export default {
    state,
    getters,
    mutations,
    actions,
};
