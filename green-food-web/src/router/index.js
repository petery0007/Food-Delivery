import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/login' // 一开始默认跳到登录页
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue') // 刚刚写的登录页
    },
    {
        path: '/layout',
        name: 'Layout',
        component: () => import('../views/Layout.vue'), // 刚刚写的框架页
        redirect: '/layout/profile',// 登录后默认显示的内部页面 (可选)
        children: [
            // 这里面放侧边栏点击后切换的页面
            {
                path: 'products', // 对应菜单的 index="/layout/products"
                name: 'ProductList',
                component: () => import('../views/ProductList.vue') // 你上一步写的商品列表！
            },
            // 新增个人中心
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/Profile.vue')
            }
        ]
    }
]

const router = new VueRouter({
    mode: 'history',
    routes
})

export default router