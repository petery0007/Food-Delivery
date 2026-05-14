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
          <el-button type="success" icon="el-icon-plus" @click="openAddDialog">新增配送员</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 配送员数据表格 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="username" label="配送员姓名" align="center"></el-table-column>
        <el-table-column prop="phone" label="联系电话" align="center"></el-table-column>
        <el-table-column prop="role" label="角色" align="center">
          <template slot-scope="scope">
            <el-tag type="warning">{{ scope.row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="money" label="账户余额" align="center">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.money }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="viewDriverDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="danger" @click="deleteDriver(scope.row.id)">删除</el-button>
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

    <!-- 4. 新增配送员弹窗 -->
    <el-dialog title="新增配送员" :visible.sync="addDialogVisible" width="500px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="100px">
        <el-form-item label="配送员姓名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入配送员姓名"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入手机号" maxlength="11"></el-input>
        </el-form-item>

        <el-form-item label="初始密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请设置初始密码（至少6位）" show-password></el-input>
        </el-form-item>

        <el-form-item label="账户余额" prop="money">
          <el-input-number v-model="addForm.money" :min="0" :precision="2" :step="10" style="width: 100%;"></el-input-number>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="success" @click="submitAddDriver" :loading="submitLoading">确 认 添 加</el-button>
      </div>
    </el-dialog>

    <!-- 5. 配送员详情弹窗 -->
    <el-dialog title="配送员详情" :visible.sync="detailDialogVisible" width="500px">
      <el-descriptions :column="1" border v-if="currentDriver">
        <el-descriptions-item label="配送员ID">{{ currentDriver.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentDriver.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentDriver.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag type="warning">{{ currentDriver.role }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户余额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ currentDriver.money }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'DriverManagement',
  data() {
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }

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
      loading: false,

      // 新增弹窗
      addDialogVisible: false,
      submitLoading: false,
      addForm: {
        username: '',
        phone: '',
        password: '',
        money: 0,
        role: 'PEISONG'
      },
      addRules: {
        username: [
          { required: true, message: '请输入配送员姓名', trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请设置初始密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ]
      },

      // 详情弹窗
      detailDialogVisible: false,
      currentDriver: null
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

    // 重置搜索
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
      this.loadDriverList(1, this.pageSize)
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
    },

    // 打开新增弹窗
    openAddDialog() {
      this.addDialogVisible = true
    },

    // 提交新增配送员
    submitAddDriver() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          this.submitLoading = true

          // 调用注册接口创建配送员账号
          request.post('/auth/register', this.addForm).then(res => {
            this.submitLoading = false
            this.addDialogVisible = false
            this.$message.success('配送员添加成功！')

            // 刷新列表
            this.currentPage = 1
            this.loadDriverList(1, this.pageSize)
          }).catch(err => {
            this.submitLoading = false
          })
        } else {
          this.$message.warning('请检查必填项是否全部填写正确')
          return false
        }
      })
    },

    // 重置新增表单
    resetAddForm() {
      if (this.$refs.addForm) {
        this.$refs.addForm.resetFields()
      }
      this.addForm.money = 0
      this.addForm.role = 'PEISONG'
    },

    // 查看配送员详情
    viewDriverDetail(driver) {
      this.currentDriver = { ...driver }
      this.detailDialogVisible = true
    },

    // 删除配送员
    deleteDriver(driverId) {
      this.$confirm('确定要删除该配送员吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 注意：后端目前没有提供删除配送员的接口，这里需要根据实际情况调整
        // 如果后端添加了删除接口，可以这样调用：
        // request.delete(`/admin/peisong/${driverId}`).then(res => {
        //   this.$message.success('删除成功')
        //   this.loadDriverList()
        // })

        this.$message.info('删除功能待后端接口支持')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.driver-container {
  min-height: calc(100vh - 100px);
}
</style>