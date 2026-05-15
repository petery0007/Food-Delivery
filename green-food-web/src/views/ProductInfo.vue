NEW_FILE_CODE
<template>
  <div class="product-detail-container" style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <!-- 返回按钮 -->
    <el-button
        icon="el-icon-arrow-left"
        @click="goBack"
        style="margin-bottom: 20px;"
    >
      返回列表
    </el-button>

    <!-- 加载状态 -->
    <div v-loading="loading" element-loading-text="加载中...">
      <el-card shadow="never" v-if="!loading && product">
        <el-row :gutter="30">
          <!-- 左侧：商品图片 -->
          <el-col :xs="24" :sm="10" :md="10">
            <div class="product-image-wrapper">
              <img
                  :src="product.imageUrl"
                  :alt="product.name"
                  @error="handleImageError"
                  class="product-main-image"
              >
            </div>
          </el-col>

          <!-- 右侧：商品信息 -->
          <el-col :xs="24" :sm="14" :md="14">
            <div class="product-info-section">
              <h1 class="product-title">{{ product.name }}</h1>

              <div class="product-meta">
                <el-tag type="success" size="medium">{{ product.specification }}</el-tag>
                <span class="meta-item">
                  <i class="el-icon-view"></i> 浏览 {{ product.clickCount }} 次
                </span>
              </div>

              <div class="price-stock-section">
                <div class="price-box">
                  <span class="price-label">价格：</span>
                  <span class="price-value">¥{{ product.price }}</span>
                </div>
                <div class="stock-box">
                  <span class="stock-label">库存：</span>
                  <span class="stock-value" :class="{ 'out-of-stock': product.stock <= 0 }">
                    {{ product.stock > 0 ? product.stock + ' 件' : '已售罄' }}
                  </span>
                </div>
              </div>

              <div class="status-section">
                <el-tag
                    :type="product.status === '上架' ? 'success' : 'info'"
                    size="large"
                >
                  {{ product.status === '上架' ? '在售' : '已下架' }}
                </el-tag>
              </div>

              <!-- 按钮：加入购物车 -->
              <div class="action-buttons" v-if="isUserView">
                <el-button
                    type="success"
                    size="large"
                    icon="el-icon-shopping-cart-2"
                    @click="addToCart"
                    :disabled="product.stock <= 0 || product.status !== '上架'"
                    style="width: 200px;"
                >
                  加入购物车
                </el-button>
              </div>

              <!-- 按钮：上下架和删除功能 -->
              <div class="action-buttons" v-if="isAdminView">
                <el-button
                    :type="product.status === '上架' ? 'warning' : 'success'"
                    size="large"
                    icon="el-icon-refresh"
                    @click="toggleProductStatus"                  style="margin-right: 15px;"
                >
                  {{ product.status === '上架' ? '下架' : '上架' }}
                </el-button>
                <el-button
                    type="danger"
                    size="large"
                    icon="el-icon-delete"
                    @click="deleteProduct"
                >
                  删除商品
                </el-button>
              </div>

              <div class="producer-section" v-if="product.producer">
                <span class="label">生产厂家：</span>
                <span class="value">{{ product.producer }}</span>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 商品描述 -->
        <el-divider></el-divider>
        <div class="description-section">
          <h2 class="section-title">商品描述</h2>
          <div class="description-content">
            {{ product.description || '暂无商品描述' }}
          </div>
        </div>

        <!-- 其他信息（仅管理员可见） -->
        <el-divider v-if="isAdminView"></el-divider>
        <div class="admin-info-section" v-if="isAdminView">
          <h2 class="section-title">管理信息</h2>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="商品ID">{{ product.id }}</el-descriptions-item>
            <el-descriptions-item label="上架状态">
              <el-tag :type="product.status === '上架' ? 'success' : 'danger'">
                {{ product.status }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 错误提示 -->
      <el-empty
          v-else-if="!loading && !product"
          description="商品不存在或已下架"
          style="padding: 100px 0;"
      >
        <el-button type="primary" @click="goBack">返回</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ProductInfo',
  data() {
    return {
      product: null,
      loading: false,
      productId: null
    }
  },
  computed: {
    // 判断是否为用户端视图
    isUserView() {
      return this.$route.path.includes('product-info-user')
    },
    // 判断是否为管理端视图
    isAdminView() {
      return !this.isUserView
    }
  },
  created() {
    this.productId = this.$route.params.id
    if (this.productId) {
      this.loadProductInfo()
    } else {
      this.$message.error('商品ID缺失')
      this.goBack()
    }
  },
  methods: {
    async loadProductInfo() {
      this.loading = true
      try {
        // 根据路由判断使用哪个接口
        const apiUrl = `/products/${this.productId}`

        const res = await request.get(apiUrl)

        if (res.data) {
          this.product = res.data
          console.log('商品详情加载成功:', this.product)
        }
      } catch (error) {
        console.error('加载商品详情失败:', error)

        if (error.response && error.response.status === 401) {
          this.$message.warning('登录已过期，请重新登录')
          localStorage.clear()
          this.$router.push('/login')
        } else if (error.response && error.response.status === 404) {
          this.$message.error('商品不存在')
        } else {
          this.$message.error('加载失败，请稍后重试')
        }
      } finally {
        this.loading = false
      }
    },

    addToCart() {
      this.$message.success(`已将【${this.product.name}】加入购物车`)
      // 替换为实际的购物车接口
      // request.post('/cart/add', { productId: this.product.id, quantity: 1 })
    },

    // 管理端：切换上下架状态
    toggleProductStatus() {
      const newStatus = this.product.status === '上架' ? '下架' : '上架'
      const actionText = newStatus === '上架' ? '上架' : '下架'

      this.$confirm(`确定要将该商品${actionText}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await request.post(`/products/status/${this.product.id}`, {
            status: newStatus
          })

          this.$message.success('状态更新成功')
          // 更新本地数据
          this.product.status = newStatus

          // 重新加载商品信息以获取最新数据
          await this.loadProductInfo()
        } catch (error) {
          console.error('更新状态失败:', error)
          this.$message.error('操作失败，请稍后重试')
        }
      }).catch(() => {
        // 用户取消操作
      })
    },

    // 管理端：删除商品
    deleteProduct() {
      this.$confirm('确定要删除该商品吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }).then(async () => {
        try {
          const res = await request.delete(`/products/${this.product.id}`)

          this.$message.success('删除成功')

          // 延迟一下再返回，让用户看到成功提示
          setTimeout(() => {
            this.goBack()
          }, 1000)
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败，请稍后重试')
        }
      }).catch(() => {
        // 用户取消操作
      })
    },

    handleImageError(event) {
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
    },

    goBack() {
      // 根据来源页面返回
      if (this.$route.query.from === 'shop') {
        this.$router.push('/layout/shop')
      } else if (this.$route.query.from === 'cart'){
        this.$router.push('/layout/cart')
      }else{
        this.$router.push('/layout/products')
      }
    }
  }
}
</script>

<style scoped>
.product-detail-container {
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.product-image-wrapper {
  width: 100%;
  height: 400px; /* 恢复到合适的高度 */
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
  background-color: #f9f9f9; /* 添加浅色背景，使图片更突出 */
}

.product-info-section {
  padding: 20px 0;
  min-height: 400px; /* 与左侧图片高度保持一致 */
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}

.meta-item {
  font-size: 14px;
  color: #909399;
}

.price-stock-section {
  background: linear-gradient(135deg, #fef5e7 0%, #fff5f5 100%);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-box, .stock-box {
  display: flex;
  align-items: baseline;
  margin-bottom: 10px;
}

.price-box:last-child, .stock-box:last-child {
  margin-bottom: 0;
}

.price-label, .stock-label {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

.price-value {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}

.stock-value {
  font-size: 16px;
  color: #67c23a;
  font-weight: 500;
}

.stock-value.out-of-stock {
  color: #f56c6c;
}

.status-section {
  margin: 20px 0;
}

.action-buttons {
  margin: 25px 0;
}

.producer-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 5px;
}

.producer-section .label {
  font-size: 14px;
  color: #909399;
}

.producer-section .value {
  font-size: 14px;
  color: #606266;
}

.section-title {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
}

.description-section {
  margin-top: 30px;
}

.description-content {
  line-height: 1.8;
  color: #606266;
  font-size: 15px;
  white-space: pre-wrap;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 5px;
  min-height: 100px;
}

.admin-info-section {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .product-image-wrapper {
    height: 300px; /* 移动端适当减小 */
    margin-bottom: 20px;
  }

  .product-info-section {
    min-height: 300px; /* 与左侧图片高度保持一致 */
    padding: 15px 0;
  }

  .product-title {
    font-size: 22px;
  }

  .price-value {
    font-size: 26px;
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .action-buttons .el-button {
    width: 100% !important;
    margin-right: 0 !important;
  }
}

@media (min-width: 769px) and (max-width: 992px) {
  .product-image-wrapper {
    height: 350px; /* 平板端适中高度 */
  }

  .product-info-section {
    min-height: 350px; /* 与左侧图片高度保持一致 */
  }
}

/* 针对较大屏幕的优化 */
@media (min-width: 1200px) {
  .product-image-wrapper {
    height: 420px; /* 大屏下稍微增加 */
  }

  .product-info-section {
    min-height: 420px; /* 与左侧图片高度保持一致 */
  }
}
</style>
