import storage from '@assets/js/config/storage';
import request from '@assets/js/config/request';

/* 查询用户信息*/
async function selectUser() {
    if (!state.user) {
        return;
    }
    const url = 'user/selectUserBy';
    const data = {uuid: state.user.uuid};
    await request.post(url, data).then((response) => {
        if (response.data.flag) {
            mutations.setUser(state, response.data.data);
        }
    });
}

/* 定义state，数据*/
const state = {
    // 页面刷新状态
    refresh: true,
    // 用户信息
    user: null,
};

/* 定义getters，计算属性*/
const getters = {};

/* 定义mutations，修改数据，同步事务*/
const mutations = {
    /* 添加用户缓存*/
    setUser(state, user) {
        state.refresh = false;
        state.user = user;
        storage.setItem('user', user);
    },
};

/* 定义actions，提交mutation，异步事务*/
const actions = {
    /* 刷新用户缓存信息*/
    async refreshUser({commit, state}, {user}) {
        if (!user) {
            await selectUser();
        } else {
            commit('setUser', user);
        }
    },
};

export default {
    state,
    getters,
    mutations,
    actions,
};
