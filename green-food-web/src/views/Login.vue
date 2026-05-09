<template>
  <div class="login-container">
    <el-card class="login-box">
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="系统登录" name="login"></el-tab-pane>
        <el-tab-pane label="账号注册" name="register"></el-tab-pane>
      </el-tabs>

      <!-- ================= 登录表单 ================= -->
      <el-form v-if="activeTab === 'login'" :model="loginForm" :rules="rules" ref="loginForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="loginForm.role">
            <!-- 【修改点】统一改为后端文档的大写格式 -->
            <el-radio label="admin">管理员</el-radio>
            <el-radio label="user">用户</el-radio>
            <el-radio label="PEISONG">配送人员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%;" @click="handleLogin" :loading="loading">登 录</el-button>
        </el-form-item>
      </el-form>

      <!-- ================= 注册表单 ================= -->
      <el-form v-if="activeTab === 'register'" :model="registerForm" :rules="rules" ref="registerForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="设置账号"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <!-- 【修改点】根据接口文档新增手机号字段 -->
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="设置密码"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="user">用户</el-radio>
            <el-radio label="PEISONG">配送人员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="success" style="width: 100%;" @click="handleRegister" :loading="loading">注 册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
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

<style scoped>
/* 保持原有样式不变 */
.login-container { height: 100vh; display: flex; justify-content: center; align-items: center; background-color: #2b4b6b; }
.login-box { width: 450px; padding: 20px; border-radius: 10px; }
</style>