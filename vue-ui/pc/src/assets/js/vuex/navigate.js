import storage from '@assets/js/config/storage';

/* 缓存tabs信息*/
function setTabs() {
    storage.setItem('tabs', state.tabs);
}

const state = {
    // 左侧导航栏折叠状态
    isCollapse: false,
    // 左侧活动导航菜单
    active: '',
    // 标签页数据
    tabs: [{name: 'index', label: '首页', path: '/index'}],
    // 活动标签页（取值为tabs索引的字符串类型）
    activeName: 'index',
};

const getters = {};

const mutations = {
    /* 修改折叠状态*/
    setIsCollapse(state) {
        state.isCollapse = !state.isCollapse;
        storage.setItem('isCollapse', state.isCollapse);
    },
    /* 设置活动菜单*/
    setActive(state, active) {
        state.active = active;
    },
    /* 新增标签导航*/
    addTabs(state, tab) {
        state.tabs.push(tab);
        setTabs();
    },
    /* 删除标签导航*/
    delTabs(state, {index, count}) {
        state.tabs.splice(index, count);
        setTabs();
    },
    /* 设置标签导航*/
    setTabs(state, tabs) {
        tabs = tabs || [];
        state.tabs = tabs;
        setTabs();
    },
    /* 设置活动标签*/
    setIsActiveName(state, activeName) {
        state.activeName = activeName;
    },
};

const actions = {
    /* 打开隐藏菜单*/
    openHideMenu({commit, state}, {router, name, label, path}) {
        // 遍历检测是否已经添加该菜单标签
        const isExist = state.tabs.some((tab) => {
            return tab.name === name;
        });
        // 不存则添加标签导航
        if (!isExist) {
            const tab = {name: name, label: label, path: path};
            commit('addTabs', tab);
        }
        // 设置活动导航
        commit('setIsActiveName', name);
        // 跳转到该路由
        router.push(path);
    },
    /* 设置默认折叠状态*/
    setIsCollapse({commit, state}) {
        const isCollapse = storage.getItem('isCollapse');
        if (isCollapse != null && isCollapse !== state.isCollapse) {
            commit('setIsCollapse');
        }
    },
};

export default {
    state,
    getters,
    mutations,
    actions,
};
