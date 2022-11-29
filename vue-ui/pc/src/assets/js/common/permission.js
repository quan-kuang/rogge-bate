import store from '@assets/js/vuex/store';

/* 判断是否拥有该权限*/
function isHasPermission(element, binding, type) {
    // 管理员无需校验
    if (store.state.user.isAdmin) {
        return true;
    }
    // 获取元素上的绑定权限值
    const {value} = binding;
    // 获取用户的权限
    const permissions = store.state.user.permissions;
    // 校验绑定值
    if (!!value && value instanceof Array && value.length > 0) {
        let isHasAllPermissions = false;
        // 校验是否具有传入的条件的任一权限
        if (type === 'some') {
            isHasAllPermissions = value.some((permission) => {
                return permissions.includes(permission);
            });
        }
        // 校验是否具备传入的所有权限
        else if (type === 'every') {
            isHasAllPermissions = value.every((permission) => {
                return permissions.includes(permission);
            });
        }
        // 如果不存在该权限则隐藏该元素
        element.style.display = isHasAllPermissions ? '' : 'none';
    } else {
        throw new Error('请设置需要验证的权限标识');
    }
}

/* 校验是否具有传入的条件的任一权限*/
function hasAnyPermissions(element, binding) {
    return isHasPermission(element, binding, 'some');
}

/* 校验是否具备传入的所有权限*/
function hasAllPermissions(element, binding) {
    return isHasPermission(element, binding, 'every');
}

/* 自定义指令*/
const permission = function (Vue) {
    Vue.directive('hasAnyPermissions', hasAnyPermissions);
    Vue.directive('hasAllPermissions', hasAllPermissions);
};

if (window.Vue) {
    window['hasAnyPermissions'] = hasAnyPermissions;
    window['hasAllPermissions'] = hasAllPermissions;
    // eslint-disable-next-line no-undef
    Vue.use(permission);
}

export default permission;
