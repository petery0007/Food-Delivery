<template>
  <div class="login-container">
    <!-- 左侧装饰区域 -->
    <div class="login-left">
      <div class="content-wrapper">
        <div class="illustration">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
          <i class="el-icon-shopping-cart-2 main-icon"></i>
        </div>
        <div class="welcome-text">
          <h1 class="title">绿色食品配送系统</h1>
          <p class="subtitle">Green Food Delivery System</p>
          <div class="features">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>新鲜食材</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>快速配送</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>品质保证</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>{{ activeTab === 'login' ? '欢迎登录' : '注册账号' }}</h2>
          <p>{{ activeTab === 'login' ? '请输入您的账号信息' : '创建新账号开始使用' }}</p>
        </div>

        <el-card class="login-box" shadow="never">
          <el-tabs v-model="activeTab" stretch>
            <el-tab-pane label="系统登录" name="login"></el-tab-pane>
            <el-tab-pane label="账号注册" name="register"></el-tab-pane>
          </el-tabs>

          <!-- ================= 登录表单 ================= -->
          <el-form v-if="activeTab === 'login'" :model="loginForm" :rules="rules" ref="loginForm" label-width="0px" class="custom-form">
            <el-form-item prop="username">
              <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="el-icon-user"
                  size="large"
                  class="custom-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  type="password"
                  v-model="loginForm.password"
                  placeholder="请输入密码"
                  prefix-icon="el-icon-lock"
                  size="large"
                  show-password
                  class="custom-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="role">
              <el-radio-group v-model="loginForm.role" class="custom-radio">
                <el-radio-button label="admin">管理员</el-radio-button>
                <el-radio-button label="user">用户</el-radio-button>
                <el-radio-button label="PEISONG">配送员</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  size="large"
                  style="width: 100%;"
                  @click="handleLogin"
                  :loading="loading"
                  class="custom-button"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <!-- ================= 注册表单 ================= -->
          <el-form v-if="activeTab === 'register'" :model="registerForm" :rules="rules" ref="registerForm" label-width="0px" class="custom-form">
            <el-form-item prop="username">
              <el-input
                  v-model="registerForm.username"
                  placeholder="设置用户名"
                  prefix-icon="el-icon-user"
                  size="large"
                  class="custom-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  prefix-icon="el-icon-mobile-phone"
                  size="large"
                  class="custom-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  type="password"
                  v-model="registerForm.password"
                  placeholder="设置密码"
                  prefix-icon="el-icon-lock"
                  size="large"
                  show-password
                  class="custom-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="role">
              <el-radio-group v-model="registerForm.role" class="custom-radio">
                <el-radio-button label="user">用户</el-radio-button>
                <el-radio-button label="PEISONG">配送员</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button
                  type="success"
                  size="large"
                  style="width: 100%;"
                  @click="handleRegister"
                  :loading="loading"
                  class="custom-button"
              >
                {{ loading ? '注册中...' : '注 册' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <div class="form-footer">
          <p>© 2026 绿色食品配送系统. All rights reserved.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 引入真实的网络请求
import request from '@/utils/request'

export default {
  data() {
    return {
      activeTab: 'login',
      loading: false,
      loginForm: {
        username: '',
        password: '',
        role: 'user' // 默认用户
      },
      registerForm: {
        username: '',
        phone: '', // 新增
        password: '',
        role: 'user'
      },
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号码', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          // 【核心修改】调用真实的后端接口
          request.post('/auth/login', this.loginForm).then(res => {
            this.loading = false;
            this.$message.success(res.msg);

            // 解析后端返回的数据
            const token = res.data.token;
            const userInfo = res.data.userInfo;

            // 存储在本地 (后续请求头会自动带上 token)
            localStorage.setItem('token', token);
            localStorage.setItem('userId', userInfo.id);
            localStorage.setItem('userRole', userInfo.role);
            localStorage.setItem('userName', userInfo.username);

            this.$router.push('/layout');
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          // 【核心修改】调用真实的后端接口
          request.post('/auth/register', this.registerForm).then(res => {
            this.loading = false;
            this.$message.success(res.msg || '注册成功，请登录！');
            this.activeTab = 'login';
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
};
</script>

<style scoped>.login-container {
  height: 100vh;
  display: flex;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* ============ 左侧装饰区域 ============ */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: moveGrid 20s linear infinite;
}

@keyframes moveGrid {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

.content-wrapper {
  text-align: center;
  color: white;
  z-index: 1;
  padding: 40px;
}

/* 装饰性图标区域 */
.illustration {
  position: relative;
  width: 300px;
  height: 300px;
  margin: 0 auto 40px;
}

.main-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 120px;
  color: white;
  z-index: 3;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0); }
  50% { transform: translate(-50%, -50%) translateY(-20px); }
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 4s ease-in-out infinite;
}

.circle-2 {
  width: 250px;
  height: 250px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid rgba(255, 255, 255, 0.2);
  animation: rotate 20s linear infinite;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.15);
  animation: pulse 3s ease-in-out infinite reverse;
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.8; }
}

@keyframes rotate {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

/* 欢迎文字 */
.welcome-text .title {
  font-size: 42px;
  font-weight: 700;
  margin: 0 0 15px 0;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
}

.welcome-text .subtitle {
  font-size: 18px;
  margin: 0 0 40px 0;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 1px;
}

.features {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 25px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.feature-item i {
  font-size: 18px;
  color: #4ade80;
}

/* ============ 右侧表单区域 ============ */
.login-right {
  width: 550px;
  background: #f8f9fa;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  box-shadow: -5px 0 20px rgba(0, 0, 0, 0.05);
}

.form-wrapper {
  width: 100%;
  max-width: 420px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 32px;
  color: #1a1a1a;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.form-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-box {
  border-radius: 16px;
  padding: 30px;
  background: white;
  border: 1px solid #e5e7eb;
}

/* 自定义表单样式 */
.custom-form {
  margin-top: 20px;
}

.custom-input {
  margin-bottom: 20px;
}

.custom-input >>> .el-input__inner {
  height: 48px;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  font-size: 15px;
  transition: all 0.3s ease;
}

.custom-input >>> .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.custom-input >>> .el-input__prefix {
  font-size: 18px;
  color: #9ca3af;
}

/* 自定义单选按钮 */
.custom-radio {
  width: 100%;
  display: flex;
  margin-bottom: 20px;
}

.custom-radio >>> .el-radio-button {
  flex: 1;
}

.custom-radio >>> .el-radio-button__inner {
  width: 100%;
  height: 44px;
  line-height: 40px;
  border-radius: 10px !important;
  border: 2px solid #e5e7eb;
  background: white;
  color: #6b7280;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 0;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.custom-radio >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 自定义按钮 */
.custom-button {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.custom-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.custom-button:active {
  transform: translateY(0);
}

/* 表单底部 */
.form-footer {
  text-align: center;
  margin-top: 30px;
  color: #9ca3af;
  font-size: 13px;
}

/* ============ 响应式设计 ============ */
@media (max-width: 1024px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .form-wrapper {
    background: white;
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  }
}

@media (max-width: 480px) {
  .login-right {
    padding: 20px;
  }

  .form-wrapper {
    padding: 30px 20px;
  }

  .form-header h2 {
    font-size: 26px;
  }

  .login-box {
    padding: 20px;
  }
}

/* ============ Tab 样式优化 ============ */
>>> .el-tabs__header {
  margin-bottom: 25px;
}

>>> .el-tabs__nav-wrap::after {
  height: 2px;
  background: #e5e7eb;
}

>>> .el-tabs__active-bar {
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

>>> .el-tabs__item {
  font-size: 16px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.3s ease;
}

>>> .el-tabs__item.is-active {
  color: #667eea;
}

>>> .el-tabs__item:hover {
  color: #667eea;
}
</style>