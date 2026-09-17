<template>
  <div class="travel-list-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">{{ config.name }}</h1>
        <p class="page-subtitle">{{ subtitleMap[type] }}</p>
      </div>

      <!-- 筛选和搜索栏 -->
      <div class="filter-bar">
        <!-- 筛选条件（根据 type 显示） -->
        <div class="filter-group" v-if="config.filterOptions.length > 0">
          <span
            v-for="opt in config.filterOptions"
            :key="opt.value"
            class="filter-item"
            :class="{ active: filterValue === opt.value }"
            @click="handleFilterChange(opt.value)"
          >
            {{ opt.label }}
          </span>
        </div>

        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索..."
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 加载中骨架屏 -->
      <div v-if="loading" class="card-grid">
        <el-skeleton v-for="i in 8" :key="i" :rows="3" animated class="skeleton-card" />
      </div>

      <!-- 卡片列表 -->
      <div v-else-if="list.length > 0" class="card-grid">
        <ItemCard
          v-for="item in list"
          :key="item.id"
          :item="item"
          :type="type"
        />
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无数据" class="empty-state" />

      <!-- 分页 -->
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import ItemCard from '@/components/ItemCard.vue'
import Pagination from '@/components/Pagination.vue'
import { typeConfig, getTravelList } from '@/api/travel'

const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (val) => ['scenic', 'food', 'hotel', 'guide'].includes(val)
  }
})

const route = useRoute()

// 配置
const config = computed(() => typeConfig[props.type])

// 副标题映射
const subtitleMap = {
  scenic: '探索寿阳美景，领略自然风光',
  food: '品味寿阳特色，感受舌尖上的美味',
  hotel: '舒适住宿体验，温暖您的旅途',
  guide: '精选旅游攻略，带您玩转寿阳'
}

// 状态
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const filterValue = ref('')

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined
    }
    // 根据 type 添加筛选参数
    if (config.value.listParam && filterValue.value !== '') {
      params[config.value.listParam] = filterValue.value
    }
    const res = await getTravelList(props.type, params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 筛选切换
const handleFilterChange = (value) => {
  filterValue.value = value
  currentPage.value = 1
  loadList()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadList()
}

// 翻页
const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 监听 type 变化（路由复用时触发，如从景点切换到美食）
watch(() => props.type, () => {
  currentPage.value = 1
  filterValue.value = ''
  keyword.value = ''
  loadList()
})

onMounted(() => {
  // 从 URL 读取关键词
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  loadList()
})
</script>

<style lang="scss" scoped>
.travel-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a2a4a;
  margin: 0 0 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #78909c;
  margin: 0;
}

/* 筛选栏 */
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-item {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  &.active {
    background: #2c3e6b;
    color: #fff;
  }
}

.search-box {
  width: 260px;
  flex-shrink: 0;
}

/* 卡片网格 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.skeleton-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  height: 280px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 10px;
  padding: 60px 0;
  margin-bottom: 24px;
}

/* 响应式 */
@media (max-width: 992px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
