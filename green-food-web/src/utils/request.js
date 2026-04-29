import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router' // 引入路由用于401跳转

const request = axios.create({
    baseURL: '/api/v1', // 【修改点】对齐文档的基础路径
    timeout: 5000
})

// 请求拦截器：注入 JWT Token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            // 【修改点】对齐文档：Authorization: Bearer <token>
            config.headers['token'] = token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器：处理全局状态码
request.interceptors.response.use(
    response => {
        const res = response.data
        // 【修改点】根据文档附录处理状态码
        if (res.code === 200) {
            return res // 成功，直接返回整个res对象，方便取data或msg
        } else if (res.code === 401) {
            Message.warning('登录已过期，请重新登录')
            localStorage.clear()
            router.push('/login')
            return Promise.reject(new Error(res.msg || '未授权'))
        } else {
            Message.error(res.msg || '系统操作异常')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
    },
    error => {
        Message.error('网络连接失败，或服务器内部异常(500)')
        return Promise.reject(error)
    }
)

export default request