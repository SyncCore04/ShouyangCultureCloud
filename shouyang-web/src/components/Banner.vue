<template>
  <div class="banner-container">
    <el-carousel
      :interval="3000"
      :autoplay="true"
      :arrow="arrowVisible ? 'always' : 'hover'"
      :indicator-position="'outside'"
      height="420px"
      @mouseenter="arrowVisible = true"
      @mouseleave="arrowVisible = false"
    >
      <el-carousel-item v-for="(item, index) in bannerList" :key="index">
        <div class="banner-item" @click="handleBannerClick(item)">
          <img :src="item.imageUrl" :alt="item.title" class="banner-image" />
          <div class="banner-overlay">
            <div class="banner-content">
              <h2 class="banner-title">{{ item.title }}</h2>
              <p v-if="item.description" class="banner-desc">{{ item.description }}</p>
              <el-button v-if="item.linkUrl" type="primary" class="banner-btn">
                查看详情
                <el-icon class="btn-icon"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

// 轮播图数据
const bannerList = ref([])
const arrowVisible = ref(false)

// 默认占位数据（后端接口未就绪时使用）
const defaultBanners = [
  {
    id: 1,
    title: '寿阳文旅云正式上线',
    description: '一站式文化旅游服务平台，带您领略寿阳魅力',
    imageUrl: 'https://picsum.photos/seed/shouyang1/1200/420',
    linkUrl: '/news'
  },
  {
    id: 2,
    title: '探寻非遗文化之美',
    description: '寿阳非遗文化展示，传承千年文化瑰宝',
    imageUrl: 'https://picsum.photos/seed/shouyang2/1200/420',
    linkUrl: '/culture/heritage'
  },
  {
    id: 3,
    title: '畅游寿阳 尽享美景',
    description: '精选景点推荐，带您玩转寿阳',
    imageUrl: 'https://picsum.photos/seed/shouyang3/1200/420',
    linkUrl: '/travel/scenic'
  }
]

// 获取轮播图数据
const fetchBanners = async () => {
  try {
    const res = await get('/banner/list')
    if (res.data && res.data.length > 0) {
      bannerList.value = res.data.map(item => ({
        id: item.id,
        title: item.title,
        description: item.description || item.subtitle || '',
        imageUrl: item.imageUrl || item.image || item.coverImage,
        linkUrl: item.linkUrl || item.link || ''
      }))
    } else {
      bannerList.value = defaultBanners
    }
  } catch (error) {
    // 接口请求失败，使用默认占位数据
    console.warn('轮播图接口请求失败，使用默认数据:', error.message)
    bannerList.value = defaultBanners
  }
}

// 点击轮播图跳转
const handleBannerClick = (item) => {
  if (item.linkUrl) {
    // 外部链接
    if (item.linkUrl.startsWith('http')) {
      window.open(item.linkUrl, '_blank')
    } else {
      // 内部路由
      router.push(item.linkUrl)
    }
  }
}

onMounted(() => {
  fetchBanners()
})
</script>

<style lang="scss" scoped>
.banner-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.banner-item:hover .banner-image {
  transform: scale(1.05);
}

/* 文字覆盖层 */
.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, rgba(26, 42, 74, 0.7) 0%, rgba(26, 42, 74, 0.3) 50%, transparent 100%);
  display: flex;
  align-items: center;
}

.banner-content {
  padding: 0 60px;
  max-width: 600px;
  color: #fff;
}

.banner-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  line-height: 1.3;
}

.banner-desc {
  font-size: 16px;
  margin-bottom: 24px;
  opacity: 0.9;
  line-height: 1.6;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.banner-btn {
  border-radius: 24px;
  padding: 10px 28px;
  font-size: 14px;
  background: #2c3e6b;
  border-color: #2c3e6b;

  &:hover {
    background: #4a6fa5;
    border-color: #4a6fa5;
  }

  .btn-icon {
    margin-left: 4px;
  }
}

/* 自定义指示器样式 */
:deep(.el-carousel__indicators--outside) {
  margin-top: 12px;

  .el-carousel__indicator {
    padding: 6px 4px;

    .el-carousel__button {
      width: 24px;
      height: 4px;
      border-radius: 2px;
      background: #cfd8dc;
      opacity: 1;
    }

    &.is-active .el-carousel__button {
      background: #2c3e6b;
      width: 32px;
    }
  }
}

/* 自定义箭头样式 */
:deep(.el-carousel__arrow) {
  width: 44px;
  height: 44px;
  font-size: 20px;
  background: rgba(255, 255, 255, 0.9);
  color: #2c3e6b;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

  &:hover {
    background: #2c3e6b;
    color: #fff;
  }
}

:deep(.el-carousel__arrow--left) {
  left: 20px;
}

:deep(.el-carousel__arrow--right) {
  right: 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .banner-container {
    border-radius: 0;
  }

  :deep(.el-carousel) {
    height: 280px !important;
  }

  .banner-content {
    padding: 0 24px;
  }

  .banner-title {
    font-size: 20px;
    margin-bottom: 10px;
  }

  .banner-desc {
    font-size: 13px;
    margin-bottom: 16px;
  }

  .banner-btn {
    padding: 6px 18px;
    font-size: 12px;
  }
}
</style>
