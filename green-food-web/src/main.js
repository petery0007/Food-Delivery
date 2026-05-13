import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// ✅ 在这里添加全局过滤器（必须在Vue.use之前）
Vue.prototype.myFilters = function (msg) {
  if (msg == null) return "";
  return String(msg).replace(/\n/g, "<br>");
};

Vue.use(ElementUI)
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')

// 全局使用 Element-UI
Vue.use(ElementUI);

Vue.config.productionTip = false

new Vue({
  router, // 挂载路由
  render: h => h(App),
}).$mount('#app')