<template>
  <div class="order-admin-container" style="padding: 20px;">
    <!-- 1. 顶部搜索和状态切换区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.receiver" placeholder="请输入用户姓名"></el-input>
        </el-form-item>
        <el-form-item label="配送员姓名">
          <el-input v-model="searchForm.deliveryStaff" placeholder="请输入配送员姓名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
          <el-button icon="el-icon-refresh" @click="onResetButtonClick">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 状态分类标签 -->
      <div style="margin-top: 20px;">
        <el-tabs v-model="activeStatus" @tab-click="handleTabClick">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="待支付" name="0"></el-tab-pane>
          <el-tab-pane label="配送中" name="1"></el-tab-pane>
          <el-tab-pane label="待收货" name="2"></el-tab-pane>
          <el-tab-pane label="已完成" name="3"></el-tab-pane>
          <el-tab-pane label="已取消" name="4"></el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <!-- 2. 订单数据表格 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="id" label="订单编号" align="center" width="160"></el-table-column>
        <el-table-column label="收货人" align="center" width="100">
          <template slot-scope="scope">
            {{ scope.row.receiver || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" align="center" width="130">
          <template slot-scope="scope">
            {{ scope.row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="收货地址" align="center" min-width="200">
          <template slot-scope="scope">
            {{ scope.row.address || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="配送员" align="center" width="100">
          <template slot-scope="scope">
            {{ scope.row.deliveryStaff || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="支付方式" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.paymentType === 'balance' ? 'success' : 'warning'" size="small">
              {{ scope.row.paymentType === 'balance' ? '余额支付' : '货到付款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品总额" align="center" width="100">
          <template slot-scope="scope">
            <span>¥{{ (scope.row.goodsTotal || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="运费" align="center" width="80">
          <template slot-scope="scope">
            <span>{{ (scope.row.deliveryFee || 0) === 0 ? '免运费' : '¥' + (scope.row.deliveryFee || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单总额" align="center" width="110">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ (scope.row.totalAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" align="center" width="160">
          <template slot-scope="scope">
            {{ scope.row.createTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="完成时间" align="center" width="160">
          <template slot-scope="scope">
            {{ scope.row.finishTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="订单备注" align="center" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="下单物品" align="center" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showOrderDetail(scope.row)">
              查看详情
            </el-button>
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

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailVisible" width="60%">
      <el-card shadow="never" v-if="currentOrder">
        <el-descriptions title="订单信息" :column="2" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiver }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="配送员">{{ currentOrder.deliveryStaff || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.paymentType === 'balance' ? '余额支付' : '货到付款' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ currentOrder.finishTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider></el-divider>

        <div style="margin-top: 15px;">
          <div style="font-weight: bold; margin-bottom: 10px;">下单物品：</div>
          <el-table :data="currentOrder.items || []" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
            <el-table-column label="商品名称" prop="productName" align="center"></el-table-column>
            <el-table-column label="规格" prop="specification" align="center" width="120"></el-table-column>
            <el-table-column label="单价" align="center" width="100">
              <template slot-scope="scope">
                ¥{{ (scope.row.price || 0).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="数量" align="center" width="80">
              <template slot-scope="scope">
                {{ scope.row.quantity }}
              </template>
            </el-table-column>
            <el-table-column label="小计" align="center" width="100">
              <template slot-scope="scope">
                ¥{{ ((scope.row.price || 0) * (scope.row.quantity || 0)).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-divider></el-divider>

        <div style="text-align: right; font-size: 16px; margin-top: 10px;">
          <span>商品总额：<span style="color: #606266;">¥{{ (currentOrder.goodsTotal || 0).toFixed(2) }}</span></span>
          <span style="margin-left: 20px;">运费：<span style="color: #606266;">{{ (currentOrder.deliveryFee || 0) === 0 ? '免运费' : '¥' + (currentOrder.deliveryFee || 0).toFixed(2) }}</span></span>
          <span style="margin-left: 20px; color: #f56c6c; font-weight: bold; font-size: 18px;">订单总额：¥{{ (currentOrder.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </el-card>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'OrderAdmin',
  data() {
    return {
      // 搜索表单
      searchForm: {
        receiver: '',
        deliveryStaff: ''
      },
      queryParams: {
        receiver: '',
        deliveryStaff: ''
      },

      // 当前激活的状态标签
      activeStatus: 'all',

      // 分页参数
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,

      // 表格数据
      tableData: [],
      loading: false,

      // 订单详情
      detailVisible: false,
      currentOrder: null,

      // 模拟数据开关（后端未实现时使用）
      useMockData: false
    }
  },
  mounted() {
    this.loadOrderList()
  },
  methods: {
    // 加载订单列表
    async loadOrderList(page, pageSize, status, receiver, deliveryStaff) {
      this.loading = true
      try {
        if (this.useMockData) {
          // 使用模拟数据
          this.loadMockOrderData(page, pageSize, status, receiver, deliveryStaff)
          return
        }

        // 调用真实接口
        const params = {
          page: page || this.currentPage,
          pageSize: pageSize || this.pageSize
        }
        
        if (status && status !== 'all') {
          params.status = status
        }
        if (receiver) {
          params.receiver = receiver
        }
        if (deliveryStaff) {
          params.deliveryStaff = deliveryStaff
        }

        const res = await request.get('/admin/orders', { params })

        if (res.data && res.data.list) {
          this.tableData = res.data.list
          this.totalCount = res.data.total || 0
        } else {
          this.tableData = []
          this.totalCount = 0
        }
      } catch (error) {
        console.error('订单列表加载失败：', error)
        if (error.response && error.response.status === 401) {
          this.$message.warning('登录已过期，请重新登录')
          localStorage.clear()
          this.$router.push('/login')
        } else {
          this.$message.error('订单列表加载失败，使用模拟数据')
        }
        // 加载模拟数据
        this.loadMockOrderData(page, pageSize, status, receiver, deliveryStaff)
      } finally {
        this.loading = false
      }
    },

    // 加载模拟订单数据
    loadMockOrderData(page = 1, pageSize = 10, status = 'all', receiver = '', deliveryStaff = '') {
      // 生成模拟订单数据
      const mockOrders = this.generateMockOrders()
      
      // 按状态筛选
      let filteredOrders = mockOrders
      if (status && status !== 'all') {
        filteredOrders = mockOrders.filter(order => order.status === parseInt(status))
      }
      
      // 按收货人筛选
      if (receiver) {
        filteredOrders = filteredOrders.filter(order => 
          order.receiver && order.receiver.includes(receiver)
        )
      }
      
      // 按配送员筛选
      if (deliveryStaff) {
        filteredOrders = filteredOrders.filter(order => 
          order.deliveryStaff && order.deliveryStaff.includes(deliveryStaff)
        )
      }

      this.totalCount = filteredOrders.length
      
      // 分页
      const start = (page - 1) * pageSize
      const end = start + pageSize
      this.tableData = filteredOrders.slice(start, end)
      
      this.$message.success(`已加载 ${this.totalCount} 条订单数据（模拟）`)
    },

    // 生成模拟订单数据
    generateMockOrders() {
      const mockOrders = [
        {
          orderId: 'ORD20260515001',
          userId: 1,
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryStaff: '李配送',
          paymentType: 'balance',
          remark: '请尽快配送',
          goodsTotal: 128.50,
          deliveryFee: 8.00,
          totalAmount: 136.50,
          createTime: '2026-05-15 09:30:00',
          finishTime: '',
          status: 0, // 待支付
          items: [
            { productName: '有机菠菜', specification: '500g', price: 12.50, quantity: 2 },
            { productName: '有机胡萝卜', specification: '500g', price: 8.80, quantity: 3 },
            { productName: '有机鸡蛋', specification: '10个装', price: 32.50, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515002',
          userId: 2,
          receiver: '李四',
          phone: '13800000002',
          address: '北京市海淀区中关村大街200号XX大厦12层',
          deliveryStaff: '王配送',
          paymentType: 'cod',
          remark: '',
          goodsTotal: 256.80,
          deliveryFee: 0,
          totalAmount: 256.80,
          createTime: '2026-05-15 10:15:00',
          finishTime: '',
          status: 1, // 配送中
          items: [
            { productName: '有机苹果', specification: '2kg', price: 45.60, quantity: 2 },
            { productName: '有机牛奶', specification: '1L', price: 18.90, quantity: 4 },
            { productName: '有机大米', specification: '5kg', price: 89.90, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515003',
          userId: 3,
          receiver: '王五',
          phone: '13800000003',
          address: '上海市浦东新区陆家嘴环路1000号XX花园8栋603',
          deliveryStaff: '赵配送',
          paymentType: 'balance',
          remark: '放在门口快递柜',
          goodsTotal: 89.50,
          deliveryFee: 8.00,
          totalAmount: 97.50,
          createTime: '2026-05-14 16:20:00',
          finishTime: '',
          status: 2, // 待收货
          items: [
            { productName: '有机西红柿', specification: '500g', price: 15.50, quantity: 2 },
            { productName: '有机黄瓜', specification: '500g', price: 9.80, quantity: 3 },
            { productName: '有机土豆', specification: '1kg', price: 12.80, quantity: 2 }
          ]
        },
        {
          orderId: 'ORD20260515004',
          userId: 4,
          receiver: '赵六',
          phone: '13800000004',
          address: '广州市天河区天河路300号XX广场B座15层',
          deliveryStaff: '张配送',
          paymentType: 'balance',
          remark: '',
          goodsTotal: 356.70,
          deliveryFee: 0,
          totalAmount: 356.70,
          createTime: '2026-05-13 14:30:00',
          finishTime: '2026-05-15 11:20:00',
          status: 3, // 已完成
          items: [
            { productName: '有机草莓', specification: '500g', price: 45.80, quantity: 2 },
            { productName: '有机蓝莓', specification: '250g', price: 38.90, quantity: 3 },
            { productName: '有机蜂蜜', specification: '500g', price: 68.50, quantity: 1 },
            { productName: '有机橄榄油', specification: '750ml', price: 89.90, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515005',
          userId: 5,
          receiver: '钱七',
          phone: '13800000005',
          address: '深圳市南山区科技园路500号XX科技大厦20层',
          deliveryStaff: '',
          paymentType: 'cod',
          remark: '订单取消',
          goodsTotal: 156.30,
          deliveryFee: 8.00,
          totalAmount: 164.30,
          createTime: '2026-05-14 09:45:00',
          finishTime: '2026-05-14 10:00:00',
          status: 4, // 已取消
          items: [
            { productName: '有机香蕉', specification: '1kg', price: 18.50, quantity: 2 },
            { productName: '有机橙子', specification: '1kg', price: 22.80, quantity: 2 },
            { productName: '有机葡萄', specification: '500g', price: 35.60, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515006',
          userId: 1,
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryStaff: '李配送',
          paymentType: 'balance',
          remark: '',
          goodsTotal: 78.90,
          deliveryFee: 8.00,
          totalAmount: 86.90,
          createTime: '2026-05-15 11:00:00',
          finishTime: '',
          status: 1, // 配送中
          items: [
            { productName: '有机白菜', specification: '1kg', price: 8.90, quantity: 2 },
            { productName: '有机萝卜', specification: '1kg', price: 9.50, quantity: 2 },
            { productName: '有机青椒', specification: '500g', price: 12.80, quantity: 2 }
          ]
        },
        {
          orderId: 'ORD20260515007',
          userId: 6,
          receiver: '孙八',
          phone: '13800000006',
          address: '杭州市西湖区文三路100号XX花园5栋301',
          deliveryStaff: '刘配送',
          paymentType: 'cod',
          remark: '周末配送',
          goodsTotal: 445.60,
          deliveryFee: 0,
          totalAmount: 445.60,
          createTime: '2026-05-12 15:20:00',
          finishTime: '2026-05-14 16:30:00',
          status: 3, // 已完成
          items: [
            { productName: '有机三文鱼', specification: '500g', price: 89.90, quantity: 2 },
            { productName: '有机虾仁', specification: '500g', price: 68.50, quantity: 2 },
            { productName: '有机牛肉', specification: '500g', price: 128.00, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515008',
          userId: 7,
          receiver: '周九',
          phone: '13800000007',
          address: '成都市武侯区天府大道200号XX大厦8层',
          deliveryStaff: '陈配送',
          paymentType: 'balance',
          remark: '',
          goodsTotal: 234.50,
          deliveryFee: 0,
          totalAmount: 234.50,
          createTime: '2026-05-15 08:30:00',
          finishTime: '',
          status: 0, // 待支付
          items: [
            { productName: '有机绿茶', specification: '250g', price: 58.90, quantity: 2 },
            { productName: '有机核桃', specification: '500g', price: 45.80, quantity: 1 },
            { productName: '有机红枣', specification: '500g', price: 38.50, quantity: 2 }
          ]
        },
        {
          orderId: 'ORD20260515009',
          userId: 8,
          receiver: '吴十',
          phone: '13800000008',
          address: '武汉市江汉区建设大道300号XX小区10栋802',
          deliveryStaff: '杨配送',
          paymentType: 'balance',
          remark: '请尽快配送，急用',
          goodsTotal: 167.80,
          deliveryFee: 8.00,
          totalAmount: 175.80,
          createTime: '2026-05-14 13:45:00',
          finishTime: '',
          status: 2, // 待收货
          items: [
            { productName: '有机大米', specification: '5kg', price: 89.90, quantity: 1 },
            { productName: '有机小米', specification: '1kg', price: 38.50, quantity: 1 },
            { productName: '有机面粉', specification: '2kg', price: 32.80, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515010',
          userId: 9,
          receiver: '郑十一',
          phone: '13800000009',
          address: '南京市鼓楼区中山路100号XX花园2栋1001',
          deliveryStaff: '',
          paymentType: 'cod',
          remark: '取消订单',
          goodsTotal: 298.60,
          deliveryFee: 0,
          totalAmount: 298.60,
          createTime: '2026-05-13 10:20:00',
          finishTime: '2026-05-13 11:00:00',
          status: 4, // 已取消
          items: [
            { productName: '有机鸡肉', specification: '1kg', price: 45.80, quantity: 2 },
            { productName: '有机鸭肉', specification: '1kg', price: 52.90, quantity: 1 },
            { productName: '有机猪肉', specification: '1kg', price: 48.50, quantity: 2 }
          ]
        },
        {
          orderId: 'ORD20260515011',
          userId: 10,
          receiver: '王小明',
          phone: '13800000010',
          address: '西安市雁塔区科技路500号XX科技园区A栋',
          deliveryStaff: '黄配送',
          paymentType: 'balance',
          remark: '',
          goodsTotal: 567.80,
          deliveryFee: 0,
          totalAmount: 567.80,
          createTime: '2026-05-11 09:00:00',
          finishTime: '2026-05-13 14:30:00',
          status: 3, // 已完成
          items: [
            { productName: '有机坚果礼盒', specification: '1盒', price: 128.00, quantity: 1 },
            { productName: '有机水果礼盒', specification: '1盒', price: 198.00, quantity: 1 },
            { productName: '有机茶叶礼盒', specification: '1盒', price: 238.00, quantity: 1 }
          ]
        },
        {
          orderId: 'ORD20260515012',
          userId: 2,
          receiver: '李四',
          phone: '13800000002',
          address: '北京市海淀区中关村大街200号XX大厦12层',
          deliveryStaff: '周配送',
          paymentType: 'balance',
          remark: '送到前台',
          goodsTotal: 189.50,
          deliveryFee: 0,
          totalAmount: 189.50,
          createTime: '2026-05-15 13:20:00',
          finishTime: '',
          status: 1, // 配送中
          items: [
            { productName: '有机酸奶', specification: '200g*6', price: 38.90, quantity: 2 },
            { productName: '有机奶酪', specification: '250g', price: 45.80, quantity: 1 },
            { productName: '有机黄油', specification: '200g', price: 32.50, quantity: 2 }
          ]
        }
      ]

      return mockOrders
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待支付',
        1: '配送中',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    },

    // 获取状态类型（用于标签颜色）
    getStatusType(status) {
      const typeMap = {
        0: 'info',      // 待支付 - 灰色
        1: 'warning',   // 配送中 - 橙色
        2: 'primary',   // 待收货 - 蓝色
        3: 'success',   // 已完成 - 绿色
        4: 'danger'     // 已取消 - 红色
      }
      return typeMap[status] || ''
    },

    // 显示订单详情
    showOrderDetail(order) {
      this.currentOrder = order
      this.detailVisible = true
    },

    // 状态标签切换
    handleTabClick(tab) {
      this.currentPage = 1
      this.loadOrderList(
        this.currentPage,
        this.pageSize,
        this.activeStatus,
        this.queryParams.receiver,
        this.queryParams.deliveryStaff
      )
    },

    // 搜索按钮
    onSearchButtonClick() {
      this.queryParams = {
        receiver: this.searchForm.receiver,
        deliveryStaff: this.searchForm.deliveryStaff
      }
      this.currentPage = 1
      this.loadOrderList(
        this.currentPage,
        this.pageSize,
        this.activeStatus,
        this.queryParams.receiver,
        this.queryParams.deliveryStaff
      )
    },

    // 重置按钮
    onResetButtonClick() {
      this.searchForm = {
        receiver: '',
        deliveryStaff: ''
      }
      this.queryParams = {
        receiver: '',
        deliveryStaff: ''
      }
      this.currentPage = 1
      this.activeStatus = 'all'
      this.loadOrderList(1, this.pageSize, 'all')
    },

    // 分页切换
    onPageChange(newPage) {
      this.currentPage = newPage
      this.loadOrderList(
        newPage,
        this.pageSize,
        this.activeStatus,
        this.queryParams.receiver,
        this.queryParams.deliveryStaff
      )
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
.order-admin-container {
  min-height: calc(100vh - 100px);
}
</style>