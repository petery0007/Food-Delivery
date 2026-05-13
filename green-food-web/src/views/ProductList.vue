<template>
  <div class="product-container" style="padding: 20px;">
    <!-- 1. 顶部搜索区域 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keywords" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        <el-form-item label="商品类型">
          <el-select v-model="searchForm.specification" placeholder="请选择类型" clearable>
            <el-option label="蔬菜" value="蔬菜"></el-option>
            <el-option label="水果" value="水果"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSearchButtonClick">查询</el-button>
          <!-- 【修改 1】：给新增按钮绑定点击事件 -->
          <el-button type="success" icon="el-icon-plus" @click="openAddDialog">新增商品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 商品数据表格 (对照图3-1的表格结构) -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
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

    <!-- 【新增】：新增商品弹窗 -->
    <el-dialog title="新增商品" :visible.sync="addDialogVisible" width="600px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入商品名称"></el-input>
        </el-form-item>

        <el-form-item label="商品类型" prop="specification">
          <el-select v-model="addForm.specification" placeholder="请选择商品类型" style="width: 100%;">
            <el-option label="新鲜蔬菜" value="新鲜蔬菜"></el-option>
            <el-option label="有机蔬菜" value="有机蔬菜"></el-option>
            <el-option label="新鲜水果" value="新鲜水果"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="生产厂家" prop="producer">
          <el-input v-model="addForm.producer" placeholder="请输入生产厂家名称"></el-input>
        </el-form-item>

        <el-form-item label="商品照片URL" prop="imageUrl">
          <el-input v-model="addForm.imageUrl" placeholder="请输入图片网络链接地址 (如: http://...)"></el-input>
          <!-- 实时预览小图 -->
          <div v-if="addForm.imageUrl" style="margin-top: 10px;">
            <img :src="addForm.imageUrl" style="max-width: 100px; max-height: 100px; border-radius: 5px; border: 1px solid #ddd;" alt="预览失败">
          </div>
        </el-form-item>

        <!-- 将价格和库存放在一行显示 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="现价(RMB)" prop="price">
              <el-input-number v-model="addForm.price" :min="0.01" :precision="2" :step="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品库存" prop="stock">
              <el-input-number v-model="addForm.stock" :min="0" :step="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="是否上架" prop="status">
          <el-radio-group v-model="addForm.status">
            <el-radio label="上架">直接上架</el-radio>
            <el-radio label="下架">暂不上架 (放入仓库)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="商品简介" prop="description">
          <el-input type="textarea" :rows="3" v-model="addForm.description" placeholder="请输入简单的商品描述..."></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="success" @click="submitAddProduct" :loading="submitLoading">确 认 添 加</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      searchForm: {
        keywords: '',
        specification: ''
      },
      queryParams: {
        keywords: '',
        specification: ''
      },
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      tableData: [],
      loading: false,

      // 【新增】：控制弹窗显示与加载状态
      addDialogVisible: false,
      submitLoading: false,

      // 【新增】：新增商品表单的数据载体
      addForm: {
        name: '',
        specification: '',
        imageUrl: '',
        price: 1.00,
        stock: 100,
        clickCount: 0,
        status: '上架',
        description: '',
        producer: ''
      },

      // 【新增】：严格的表单校验规则
      addRules: {
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        specification: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
        imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }],
        price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
        stock: [{ required: true, message: '请输入商品库存', trigger: 'blur' }],
        status: [{ required: true, message: '请选择上架状态', trigger: 'change' }],
        producer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.initializeProductPage();
  },
  methods: {
    initializeProductPage() {
      this.loadProductData(this.currentPage, this.pageSize, 'create_time', this.queryParams.keywords, this.queryParams.specification);
    },

    loadProductData(page, pageSize, sortBy, keywords, specification) {
      this.loading = true;
      let targetUrl = keywords || specification ? '/products/list' : '/products';

      request.get(targetUrl, {
        params: {
          page: page,
          pageSize: pageSize,
          keywords: keywords,
          specification: specification
        }
      }).then(res => {
        this.tableData = res.data.list;
        this.totalCount = res.data.total;
        this.loading = false;
      }).catch(err => {
        console.error("请求失败", err);
        this.loading = false;
      });
    },

    onSearchButtonClick() {
      this.queryParams = {
        keywords: this.searchForm.keywords,
        specification: this.searchForm.specification
      };
      this.currentPage = 1;
      this.initializeProductPage();
    },

    onPageChange(newPage) {
      this.currentPage = newPage;
      this.initializeProductPage();
    },

    toggleProductStatus(row) {
      const newStatus = row.status === '上架' ? '下架' : '上架';
      this.$confirm(`确定要将该商品 ${newStatus} 吗?`, '提示').then(() => {
        request.post(`/products/status/${row.id}`, { status: newStatus }).then(res => {
          this.$message.success('状态更新成功');
          this.initializeProductPage();
        });
      }).catch(() => {});
    },

    deleteProduct(productId) {
      this.$confirm('确定要删除吗?', '警告', { type: 'error' }).then(() => {
        request.delete(`/products/${productId}`).then(res => {
          this.$message.success('删除成功');
          this.initializeProductPage();
        });
      }).catch(() => {});
    },

    // ================= 【新增方法开始】 =================

    // 1. 打开新增商品弹窗
    openAddDialog() {
      this.addDialogVisible = true;
      // 注意：表单清空交给 @close 绑定的 resetAddForm 处理了，这里直接打开即可
    },

    // 2. 提交新增商品表单
    submitAddProduct() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;

          // 依据之前文档的接口，向 /products/add 发送新增请求
          // 这里使用 /admin/products 符合 PDF 接口文档规范
          request.post('/products/add', this.addForm).then(res => {
            this.submitLoading = false;
            this.addDialogVisible = false; // 关闭弹窗
            this.$message.success('商品添加成功！');

            // 添加成功后，回到第一页并刷新列表，让用户直接看到新加的商品
            this.currentPage = 1;
            this.initializeProductPage();

          }).catch(err => {
            // 请求失败时，也要解除按钮的 loading 状态
            this.submitLoading = false;
          });
        } else {
          this.$message.warning('请检查必填项是否全部填写正确');
          return false;
        }
      });
    },

    // 3. 关闭弹窗时，自动重置表单，避免下次打开残留上一次的数据
    resetAddForm() {
      if (this.$refs.addForm) {
        this.$refs.addForm.resetFields();
      }
      // 将数据重置为默认值
      this.addForm.price = 1.00;
      this.addForm.stock = 100;
      this.addForm.clickCount = 0;
      this.addForm.status = '上架';
      this.addForm.description = '';
      this.addForm.producer = '';
    }

    // ================= 【新增方法结束】 =================
  }
}
</script>