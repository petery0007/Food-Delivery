<template>
  <div class="notice-container" style="padding: 20px;">
    <!-- 1. 顶部搜索区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.keywords" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="显示状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="显示" value="1"></el-option>
            <el-option label="隐藏" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
          <el-button type="success" icon="el-icon-plus">新增公告</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 公告数据表格 (严格对照商品表格结构仿写) -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="索引" width="60" align="center"></el-table-column>
        <el-table-column prop="title" label="公告标题" align="center"></el-table-column>
        <el-table-column prop="content" label="公告内容" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" align="center"></el-table-column>
        <el-table-column prop="sort" label="排序权重" align="center"></el-table-column>
        <el-table-column prop="status" label="显示状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="250">
          <template slot-scope="scope">
            <el-button size="mini" type="info" @click="toggleNoticeStatus(scope.row)">显隐状态</el-button>
            <el-button size="mini" type="primary">详情</el-button>
            <el-button size="mini" type="danger" @click="deleteNotice(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 3. 分页控件 (和商品分页完全一致) -->
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
// 1. 引入请求工具（和商品代码完全一致）
import request from '@/utils/request'

export default {
  data() {
    return {
      // 搜索表单（公告专属字段）
      searchForm: {
        keywords: '',
        status: ''
      },
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      tableData: [],
      loading: false
    }
  },
  mounted() {
    // 初始化页面（和商品代码格式一致）
    this.initNoticePage();
  },
  methods: {
    // 初始化公告页面
    initNoticePage() {
      this.loadNoticeData(this.currentPage, this.pageSize, this.searchForm.keywords, this.searchForm.status);
    },

    // 加载公告数据（核心请求，仿写商品加载方法）
    loadNoticeData(page, pageSize, keywords, status) {
      this.loading = true;

      // 请求公告列表接口
      request.get('/notice/list', {
        params: {
          page: page,
          limit: pageSize,
          keywords: keywords,
          status: status
        }
      }).then(res => {
        this.tableData = res.data.list;
        this.totalCount = res.data.total;
        this.loading = false;
      }).catch(err => {
        console.error("公告请求失败", err);
        this.loading = false;
      });
    },

    // 查询按钮点击（和商品代码一致）
    onSearchButtonClick() {
      this.currentPage = 1;
      this.initNoticePage();
    },

    // 分页切换（和商品代码一致）
    onPageChange(newPage) {
      this.currentPage = newPage;
      this.initNoticePage();
    },

    // 切换公告显隐状态（仿写商品上下架）
    toggleNoticeStatus(row) {
      const newStatus = row.status === 1 ? 2 : 1;
      this.$confirm(`确定要${newStatus === 1 ? '显示' : '隐藏'}该公告吗?`, '提示').then(() => {
        // 修改公告状态接口
        request.post(`/notice/status/${row.id}`, { status: newStatus }).then(res => {
          this.$message.success('状态更新成功');
          this.initNoticePage();
        });
      }).catch(() => {});
    },

    // 删除公告（仿写商品删除）
    deleteNotice(noticeId) {
      this.$confirm('确定要删除该公告吗?', '警告', { type: 'error' }).then(() => {
        // 删除公告接口
        request.delete(`/notice/${noticeId}`).then(res => {
          this.$message.success('删除成功');
          this.initNoticePage();
        });
      }).catch(() => {});
    }
  }
}
</script>
