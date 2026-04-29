module.exports = {
  lintOnSave: false,
  devServer: {
    port: 8080, // 前端启动端口
    // 配置跨域代理
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // 假设你的 Java 后端跑在 8081 端口
        changeOrigin: true, // 允许跨域
        //pathRewrite: { '^/api': '' } // 如果后端接口没有 /api 前缀，把这行注释打开
      }
    }
  }
}
