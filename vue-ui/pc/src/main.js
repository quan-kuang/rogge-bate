/* eslint-disable no-unused-vars */
// noinspection ES6CheckImport,ES6UnusedImports

// 引入组件
import Vue from 'vue';
import App from './App.vue';
import VueCookies from 'vue-cookies';
import VueParticles from 'vue-particles';

// 引入自定义js
import router from './assets/js/config/router';
import storage from './assets/js/config/storage';
import store from './assets/js/vuex/store';
import request from './assets/js/config/request';

// 引入element库
import Element from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 引入扩展属性
import './assets/icon/iconfont.css';
import permission from './assets/js/common/permission';
import custom from './assets/js/config/custom';

// 定义全局变量
Vue.prototype.storage = storage;
Vue.prototype.request = request;
Vue.config.productionTip = false;

Vue.use(Element);
Vue.use(VueCookies);
Vue.use(permission);
Vue.use(VueParticles);

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount('#app');
