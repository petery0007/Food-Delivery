<template>
  <div class="order-confirm-container" style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <!-- 1. 页面标题 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133;">
        <i class="el-icon-s-order"></i> 确认订单
      </h2>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧：订单信息填写 -->
      <el-col :span="16">
        <!-- 收货地址 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header" class="card-header">
            <span><i class="el-icon-location"></i> 收货地址</span>
          </div>
          <el-form :model="addressForm" :rules="addressRules" ref="addressForm" label-width="100px">
            <el-form-item label="收货人" prop="receiver">
              <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="addressForm.phone" placeholder="请输入手机号" maxlength="11"></el-input>
            </el-form-item>
            <el-form-item label="详细地址" prop="address">
              <el-input
                  type="textarea"
                  :rows="3"
                  v-model="addressForm.address"
                  placeholder="请输入详细地址（街道、门牌号等）"
              ></el-input>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 配送方式 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header" class="card-header">
            <span><i class="el-icon-truck"></i> 配送方式</span>
          </div>
          <div class="delivery-info">
            <i class="el-icon-truck" style="font-size: 20px; color: #409EFF;"></i>
            <div style="margin-left: 10px;">
              <div style="font-weight: bold;">快递配送</div>
              <div style="font-size: 12px; color: #909399;">预计2-3天送达</div>
            </div>
          </div>
        </el-card>

        <!-- 支付方式 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header" class="card-header">
            <span><i class="el-icon-wallet"></i> 支付方式</span>
          </div>
          <el-radio-group v-model="paymentType" class="payment-group">
            <el-radio label="balance" border class="payment-radio">
              <div class="payment-content">
                <div class="payment-title">余额支付</div>
                <div class="payment-desc">当前余额：¥{{ userBalance }}</div>
              </div>
            </el-radio>
            <el-radio label="cod" border class="payment-radio">
              <div class="payment-content">
                <div class="payment-title">货到付款</div>
                <div class="payment-desc">配送员送货时收款</div>
              </div>
            </el-radio>
          </el-radio-group>
        </el-card>
        <!-- 订单备注 -->
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span><i class="el-icon-edit"></i> 订单备注</span>
          </div>
          <el-input
              type="textarea"
              :rows="3"
              v-model="orderRemark"
              placeholder="选填，可以告诉商家您的特殊需求..."
              maxlength="200"
              show-word-limit
          ></el-input>
        </el-card>
      </el-col>

      <!-- 右侧：订单商品和结算 -->
      <el-col :span="8">
        <!-- 商品清单 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header" class="card-header">
            <span><i class="el-icon-goods"></i> 商品清单</span>
          </div>
          <div class="cart-items">
            <div v-for="item in selectedItems" :key="item.id" class="cart-item">
              <img :src="item.imageUrl" class="item-image" @error="handleImageError($event)">
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-spec">{{ item.specification }}</div>
                <div class="item-price-qty">
                  <span class="item-price">¥{{ item.price.toFixed(2) }}</span>
                  <span class="item-quantity">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 费用明细 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header" class="card-header">
            <span><i class="el-icon-tickets"></i> 费用明细</span>
          </div>
          <div class="fee-details">
            <div class="fee-item">
              <span>商品总额</span>
              <span>¥{{ goodsTotal.toFixed(2) }}</span>
            </div>
            <div class="fee-item">
              <span>运费</span>
              <span>{{ deliveryFee === 0 ? '免运费' : '¥' + deliveryFee.toFixed(2) }}</span>
            </div>
            <div class="fee-item total">
              <span>应付总额</span>
              <span class="total-amount">¥{{ finalTotal.toFixed(2) }}</span>
            </div>
          </div>
        </el-card>

        <!-- 提交订单按钮 -->
        <div class="button-group">
          <el-button
              type="danger"
              size="large"
              class="submit-btn"
              @click="submitOrder"
              :loading="submitLoading"
          >
            提交订单
          </el-button>

          <el-button
              size="large"
              class="back-btn"
              @click="goBackToCart"
          >
            返回购物车
          </el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'OrderConfirm',
  data() {
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
      // 收货地址表单
      addressForm: {
        receiver: '',
        phone: '',
        address: ''
      },
      addressRules: {
        receiver: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入详细地址', trigger: 'blur' }
        ]
      },

      // 配送方式
      deliveryType: 'express',

      // 支付方式
      paymentType: 'balance',

      // 订单备注
      orderRemark: '',

      // 选中的商品
      selectedItems: [],

      // 用户余额
      userBalance: 0,

      // 运费
      deliveryFee: 0,

      // 提交状态
      submitLoading: false,

      userId: null
    }
  },
  computed: {
    // 商品总额
    goodsTotal() {
      return this.selectedItems.reduce((total, item) => {
        return total + (item.price * item.quantity)
      }, 0)
    },

    // 最终总额
    finalTotal() {
      return this.goodsTotal + this.deliveryFee
    }
  },
  mounted() {
    this.userId = localStorage.getItem('userId')
    if (!this.userId) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }

    // 从路由参数获取选中的商品
    const cartData = this.$route.params.cartData
    if (cartData && cartData.items) {
      this.selectedItems = cartData.items
    } else {
      this.$message.error('没有选择商品')
      this.goBackToCart()
      return
    }

    // 加载用户信息（包括余额）
    this.loadUserInfo()

    // 计算运费
    this.calculateDeliveryFee()
  },
  methods: {
     //加载用户信息
    async loadUserInfo() {
      try {
        const res = await request.get('/user/info')
        if (res.data) {
          this.userBalance = res.data.money || 0

          自动填充用户信息
          if (!this.addressForm.receiver) {
           this.addressForm.receiver = res.data.username || ''
         }
          if (!this.addressForm.phone) {
            this.addressForm.phone = res.data.phone || ''
         }
        }
     } catch (error) {
        console.error('加载用户信息失败:', error)
        //this.loadMockUserInfo()
      }
    },

    // 加载模拟用户数据
    //loadMockUserInfo() {
      //console.log('使用模拟用户数据')
      //this.userBalance = 500.00
      //this.addressForm = {
        //receiver: '张三',
        //: '13800000002',
        //address: '北京市朝阳区建国路100号XX小区3号楼502室'
      //}
      //this.$message.info('已加载模拟用户数据（余额：¥500）')
    //},

    // 计算运费
    calculateDeliveryFee() {
      // 满50免运费，否则运费8元
      if (this.goodsTotal >= 50) {
        this.deliveryFee = 0
      } else {
        this.deliveryFee = 8
      }

      // 自提免运费
      if (this.deliveryType === 'self') {
        this.deliveryFee = 0
      }
    },

    // 提交订单
    submitOrder() {
      // 验证表单
      this.$refs.addressForm.validate(async (valid) => {
        if (!valid) {
          this.$message.warning('请填写完整的收货信息')
          return
        }

        // 检查余额
        if (this.paymentType === 'balance' && this.userBalance < this.finalTotal) {
          this.$message.error('余额不足，请选择其他支付方式或充值')
          return
        }

        this.submitLoading = true

        try {
          // 构建订单数据
          const orderData = {
            userId: this.userId,
            receiver: this.addressForm.receiver,
            phone: this.addressForm.phone,
            address: this.addressForm.address,
            deliveryType: this.deliveryType,
            paymentType: this.paymentType,
            remark: this.orderRemark,
            items: this.selectedItems.map(item => ({
              productId: item.productId,
              productName: item.productName,
              price: item.price,
              quantity: item.quantity,
              specification: item.specification,
              imageUrl: item.imageUrl
            })),
            goodsTotal: this.goodsTotal,
            deliveryFee: this.deliveryFee,
            totalAmount: this.finalTotal
          }

          console.log('提交订单数据:', orderData)
          // 调用后端接口创建订单
          const res = await request.post('/user/order/create', orderData)

          this.$message.success('订单提交成功！')

          // 跳转到订单详情页或订单列表页
          setTimeout(() => {
            this.$router.push('/layout/my-orders')
          }, 1500)

        } catch (error) {
          console.error('订单提交失败:', error)
          this.$message.error('订单提交失败，请稍后重试')
          // 模拟订单提交成功
          //this.$confirm('后端接口暂未实现，是否模拟订单提交？', '提示', {
            //confirmButtonText: '模拟提交',
            //cancelButtonText: '取消',
           // type: 'info'
          //}).then(() => {
            //this.simulateOrderSubmit()
          //}).catch(() => {
            //this.submitLoading = false
          //})
        }
      })
    },

    // 模拟订单提交
   // simulateOrderSubmit() {
      //console.log('模拟订单提交...')

     // setTimeout(() => {
        //this.submitLoading = false
        //this.$message.success('订单提交成功！（模拟）')

        // 显示订单信息
        //this.$alert(`
          //<div style="text-align: left;">
            //<p><strong>订单编号：</strong>ORD${Date.now()}</p>
            //<p><strong>收货人：</strong>${this.addressForm.receiver}</p>
           // <p><strong>联系电话：</strong>${this.addressForm.phone}</p>
           // <p><strong>收货地址：</strong>${this.addressForm.address}</p>
           // <p><strong>配送方式：</strong>${this.deliveryType === 'express' ? '快递配送' : '门店自提'}</p>
           // <p><strong>支付方式：</strong>${this.paymentType === 'balance' ? '余额支付' : '货到付款'}</p>
            //<p><strong>商品数量：</strong>${this.selectedItems.length} 件</p>
            //<p><strong>应付金额：</strong><span style="color: #f56c6c; font-size: 18px;">¥${this.finalTotal.toFixed(2)}</span></p>
          //</div>
        //`, '订单提交成功', {
         // dangerouslyUseHTMLString: true,
         // confirmButtonText: '查看我的订单'
        //}).then(() => {
         // this.$router.push('/layout/my-orders')
       // })
     // }, 1500)
   // },
    // 返回购物车
    goBackToCart() {
      this.$router.push('/layout/cart')
    },

    // 图片加载失败处理
    handleImageError(event) {
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
    }
  },
  watch: {
    // 监听配送方式变化，重新计算运费
    deliveryType() {
      this.calculateDeliveryFee()
    }
  }
}
</script>

<style scoped>
.card-header {
  font-weight: bold;
  color: #303133;
}

/* 配送方式样式 */
.delivery-info {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 5px;
}

/* 支付方式样式 */
.payment-group {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.payment-radio {
  width: 100%;
  margin: 10px 0 !important;
  height: auto !important;
  line-height: normal !important;
}

.payment-radio ::v-deep .el-radio__label {
  width: 100%;
  padding: 10px;
}

.payment-content {
  width: 100%;
}

.payment-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}

.payment-desc {
  font-size: 12px;
  color: #909399;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.submit-btn,
.back-btn {
  width: 100%;
}

/* 商品清单样式 */
.cart-items {
  max-height: 400px;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 10px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.item-spec {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.item-price-qty {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  color: #f56c6c;
  font-weight: bold;
}

.item-quantity {
  color: #909399;
}

/* 费用明细样式 */
.fee-details {
  padding: 10px 0;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #606266;
}

.fee-item.total {
  border-top: 1px solid #ebeef5;
  margin-top: 10px;
  padding-top: 15px;
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.submit-btn,
.back-btn {
  width: 100% !important;
  margin: 0 !important;
}

.submit-btn {
  order: 1;
}

.back-btn {
  order: 2;
}
</style>
