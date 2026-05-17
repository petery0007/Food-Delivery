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

        <!-- 商品评价 -->
        <el-divider></el-divider>
        <div class="reviews-section">
          <div class="section-header">
            <h2 class="section-title">商品评价</h2>
            <el-tag type="warning" size="medium">
              共 {{ reviews.length }} 条评价
            </el-tag>
          </div>

          <!-- 评价统计 -->
          <div class="review-summary" v-if="reviews.length > 0">
            <div class="summary-item">
              <span class="label">平均评分：</span>
              <el-rate
                  v-model="averageRating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
              ></el-rate>
              <span class="score-text">{{ averageRating.toFixed(1) }} 分</span>
            </div>
            <div class="summary-item">
              <span class="label">评分分布：</span>
              <div class="rating-distribution">
                <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="distribution-row">
                  <span class="star-label">{{ star }}星</span>
                  <el-progress
                      :percentage="getRatingPercentage(star)"
                      :color="getProgressColor(star)"
                      :stroke-width="8"                      style="width: 200px;"
                  ></el-progress>
                  <span class="count-text">{{ getRatingCount(star) }}条</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 评价列表 -->
          <div class="reviews-list" v-if="reviews.length > 0">
            <div v-for="review in displayedReviews" :key="review.id" class="review-card">
              <div class="review-header">
                <div class="user-info">
                  <el-avatar :size="40" :src="review.userAvatar">
                    {{ review.username ? review.username.charAt(0) : '用' }}
                  </el-avatar>
                  <div class="user-details">
                    <div class="username">{{ review.username || '匿名用户' }}</div>
                    <div class="review-time">{{ review.createTime }}</div>
                  </div>
                </div>
                <el-rate
                    v-model="review.rating"
                    disabled
                    show-score
                    text-color="#ff9900"
                    score-template="{value}分"
                ></el-rate>
              </div>
              <div class="review-content">
                <p>{{ review.comment }}</p>
              </div>
              <!-- 如果有图片评价可以在此处展示 -->
              <div class="review-images" v-if="review.images && review.images.length > 0">
                <img
                    v-for="(img, index) in review.images"
                    :key="index"
                    :src="img"
                    class="review-image"
                    @click="previewImage(img)"
                >
              </div>
            </div>

            <!-- 加载更多按钮 -->
            <div class="load-more" v-if="reviews.length > pageSize">
              <el-button
                  type="primary"
                  plain
                  @click="loadMoreReviews"
                  v-if="displayedReviews.length < reviews.length"
              >
                加载更多
              </el-button>
              <span v-else class="no-more">没有更多评价了</span>
            </div>
          </div>

          <!-- 空评价提示 -->
          <el-empty
              v-else
              description="暂无评价，快来成为第一个评价的人吧！"
              :image-size="100"
          >
          </el-empty>
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
      productId: null,
      reviews: [],
      reviewsLoading: false,
      displayedReviews: [],
      pageSize: 5
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
    },
    // 计算平均评分
    averageRating() {
      if (this.reviews.length === 0) return 0
      const sum = this.reviews.reduce((acc, review) => acc + review.rating, 0)
      return sum / this.reviews.length
    }
  },
  created() {
    this.productId = this.$route.params.id
    if (this.productId) {
      this.loadProductInfo()
      this.loadProductReviews()
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

    async loadProductReviews() {
      this.reviewsLoading = true
      try {
        // 尝试从后端获取评价数据
        const res = await request.get(`/products/${this.productId}/reviews`)
    
        console.log('后端返回的评价数据:', res.data)
    
        // 适配不同的返回格式
        let reviewList = []
            
        if (Array.isArray(res.data)) {
          // 格式1: 直接返回数组
          reviewList = res.data
        } else if (res.data && res.data.list && Array.isArray(res.data.list)) {
          // 格式2: 返回分页对象 { list: [...], total: 6 }
          reviewList = res.data.list
        } else if (res.data && res.data.records && Array.isArray(res.data.records)) {
          // 格式3: MyBatis-Plus 分页格式 { records: [...], total: 6 }
          reviewList = res.data.records
        } else if (res.code === 200 && res.data) {
          // 格式4: 标准响应 { code: 200, data: [...] }
          reviewList = Array.isArray(res.data) ? res.data : []
        }
    
        if (reviewList.length > 0) {
          // 处理后端数据,映射为前端需要的格式
          this.reviews = reviewList.map(item => ({
            id: item.comment_id || item.id,
            productId: item.goods_id || item.productId,
            userId: item.user_id || item.userId,
            username: item.username || '匿名用户',
            userAvatar: item.userAvatar || '',
            rating: item.score || item.rating,
            comment: item.content || item.comment,
            createTime: item.create_time || item.createTime,
            images: item.images || []
          }))
          this.displayedReviews = this.reviews.slice(0, this.pageSize)
          console.log('成功加载评价数据:', this.reviews.length, '条')
        } else {
          console.warn('后端返回的评价数据为空,使用模拟数据')
          this.loadMockReviews()
        }
      } catch (error) {
        console.error('加载评价失败,使用模拟数据:', error)
        this.loadMockReviews()
      } finally {
        this.reviewsLoading = false
      }
    },

    loadMockReviews() {
      console.log('使用模拟评价数据')
      this.reviews = [
        {
          id: 1,
          productId: this.productId,
          userId: 2,
          username: '张三',
          userAvatar: '',
          rating: 5,
          comment: '非常新鲜的商品，质量很好，包装也很仔细，物流速度快，非常满意的一次购物体验！',
          createTime: '2026-05-10 14:30:00',
          images: []
        },
        {
          id: 2,
          productId: this.productId,
          userId: 3,
          username: '李四',
          userAvatar: '',
          rating: 4,
          comment: '整体不错，就是有一个有点小瑕疵，但不影响使用，性价比还是很高的。',
          createTime: '2026-05-08 10:15:00',
          images: []
        },
        {
          id: 3,
          productId: this.productId,
          userId: 4,
          username: '王五',
          userAvatar: '',
          rating: 5,
          comment: '第二次购买了，品质一如既往的好，会继续支持！',
          createTime: '2026-05-05 16:45:00',
          images: []
        },
        {
          id: 4,
          productId: this.productId,
          userId: 5,
          username: '赵六',
          userAvatar: '',
          rating: 3,
          comment: '一般般吧，感觉和描述有点差距，不过价格还算合理。',
          createTime: '2026-05-01 09:20:00',
          images: []
        },
        {
          id: 5,
          productId: this.productId,
          userId: 6,
          username: '孙七',
          userAvatar: '',
          rating: 5,
          comment: '超级棒！比超市新鲜多了，而且价格便宜，强烈推荐！',
          createTime: '2026-04-28 20:10:00',
          images: []
        },
        {
          id: 6,
          productId: this.productId,
          userId: 7,
          username: '周八',
          userAvatar: '',
          rating: 4,
          comment: '挺好的，送货速度快，包装完好，就是分量稍微少了一点。',
          createTime: '2026-04-25 11:30:00',
          images: []
        },
        {
          id: 7,
          productId: this.productId,
          userId: 8,
          username: '吴九',
          userAvatar: '',
          rating: 5,
          comment: '非常好，已经是老顾客了，每次都很满意！',
          createTime: '2026-04-20 15:50:00',
          images: []
        }
      ]
      this.displayedReviews = this.reviews.slice(0, this.pageSize)
    },

    // 获取某个评分的数量
    getRatingCount(star) {
      return this.reviews.filter(r => r.rating === star).length
    },

    // 获取某个评分的百分比
    getRatingPercentage(star) {
      if (this.reviews.length === 0) return 0
      const count = this.getRatingCount(star)
      return Math.round((count / this.reviews.length) * 100)
    },

    // 获取进度条颜色
    getProgressColor(star) {
      const colors = {
        5: '#67c23a',
        4: '#409eff',
        3: '#e6a23c',
        2: '#f56c6c',
        1: '#f56c6c'
      }
      return colors[star] || '#dcdfe6'
    },

    // 加载更多评价
    loadMoreReviews() {
      const currentLength = this.displayedReviews.length
      const nextReviews = this.reviews.slice(currentLength, currentLength + this.pageSize)
      this.displayedReviews = [...this.displayedReviews, ...nextReviews]
    },

    // 预览图片
    previewImage(imageUrl) {
      window.open(imageUrl, '_blank')
    },

    addToCart() {
      this.$message.success(`已将【${this.product.name}】加入购物车`)
      // 替换为实际的购物车接口
      request.post('/user/cart/add', { productId: this.product.id, quantity: 1 })
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
          const res = await request.post(`/admin/products/status/${this.product.id}`, {
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

/* 评价区域样式 */
.reviews-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.review-summary {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.summary-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item .label {
  font-size: 14px;
  color: #606266;
  margin-right: 15px;
  font-weight: bold;
  min-width: 80px;
}

.score-text {
  margin-left: 10px;
  font-size: 16px;
  color: #ff9900;
  font-weight: bold;
}

.rating-distribution {
  flex: 1;
}

.distribution-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.distribution-row:last-child {
  margin-bottom: 0;
}

.star-label {
  font-size: 12px;
  color: #909399;
  margin-right: 10px;
  min-width: 30px;
}

.count-text {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
  min-width: 50px;
}

.reviews-list {
  margin-top: 20px;
}

.review-card {
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.review-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.review-card:last-child {
  margin-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 12px;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 3px;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  margin-bottom: 15px;
}

.review-content p {
  margin: 0;
  line-height: 1.8;
  color: #606266;
  font-size: 14px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
  cursor: pointer;
  transition: transform 0.3s;
}

.review-image:hover {
  transform: scale(1.05);
}

.load-more {
  text-align: center;
  margin-top: 20px;
  padding: 20px 0;
}

.no-more {
  color: #909399;
  font-size: 14px;
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

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .distribution-row {
    flex-wrap: wrap;
  }

  .distribution-row .el-progress {
    width: 100% !important;
    margin: 5px 0;
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
