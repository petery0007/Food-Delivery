<template>
  <div class="user-container" style="padding: 20px;">
    <!-- 1. 顶部搜索区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.username" placeholder="请输入用户姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
          <el-button icon="el-icon-refresh" @click="onResetButtonClick">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 用户数据表格 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="username" label="用户姓名" align="center"></el-table-column>
        <el-table-column prop="phone" label="联系电话" align="center"></el-table-column>
        <el-table-column prop="money" label="账户余额" align="center" width="150">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.money || 0 }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 3. 分页控件 -->
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
            @current-change="onPageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next, jumper"
            :total="totalCount">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'UserManagement',
  data() {
    return {
      // 搜索表单
      searchForm: {
        username: '',
        phone: ''
      },
      queryParams: {
        username: '',
        phone: ''
      },

      // 分页参数
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,

      // 表格数据
      tableData: [],
      loading: false
    }
  },
  mounted() {
    this.loadUserList()
  },
  methods: {
    // 加载用户列表
    async loadUserList(page, pageSize, username, phone) {
      this.loading = true
      try {
        let targetUrl = ''
        let params = {}

        if (username || phone) {
          // 有搜索条件，使用筛选接口
          targetUrl = '/admin/user/list'
          params = {
            page: page || this.currentPage,
            pageSize: pageSize || this.pageSize,
            username: username || '',
            phone: phone || ''
          }
        } else {
          // 无搜索条件，使用普通分页接口
          targetUrl = '/admin/user'
          params = {
            page: page || this.currentPage,
            pageSize: pageSize || this.pageSize
          }
        }

        const res = await request.get(targetUrl, { params })

        if (res.data && res.data.list) {
          this.tableData = res.data.list
          this.totalCount = res.data.total || 0
        } else {
          this.tableData = []
          this.totalCount = 0
        }
      } catch (error) {
        console.error('用户列表加载失败：', error)
        if (error.response && error.response.status === 401) {
          this.$message.warning('登录已过期，请重新登录')
          localStorage.clear()
          this.$router.push('/login')
        } else {
          this.$message.error('用户列表加载失败，请稍后重试')
        }
        this.tableData = []
        this.totalCount = 0
      } finally {
        this.loading = false
      }
    },

    // 搜索按钮
    onSearchButtonClick() {
      this.queryParams = {
        username: this.searchForm.username,
        phone: this.searchForm.phone
      }
      this.currentPage = 1
      this.loadUserList(
          this.currentPage,
          this.pageSize,
          this.queryParams.username,
          this.queryParams.phone
      )
    },

    // 重置按钮
    onResetButtonClick() {
      this.searchForm = {
        username: '',
        phone: ''
      }
      this.queryParams = {
        username: '',
        phone: ''
      }
      this.currentPage = 1
      this.loadUserList(1, this.pageSize)
    },

    // 分页切换
    onPageChange(newPage) {
      this.currentPage = newPage
      this.loadUserList(
          newPage,
          this.pageSize,
          this.queryParams.username,
          this.queryParams.phone
      )
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
.user-container {
  min-height: calc(100vh - 100px);
}
</style>
