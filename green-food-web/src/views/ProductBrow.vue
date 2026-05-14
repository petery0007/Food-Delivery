<template>
  <div class="product-browse-container" style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <!-- 1. 顶部搜索与筛选区域 -->
    <el-card shadow="never" style="margin-bottom: 20px;">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input
              v-model="searchForm.keywords"
              placeholder="请输入商品名称"
              clearable
              style="width: 200px;"
          ></el-input>
        </el-form-item>
        <el-form-item label="商品类型">
          <el-select
              v-model="searchForm.specification"
              placeholder="全部类型"
              clearable
              style="width: 150px;"
          >
            <el-option label="蔬菜" value="蔬菜"></el-option>
            <el-option label="水果" value="水果"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              icon="el-icon-search"
              @click="onSearchButtonClick"
              :loading="loading"
          >搜索</el-button>
          <el-button @click="onResetButtonClick">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 商品列表（卡片式布局） -->
    <div v-loading="loading" element-loading-text="商品加载中...">
      <el-row :gutter="20" v-if="tableData.length > 0">
        <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="item in tableData" :key="item.id">
          <el-card
              class="product-card"
              shadow="hover"
              :body-style="{ padding: 0 }"
          >
            <!-- 商品图片 -->
            <div class="product-image">
              <img
                  :src="item.imageUrl"
                  :alt="item.name"
                  @error="handleImageError($event)"
                  @click="viewProductInfo(item)"
                  style="cursor: pointer;"
              >
            </div>

            <!-- 商品信息 -->
            <div class="product-info" style="padding: 15px;">
              <h3 class="product-name" :title="item.name" @click="viewProductInfo(item)" style="cursor: pointer;">{{ item.name }}</h3>
              <div class="product-spec">{{ item.specification }}</div>
              <div class="product-price">
                <span class="current-price">¥{{ item.price }}</span>
                <span class="stock">库存: {{ item.stock }}</span>
              </div>
              <div class="product-actions" style="margin-top: 10px;">
                <el-button
                    type="success"
                    size="small"
                    @click="addToCart(item)"
                    :disabled="item.stock <= 0"
                    style="width: 100%;"
                >
                  <i class="el-icon-shopping-cart-2"></i> 加入购物车
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空数据提示 -->
      <el-empty
          description="暂无上架商品"
          v-else
          style="padding: 100px 0;"
      ></el-empty>
    </div>

    <!-- 3. 分页控件 -->
    <div style="margin-top: 30px; text-align: center;">
      <el-pagination
          @current-change="onPageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next, jumper"
          :total="totalCount"
          :hide-on-single-page="true"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ProductBrowse',
  data() {
    return {
      // 搜索筛选表单
      searchForm: {
        keywords: '',
        specification: ''
      },
      queryParams: {
        keywords: '',
        specification: ''
      },
      // 分页参数
      currentPage: 1,
      pageSize: 12, // 用户端每页显示12个商品
      totalCount: 0,
      // 商品列表数据（只包含上架商品）
      tableData: [],
      loading: false,
    }
  },
  mounted() {
    this.loadProductList(this.currentPage, this.pageSize, 'create_time', this.queryParams.keywords, this.queryParams.specification);
  },
  methods: {
    // 加载商品列表（只请求上架商品）
    async loadProductList(page, pageSize, sortBy, keywords, specification) {
      this.loading = true;
      try {
        // 检查 token 是否存在
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.warning('请先登录');
          this.$router.push('/login');
          return;
        }

        // 根据是否有搜索条件选择接口
        let targetUrl = '';
        let params = {};

        if (keywords || specification) {
          // 有搜索条件，使用筛选接口
          targetUrl = '/user/products/list';
          params = {
            page: page || this.currentPage,
            pageSize: pageSize || this.pageSize,
            keywords: keywords || '',
            specification: specification || ''
          };
        } else {
          // 无搜索条件，使用普通分页接口
          targetUrl = '/user/products';
          params = {
            page: page || this.currentPage,
            pageSize: pageSize || this.pageSize
          };
        }

        console.log('请求接口:', targetUrl);
        console.log('请求参数:', params);

        const res = await request.get(targetUrl, { params });

        console.log('后端返回数据:', res);

        // 后端返回格式: { list: [...], total: ... }
        if (res.data && res.data.list) {
          this.tableData = res.data.list;
          this.totalCount = res.data.total || 0;
          console.log('商品列表加载成功，共', this.totalCount, '条记录');
        } else {
          console.warn('数据格式异常:', res);
          this.tableData = [];
          this.totalCount = 0;
        }
      } catch (error) {
        console.error('商品列表加载失败：', error);
        console.error('错误详情:', error.response);

        // 如果是 401 错误，跳转到登录页
        if (error.response && error.response.status === 401) {
          this.$message.warning('登录已过期，请重新登录');
          localStorage.clear();
          this.$router.push('/login');
        } else {
          this.$message.error('商品加载失败，请稍后重试');
        }

        this.tableData = [];
        this.totalCount = 0;
      } finally {
        this.loading = false;
      }
    },


    // 搜索按钮
    onSearchButtonClick() {
      this.queryParams = {
        keywords: this.searchForm.keywords,
        specification: this.searchForm.specification
      };
      this.currentPage = 1;
      this.loadProductList(this.currentPage, this.pageSize, 'create_time', this.queryParams.keywords, this.queryParams.specification);

    },

    // 重置按钮
    onResetButtonClick() {
      this.searchForm = {
        keywords: '',
        specification: ''
      };
      this.currentPage = 1;
      this.loadProductList();
    },

    // 分页切换
    onPageChange(newPage) {
      this.currentPage = newPage;
      this.loadProductList();
      // 滚动到页面顶部
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    // 查看商品详情
    viewProductInfo(product) {
      this.$router.push({
        name: 'ProductInfoUser',
        params: { id: product.id },
        query:{ from: 'shop'}
      })

      // 可选：调用接口增加浏览次数
      // request.post(`/products/click/${product.id}`);
    },

    // 加入购物车（唯一操作功能）
    addToCart(product) {
      // 这里替换为你的实际购物车接口
      this.$message.success(`已将【${product.name}】加入购物车`);

      // 示例：调用真实购物车接口
      // request.post('/cart/add', { productId: product.id, quantity: 1 })
      //   .then(() => {
      //     this.$message.success('加入购物车成功');
      //   })
      //   .catch(() => {
      //     this.$message.error('加入购物车失败，请稍后重试');
      //   });
    },

    // 图片加载失败处理
    handleImageError(event) {
      // 使用ElementUI默认占位图
      event.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png';
    }
  }
}
</script>

<style scoped>
/* 商品卡片样式 */
.product-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin: 0 0 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-name:hover {
  color: #409eff;
}

.product-spec {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.stock {
  font-size: 12px;
  color: #909399;
}

/* 详情弹窗样式 */
.detail-image {
  width: 100%;
  height: 300px;
  border-radius: 5px;
  overflow: hidden;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-name {
  font-size: 22px;
  color: #303133;
  margin: 0 0 15px 0;
}

.detail-spec, .detail-click {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.detail-price {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 5px;
}

.detail-description h3 {
  font-size: 16px;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.description-content {
  line-height: 1.6;
  color: #606266;
}
</style>