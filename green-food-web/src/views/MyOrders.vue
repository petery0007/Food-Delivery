<template>
  <div class="my-orders-container" style="padding: 20px; max-width: 1400px; margin: 0 auto;">
    <!-- 页面标题 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133;">
        <i class="el-icon-s-ticket"></i> 我的订单
      </h2>
    </el-card>

    <!-- 订单状态筛选 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待支付" name="pending_payment"></el-tab-pane>
        <el-tab-pane label="配送中" name="delivering"></el-tab-pane>
        <el-tab-pane label="待收货" name="delivered"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
        <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 订单列表 -->
    <el-card shadow="never" v-loading="loading">
      <!-- 空订单提示 -->
      <el-empty
          description="暂无订单"
          v-if="orderList.length === 0 && !loading"
      >
        <el-button type="primary" @click="goToShop">去购物</el-button>
      </el-empty>

      <!-- 订单列表 -->
      <div v-else>
        <div v-for="order in orderList" :key="order.id" class="order-item">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单编号：{{ order.id }}</span>
              <span class="order-time">{{ order.createTime }}</span>
              <span class="order-time" v-if="order.finishTime">完成时间：{{ order.finishTime }}</span>
              <span class="order-time" v-if="order.deliveryStaff">配送员：{{ order.deliveryStaff }} {{ order.deliveryPhone ? '(' + order.deliveryPhone + ')' : '' }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.status)" size="medium">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
          </div>

          <!-- 收货地址信息 -->
          <div class="order-address">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="address-item">
                  <span class="label">收货人：</span>
                  <span class="value">{{ order.receiver }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="address-item">
                  <span class="label">联系电话：</span>
                  <span class="value">{{ order.phone }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="address-item">
                  <span class="label">收货地址：</span>
                  <span class="value address-text">{{ order.address }}</span>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 订单商品列表 -->
          <div class="order-products">
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
              <span class="total-amount">应付总额：<strong>¥{{ order.totalAmount.toFixed(2) }}</strong></span>
            </div>
            <div class="order-actions">
              <!-- 待支付状态 -->
              <template v-if="order.status === 0">
                <el-button size="small" type="danger" @click="handlePay(order)">立即支付</el-button>
                <el-button size="small" @click="handleCancel(order)">取消订单</el-button>
              </template>

              <!-- 配送中状态 -->
              <template v-if="order.status === 1">
              </template>

              <!-- 待收货状态 -->
              <template v-if="order.status === 2">
                <el-button size="small" type="success" @click="handleConfirmReceive(order)">确认收货</el-button>
              </template>

              <!-- 已完成状态 -->
              <template v-if="order.status === 3">
                <el-button size="small" type="warning" @click="handleReview(order)">评价</el-button>
              </template>

              <!-- 已取消状态 -->
              <template v-if="order.status === 4">
                <el-button size="small" type="primary" @click="handleReorder(order)">重新下单</el-button>
                <el-button size="small" type="danger" @click="handleDelete(order)">删除订单</el-button>
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

    <!-- 充值弹窗组件 -->
    <RechargeModal ref="rechargeModal" @success="handleRechargeSuccess" />
  </div>
</template>

<script>
import request from '@/utils/request'
import RechargeModal from '@/components/Recharge.vue'

export default {
  name: 'MyOrders',
  components: {
    RechargeModal
  },
  data() {
    return {
      activeTab: 'all',
      orderList: [],
      loading: false,
      userId: null,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      userBalance: 0,
      currentPayOrder: null
    }
  },
  computed: {
    currentStatus() {
      if (this.activeTab === 'all') {
        return null
      }
      const statusMap = {
        'pending_payment': 0,
        'delivering': 1,
        'delivered': 2,
        'completed': 3,
        'cancelled': 4
      }
      return statusMap[this.activeTab]
    }
  },
  mounted() {
    this.userId = localStorage.getItem('userId')
    if (!this.userId) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.loadUserInfo()
    this.loadOrders()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await request.get('/user/info')
        if (res.data) {
          this.userBalance = res.data.money || 0
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    },

    async loadOrders() {
      this.loading = true
      try {
        let url = `/user/order/list?page=${this.currentPage}&pageSize=${this.pageSize}`

        if (this.currentStatus !== null) {
          url += `&status=${this.currentStatus}`
        }

        const res = await request.get(url)

        if (res && res.data && res.data.list) {
          this.orderList = res.data.list
          this.total = res.data.total || 0
        } else {
          this.orderList = []
          this.total = 0
        }
      } catch (error) {
        console.error('加载订单失败，使用模拟数据:', error)
        this.loadMockOrders()
      } finally {
        this.loading = false
      }
    },

    loadMockOrders() {
      console.log('使用模拟订单数据')
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
          remark: '请尽快送达',
          goodsTotal: 61.00,
          deliveryFee: 0,
          totalAmount: 61.00,
          status: 0,
          createTime: '2026-05-15 12:00:00',
          finishTime: null,
          deliveryStaff: null,
          deliveryPhone: null,
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
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '',
          goodsTotal: 30.00,
          deliveryFee: 8,
          totalAmount: 38.00,
          status: 1,
          createTime: '2026-05-14 10:00:00',
          finishTime: null,
          deliveryStaff: '李师傅',
          deliveryPhone: '13900001111',
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
          receiver: '李四',
          phone: '13900000002',
          address: '北京市海淀区中关村大街200号YY大厦10楼',
          deliveryType: 'self',
          paymentType: 'balance',
          remark: '包装完好',
          goodsTotal: 76.00,
          deliveryFee: 0,
          totalAmount: 76.00,
          status: 2,
          createTime: '2026-05-13 08:00:00',
          finishTime: null,
          deliveryStaff: '王师傅',
          deliveryPhone: '13900002222',
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
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '小心轻放',
          goodsTotal: 45.00,
          deliveryFee: 8,
          totalAmount: 53.00,
          status: 1,
          createTime: '2026-05-12 15:00:00',
          finishTime: null,
          deliveryStaff: '张师傅',
          deliveryPhone: '13900003333',
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
            },
            {
              id: 9,
              orderId: 4,
              productId: 24,
              productName: '有机西兰花',
              price: 8.00,
              quantity: 1,
              specification: '有机蔬菜',
              imageUrl: 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain',
              subtotal: 8.00
            }
          ]
        },
        {
          id: 5,
          orderNo: 'ORD202605110900007890',
          userId: 1,
          receiver: '王五',
          phone: '13700000003',
          address: '北京市东城区王府井大街300号ZZ商场',
          deliveryType: 'express',
          paymentType: 'balance',
          remark: '暂时不需要了',
          goodsTotal: 25.00,
          deliveryFee: 8,
          totalAmount: 33.00,
          status: 4,
          createTime: '2026-05-11 09:00:00',
          finishTime: '2026-05-11 09:30:00',
          deliveryStaff: null,
          deliveryPhone: null,
          items: [
            {
              id: 10,
              orderId: 5,
              productId: 7,
              productName: '苹果',
              price: 4.50,
              quantity: 5,
              specification: '新鲜水果',
              imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain',
              subtotal: 22.50
            }
          ]
        },
        {
          id: 6,
          orderNo: 'ORD202605101800002345',
          userId: 1,
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '',
          goodsTotal: 120.50,
          deliveryFee: 0,
          totalAmount: 120.50,
          status: 0,
          createTime: '2026-05-10 18:00:00',
          finishTime: null,
          deliveryStaff: null,
          deliveryPhone: null,
          items: [
            {
              id: 11,
              orderId: 6,
              productId: 9,
              productName: '草莓',
              price: 15.00,
              quantity: 5,
              specification: '新鲜水果',
              imageUrl: 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg',
              subtotal: 75.00
            },
            {
              id: 12,
              orderId: 6,
              productId: 24,
              productName: '有机西兰花',
              price: 8.00,
              quantity: 3,
              specification: '有机蔬菜',
              imageUrl: 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain',
              subtotal: 24.00
            },
            {
              id: 13,
              orderId: 6,
              productId: 1,
              productName: '番茄',
              price: 3.50,
              quantity: 6,
              specification: '新鲜蔬菜',
              imageUrl: 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg',
              subtotal: 21.00
            }
          ]
        },
        {
          id: 7,
          orderNo: 'ORD202605051400006789',
          userId: 1,
          receiver: '张三',
          phone: '13800000001',
          address: '北京市朝阳区建国路100号XX小区3号楼502室',
          deliveryType: 'express',
          paymentType: 'cod',
          remark: '',
          goodsTotal: 52.00,
          deliveryFee: 8,
          totalAmount: 60.00,
          status: 3,
          createTime: '2026-05-05 14:00:00',
          finishTime: '2026-05-05 20:00:00',
          deliveryStaff: '赵师傅',
          deliveryPhone: '13900004444',
          items: [
            {
              id: 14,
              orderId: 7,
              productId: 3,
              productName: '黄瓜',
              price: 3.00,
              quantity: 8,
              specification: '新鲜蔬菜',
              imageUrl: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain',
              subtotal: 24.00
            },
            {
              id: 15,
              orderId: 7,
              productId: 7,
              productName: '苹果',
              price: 4.50,
              quantity: 4,
              specification: '新鲜水果',
              imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain',
              subtotal: 18.00
            },
            {
              id: 16,
              orderId: 7,
              productId: 1,
              productName: '番茄',
              price: 3.50,
              quantity: 3,
              specification: '新鲜蔬菜',
              imageUrl: 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg',
              subtotal: 10.50
            }
          ]
        },
        {
          id: 8,
          orderNo: 'ORD202605091100004567',
          userId: 1,
          receiver: '赵六',
          phone: '13600000004',
          address: '北京市西城区西单北大街400号AA广场',
          deliveryType: 'express',
          paymentType: 'balance',
          remark: '加急配送',
          goodsTotal: 90.00,
          deliveryFee: 0,
          totalAmount: 90.00,
          status: 2,
          createTime: '2026-05-09 11:00:00',
          finishTime: null,
          deliveryStaff: '孙师傅',
          deliveryPhone: '13900005555',
          items: [
            {
              id: 17,
              orderId: 8,
              productId: 9,
              productName: '草莓',
              price: 15.00,
              quantity: 6,
              specification: '新鲜水果',
              imageUrl: 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg',
              subtotal: 90.00
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
      this.$message.info('已加载模拟订单数据')
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
        0: '待支付',
        1: '配送中',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知状态'
    },

    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'primary',
        2: 'success',
        3: '',
        4: 'info'
      }
      return typeMap[status] || ''
    },
    getOrderItemCount(order) {
      if (!order.items || order.items.length === 0) {
        return 0
      }
      return order.items.reduce((total, item) => total + item.quantity, 0)
    },

    viewOrderDetail(order) {
      this.$router.push({
        name: 'OrderDetail',
        params: { orderId: order.id }
      })
    },

    handlePay(order) {
      // 保存当前要支付的订单
      this.currentPayOrder = order
      
      // 检查余额是否足够
      if (this.userBalance < order.totalAmount) {
        this.$confirm(
            `您的余额不足，当前余额：¥${this.userBalance.toFixed(2)}，应付金额：¥${order.totalAmount.toFixed(2)}。是否立即充值？`,
            '余额不足',
            {
              confirmButtonText: '立即充值',
              cancelButtonText: '取消',
              type: 'warning'
            }
        ).then(() => {
          // 打开充值弹窗
          this.$refs.rechargeModal.open()
        }).catch(() => {
          // 用户取消充值
        })
        return
      }

      // 余额充足，直接支付
      this.confirmPay(order)
    },

    // 确认支付
    async confirmPay(order) {
      try {
        await this.$confirm(`确认支付订单金额 ¥${order.totalAmount.toFixed(2)}？`, '支付确认', {
          confirmButtonText: '确认支付',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.post(`/user/order/pay/${order.id}`)
        this.$message.success('支付成功')
        
        // 重新加载用户余额和订单列表
        await this.loadUserInfo()
        await this.loadOrders()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('支付失败:', error)
          this.$message.error('支付失败，请稍后重试')
        }
      }
    },

    // 处理充值成功事件
    handleRechargeSuccess() {
      // 重新加载用户余额
      this.loadUserInfo()

      // 如果之前有等待支付的订单，充值成功后自动继续支付
      if (this.currentPayOrder) {
        const order = this.currentPayOrder
        this.currentPayOrder = null
        
        // 检查充值后余额是否足够
        if (this.userBalance >= order.totalAmount) {
          setTimeout(() => {
            this.confirmPay(order)
          }, 500)
        } else {
          this.$message.warning('充值后余额仍不足，请继续充值或选择其他支付方式')
        }
      }
    },

    handleCancel(order) {
      this.$confirm(`确认取消订单？`, '取消确认', {
        confirmButtonText: '确认取消',
        cancelButtonText: '不取消了',
        type: 'warning'
      }).then(async () => {
        try {
          await request.put(`/user/order/cancel/${order.id}`)
          this.$message.success('订单已取消')
          this.loadOrders()
        } catch (error) {
          console.error('取消订单失败:', error)
          this.$message.error('取消订单失败，请稍后重试')
        }
      }).catch(() => {})
    },

    handleConfirmReceive(order) {
      this.$confirm(`确认已收到订单的商品？`, '确认收货', {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'success'
      }).then(async () => {
        try {
          await request.put(`/order/update/${order.id}`)
          this.$message.success('确认收货成功')
          this.loadOrders()
        } catch (error) {
          console.error('确认收货失败:', error)
          this.$message.error('操作失败，请稍后重试')
        }
      }).catch(() => {})
    },

    handleReview(order) {
      // 跳转到评价页面，并传递订单信息
      this.$router.push({
        name: 'MyReviews',
        query: {
          orderId: order.id,
          items: JSON.stringify(order.items)
        }
      })
    },

    handleDelete(order) {
      this.$confirm(`确认删除订单？删除后不可恢复！`, '删除确认', {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/user/order/delete/${order.id}`)
          this.$message.success('订单已删除')
          this.loadOrders()
        } catch (error) {
          console.error('删除订单失败:', error)
          this.orderList = this.orderList.filter(item => item.id !== order.id)
          this.total = this.orderList.length
          this.$message.success('订单已删除（模拟）')
        }
      }).catch(() => {})
    },

    handleReorder(order) {
      this.$confirm(`确认根据订单重新下单？`, '重新下单', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        const cartData = {
          items: order.items.map(item => ({
            productId: item.productId,
            productName: item.productName,
            price: item.price,
            quantity: item.quantity,
            specification: item.specification,
            imageUrl: item.imageUrl
          }))
        }
        this.$router.push({
          name: 'OrderConfirm',
          params: { cartData }
        })
      }).catch(() => {})
    },


    goToShop() {
      this.$router.push('/layout/shop')
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

.order-address {
  padding: 15px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.address-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.address-item:last-child {
  margin-bottom: 0;
}

.address-item .label {
  color: #909399;
  font-size: 14px;
  min-width: 70px;
  flex-shrink: 0;
}

.address-item .value {
  color: #303133;
  font-size: 14px;
}

.address-item .address-text {
  color: #606266;
  word-break: break-all;
}

.order-products {
  padding: 20px;
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
