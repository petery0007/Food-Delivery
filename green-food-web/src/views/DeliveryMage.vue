<template>
  <div class="driver-container" style="padding: 20px;">
    <!-- 1. 顶部搜索区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="配送员姓名">
          <el-input v-model="searchForm.username" placeholder="请输入配送员姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 配送员数据表格 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="username" label="配送员姓名" align="center"></el-table-column>
        <el-table-column prop="phone" label="联系电话" align="center"></el-table-column>
        <el-table-column prop="status" label="配送状态" align="center" width="150">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '空闲' ? 'success' : 'warning'">
              {{ scope.row.status || '未设置' }}
            </el-tag>
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
  name: 'DriverManagement',
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
    this.loadDriverList()
  },
  methods: {
    // 加载配送员列表
    async loadDriverList(page, pageSize, username, phone) {
      this.loading = true
      try {
        let targetUrl = ''
        let params = {}

        if (username || phone) {
          // 有搜索条件，使用筛选接口
          targetUrl = '/admin/peisong/list'
          params = {
            page: page || this.currentPage,
            pageSize: pageSize || this.pageSize,
            username: username || '',
            phone: phone || ''
          }
        } else {
          // 无搜索条件，使用普通分页接口
          targetUrl = '/admin/peisong'
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
        console.error('配送员列表加载失败：', error)
        if (error.response && error.response.status === 401) {
          this.$message.warning('登录已过期，请重新登录')
          localStorage.clear()
          this.$router.push('/login')
        } else {
          this.$message.error('配送员列表加载失败，请稍后重试')
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
      this.loadDriverList(
          this.currentPage,
          this.pageSize,
          this.queryParams.username,
          this.queryParams.phone
      )
    },

    // 分页切换
    onPageChange(newPage) {
      this.currentPage = newPage
      this.loadDriverList(
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
.driver-container {
  min-height: calc(100vh - 100px);
}
</style>
