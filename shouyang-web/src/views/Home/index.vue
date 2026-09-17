<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <section class="banner-section">
      <el-carousel height="400px" :interval="4000" arrow="hover">
        <el-carousel-item v-for="item in bannerList" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-overlay">
              <h2 class="banner-title">{{ item.title }}</h2>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 快捷入口 -->
    <section class="quick-entry container">
      <div
        v-for="entry in quickEntries"
        :key="entry.name"
        class="entry-card"
        @click="router.push(entry.path)"
      >
        <div class="entry-icon" :style="{ background: entry.bgColor }">
          <el-icon :size="32" color="#fff"><component :is="entry.icon" /></el-icon>
        </div>
        <div class="entry-name">{{ entry.name }}</div>
        <div class="entry-desc">{{ entry.desc }}</div>
      </div>
    </section>

    <!-- 文旅动态 -->
    <section class="news-section container">
      <div class="section-header">
        <h3 class="section-title">
          <span class="title-bar"></span>
          文旅动态
        </h3>
        <router-link to="/news" class="more-link">查看更多 <el-icon><ArrowRight /></el-icon></router-link>
      </div>
      <div class="news-grid">
        <div v-for="news in newsList" :key="news.id" class="news-card" @click="goNewsDetail(news.id)">
          <div class="news-cover" :style="{ backgroundImage: `url(${news.coverImage})` }"></div>
          <div class="news-info">
            <h4 class="news-title text-ellipsis-2">{{ news.title }}</h4>
            <p class="news-summary text-ellipsis-2">{{ news.summary }}</p>
            <div class="news-meta">
              <span><el-icon><Clock /></el-icon> {{ formatDate(news.createTime) }}</span>
              <span><el-icon><View /></el-icon> {{ news.viewCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 畅游寿阳推荐 -->
    <section class="travel-section">
      <div class="container">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: $success-color"></span>
            畅游寿阳
          </h3>
        </div>
        <el-tabs v-model="activeTab" class="travel-tabs">
          <el-tab-pane label="景点推荐" name="scenic">
            <div class="travel-grid">
              <div v-for="item in scenicList" :key="item.id" class="travel-card">
                <div class="travel-cover" :style="{ backgroundImage: `url(${item.coverImage})` }">
                  <span v-if="item.level" class="level-tag">{{ item.level }}</span>
                </div>
                <div class="travel-info">
                  <h4 class="travel-name text-ellipsis">{{ item.name }}</h4>
                  <p class="travel-addr text-ellipsis"><el-icon><Location /></el-icon> {{ item.address }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="特色美食" name="food">
            <div class="travel-grid">
              <div v-for="item in foodList" :key="item.id" class="travel-card">
                <div class="travel-cover" :style="{ backgroundImage: `url(${item.coverImage})` }"></div>
                <div class="travel-info">
                  <h4 class="travel-name text-ellipsis">{{ item.name }}</h4>
                  <p class="travel-addr text-ellipsis">{{ item.category }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="民宿酒店" name="hotel">
            <div class="travel-grid">
              <div v-for="item in hotelList" :key="item.id" class="travel-card">
                <div class="travel-cover" :style="{ backgroundImage: `url(${item.coverImage})` }">
                  <span class="star-tag">{{ item.star }}星</span>
                </div>
                <div class="travel-info">
                  <h4 class="travel-name text-ellipsis">{{ item.name }}</h4>
                  <p class="travel-addr text-ellipsis"><el-icon><Location /></el-icon> {{ item.address }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'

const router = useRouter()
const activeTab = ref('scenic')

// 轮播图数据（占位，后续接接口）
const bannerList = ref([
  { id: 1, title: '寿阳文旅云欢迎您', image: 'https://picsum.photos/1200/400?random=1' },
  { id: 2, title: '炫彩非遗·魅力寿阳', image: 'https://picsum.photos/1200/400?random=2' },
  { id: 3, title: '怡然见晋中 休闲寿阳游', image: 'https://picsum.photos/1200/400?random=3' }
])

// 快捷入口
const quickEntries = [
  { name: '文旅动态', desc: '最新资讯', path: '/news', icon: 'Document', bgColor: '#e57373' },
  { name: '活动报名', desc: '精彩活动', path: '/service/activity', icon: 'Tickets', bgColor: '#ffa726' },
  { name: '数字展馆', desc: '线上观展', path: '/culture/pavilion', icon: 'Picture', bgColor: '#ab47bc' },
  { name: '非遗文化', desc: '传承经典', path: '/culture/heritage', icon: 'Star', bgColor: '#8e24aa' },
  { name: '景点推荐', desc: '发现美景', path: '/travel/scenic', icon: 'Location', bgColor: '#66bb6a' },
  { name: '文旅单位', desc: '机构名录', path: '/org', icon: 'OfficeBuilding', bgColor: '#26a69a' }
]

// 资讯列表（占位）
const newsList = ref([])
// 景点/美食/酒店（占位）
const scenicList = ref([])
const foodList = ref([])
const hotelList = ref([])

// 加载数据
async function loadData() {
  try {
    // 加载轮播图
    // const bannerRes = await get('/banner/list')
    // bannerList.value = bannerRes.data || []

    // 加载最新资讯
    // const newsRes = await get('/news/list', { size: 6 })
    // newsList.value = newsRes.data?.records || []

    // 加载景点推荐
    // const scenicRes = await get('/scenic/list', { size: 4 })
    // scenicList.value = scenicRes.data?.records || []
  } catch (e) {
    console.error('加载首页数据失败:', e)
  }
}

// 跳转资讯详情
function goNewsDetail(id) {
  router.push(`/news/${id}`)
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.home-page {
  padding-bottom: 40px;
}

// 轮播图
.banner-section {
  :deep(.el-carousel__item) {
    overflow: hidden;
  }
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;

  .banner-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.5), transparent);
    display: flex;
    align-items: flex-end;
    padding: 40px 60px;
  }

  .banner-title {
    color: #fff;
    font-size: 32px;
    font-weight: bold;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }
}

// 快捷入口
.quick-entry {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  margin-top: -40px;
  position: relative;
  z-index: 10;
}

.entry-card {
  background: #fff;
  border-radius: $border-radius-lg;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  box-shadow: $shadow-md;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-hover;
  }

  .entry-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 12px;
  }

  .entry-name {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 4px;
  }

  .entry-desc {
    font-size: 12px;
    color: $text-secondary;
  }
}

// 通用区块标题
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  color: $text-primary;
  display: flex;
  align-items: center;
  gap: 10px;

  .title-bar {
    width: 4px;
    height: 22px;
    background: $primary-color;
    border-radius: 2px;
  }
}

.more-link {
  font-size: 14px;
  color: $text-secondary;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover {
    color: $primary-color;
  }
}

// 文旅动态
.news-section {
  margin-top: 40px;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.news-card {
  background: #fff;
  border-radius: $border-radius;
  overflow: hidden;
  cursor: pointer;
  box-shadow: $shadow-sm;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-3px);
    box-shadow: $shadow-md;

    .news-title {
      color: $primary-color;
    }
  }

  .news-cover {
    height: 180px;
    background-size: cover;
    background-position: center;
  }

  .news-info {
    padding: 16px;

    .news-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      line-height: 1.5;
      margin-bottom: 8px;
      transition: color $transition-fast;
    }

    .news-summary {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;
      margin-bottom: 12px;
    }

    .news-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 12px;
      color: $text-placeholder;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

// 畅游寿阳
.travel-section {
  margin-top: 40px;
  background: #fff;
  padding: 30px 0;
}

.travel-tabs {
  :deep(.el-tabs__item) {
    font-size: 15px;
    height: 44px;
  }
}

.travel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 10px;
}

.travel-card {
  border-radius: $border-radius;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid $border-light;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-3px);
    box-shadow: $shadow-md;

    .travel-name {
      color: $primary-color;
    }
  }

  .travel-cover {
    height: 160px;
    background-size: cover;
    background-position: center;
    position: relative;

    .level-tag,
    .star-tag {
      position: absolute;
      top: 8px;
      left: 8px;
      background: rgba(255, 152, 0, 0.9);
      color: #fff;
      font-size: 11px;
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  .travel-info {
    padding: 12px;

    .travel-name {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 6px;
      transition: color $transition-fast;
    }

    .travel-addr {
      font-size: 12px;
      color: $text-secondary;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}
</style>
