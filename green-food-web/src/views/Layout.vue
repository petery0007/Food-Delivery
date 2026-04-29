<template>
  <el-container style="height: 100vh;">
    <!-- 左侧侧边栏 -->
    <el-aside width="200px" style="background-color: #304156;">
      <div style="color: white; text-align: center; line-height: 60px; font-size: 18px; font-weight: bold;">
        绿色食品配送系统
      </div>

      <!-- 菜单 -->
      <el-menu
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router>

        <!-- 【权限控制核心】：使用 v-if 判断当前角色是否包含在该菜单的允许角色数组中 -->

        <!-- 1. 管理员专属菜单 -->
        <template v-if="currentRole === 'admin'">
          <el-menu-item index="/layout/profile"><i class="el-icon-s-custom"></i>个人中心</el-menu-item>
          <el-menu-item index="/layout/users"><i class="el-icon-user"></i>用户信息管理</el-menu-item>
          <el-menu-item index="/layout/products"><i class="el-icon-goods"></i>商品信息管理</el-menu-item>
          <el-menu-item index="/layout/orders-admin"><i class="el-icon-s-order"></i>订单信息管理</el-menu-item>
          <el-menu-item index="/layout/notices"><i class="el-icon-message-solid"></i>公告信息管理</el-menu-item>
          <el-menu-item index="/layout/delivery-staff"><i class="el-icon-truck"></i>配送人员管理</el-menu-item>
        </template>

        <!-- 2. 用户专属菜单 -->
        <template v-if="currentRole === 'user'">
          <el-menu-item index="/layout/profile"><i class="el-icon-s-custom"></i>个人中心</el-menu-item>
          <el-menu-item index="/layout/shop"><i class="el-icon-shopping-cart-2"></i>浏览商品信息</el-menu-item>
          <el-menu-item index="/layout/my-orders"><i class="el-icon-s-ticket"></i>我的订单管理</el-menu-item>
          <el-menu-item index="/layout/reviews"><i class="el-icon-chat-dot-round"></i>我的商品评价</el-menu-item>
        </template>

        <!-- 3. 配送员专属菜单 -->
        <template v-if="currentRole === 'PEISONG'">
          <el-menu-item index="/layout/profile"><i class="el-icon-s-custom"></i>个人中心</el-menu-item>
          <el-menu-item index="/layout/delivery-tasks"><i class="el-icon-map-location"></i>查看配送订单</el-menu-item>
          <!-- 更新配送状态通常做在查看订单详情里，这里不单设菜单 -->
        </template>

      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <el-header style="background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08); display: flex; justify-content: space-between; align-items: center;">
        <div>当前角色：<el-tag>{{ getRoleName() }}</el-tag></div>
        <div>
          欢迎您，{{ currentName }}
          <el-button type="text" style="color: red; margin-left: 15px;" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <!-- 子路由页面渲染出口（之前写的商品列表就在这里显示） -->
      <el-main style="background-color: #f0f2f5;">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      currentRole: '', // 当前登录的角色
      currentName: ''
    };
  },
  created() {
    // 页面创建时，从本地存储中取出登录时保存的信息
    this.currentRole = localStorage.getItem('userRole') || 'user';
    this.currentName = localStorage.getItem('userName') || '未登录';
  },
  methods: {
    // 翻译角色名称用于展示
    getRoleName() {
      const map = { 'admin': '超级管理员', 'user': '普通消费者', 'PEISONG': '系统配送员' };
      return map[this.currentRole];
    },
    logout() {
      // 退出登录：清空缓存并跳回登录页
      localStorage.clear();
      this.$router.push('/login');
      this.$message.success('已退出登录');
    }
  }
};
</script>