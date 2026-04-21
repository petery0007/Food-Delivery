import Vue from 'vue'
import App from './App.vue'
import router from './router' // 引入我们稍后要写的路由配置

// 引入 Element-UI 及其样式
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 全局使用 Element-UI
Vue.use(ElementUI);

Vue.config.productionTip = false

new Vue({
  router, // 挂载路由
  render: h => h(App),
}).$mount('#app')