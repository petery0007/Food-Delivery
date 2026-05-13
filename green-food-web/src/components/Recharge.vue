<template>
  <!-- append-to-body 属性非常关键：它保证弹窗挂载在最外层，未来无论在哪个层级深的页面调用，都不会被遮挡 -->
  <el-dialog
      title="账户充值"
      :visible.sync="visible"
      width="350px"
      @close="resetForm"
      append-to-body>

    <div style="text-align: center; margin-bottom: 20px; color: #666;">
      支持微信 / 支付宝在线安全支付
    </div>

    <el-form :model="form" :rules="rules" ref="rechargeForm" label-width="100px">
      <el-form-item label="充值金额(元)" prop="amount">
        <!-- 使用数字输入框，限制最小充值1元，保留两位小数 -->
        <el-input-number
            v-model="form.amount"
            :min="1"
            :max="50000"
            :precision="2"
            :step="50"
            style="width: 100%;">
        </el-input-number>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="success" @click="submitRecharge" :loading="loading" icon="el-icon-wallet">
        确认支付 ￥{{ form.amount }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'RechargeModal',
  data() {
    return {
      visible: false, // 控制弹窗显示隐藏
      loading: false,
      form: {
        amount: 50 // 默认充值金额
      },
      rules: {
        amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }]
      }
    }
  },
  methods: {
    // 【核心暴露方法】：供父组件调用来打开这个弹窗
    open() {
      this.visible = true;
    },

    // 关闭时重置表单
    resetForm() {
      if (this.$refs.rechargeForm) {
        this.$refs.rechargeForm.resetFields();
      }
      this.form.amount = 50;
    },

    // 提交充值请求
    submitRecharge() {
      this.$refs.rechargeForm.validate(valid => {
        if (valid) {
          this.loading = true;
          // 假设后端的充值接口是 POST /user/recharge
          request.post('/user/recharge', {
            amount: this.form.amount
          }).then(res => {
            this.loading = false;
            this.visible = false;
            this.$message.success(`成功充值 ${this.form.amount} 元！`);

            // 【关键】：触发一个自定义事件告诉父组件“充值成功了”
            // 父组件收到这个通知后，就可以去刷新页面的余额显示了
            this.$emit('success');
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
}
</script>