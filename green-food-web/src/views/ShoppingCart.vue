NEW_FILE_CODE
<template>
  <div class="shopping-cart-container" style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <!-- 1. 页面标题 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133;">
        <i class="el-icon-shopping-cart-2"></i> 我的购物车
      </h2>
    </el-card>

    <!-- 2. 购物车商品列表 -->
    <el-card shadow="never" v-loading="loading">
      <!-- 空购物车提示 -->
      <el-empty
          description="购物车是空的，快去选购吧！"
          v-if="cartList.length === 0 && !loading"
      >
        <el-button type="primary" @click="goToShop">去购物</el-button>
      </el-empty>

      <!-- 购物车表格 -->
      <div v-else>
        <el-table
            :data="cartList"
            border
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
          <!-- 选择框 -->
          <el-table-column type="selection" width="55" align="center"></el-table-column>

          <!-- 商品信息 -->
          <el-table-column label="商品信息" min-width="300">
            <template slot-scope="scope">
              <div class="product-info-cell">
                <img
                    :src="scope.row.imageUrl"
                    :alt="scope.row.productName"
                    class="product-thumbnail"
                    @error="handleImageError($event)"
                    @click="viewProductInfo(scope.row.productId)"               style="cursor: pointer;"
                >
                <div class="product-detail">
                  <div class="product-name" @click="viewProductInfo(scope.row.productId)" style="cursor:pointer;">{{ scope.row.productName }}</div>
                  <div class="product-spec">{{ scope.row.specification }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <!-- 单价 -->
          <el-table-column prop="price" label="单价" width="120" align="center">
            <template slot-scope="scope">
              <span class="price-text">¥{{ scope.row.price.toFixed(2) }}</span>
            </template>
          </el-table-column>

          <!-- 数量 -->
          <el-table-column label="数量" width="150" align="center">
            <template slot-scope="scope">
              <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  :max="scope.row.stock"
                  size="small"
                  @change="handleQuantityChange(scope.row)"
              ></el-input-number>
            </template>
          </el-table-column>

          <!-- 小计 -->
          <el-table-column label="小计" width="120" align="center">
            <template slot-scope="scope">
              <span class="subtotal-text">
                ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
              </span>
            </template>
          </el-table-column>

          <!-- 操作 -->
          <el-table-column label="操作" width="100" align="center">
            <template slot-scope="scope">
              <el-button
                  type="danger"
                  size="mini"
                  icon="el-icon-delete"
                  @click="removeFromCart(scope.row.id)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 3. 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox
                v-model="selectAll"
                @change="handleSelectAll"
            >全选</el-checkbox>
            <span class="selected-count">已选择 {{ selectedItems.length }} 件商品</span>
          </div>
          <div class="footer-right">
            <div class="total-price">
              合计：<span class="price-amount">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <el-button
                type="danger"
                size="large"
                :disabled="selectedItems.length === 0"
                @click="handleCheckout"
                :loading="checkoutLoading"
            >
              去结算 ({{ selectedItems.length }})
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ShoppingCart',
  data() {
    return {
      cartList: [],
      loading: false,
      checkoutLoading: false,
      selectAll: false,
      selectedItems: [],
      userId: null
    }
  },
  computed: {
    // 计算选中商品的总价
    totalPrice() {
      return this.selectedItems.reduce((total, item) => {
        return total + (item.price * item.quantity)
      }, 0)
    }
  },
  mounted() {
    this.userId = localStorage.getItem('userId')
    if (!this.userId) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.loadCartData()
  },
  methods: {
    // 加载购物车数据
    async loadCartData() {
      this.loading = true
      try {
        const res = await request.get(`/user/cart/${this.userId}`)

        if (res.data && res.data.list) {
          this.cartList = res.data.list
          console.log('购物车数据加载成功:', this.cartList)
        } else {
          this.cartList = []
        }
      } catch (error) {
        console.error('购物车加载失败:', error)

        // 如果接口不存在，使用模拟数据
        this.loadMockCartData()
      } finally {
        this.loading = false
      }
    },

    // 查看商品详情
    viewProductInfo(productId) {
      this.$router.push({
        name: 'ProductInfoUser',
        params: { id: productId },
        query: { from: 'cart' }
      })
    },
    // 处理全选/取消全选
    handleSelectAll(value) {
      if (value) {
        this.selectedItems = [...this.cartList]
      } else {
        this.selectedItems = []
      }
    },

    // 处理表格选择变化
    handleSelectionChange(selection) {
      this.selectedItems = selection
      this.selectAll = selection.length === this.cartList.length && this.cartList.length > 0
    },

    // 处理数量变化
    async handleQuantityChange(item) {
      try {
        // 调用后端接口更新数量
        await request.put('/user/cart/update', {
          id: item.id,
          quantity: item.quantity
        })
        this.$message.success('数量已更新')
      } catch (error) {
        console.error('更新数量失败:', error)
        // 如果是模拟数据，不提示错误
      }
    },

    // 从购物车删除商品
    removeFromCart(cartId) {
      this.$confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/user/cart/${cartId}`)
          this.$message.success('删除成功')
          this.loadCartData()
        } catch (error) {
          console.error('删除失败:', error)
          // 模拟删除
          this.cartList = this.cartList.filter(item => item.id !== cartId)
          this.$message.success('删除成功（模拟）')
        }
      }).catch(() => {})
    },

    // 去结算
    handleCheckout() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请至少选择一件商品')
        return
      }

      this.$confirm(
          `共 ${this.selectedItems.length} 件商品，合计 ¥${this.totalPrice.toFixed(2)}，确认结算？`,
          '确认订单',
          {
            confirmButtonText: '确认结算',
            cancelButtonText: '继续购物',
            type: 'success'
          }
      ).then(() => {
        this.checkoutLoading = true

        // // 这里替换为你的实际结算接口
        // setTimeout(() => {
        //   this.checkoutLoading = false
        //   this.$message.success('订单提交成功！')
        //
        //   // 清空已选商品（实际应该从购物车移除）
        //   const selectedIds = this.selectedItems.map(item => item.id)
        //   this.cartList = this.cartList.filter(item => !selectedIds.includes(item.id))
        //   this.selectedItems = []
        //   this.selectAll = false
        // }, 1500)

        //订单创建接口
        request.post('/user/order/create', {
          userId: this.userId,
          items: this.selectedItems.map(item => ({
            productId: item.productId,
            quantity: item.quantity
          }))
        }).then(res => {
          this.$message.success('订单提交成功')
          this.loadCartData()
        }).catch(err => {
          this.$message.error('订单提交失败')
        }).finally(() => {
          this.checkoutLoading = false
        })
      }).catch(() => {})
    },

    // 去购物
    goToShop() {
      this.$router.push('/layout/shop')
    },

    // 图片加载失败处理
    handleImageError(event) {
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
    }
  }
}
</script>

<style scoped>
/* 商品信息单元格样式 */
.product-info-cell {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.product-thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 15px;
  border: 1px solid #ebeef5;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 5px;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

/* 价格样式 */
.price-text {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.subtotal-text {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

/* 底部结算栏样式 */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  margin-top: 20px;
  background-color: #f5f7fa;
  border-radius: 5px;
}

.footer-left {
  display: flex;
  align-items: center;
}

.selected-count {
  margin-left: 15px;
  color: #606266;
  font-size: 14px;
}

.footer-right {
  display: flex;
  align-items: center;
}

.total-price {
  margin-right: 20px;
  font-size: 14px;
  color: #303133;
}

.price-amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .cart-footer {
    flex-direction: column;
    gap: 15px;
  }

  .footer-left,
  .footer-right {
    width: 100%;
    justify-content: center;
  }
}
</style>
