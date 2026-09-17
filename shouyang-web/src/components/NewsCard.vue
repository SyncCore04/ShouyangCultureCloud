<template>
  <div class="news-card" @click="handleClick">
    <!-- 左侧封面图 -->
    <div class="news-cover">
      <el-image
        :src="coverUrl"
        :lazy="true"
        fit="cover"
        class="cover-img"
        :preview-src-list="[coverUrl]"
        preview-teleported
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon :size="24" color="#cfd8dc"><Picture /></el-icon>
          </div>
        </template>
      </el-image>
    </div>

    <!-- 右侧文字区 -->
    <div class="news-content">
      <!-- 分类标签 -->
      <div class="news-tags" v-if="categoryName">
        <span class="tag category">{{ categoryName }}</span>
        <span v-if="isTop" class="tag top">置顶</span>
      </div>

      <!-- 标题 -->
      <h4 class="news-title">{{ news.title }}</h4>

      <!-- 摘要 -->
      <p class="news-summary">{{ news.summary || news.description || '暂无摘要' }}</p>

      <!-- 底部元信息 -->
      <div class="news-meta">
        <span class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ formatDate }}
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon>
          {{ news.viewCount || 0 }}
        </span>
        <span v-if="news.author" class="meta-item">
          <el-icon><User /></el-icon>
          {{ news.author }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, Clock, View, User } from '@element-plus/icons-vue'

const props = defineProps({
  // 资讯数据对象
  news: {
    type: Object,
    required: true
  },
  // 分类名称（可选，不传则从 news.categoryName 获取）
  categoryName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['click'])
const router = useRouter()

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/news-cover/320/200'

// 封面图 URL
const coverUrl = computed(() => {
  return props.news.coverImage || props.news.image || props.news.cover || defaultCover
})

// 是否置顶
const isTop = computed(() => {
  return props.news.isTop === 1 || props.news.isTop === true
})

// 分类名称
const categoryName = computed(() => {
  return props.categoryName || props.news.categoryName || props.news.category || ''
})

// 格式化日期
const formatDate = computed(() => {
  const date = props.news.createTime || props.news.publishTime || props.news.date
  if (!date) return ''
  return typeof date === 'string' ? date.substring(0, 10) : ''
})

// 点击跳转资讯详情
const handleClick = () => {
  emit('click', props.news)
  if (props.news.id) {
    router.push(`/news/${props.news.id}`)
  }
}
</script>

<style lang="scss" scoped>
.news-card {
  display: flex;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    border-color: transparent;

    .news-title {
      color: #2c3e6b;
    }

    .cover-img {
      transform: scale(1.05);
    }
  }
}

/* 左侧封面图 */
.news-cover {
  width: 200px;
  min-width: 200px;
  height: 140px;
  overflow: hidden;
  background: #f5f7fa;
  position: relative;
}

.cover-img {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.image-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

/* 右侧文字区 */
.news-content {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 标签 */
.news-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;

  &.category {
    background: #e3f2fd;
    color: #1976d2;
  }

  &.top {
    background: #ffebee;
    color: #e53935;
  }
}

/* 标题 */
.news-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
  line-height: 1.5;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

/* 摘要 */
.news-summary {
  font-size: 13px;
  color: #78909c;
  line-height: 1.6;
  margin: 0 0 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部元信息 */
.news-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #b0bec5;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 响应式 */
@media (max-width: 576px) {
  .news-card {
    flex-direction: column;
  }

  .news-cover {
    width: 100%;
    min-width: 100%;
    height: 180px;
  }
}
</style>
