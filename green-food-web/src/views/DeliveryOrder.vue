<template>
  <div class="delivery-tasks-container" style="padding: 20px; max-width: 1400px; margin: 0 auto;">
    <!-- 页面标题 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133;">
        <i class="el-icon-map-location"></i> 配送订单
      </h2>
    </el-card>

    <!-- 订单状态筛选 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="配送中" name="delivering"></el-tab-pane>
        <el-tab-pane label="已送达" name="delivered"></el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 订单列表 -->
    <el-card shadow="never" v-loading="loading">
      <!-- 空订单提示 -->
      <el-empty
          description="暂无配送订单"
          v-if="orderList.length === 0 && !loading"
      >
      </el-empty>

      <!-- 订单列表 -->
      <div v-else>
        <div v-for="order in orderList" :key="order.id" class="order-item">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单编号：{{ order.id }}</span>
              <span class="order-time">下单时间：{{ order.createTime }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.status)" size="medium">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
          </div>

          <!-- 订单基本信息 -->
          <div class="order-basic-info">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="info-item">
                  <span class="label">收货人：</span>
                  <span class="value">{{ order.receiver }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">{{ order.phone }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item">
                  <span class="label">支付方式：</span>
                  <span class="value">{{ order.paymentType === 'balance' ? '余额支付' : '货到付款' }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 10px;">
              <el-col :span="24">
                <div class="info-item">
                  <span class="label">收货地址：</span>
                  <span class="value">{{ order.address }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 10px;" v-if="order.remark">
              <el-col :span="24">
                <div class="info-item">
                  <span class="label">订单备注：</span>
                  <span class="value remark">{{ order.remark }}</span>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 订单商品列表 -->
          <div class="order-products">
            <div class="products-title">商品信息</div>
            <div v-for="item in order.items" :key="item.id" class="product-row">
              <img :src="item.imageUrl" class="product-img" @error="handleImageError($event)">
              <div class="product-details">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-spec">{{ item.specification }}</div>
              </div>
              <div class="product-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="product-quantity">x{{ item.quantity }}</div>
              <div class="product-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
            </div>
          </div>

          <!-- 订单底部信息 -->
          <div class="order-footer">
            <div class="order-summary">
              <span>共 {{ getOrderItemCount(order) }} 件商品</span>
              <span class="divider">|</span>
              <span>运费：¥{{ order.deliveryFee.toFixed(2) }}</span>
              <span class="divider">|</span>
              <span class="total-amount">订单总额：<strong>¥{{ order.totalAmount.toFixed(2) }}</strong></span>
            </div>
            <div class="order-actions">
              <!-- 待配送状态 -->
              <template v-if="order.status === 0">
              </template>

              <!-- 配送中状态 -->
              <template v-if="order.status === 1">
                <el-button size="small" type="success" @click="handleDelivered(order)">确认送达</el-button>
              </template>

              <!-- 已送达状态 -->
              <template v-if="order.status === 2">
                <el-tag type="success">已完成配送</el-tag>
              </template>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[5, 10, 20, 50]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
          ></el-pagination>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'DeliveryTasks',
  data() {
    return {
      activeTab: 'all',
      orderList: [],
      loading: false,
      deliveryId: null,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    currentStatus() {
      if (this.activeTab === 'all') {
        return null
      }
      const statusMap = {
        'delivering': 1,
        'delivered': 2
      }
      return statusMap[this.activeTab]
    }
  },
  mounted() {
    this.deliveryId = localStorage.getItem('userId')
    if (!this.deliveryId) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      this.loading = true
      try {
        let url = `/delivery/order/list?deliveryId=${this.deliveryId}&pageNum=${this.currentPage}&pageSize=${this.pageSize}`

        if (this.currentStatus !== null) {
          url += `&status=${this.currentStatus}`
        }

        const res = await request.get(url)

        if (res.data && res.data.list) {
          this.orderList = res.data.list
          this.total = res.data.total || 0
          this.loading = false
        } else {
          this.orderList = []
          this.total = 0
          this.loading = false
        }
      } catch (error) {
        console.error('加载配送订单失败，使用模拟数据:', error)
        this.loadMockOrders()
        this.loading = false
      }
    },

    loadMockOrders() {
      console.log('使用模拟配送订单数据')
      const allOrders = [
        {
          id: 1,
          orderNo: 'ORD202605151200001234',
          userId: 1,
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryType: 'express',
          paymentType: 'balance',
          remark: '请尽快送达，谢谢！',
          goodsTotal: 61.00,
          deliveryFee: 0,
          totalAmount: 61.00,
          status: 0,
          createTime: '2026-05-15 12:00:00',
          finishTime: null,
          items: [
            {
              id: 1,
              orderId: 1,
              productId: 1,
              productName: '番茄',
              price: 3.50,
              quantity: 5,
              specification: '新鲜蔬菜',
              imageUrl: 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg',
              subtotal: 17.50
            },
            {
              id: 2,
              orderId: 1,
              productId: 7,
              productName: '苹果',
              price: 4.50,
              quantity: 3,
              specification: '新鲜水果',
              imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain',
              subtotal: 13.50
            },
            {
              id: 3,
              orderId: 1,
              productId: 9,
              productName: '草莓',
              price: 15.00,
              quantity: 2,
              specification: '新鲜水果',
              imageUrl: 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg',
              subtotal: 30.00
            }
          ]
        },
        {
          id: 2,
          orderNo: 'ORD202605141000005678',
          userId: 1,
          receiver: '李四',
          phone: '13900000002',
          address: '北京市海淀区中关村大街200号YY大厦10楼',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '',
          goodsTotal: 30.00,
          deliveryFee: 8,
          totalAmount: 38.00,
          status: 1,
          createTime: '2026-05-14 10:00:00',
          finishTime: null,
          items: [
            {
              id: 4,
              orderId: 2,
              productId: 3,
              productName: '黄瓜',
              price: 3.00,
              quantity: 10,
              specification: '新鲜蔬菜',
              imageUrl: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain',
              subtotal: 30.00
            }
          ]
        },
        {
          id: 3,
          orderNo: 'ORD202605130800009012',
          userId: 1,
          receiver: '王五',
          phone: '13700000003',
          address: '北京市东城区王府井大街300号ZZ商场5楼',
          deliveryType: 'express',
          paymentType: 'balance',
          remark: '放前台即可',
          goodsTotal: 76.00,
          deliveryFee: 0,
          totalAmount: 76.00,
          status: 1,
          createTime: '2026-05-13 08:00:00',
          finishTime: null,
          items: [
            {
              id: 5,
              orderId: 3,
              productId: 24,
              productName: '有机西兰花',
              price: 8.00,
              quantity: 5,
              specification: '有机蔬菜',
              imageUrl: 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain',
              subtotal: 40.00
            },
            {
              id: 6,
              orderId: 3,
              productId: 7,
              productName: '苹果',
              price: 4.50,
              quantity: 8,
              specification: '新鲜水果',
              imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain',
              subtotal: 36.00
            }
          ]
        },
        {
          id: 4,
          orderNo: 'ORD202605121500003456',
          userId: 1,
          receiver: '赵六',
          phone: '13600000004',
          address: '北京市西城区西单北大街400号AA广场B座',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '小心轻放',
          goodsTotal: 45.00,
          deliveryFee: 8,
          totalAmount: 53.00,
          status: 2,
          createTime: '2026-05-12 15:00:00',
          finishTime: '2026-05-12 17:30:00',
          items: [
            {
              id: 7,
              orderId: 4,
              productId: 1,
              productName: '番茄',
              price: 3.50,
              quantity: 10,
              specification: '新鲜蔬菜',
              imageUrl: 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg',
              subtotal: 35.00
            },
            {
              id: 8,
              orderId: 4,
              productId: 3,
              productName: '黄瓜',
              price: 3.00,
              quantity: 3,
              specification: '新鲜蔬菜',
              imageUrl: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain',
              subtotal: 9.00
            }
          ]
        },
        {
          id: 5,
          orderNo: 'ORD202605101800002345',
          userId: 1,
          receiver: '孙七',
          phone: '13500000005',
          address: '北京市丰台区南三环西路500号BB家园',
          deliveryType: 'express',
          paymentType: 'balance',
          remark: '',
          goodsTotal: 120.50,
          deliveryFee: 0,
          totalAmount: 120.50,
          status: 0,
          createTime: '2026-05-10 18:00:00',
          finishTime: null,
          items: [
            {
              id: 9,
              orderId: 5,
              productId: 9,
              productName: '草莓',
              price: 15.00,
              quantity: 5,
              specification: '新鲜水果',
              imageUrl: 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg',
              subtotal: 75.00
            },
            {
              id: 10,
              orderId: 5,
              productId: 24,
              productName: '有机西兰花',
              price: 8.00,
              quantity: 3,
              specification: '有机蔬菜',
              imageUrl: 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain',
              subtotal: 24.00
            }
          ]
        }
      ]

      // 根据当前选中的状态过滤订单
      if (this.currentStatus !== null) {
        this.orderList = allOrders.filter(order => order.status === this.currentStatus)
      } else {
        this.orderList = allOrders
      }

      this.total = this.orderList.length
    },

    handleTabClick(tab) {
      this.currentPage = 1
      this.loadOrders()
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadOrders()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadOrders()
    },

    getStatusText(status) {
      const statusMap = {
        0: '待配送',
        1: '配送中',
        2: '已送达',
        3: '已完成'
      }
      return statusMap[status] || '未知状态'
    },

    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'primary',
        2: 'success'
      }
      return typeMap[status] || ''
    },

    getOrderItemCount(order) {
      if (!order.items || order.items.length === 0) {
        return 0
      }
      return order.items.reduce((total, item) => total + item.quantity, 0)
    },

    handleDelivered(order) {
      this.$confirm(`确认订单 ${order.orderNo} 已送达？`, '确认送达', {
        confirmButtonText: '确认送达',
        cancelButtonText: '取消',
        type: 'success'
      }).then(async () => {
        try {
          await request.put(`/order/update/${order.id}`)
          this.$message.success('已确认送达')
          this.loadOrders()
        } catch (error) {
          console.error('操作失败:', error)
          this.$message.error('操作失败，请稍后重试')
        }
      }).catch(() => {})
    },

    handleCallCustomer(order) {
      window.location.href = `tel:${order.phone}`
    },

    handleImageError(event) {
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
    }
  }
}
</script>

<style scoped>
.order-item {
  border: 1px solid #ebeef5;
  border-radius: 5px;
  margin-bottom: 20px;
  background-color: #fff;
}

.order-item:last-child {
  margin-bottom: 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.order-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-no {
  font-weight: bold;
  color: #303133;
}

.order-time {
  color: #909399;
  font-size: 14px;
}

.order-basic-info {
  padding: 15px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.info-item {
  font-size: 14px;
}

.info-item .label {
  color: #909399;
  margin-right: 5px;
}

.info-item .value {
  color: #303133;
}

.info-item .remark {
  color: #e6a23c;
}

.order-products {
  padding: 20px;
}

.products-title {
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  font-size: 14px;
}

.product-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-row:last-child {
  border-bottom: none;
}

.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 15px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

.product-price {
  width: 100px;
  text-align: center;
  color: #606266;
}

.product-quantity {
  width: 80px;
  text-align: center;
  color: #606266;
}

.product-subtotal {
  width: 120px;
  text-align: right;
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fafafa;
  border-top: 1px solid #ebeef5;
}

.order-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
  font-size: 14px;
}

.divider {
  color: #dcdfe6;
}

.total-amount {
  color: #303133;
}

.total-amount strong {
  color: #f56c6c;
  font-size: 18px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}
</style>
