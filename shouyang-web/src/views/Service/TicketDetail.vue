<template>
  <div class="ticket-detail-page">
    <div class="page-container">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <el-button text @click="$router.push('/service/ticket')">
          <el-icon><ArrowLeft /></el-icon>
          返回票务列表
        </el-button>
      </div>

      <div v-if="loading" class="detail-card">
        <el-skeleton :rows="6" animated class="detail-skeleton" />
      </div>

      <template v-else-if="ticket">
        <div class="detail-card">
          <div class="detail-main">
            <!-- 左侧大图 -->
            <div class="cover-wrap">
              <el-image
                :src="ticket.coverImage || defaultCover"
                fit="cover"
                class="cover-img"
                :preview-src-list="[ticket.coverImage || defaultCover]"
                preview-teleported
              />
            </div>

            <!-- 右侧信息 -->
            <div class="info-wrap">
              <div class="info-header">
                <h1 class="ticket-name">{{ ticket.name }}</h1>
                <span class="price-tag" :class="{ free: isFree }">
                  {{ isFree ? '免费' : '¥' + ticket.price }}
                  <em v-if="!isFree" class="price-unit">/张</em>
                </span>
              </div>

              <div class="info-list">
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ ticket.venue || '待定' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDateTime(ticket.startTime) }} ~ {{ formatDateTime(ticket.endTime) }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Collection /></el-icon>
                  <span>总票数 {{ ticket.totalCount }} 张</span>
                </div>
              </div>

              <!-- 库存进度条 -->
              <div class="stock-block">
                <div class="stock-header">
                  <span class="stock-label">预订进度</span>
                  <span class="stock-num" :class="{ low: isLowStock }">
                    剩余 <b>{{ ticket.remainCount }}</b> / {{ ticket.totalCount }} 张
                  </span>
                </div>
                <el-progress
                  :percentage="soldPercent"
                  :stroke-width="10"
                  :color="progressColor"
                  :show-text="false"
                />
                <div class="stock-tags">
                  <el-tag v-if="statusInfo.label" :type="statusInfo.type" size="small" effect="light">
                    {{ statusInfo.label }}
                  </el-tag>
                  <el-tag v-if="isLowStock && !isSoldOut" type="warning" size="small" effect="light">
                    余票紧张
                  </el-tag>
                </div>
              </div>

              <!-- 预订操作 -->
              <div class="order-bar">
                <el-button
                  type="primary"
                  size="large"
                  class="order-btn"
                  :disabled="!canOrder"
                  @click="handleOrderClick"
                >
                  {{ orderButtonText }}
                </el-button>
                <p v-if="isOrdered" class="ordered-tip">
                  <el-icon><CircleCheck /></el-icon>
                  您已预订过该票务，可在用户中心"我的门票"中查看
                </p>
                <p v-else-if="!canOrder" class="order-tip">{{ orderButtonTip }}</p>
              </div>
            </div>
          </div>

          <!-- 详情描述 -->
          <div class="desc-block">
            <h3 class="desc-title">详情介绍</h3>
            <div class="desc-content" v-html="ticket.description || '暂无详情介绍'"></div>
          </div>
        </div>
      </template>

      <!-- 不存在 -->
      <el-empty v-else description="票务不存在或已下架" class="empty-state" />

      <!-- 预订表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        title="门票预订"
        width="480px"
        :close-on-click-modal="false"
        @closed="resetForm"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="票务名称">
            <span class="form-static">{{ ticket?.name }}</span>
          </el-form-item>
          <el-form-item label="单价">
            <span class="form-static price">{{ isFree ? '免费' : '¥' + ticket?.price + ' /张' }}</span>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入取票人姓名" maxlength="20" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="数量" prop="quantity">
            <el-input-number
              v-model="form.quantity"
              :min="1"
              :max="ticket?.remainCount || 1"
              step-strictly
            />
            <span class="quantity-tip">最多可订 {{ ticket?.remainCount }} 张</span>
          </el-form-item>
        </el-form>
        <div class="dialog-total">
          合计：
          <b class="total-price">{{ isFree ? '免费' : '¥' + (ticket?.price * form.quantity).toFixed(2) }}</b>
        </div>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">确认预订</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Location, Clock, Collection, CircleCheck } from '@element-plus/icons-vue'
import { getTicketDetail, orderTicket } from '@/api/service'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const ticket = ref(null)
const isOrdered = ref(false)
const defaultCover = 'https://picsum.photos/seed/ticket-cover/800/480'

// 预订对话框
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  name: '',
  phone: '',
  quantity: 1
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 是否免费
const isFree = computed(() => !ticket.value || Number(ticket.value.price) === 0)

// 余票率（已预订百分比）
const soldPercent = computed(() => {
  if (!ticket.value || !ticket.value.totalCount) return 0
  const remain = ticket.value.remainCount || 0
  return Math.min(100, Math.max(0, Math.round(remain / ticket.value.totalCount * 100)))
})

const isSoldOut = computed(() => !ticket.value || (ticket.value.remainCount || 0) <= 0)
const isLowStock = computed(() => !isSoldOut.value && (ticket.value?.remainCount || 0) <= 10)

// 票务状态
const statusInfo = computed(() => {
  if (!ticket.value) return { label: '', type: 'info' }
  const now = Date.now()
  const start = ticket.value.startTime ? new Date(ticket.value.startTime).getTime() : 0
  const end = ticket.value.endTime ? new Date(ticket.value.endTime).getTime() : 0
  if (end && now > end) return { label: '已结束', type: 'info' }
  if (start && now >= start && now <= end) return { label: '进行中', type: 'success' }
  if (start && now < start) {
    // 48 小时内开始视为即将开场
    if (start - now < 48 * 3600 * 1000) return { label: '即将开场', type: 'warning' }
    return { label: '未开始', type: 'primary' }
  }
  return { label: '', type: 'info' }
})

const isEnded = computed(() => statusInfo.value.label === '已结束')

// 是否可预订
const canOrder = computed(() => {
  if (!ticket.value) return false
  return !isEnded.value && !isSoldOut.value
})

const orderButtonText = computed(() => {
  if (isSoldOut.value) return '已售罄'
  if (isEnded.value) return '已结束'
  return '立即预订'
})

const orderButtonTip = computed(() => {
  if (isSoldOut.value) return '该票务已售罄，请关注其他票务'
  if (isEnded.value) return '该票务已结束预订'
  return ''
})

// 进度条颜色（余票越多越绿）
const progressColor = computed(() => {
  if (isSoldOut.value) return '#c0c4cc'
  if (isLowStock.value) return '#e6a23c'
  return '#67c23a'
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return '待定'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getTicketDetail(route.params.id)
    ticket.value = res.data || null
  } catch (e) {
    console.error('加载票务详情失败:', e)
    ticket.value = null
  } finally {
    loading.value = false
  }
}

const handleOrderClick = () => {
  // 未登录跳转登录页，携带回跳地址
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再预订')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await orderTicket({
        ticketId: ticket.value.id,
        name: form.value.name,
        phone: form.value.phone,
        quantity: form.value.quantity
      })
      ElMessage.success('预订成功！可在用户中心"我的门票"中查看')
      dialogVisible.value = false
      isOrdered.value = true
      // 刷新详情以更新余票
      loadDetail()
    } catch (e) {
      // 错误提示由 request 拦截器统一处理
    } finally {
      submitting.value = false
    }
  })
}

const resetForm = () => {
  form.value = { name: '', phone: '', quantity: 1 }
  formRef.value?.clearValidate()
}

onMounted(() => { loadDetail() })
</script>

<style lang="scss" scoped>
.ticket-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}
.breadcrumb { margin-bottom: 16px; }

.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  border: 1px solid #eceff1;
}
.detail-skeleton { padding: 20px; }

.detail-main {
  display: flex;
  gap: 28px;
}
.cover-wrap {
  width: 440px;
  flex-shrink: 0;
  .cover-img {
    width: 100%;
    height: 300px;
    border-radius: 10px;
    background: #e0e6ed;
  }
}

.info-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.info-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}
.ticket-name {
  flex: 1;
  font-size: 24px;
  font-weight: 700;
  color: #1a2a4a;
  margin: 0;
  line-height: 1.4;
}
.price-tag {
  flex-shrink: 0;
  padding: 6px 16px;
  border-radius: 20px;
  background: #fef0f0;
  color: #f56c6c;
  font-size: 22px;
  font-weight: 700;
  .price-unit { font-size: 12px; font-style: normal; font-weight: 400; }
  &.free { background: #f0f9eb; color: #67c23a; }
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  margin-bottom: 18px;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #546e7a;
  .el-icon { color: #90a4ae; }
}

.stock-block { margin-bottom: 20px; }
.stock-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.stock-label { font-size: 14px; font-weight: 600; color: #1a2a4a; }
.stock-num {
  font-size: 13px;
  color: #78909c;
  b { color: #1a2a4a; font-size: 16px; }
  &.low b { color: #e6a23c; }
}
.stock-tags {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.order-bar { margin-top: auto; }
.order-btn {
  width: 200px;
  font-weight: 600;
}
.order-tip, .ordered-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 10px 0 0;
  font-size: 13px;
  color: #90a4ae;
}
.ordered-tip { color: #67c23a; }

.desc-block {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #f0f2f5;
}
.desc-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 14px;
}
.desc-content {
  font-size: 14px;
  line-height: 1.9;
  color: #546e7a;
  :deep(p) { margin: 0 0 10px; }
}

.empty-state {
  background: #fff;
  border-radius: 10px;
  padding: 60px 0;
}

.form-static {
  color: #1a2a4a;
  font-weight: 500;
  &.price { color: #f56c6c; }
}
.quantity-tip {
  margin-left: 12px;
  font-size: 12px;
  color: #90a4ae;
}
.dialog-total {
  text-align: right;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 14px;
  color: #546e7a;
  .total-price { color: #f56c6c; font-size: 18px; }
}

@media (max-width: 768px) {
  .detail-main { flex-direction: column; }
  .cover-wrap { width: 100%; }
}
</style>
