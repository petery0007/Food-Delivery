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
            // 新增商品详情页(管理端）
             {
                 path: 'product-info/:id',
                 name: 'ProductInfo',
                 component: () => import('../views/ProductInfo.vue'),
                 props: true
             },
            // 新增个人中心
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/Profile.vue')
            },
            // 新增用户端浏览商品界面
            {
                path: 'shop',
                name: 'Shop',
                component: () => import('../views/ProductBrow.vue')
            },
            {
                path: 'delivery-staff',
                name: 'DriverMage',
                component: () => import('../views/DriverMage.vue')
            },
            // 用户信息管理（管理员）
            {
                path: 'users',
                name: 'UserManagement',
                component: () => import('../views/UserManagement.vue')
            },
            // 新增品详情界面（用户端）
            {
                path: 'product-info-user/:id',
                name: 'ProductInfoUser',
                component: () => import('../views/ProductInfo.vue'),
                props: true
            },
            // 新增用户端购物车界面
            {
                path: 'cart',
                name: 'ShoppingCart',
                component: () => import('../views/ShoppingCart.vue')
            },
            // 新增用户端订单界面
            {
                path: 'order-confirm',
                name: 'OrderConfirm',
                component: () => import('../views/Order.vue'),
                props: true
            },
            // 新增用户端我的订单管理
            {
                path: 'my-orders',
                name: 'MyOrders',
                component: () => import('../views/MyOrders.vue')
            },
            // 新增配送员端配送订单界面
            {
                path: 'delivery-tasks',
                name: 'DeliveryTasks',
                component: () => import('../views/DriverOrder.vue')
            },
        ]
    }
]

const router = new VueRouter({
    mode: 'history',
    routes
})

export default router