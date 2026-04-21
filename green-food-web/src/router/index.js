import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/product' // 默认打开商品列表
    },
    {
        path: '/product',
        name: 'ProductList',
        component: () => import('../views/ProductList.vue') // 懒加载我们刚才写的页面
    }
]

const router = new VueRouter({
    mode: 'history', // 去掉 URL 上的 # 号
    routes
})

export default router