<template>
  <div class="venue-detail-page">
    <div class="page-container">
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/service/venue' }">场馆预订</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div v-if="loading" class="detail-skeleton"><el-skeleton :rows="15" animated /></div>

      <template v-else-if="venue">
        <div class="detail-cover" v-if="venue.coverImage">
          <el-image :src="venue.coverImage" fit="cover" class="cover-img" />
        </div>

        <div class="detail-content">
          <h1 class="detail-title">{{ venue.name }}</h1>

          <div class="info-card">
            <h3 class="info-title"><el-icon color="#2c3e6b"><InfoFilled /></el-icon>场馆信息</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="场馆地址">{{ venue.address || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ venue.contact || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="开放时间" :span="2">{{ venue.openTime || '暂无' }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="content-card">
            <h3 class="content-title"><el-icon color="#2c3e6b"><Document /></el-icon>场馆介绍</h3>
            <div class="article-content" v-html="venue.description || '<p>暂无介绍</p>'"></div>
          </div>

          <el-button type="primary" plain @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>返回列表
          </el-button>
        </div>
      </template>

      <el-empty v-else description="场馆不存在" class="empty-state" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { InfoFilled, Document, ArrowLeft } from '@element-plus/icons-vue'
import { getVenueDetail } from '@/api/service'

const route = useRoute()
const loading = ref(true)
const venue = ref(null)

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getVenueDetail(id)
    venue.value = res.data
  } catch (e) {
    console.error('加载场馆详情失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadDetail() })
</script>

<style lang="scss" scoped>
.venue-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.breadcrumb-bar { margin-bottom: 20px;
  :deep(.el-breadcrumb__inner) { color: #78909c; font-size: 13px; }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) { color: #2c3e6b; font-weight: 500; }
}
.detail-cover { width: 100%; height: 320px; border-radius: 12px; overflow: hidden; margin-bottom: 24px; }
.cover-img { width: 100%; height: 100%; }
.detail-content { background: #fff; border-radius: 12px; padding: 30px; }
.detail-title { font-size: 26px; font-weight: 700; color: #1a2a4a; margin: 0 0 24px; }
.info-card, .content-card { margin-bottom: 24px; }
.info-title, .content-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 16px; display: flex; align-items: center; gap: 8px; }
.article-content { font-size: 15px; line-height: 1.9; color: #37474f;
  :deep(p) { margin: 0 0 16px; text-indent: 2em; }
  :deep(img) { max-width: 100%; border-radius: 8px; margin: 16px auto; display: block; }
}
.detail-skeleton { background: #fff; border-radius: 12px; padding: 30px; }
.empty-state { background: #fff; border-radius: 12px; padding: 80px 0; }
</style>
