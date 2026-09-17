<template>
  <div class="heritage-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">非遗文化</h1>
        <p class="page-subtitle">传承千年文化根脉，守护寿阳非遗瑰宝</p>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-group">
          <span class="filter-label">级别：</span>
          <span
            v-for="opt in levelOptions"
            :key="opt.value"
            class="filter-item"
            :class="{ active: levelFilter === opt.value }"
            @click="handleLevelChange(opt.value)"
          >
            {{ opt.label }}
          </span>
        </div>
        <div class="filter-group">
          <span class="filter-label">类别：</span>
          <span
            v-for="opt in categoryOptions"
            :key="opt"
            class="filter-item"
            :class="{ active: categoryFilter === opt }"
            @click="handleCategoryChange(opt)"
          >
            {{ opt }}
          </span>
        </div>
      </div>

      <div v-if="loading" class="heritage-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <div v-else-if="list.length > 0" class="heritage-grid">
        <div v-for="item in list" :key="item.id" class="heritage-card" @click="goDetail(item.id)">
          <div class="card-cover">
            <el-image :src="item.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <span class="level-tag" :style="getLevelStyle(item.level)">{{ item.level }}</span>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ item.name }}</h4>
            <div class="card-meta">
              <span class="meta-item"><el-icon><Collection /></el-icon>{{ item.category }}</span>
              <span class="meta-item"><el-icon><User /></el-icon>{{ item.inheritor || '暂无' }}</span>
            </div>
            <p class="card-desc">{{ item.description || '暂无简介' }}</p>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无非遗项目" class="empty-state" />

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
import { Collection, User } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getHeritageList, getLevelStyle } from '@/api/culture'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const levelFilter = ref('')
const categoryFilter = ref('')
const defaultCover = 'https://picsum.photos/seed/heritage-cover/600/360'

const levelOptions = [
  { label: '全部', value: '' },
  { label: '国家级', value: '国家级' },
  { label: '省级', value: '省级' },
  { label: '市级', value: '市级' },
  { label: '县级', value: '县级' }
]

const categoryOptions = ['全部', '传统技艺', '民俗', '传统音乐', '传统舞蹈', '传统戏剧', '传统美术', '传统医药']

const loadList = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (levelFilter.value) params.level = levelFilter.value
    if (categoryFilter.value && categoryFilter.value !== '全部') params.category = categoryFilter.value
    const res = await getHeritageList(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载非遗列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handleLevelChange = (value) => {
  levelFilter.value = value
  currentPage.value = 1
  loadList()
}

const handleCategoryChange = (value) => {
  categoryFilter.value = value
  currentPage.value = 1
  loadList()
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goDetail = (id) => {
  router.push(`/culture/heritage/${id}`)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.heritage-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 30px 20px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; color: #1a2a4a; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #78909c; margin: 0; }

.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.filter-group { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.filter-label { font-size: 13px; color: #78909c; font-weight: 500; flex-shrink: 0; }
.filter-item {
  padding: 5px 14px;
  border-radius: 16px;
  font-size: 12px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { background: #f0f4fa; color: #2c3e6b; }
  &.active { background: #2c3e6b; color: #fff; }
}

.heritage-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 12px; padding: 16px; height: 340px; }

.heritage-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0,0,0,0.12);
    border-color: transparent;
    .cover-img { transform: scale(1.06); }
    .card-title { color: #2c3e6b; }
  }
}
.card-cover { position: relative; width: 100%; height: 180px; overflow: hidden; background: #e0e6ed; }
.cover-img { width: 100%; height: 100%; transition: transform 0.5s ease; }
.level-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}
.card-body { padding: 16px 18px; }
.card-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 10px;
  transition: color 0.2s;
}
.card-meta { display: flex; gap: 14px; margin-bottom: 10px; }
.meta-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #90a4ae; }
.card-desc {
  font-size: 13px;
  color: #78909c;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.empty-state { background: #fff; border-radius: 12px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) { .heritage-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .heritage-grid { grid-template-columns: 1fr; } }
</style>
