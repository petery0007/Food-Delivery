<template>
  <div class="product-container" style="padding: 20px;">
    <!-- 1. 顶部搜索区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keywords" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        <el-form-item label="商品类型">
          <el-select v-model="searchForm.categoryId" placeholder="请选择类型" clearable>
            <el-option label="蔬菜" value="1"></el-option>
            <el-option label="水果" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
          <el-button type="success" icon="el-icon-plus">新增商品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 商品数据表格 (对照图3-1的表格结构) -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column type="index" label="索引" width="60" align="center"></el-table-column>
        <el-table-column prop="name" label="商品名称" align="center"></el-table-column>
        <el-table-column label="商品照片" align="center">
          <template slot-scope="scope">
            <img :src="scope.row.imageUrl" style="width: 50px; height: 50px; border-radius: 5px;">
          </template>
        </el-table-column>
        <el-table-column prop="specification" label="商品类型" align="center"></el-table-column>
        <el-table-column prop="stock" label="商品库存" align="center"></el-table-column>
        <el-table-column prop="price" label="现价(RMB)" align="center"></el-table-column>
        <el-table-column prop="clickCount" label="点击次数" align="center"></el-table-column>
        <el-table-column prop="status" label="是否上架" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '上架' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="250">
          <template slot-scope="scope">
            <el-button size="mini" type="info" @click="toggleProductStatus(scope.row)">上下架</el-button>
            <el-button size="mini" type="primary">详情</el-button>
            <el-button size="mini" type="danger" @click="deleteProduct(scope.row.id)">删除</el-button>
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
  </div>
</template>

<script>
// 1. 引入我们刚刚封装好的请求工具
import request from '@/utils/request'

export default {
  data() {
    return {
      searchForm: {
        keywords: '',
        categoryId: ''
      },
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      tableData: [],
      loading: false // 增加一个加载状态
    }
  },
  mounted() {
    this.initializeProductPage();
  },
  methods: {
    initializeProductPage() {
      this.loadProductData(this.currentPage, this.pageSize, 'create_time', this.searchForm.keywords, this.searchForm.categoryId);
    },

    // 【修改核心】：真实的请求方法
    loadProductData(page, pageSize, sortBy, keywords, categoryId) {
      this.loading = true; // 开启表格加载动画 (需要在el-table上加 v-loading="loading")

      // 2. 发送真实的 GET 请求到后端的 /products/list 接口
      request.get('/products/list', {
        params: {
          page: page,
          limit: pageSize,
          keywords: keywords,
          categoryId: categoryId
        }
      }).then(res => {
        // 3. 假设后端返回的数据结构是 { code: 200, data: { total: 100, list: [...] } }
        this.tableData = res.data.list;
        this.totalCount = res.data.total;
        this.loading = false;
      }).catch(err => {
        console.error("请求失败", err);
        this.loading = false;
      });
    },

    onSearchButtonClick() {
      this.currentPage = 1;
      this.initializeProductPage();
    },

    onPageChange(newPage) {
      this.currentPage = newPage;
      this.initializeProductPage();
    },

    toggleProductStatus(row) {
      // 真实请求示例：
      const newStatus = row.status === '上架' ? '下架' : '上架';
      this.$confirm(`确定要将该商品 ${newStatus} 吗?`, '提示').then(() => {

        // 发送真实 POST 请求去修改状态
        request.post(`/products/status/${row.id}`, { status: newStatus }).then(res => {
          this.$message.success('状态更新成功');
          this.initializeProductPage(); // 重新加载数据
        });

      }).catch(() => {});
    },

    deleteProduct(productId) {
      this.$confirm('确定要删除吗?', '警告', { type: 'error' }).then(() => {

        // 发送真实 DELETE 请求
        request.delete(`/products/${productId}`).then(res => {
          this.$message.success('删除成功');
          this.initializeProductPage();
        });

      }).catch(() => {});
    }
  }
}
</script>