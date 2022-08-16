import Vue from 'vue';
import Vuex from 'vuex';
import navigate from './navigate';
import user from './user';
import style from './style';

Vue.use(Vuex);

// 创建 vuex 对象
const store = new Vuex.Store({
    modules: {
        navigate,
        user,
        style,
    },
});

// 导出store对象
export default store;
