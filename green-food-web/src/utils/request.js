import axios from 'axios'
import { Message } from 'element-ui'

// 创建一个 axios 实例
const request = axios.create({
    baseURL: '/api', // 所有的请求都会自动加上 /api 前缀，然后被 vue.config.js 代理转发给后端
    timeout: 5000    // 请求超时时间 5 秒
})

// 请求拦截器：可以在这里统一给请求头上加上 Token
request.interceptors.request.use(
    config => {
        // 例如：config.headers['Authorization'] = localStorage.getItem('token');
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器：统一处理后端返回的报错
request.interceptors.response.use(
    response => {
        const res = response.data
        // 假设后端成功时返回的 code 是 200 (或者 0)
        if (res.code !== 200 && res.code !== 0) {
            Message.error(res.message || '系统错误')
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res // 直接返回里面的真实数据
        }
    },
    error => {
        Message.error('网络连接失败，请检查后端是否启动')
        return Promise.reject(error)
    }
)

export default request