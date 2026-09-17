<template>
  <div class="product-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">文创商城</h1>
        <p class="page-subtitle">寿阳特色文创产品，把文化带回家</p>
      </div>

      <!-- 排序栏 -->
      <div class="sort-bar">
        <div class="sort-group">
          <span
            v-for="opt in sortOptions"
            :key="opt.value"
            class="sort-item"
            :class="{ active: sortBy === opt.value }"
            @click="handleSortChange(opt.value)"
          >
            {{ opt.label }}
            <el-icon v-if="opt.value === 'price_asc'" class="sort-icon"><Top /></el-icon>
            <el-icon v-if="opt.value === 'price_desc'" class="sort-icon"><Bottom /></el-icon>
          </span>
        </div>
        <div class="result-count">共 {{ total }} 件商品</div>
      </div>

      <div v-if="loading" class="product-grid">
        <el-skeleton v-for="i in 8" :key="i" :rows="3" animated class="skeleton-card" />
      </div>

      <div v-else-if="list.length > 0" class="product-grid">
        <div v-for="item in list" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <div class="card-cover">
            <el-image :src="item.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <span v-if="item.originalPrice && item.originalPrice > item.price" class="discount-tag">
              特惠
            </span>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ item.name }}</h4>
            <div class="card-price">
              <span class="price-current">¥{{ formatPrice(item.price) }}</span>
              <span v-if="item.originalPrice && item.originalPrice > item.price" class="price-original">
                ¥{{ formatPrice(item.originalPrice) }}
              </span>
            </div>
            <div class="card-footer">
              <span class="stock">库存 {{ item.stock }} 件</span>
              <span class="view-btn">查看详情</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无商品" class="empty-state" />

      <Pagination
        v-if="total > 0"
        v-model:page="currentPage"
        :total="total"
        :page-size="pageSize"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Top, Bottom } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getProductList } from '@/api/culture'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const sortBy = ref('default')
const defaultCover = 'https://picsum.photos/seed/product-cover/400/400'

const sortOptions = [
  { label: '综合', value: 'default' },
  { label: '价格', value: 'price_asc' },
  { label: '价格', value: 'price_desc' }
]

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      page: currentPage.value,
      size: pageSize.value,
      sort: sortBy.value
    })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载商品列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handleSortChange = (value) => {
  sortBy.value = value
  currentPage.value = 1
  loadList()
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goDetail = (id) => {
  router.push(`/culture/product/${id}`)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.product-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 30px 20px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 28px; font-weight: 700; color: #1a2a4a; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #78909c; margin: 0; }

.sort-bar {
  background: #fff;
  border-radius: 10px;
  padding: 14px 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.sort-group { display: flex; align-items: center; gap: 4px; }
.sort-item {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 13px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 2px;
  &:hover { color: #2c3e6b; }
  &.active { background: #2c3e6b; color: #fff; }
}
.sort-icon { font-size: 12px; }
.result-count { font-size: 13px; color: #90a4ae; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 10px; padding: 12px; height: 300px; }

.product-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 24px rgba(0,0,0,0.1);
    border-color: transparent;
    .cover-img { transform: scale(1.06); }
    .card-title { color: #2c3e6b; }
  }
}
.card-cover { position: relative; width: 100%; padding-top: 100%; overflow: hidden; background: #f0f2f5; }
.cover-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; transition: transform 0.5s ease; }
.discount-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 4px;
}
.card-body { padding: 14px; }
.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #1a2a4a;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
  transition: color 0.2s;
}
.card-price { display: flex; align-items: baseline; gap: 8px; margin-bottom: 10px; }
.price-current { font-size: 18px; font-weight: 700; color: #f56c6c; }
.price-original { font-size: 12px; color: #b0bec5; text-decoration: line-through; }
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f2f5;
  padding-top: 10px;
}
.stock { font-size: 12px; color: #90a4ae; }
.view-btn { font-size: 12px; color: #2c3e6b; font-weight: 500; }
.empty-state { background: #fff; border-radius: 10px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 480px) { .product-grid { grid-template-columns: 1fr; } }
</style>
