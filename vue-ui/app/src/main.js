// 引入组件
import Vue from 'vue';
import App from './App.vue';
import VueCookies from 'vue-cookies';

// 引入自定义js
import router from './assets/js/config/router';
import storage from './assets/js/config/storage';
import store from './assets/js/vuex/store';
import request from './assets/js/config/request';

// 引入vant库
import Vant, {Lazyload} from 'vant';
import 'vant/lib/index.css';

// 引入扩展属性
// eslint-disable-next-line no-unused-vars
import custom from './assets/js/config/custom';

// 定义全局变量
Vue.prototype.storage = storage;
Vue.prototype.request = request;
Vue.config.productionTip = false;

Vue.use(Vant);
Vue.use(Lazyload);
Vue.use(VueCookies);

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount('#app');
