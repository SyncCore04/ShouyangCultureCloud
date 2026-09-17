<template>
  <div class="pavilion-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">数字展馆</h1>
        <p class="page-subtitle">线上云游寿阳文化展馆，沉浸式体验文化魅力</p>
      </div>

      <div v-if="loading" class="pavilion-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <div v-else-if="list.length > 0" class="pavilion-grid">
        <div v-for="item in list" :key="item.id" class="pavilion-card" @click="goDetail(item.id)">
          <div class="card-cover">
            <el-image :src="item.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <div class="cover-mask">
              <span class="view-count"><el-icon><View /></el-icon>{{ item.viewCount || 0 }} 次浏览</span>
            </div>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ item.name }}</h4>
            <p class="card-desc">{{ item.description || '暂无简介' }}</p>
            <div class="card-footer">
              <span class="enter-btn">进入展馆 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无展馆" class="empty-state" />

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
import { View, ArrowRight } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getPavilionList } from '@/api/culture'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const defaultCover = 'https://picsum.photos/seed/pavilion-cover/600/360'

const loadList = async () => {
  loading.value = true
  try {
    const res = await getPavilionList({ page: currentPage.value, size: pageSize.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载展馆列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goDetail = (id) => {
  router.push(`/culture/pavilion/${id}`)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.pavilion-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 30px 20px; }
.page-header { margin-bottom: 28px; }
.page-title { font-size: 28px; font-weight: 700; color: #1a2a4a; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #78909c; margin: 0; }

.pavilion-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 12px; padding: 16px; height: 360px; }

.pavilion-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.4s ease;
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 16px 36px rgba(0,0,0,0.14);
    border-color: transparent;
    .cover-img { transform: scale(1.08); }
    .card-title { color: #2c3e6b; }
    .enter-btn { color: #2c3e6b; }
  }
}
.card-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #e0e6ed;
}
.cover-img { width: 100%; height: 100%; transition: transform 0.6s ease; }
.cover-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.5), transparent 50%);
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  padding: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}
.pavilion-card:hover .cover-mask { opacity: 1; }
.view-count {
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(0,0,0,0.3);
  padding: 4px 10px;
  border-radius: 12px;
}
.card-body { padding: 20px; }
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 10px;
  transition: color 0.2s;
}
.card-desc {
  font-size: 13px;
  color: #78909c;
  line-height: 1.7;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}
.card-footer {
  border-top: 1px solid #f0f2f5;
  padding-top: 14px;
}
.enter-btn {
  font-size: 13px;
  color: #90a4ae;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}
.empty-state { background: #fff; border-radius: 12px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) { .pavilion-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .pavilion-grid { grid-template-columns: 1fr; } }
</style>
