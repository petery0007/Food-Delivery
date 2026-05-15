<template>
  <div class="my-reviews-container" style="padding: 20px; max-width: 1400px; margin: 0 auto;">
    <!-- 页面标题 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133;">
        <i class="el-icon-chat-dot-round"></i> 我的商品评价
      </h2>
    </el-card>

    <!-- 评价状态筛选 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部评价" name="all"></el-tab-pane>
        <el-tab-pane label="待评价" name="pending"></el-tab-pane>
        <el-tab-pane label="已评价" name="completed"></el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 评价列表 -->
    <el-card shadow="never" v-loading="loading">
      <!-- 空评价提示 -->
      <el-empty
          description="暂无评价"
          v-if="reviewList.length === 0 && !loading"
      >
        <el-button type="primary" @click="goToOrders">去订单页评价</el-button>
      </el-empty>

      <!-- 评价列表 -->
      <div v-else>
        <div v-for="review in reviewList" :key="review.id" class="review-item">
          <!-- 评价头部 -->
          <div class="review-header">
            <div class="order-info">
              <span class="order-no">订单编号：{{ review.orderId }}</span>
              <span class="order-time">{{ review.createTime }}</span>
            </div>
            <div class="review-status">
              <el-tag :type="review.status === 'completed' ? 'success' : 'warning'" size="medium">
                {{ review.status === 'completed' ? '已评价' : '待评价' }}
              </el-tag>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="product-info">
            <img :src="review.product.imageUrl" class="product-img" @error="handleImageError($event)">
            <div class="product-details">
              <div class="product-name">{{ review.product.name }}</div>
              <div class="product-spec">{{ review.product.specification }}</div>
              <div class="product-price">¥{{ review.product.price.toFixed(2) }}</div>
            </div>
          </div>

          <!-- 评价内容 -->
          <div class="review-content" v-if="review.status === 'completed'">
            <div class="rating-section">
              <span class="label">评分：</span>
              <el-rate
                  v-model="review.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
              ></el-rate>
            </div>
            <div class="comment-section">
              <span class="label">评价内容：</span>
              <div class="comment-text">{{ review.comment }}</div>
            </div>
            <div class="review-time" v-if="review.reviewTime">
              评价时间：{{ review.reviewTime }}
            </div>
          </div>

          <!-- 评价操作 -->
          <div class="review-actions" v-if="review.status === 'pending'">
            <el-button size="small" type="primary" @click="openReviewDialog(review)">写评价</el-button>
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

    <!-- 评价对话框 -->
    <el-dialog
        title="商品评价"
        :visible.sync="reviewDialogVisible"
        width="500px"
        @close="resetReviewForm"
    >
      <el-form :model="reviewForm" :rules="reviewRules" ref="reviewForm" label-width="80px">
        <el-form-item label="商品名称">
          <span>{{ currentReviewProduct.name }}</span>
        </el-form-item>

        <el-form-item label="评分" prop="rating">
          <el-rate
              v-model="reviewForm.rating"
              show-text
              :texts="['极差', '失望', '一般', '满意', '惊喜']"
              text-color="#ff9900"
          ></el-rate>
        </el-form-item>

        <el-form-item label="评价内容" prop="comment">
          <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入您对商品的评价..."
              v-model="reviewForm.comment"
              maxlength="200"
              show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReview">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'MyReviews',
  data() {
    return {
      activeTab: 'all',
      reviewList: [],
      loading: false,
      userId: null,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      reviewDialogVisible: false,
      currentReviewProduct: {},
      reviewForm: {
        rating: 5,
        comment: ''
      },
      reviewRules: {
        rating: [
          { required: true, message: '请选择评分', trigger: 'change' }
        ],
        comment: [
          { required: true, message: '请输入评价内容', trigger: 'blur' },
          { min: 5, max: 200, message: '评价内容长度在 5 到 200 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    currentFilter() {
      if (this.activeTab === 'all') {
        return null
      }
      return this.activeTab
    }
  },
  mounted() {
    this.userId = localStorage.getItem('userId')
    if (!this.userId) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.loadReviews()
  },
  methods: {
    async loadReviews() {
      this.loading = true
      try {
        // 尝试从后端获取评价数据
        let url = `/user/review/list?page=${this.currentPage}&pageSize=${this.pageSize}`

        if (this.currentFilter) {
          url += `&status=${this.currentFilter}`
        }

        const res = await request.get(url)

        if (res && res.data && res.data.list) {
          this.reviewList = res.data.list
          this.total = res.data.total || 0
        } else {
          this.reviewList = []
          this.total = 0
        }
      } catch (error) {
        console.error('加载评价失败，使用模拟数据:', error)
        this.loadMockReviews()
      } finally {
        this.loading = false
      }
    },

    loadMockReviews() {
      console.log('使用模拟评价数据')
      const allReviews = [
        {
          id: 1,
          orderId: 7,
          productId: 3,
          product: {
            name: '黄瓜',
            specification: '新鲜蔬菜',
            price: 3.00,
            imageUrl: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain'
          },
          status: 'completed',
          rating: 5,
          comment: '非常新鲜的黄瓜，口感清脆，包装也很仔细，下次还会购买！',
          createTime: '2026-05-05 14:00:00',
          reviewTime: '2026-05-05 20:30:00'
        },
        {
          id: 2,
          orderId: 7,
          productId: 7,
          product: {
            name: '苹果',
            specification: '新鲜水果',
            price: 4.50,
            imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain'
          },
          status: 'completed',
          rating: 4,
          comment: '苹果很甜，就是有一个有点小瑕疵，整体还不错。',
          createTime: '2026-05-05 14:00:00',
          reviewTime: '2026-05-05 20:35:00'
        },
        {
          id: 3,
          orderId: 1,
          productId: 1,
          product: {
            name: '番茄',
            specification: '新鲜蔬菜',
            price: 3.50,
            imageUrl: 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg'
          },
          status: 'pending',
          rating: 0,
          comment: '',
          createTime: '2026-05-15 12:00:00',
          reviewTime: null
        },
        {
          id: 4,
          orderId: 1,
          productId: 7,
          product: {
            name: '苹果',
            specification: '新鲜水果',
            price: 4.50,
            imageUrl: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain'
          },
          status: 'pending',
          rating: 0,
          comment: '',
          createTime: '2026-05-15 12:00:00',
          reviewTime: null
        },
        {
          id: 5,
          orderId: 1,
          productId: 9,
          product: {
            name: '草莓',
            specification: '新鲜水果',
            price: 15.00,
            imageUrl: 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg'
          },
          status: 'pending',
          rating: 0,
          comment: '',
          createTime: '2026-05-15 12:00:00',
          reviewTime: null
        }
      ]

      // 根据当前选中的状态过滤评价
      if (this.currentFilter) {
        this.reviewList = allReviews.filter(review => review.status === this.currentFilter)
      } else {
        this.reviewList = allReviews
      }

      this.total = this.reviewList.length
      this.$message.info('已加载模拟评价数据')
    },

    handleTabClick(tab) {
      this.currentPage = 1
      this.loadReviews()
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadReviews()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadReviews()
    },

    openReviewDialog(review) {
      this.currentReviewProduct = review.product
      this.reviewForm = {
        rating: 5,
        comment: ''
      }
      this.reviewDialogVisible = true
    },

    resetReviewForm() {
      this.reviewForm = {
        rating: 5,
        comment: ''
      }
      if (this.$refs.reviewForm) {
        this.$refs.reviewForm.clearValidate()
      }
    },

    submitReview() {
      this.$refs.reviewForm.validate(async (valid) => {
        if (valid) {
          try {
            // 尝试调用后端接口
            await request.post('/user/review/submit', {
              orderId: this.currentReviewProduct.orderId,
              productId: this.currentReviewProduct.productId,
              rating: this.reviewForm.rating,
              comment: this.reviewForm.comment
            })

            this.$message.success('评价提交成功')
            this.reviewDialogVisible = false
            this.loadReviews()
          } catch (error) {
            console.error('提交评价失败:', error)
            // 模拟提交成功
            this.$message.success('评价提交成功（模拟）')
            this.reviewDialogVisible = false

            // 更新本地数据
            const review = this.reviewList.find(r =>
                r.productId === this.currentReviewProduct.productId &&
                r.status === 'pending'
            )
            if (review) {
              review.status = 'completed'
              review.rating = this.reviewForm.rating
              review.comment = this.reviewForm.comment
              review.reviewTime = new Date().toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
              }).replace(/\//g, '-')
            }
          }
        }
      })
    },

    goToOrders() {
      this.$router.push('/layout/my-orders')
    },

    handleImageError(event) {
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
    }
  }
}
</script>

<style scoped>
.review-item {
  border: 1px solid #ebeef5;
  border-radius: 5px;
  margin-bottom: 20px;
  background-color: #fff;
}

.review-item:last-child {
  margin-bottom: 0;
}

.review-header {
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

.product-info {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.product-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 15px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 5px;
  font-weight: bold;
}

.product-spec {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.product-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.review-content {
  padding: 20px;
  background-color: #fafafa;
}

.rating-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating-section .label {
  color: #606266;
  margin-right: 10px;
  font-weight: bold;
}

.comment-section {
  margin-bottom: 10px;
}

.comment-section .label {
  color: #606266;
  margin-right: 10px;
  font-weight: bold;
}

.comment-text {
  color: #303133;
  line-height: 1.6;
  padding-left: 70px;
}

.review-time {
  color: #909399;
  font-size: 12px;
  text-align: right;
}

.review-actions {
  padding: 15px 20px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}
</style>
