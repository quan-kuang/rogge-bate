import Vue from 'vue';
import Vuex from 'vuex';
import user from './user';

Vue.use(Vuex);

// 创建 vuex 对象
const store = new Vuex.Store({
    modules: {
        user,
    },
});

// 导出store对象
export default store;
