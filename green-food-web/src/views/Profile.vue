<template>
  <div style="padding: 20px;">
    <!-- 个人信息卡片 -->
    <el-card shadow="never" style="max-width: 800px; margin: 0 auto;">
      <div slot="header" class="clearfix" style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 18px; font-weight: bold;">个人中心</span>
        <div>
          <el-button type="primary" size="small" icon="el-icon-edit" @click="openEditDialog">修改资料</el-button>
          <el-button type="warning" size="small" icon="el-icon-lock" @click="openPwdDialog">修改密码</el-button>
        </div>
      </div>

      <div v-loading="loading">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="用户ID">
            <el-tag size="small" type="info">{{ userInfo.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账号名称 (登录名)">
            <b>{{ userInfo.username }}</b>
          </el-descriptions-item>
          <el-descriptions-item label="联系手机">
            {{ userInfo.phone || '尚未绑定手机号' }}
          </el-descriptions-item>
          <el-descriptions-item label="当前角色">
            <el-tag size="small" :type="userInfo.role === 'user' ? '' : 'success'">
              {{ getRoleName(userInfo.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账户余额" v-if="userInfo.role === 'user'">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">￥{{ userInfo.money || '0.00' }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 弹窗 1：修改基本资料 -->
    <el-dialog title="修改个人资料" :visible.sync="editDialogVisible" width="400px" @close="resetEditForm">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="80px">
        <el-form-item label="账号名称" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入新的登录账号"></el-input>
          <div style="font-size: 12px; color: #999;">修改后下次登录需使用新账号</div>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitLoading">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 弹窗 2：修改密码 -->
    <el-dialog title="修改密码" :visible.sync="pwdDialogVisible" width="400px" @close="resetPwdForm">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="pwdForm.oldPassword" placeholder="请输入原密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="pwdForm.newPassword" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="pwdForm.confirmPassword" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="warning" @click="submitPwd" :loading="submitLoading">确认修改</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    // 自定义验证规则：确认密码必须和新密码一致
    const validateConfirmPwd = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    return {
      loading: false,
      submitLoading: false,
      userInfo: {}, // 页面显示的数据

      // 修改资料弹窗相关
      editDialogVisible: false,
      editForm: {
        username: '',
        phone: ''
      },
      editRules: {
        username: [
          { required: true, message: '账号名称不能为空', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '手机号不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号码', trigger: 'blur' }
        ]
      },

      // 修改密码弹窗相关
      pwdDialogVisible: false,
      pwdForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        confirmPassword: [{ required: true, validator: validateConfirmPwd, trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    // 1. 获取最新用户信息
    getUserInfo() {
      this.loading = true;
      request.get('/user/info').then(res => {
        this.userInfo = res.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },

    // 2. 翻译角色名
    getRoleName(role) {
      const map = { 'admin': '超级管理员', 'user': '普通消费者', 'PEISONG': '系统配送员' };
      return map[role] || '未知角色';
    },

    // 3. 打开修改资料弹窗（回显数据）
    openEditDialog() {
      this.editForm = {
        username: this.userInfo.username,
        phone: this.userInfo.phone
      };
      this.editDialogVisible = true;
    },

    // 4. 提交资料修改
    submitEdit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          // 假设后端更新信息的接口是 PUT /user/info
          request.put('/user/info', this.editForm).then(res => {
            this.submitLoading = false;
            this.$message.success('资料修改成功！');
            this.editDialogVisible = false;

            // 更新本地缓存的名字 (顶部导航栏显示用)
            localStorage.setItem('userName', this.editForm.username);
            // 刷新页面数据
            this.getUserInfo();
            // 提示外层 Layout 组件更新用户名显示（如果在实际项目中，可使用 Vuex 或事件总线，这里简单刷新即可）
            window.location.reload();
          }).catch(() => {
            this.submitLoading = false;
          });
        }
      });
    },

    // 5. 打开修改密码弹窗
    openPwdDialog() {
      this.pwdDialogVisible = true;
    },

    // 6. 提交密码修改
    submitPwd() {
      this.$refs.pwdForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          // 假设后端修改密码的接口是 PUT /user/password
          request.put('/user/password', {
            oldPassword: this.pwdForm.oldPassword,
            newPassword: this.pwdForm.newPassword
          }).then(res => {
            this.submitLoading = false;
            this.pwdDialogVisible = false;

            // 密码修改成功，必须强制重新登录
            this.$alert('密码修改成功，请使用新密码重新登录', '提示', {
              confirmButtonText: '确定',
              type: 'warning',
              callback: action => {
                localStorage.clear();
                this.$router.push('/login');
              }
            });
          }).catch(() => {
            this.submitLoading = false;
          });
        }
      });
    },

    // 清空表单验证状态，防止下次打开时残留红字报错
    resetEditForm() {
      if (this.$refs.editForm) this.$refs.editForm.resetFields();
    },
    resetPwdForm() {
      if (this.$refs.pwdForm) this.$refs.pwdForm.resetFields();
    }
  }
}
</script>