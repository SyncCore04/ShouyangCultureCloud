<template>
  <div class="home-page">
    <!-- 1. Banner 轮播图 -->
    <section class="banner-section">
      <Banner />
    </section>

    <div class="page-container">
      <!-- 2. 快捷入口区 -->
      <section class="quick-entry">
        <div
          v-for="entry in quickEntries"
          :key="entry.name"
          class="entry-card"
          @click="router.push(entry.path)"
        >
          <div class="entry-icon" :style="{ background: entry.bgColor }">
            <el-icon :size="28" color="#fff"><component :is="entry.icon" /></el-icon>
          </div>
          <div class="entry-name">{{ entry.name }}</div>
        </div>
      </section>

      <!-- 3. 文旅动态速览区 -->
      <section class="section-block">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: #e57373"></span>
            文旅动态
          </h3>
          <router-link to="/news" class="more-link">
            更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <!-- 加载中骨架屏 -->
        <el-skeleton v-if="newsLoading" :rows="4" animated />

        <div v-else class="news-list">
          <div
            v-for="news in newsList"
            :key="news.id"
            class="news-item"
            @click="goNewsDetail(news.id)"
          >
            <div class="news-cover">
              <el-image
                :src="news.coverImage || defaultCover"
                :lazy="true"
                fit="cover"
                class="cover-img"
              />
            </div>
            <div class="news-content">
              <h4 class="news-title">{{ news.title }}</h4>
              <p class="news-summary">{{ news.summary || '暂无摘要' }}</p>
              <div class="news-meta">
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(news.createTime) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ news.viewCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 4. 文旅服务区 -->
      <section class="section-block">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: #ffa726"></span>
            文旅服务
          </h3>
        </div>
        <div class="service-grid">
          <div
            v-for="item in serviceList"
            :key="item.name"
            class="service-card"
            @click="router.push(item.path)"
          >
            <div class="service-icon" :style="{ background: item.bgColor }">
              <el-icon :size="32" color="#fff"><component :is="item.icon" /></el-icon>
            </div>
            <h4 class="service-name">{{ item.name }}</h4>
            <p class="service-desc">{{ item.desc }}</p>
          </div>
        </div>
      </section>

      <!-- 5. 文旅时空区 -->
      <section class="section-block">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: #ab47bc"></span>
            文旅时空
          </h3>
        </div>
        <div class="culture-grid">
          <div
            v-for="item in cultureList"
            :key="item.name"
            class="culture-card"
            @click="router.push(item.path)"
          >
            <div class="culture-bg" :style="{ background: item.bgGradient }"></div>
            <div class="culture-content">
              <el-icon :size="36" color="#fff"><component :is="item.icon" /></el-icon>
              <h4 class="culture-name">{{ item.name }}</h4>
              <p class="culture-desc">{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 6. 畅游寿阳区 -->
      <section class="section-block travel-section">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: #66bb6a"></span>
            畅游寿阳
          </h3>
        </div>

        <el-tabs v-model="activeTravelTab" class="travel-tabs">
          <!-- 景点推荐 -->
          <el-tab-pane label="景点推荐" name="scenic">
            <el-skeleton v-if="travelLoading.scenic" :rows="3" animated />
            <div v-else class="travel-grid">
              <div
                v-for="item in scenicList"
                :key="item.id"
                class="travel-card"
                @click="goTravelDetail('scenic', item.id)"
              >
                <div class="travel-cover">
                  <el-image :src="item.coverImage || defaultCover" :lazy="true" fit="cover" class="cover-img" />
                  <span v-if="item.level" class="travel-tag">{{ item.level }}</span>
                </div>
                <div class="travel-info">
                  <h4 class="travel-name">{{ item.name }}</h4>
                  <p class="travel-addr">
                    <el-icon><Location /></el-icon>
                    {{ item.address || '寿阳县' }}
                  </p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 特色美食 -->
          <el-tab-pane label="特色美食" name="food">
            <el-skeleton v-if="travelLoading.food" :rows="3" animated />
            <div v-else class="travel-grid">
              <div
                v-for="item in foodList"
                :key="item.id"
                class="travel-card"
                @click="goTravelDetail('food', item.id)"
              >
                <div class="travel-cover">
                  <el-image :src="item.coverImage || defaultCover" :lazy="true" fit="cover" class="cover-img" />
                </div>
                <div class="travel-info">
                  <h4 class="travel-name">{{ item.name }}</h4>
                  <p class="travel-addr">{{ item.category || '特色美食' }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 民宿酒店 -->
          <el-tab-pane label="民宿酒店" name="hotel">
            <el-skeleton v-if="travelLoading.hotel" :rows="3" animated />
            <div v-else class="travel-grid">
              <div
                v-for="item in hotelList"
                :key="item.id"
                class="travel-card"
                @click="goTravelDetail('hotel', item.id)"
              >
                <div class="travel-cover">
                  <el-image :src="item.coverImage || defaultCover" :lazy="true" fit="cover" class="cover-img" />
                  <span v-if="item.star" class="travel-tag star">{{ item.star }}星</span>
                </div>
                <div class="travel-info">
                  <h4 class="travel-name">{{ item.name }}</h4>
                  <p class="travel-addr">
                    <el-icon><Location /></el-icon>
                    {{ item.address || '寿阳县' }}
                  </p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </section>

      <!-- 7. 文旅单位区 -->
      <section class="section-block">
        <div class="section-header">
          <h3 class="section-title">
            <span class="title-bar" style="background: #26a69a"></span>
            文旅单位
          </h3>
          <router-link to="/org" class="more-link">
            更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="org-scroll">
          <div
            v-for="org in orgList"
            :key="org.id"
            class="org-card"
            @click="router.push('/org')"
          >
            <div class="org-logo">
              <el-icon :size="28" color="#2c3e6b"><OfficeBuilding /></el-icon>
            </div>
            <div class="org-info">
              <h4 class="org-name">{{ org.name }}</h4>
              <p class="org-type">{{ org.type || '文旅单位' }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import Banner from '@/components/Banner.vue'
import {
  ArrowRight, Clock, View, Location, OfficeBuilding,
  Document, Service, Monitor, Place, Dish, House, Calendar, Collection
} from '@element-plus/icons-vue'

const router = useRouter()

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/shouyang-cover/400/300'

// ==================== 2. 快捷入口 ====================
const quickEntries = [
  { name: '文旅动态', path: '/news', icon: Document, bgColor: 'linear-gradient(135deg, #e57373, #ef5350)' },
  { name: '文旅服务', path: '/service/ticket', icon: Service, bgColor: 'linear-gradient(135deg, #ffa726, #ff9800)' },
  { name: '文旅时空', path: '/culture/pavilion', icon: Monitor, bgColor: 'linear-gradient(135deg, #ab47bc, #9c27b0)' },
  { name: '畅游寿阳', path: '/travel/scenic', icon: Place, bgColor: 'linear-gradient(135deg, #66bb6a, #4caf50)' },
  { name: '文旅单位', path: '/org', icon: OfficeBuilding, bgColor: 'linear-gradient(135deg, #26a69a, #009688)' },
  { name: '活动日历', path: '/service/calendar', icon: Calendar, bgColor: 'linear-gradient(135deg, #42a5f5, #2196f3)' }
]

// ==================== 3. 文旅动态 ====================
const newsLoading = ref(true)
const newsList = ref([])

// 占位新闻数据
const defaultNews = [
  { id: 1, title: '寿阳县举办2024年文化旅游推介会', summary: '本次推介会集中展示了寿阳县丰富的文化旅游资源，吸引了众多游客和旅行商参与。', coverImage: 'https://picsum.photos/seed/news1/300/200', createTime: '2024-01-15', viewCount: 1256 },
  { id: 2, title: '非遗文化进校园活动圆满举办', summary: '通过非遗传承人现场展示和互动体验，让学生们近距离感受传统文化的魅力。', coverImage: 'https://picsum.photos/seed/news2/300/200', createTime: '2024-01-12', viewCount: 892 },
  { id: 3, title: '寿阳美食节开幕，百种特色小吃齐聚', summary: '本次美食节汇聚了寿阳及周边地区的特色美食，为市民和游客带来一场味蕾盛宴。', coverImage: 'https://picsum.photos/seed/news3/300/200', createTime: '2024-01-10', viewCount: 2341 },
  { id: 4, title: '数字展馆上线，足不出户游寿阳', summary: '全新数字展馆采用VR技术，让游客在线即可沉浸式游览寿阳各大景点和文化场馆。', coverImage: 'https://picsum.photos/seed/news4/300/200', createTime: '2024-01-08', viewCount: 1567 },
  { id: 5, title: '春节文旅活动安排发布，精彩不断', summary: '春节期间，寿阳县将举办系列文化旅游活动，包括灯会、庙会、民俗表演等。', coverImage: 'https://picsum.photos/seed/news5/300/200', createTime: '2024-01-05', viewCount: 3102 },
  { id: 6, title: '文旅志愿服务队成立，招募志愿者中', summary: '文旅志愿服务队将为游客提供导览、咨询、应急救助等服务，现面向社会招募志愿者。', coverImage: 'https://picsum.photos/seed/news6/300/200', createTime: '2024-01-03', viewCount: 756 }
]

const loadNews = async () => {
  try {
    const res = await get('/news/list', { size: 6 })
    const list = res.data?.records || res.data || []
    newsList.value = list.length > 0 ? list : defaultNews
  } catch (e) {
    newsList.value = defaultNews
  } finally {
    newsLoading.value = false
  }
}

// ==================== 4. 文旅服务 ====================
const serviceList = [
  { name: '票务预订', desc: '在线预订景点门票', path: '/service/ticket', icon: 'Ticket', bgColor: '#ffa726' },
  { name: '场馆预订', desc: '预约文化体育场馆', path: '/service/venue', icon: 'OfficeBuilding', bgColor: '#26a69a' },
  { name: '活动报名', desc: '报名参加精彩活动', path: '/service/activity', icon: 'Calendar', bgColor: '#ab47bc' },
  { name: '文旅日历', desc: '查看近期活动安排', path: '/service/calendar', icon: 'Calendar', bgColor: '#42a5f5' },
  { name: '志愿者', desc: '加入文旅志愿服务', path: '/service/volunteer', icon: 'Service', bgColor: '#66bb6a' }
]

// ==================== 5. 文旅时空 ====================
const cultureList = [
  { name: '数字展馆', desc: 'VR在线观展', path: '/culture/pavilion', icon: Monitor, bgGradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { name: '非遗文化', desc: '传承千年瑰宝', path: '/culture/heritage', icon: Collection, bgGradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { name: '文创商城', desc: '精选文创好物', path: '/culture/product', icon: 'ShoppingCart', bgGradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { name: '云阅读', desc: '在线数字图书馆', path: '/culture/reading', icon: 'Reading', bgGradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
]

// ==================== 6. 畅游寿阳 ====================
const activeTravelTab = ref('scenic')
const travelLoading = reactive({ scenic: true, food: true, hotel: true })
const scenicList = ref([])
const foodList = ref([])
const hotelList = ref([])

// 占位数据
const defaultScenic = [
  { id: 1, name: '方山国家森林公园', address: '寿阳县方山', level: '4A', coverImage: 'https://picsum.photos/seed/scenic1/400/250' },
  { id: 2, name: '祁寯藻故里', address: '寿阳县平舒村', level: '3A', coverImage: 'https://picsum.photos/seed/scenic2/400/250' },
  { id: 3, name: '五峰山龙泉寺', address: '寿阳县南燕竹镇', coverImage: 'https://picsum.photos/seed/scenic3/400/250' }
]
const defaultFood = [
  { id: 1, name: '寿阳头脑', category: '传统小吃', coverImage: 'https://picsum.photos/seed/food1/400/250' },
  { id: 2, name: '寿阳豆腐干', category: '特色美食', coverImage: 'https://picsum.photos/seed/food2/400/250' },
  { id: 3, name: '寿阳油柿子', category: '传统糕点', coverImage: 'https://picsum.photos/seed/food3/400/250' }
]
const defaultHotel = [
  { id: 1, name: '寿阳宾馆', address: '寿阳县朝阳街', star: 4, coverImage: 'https://picsum.photos/seed/hotel1/400/250' },
  { id: 2, name: '福田酒店', address: '寿阳县宾阳路', star: 3, coverImage: 'https://picsum.photos/seed/hotel2/400/250' },
  { id: 3, name: '方山度假山庄', address: '寿阳县方山景区', coverImage: 'https://picsum.photos/seed/hotel3/400/250' }
]

const loadScenic = async () => {
  try {
    const res = await get('/scenic/list', { size: 3 })
    const list = res.data?.records || res.data || []
    scenicList.value = list.length > 0 ? list : defaultScenic
  } catch (e) {
    scenicList.value = defaultScenic
  } finally {
    travelLoading.scenic = false
  }
}

const loadFood = async () => {
  try {
    const res = await get('/food/list', { size: 3 })
    const list = res.data?.records || res.data || []
    foodList.value = list.length > 0 ? list : defaultFood
  } catch (e) {
    foodList.value = defaultFood
  } finally {
    travelLoading.food = false
  }
}

const loadHotel = async () => {
  try {
    const res = await get('/hotel/list', { size: 3 })
    const list = res.data?.records || res.data || []
    hotelList.value = list.length > 0 ? list : defaultHotel
  } catch (e) {
    hotelList.value = defaultHotel
  } finally {
    travelLoading.hotel = false
  }
}

// ==================== 7. 文旅单位 ====================
const orgList = ref([
  { id: 1, name: '寿阳县文化和旅游局', type: '政府部门' },
  { id: 2, name: '寿阳县文化馆', type: '文化场馆' },
  { id: 3, name: '寿阳县图书馆', type: '文化场馆' },
  { id: 4, name: '寿阳县博物馆', type: '文化场馆' },
  { id: 5, name: '寿阳县剧团', type: '文艺团体' },
  { id: 6, name: '方山景区管理处', type: '景区管理' }
])

// ==================== 工具方法 ====================
const goNewsDetail = (id) => router.push(`/news/${id}`)
const goTravelDetail = (type, id) => router.push(`/travel/${type}/${id}`)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

// ==================== 加载数据 ====================
onMounted(() => {
  loadNews()
  loadScenic()
  loadFood()
  loadHotel()
})
</script>

<style lang="scss" scoped>
.home-page {
  padding-bottom: 60px;
}

/* Banner 区域 */
.banner-section {
  padding: 20px 20px 0;
}

/* 页面容器 */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 通用区块 */
.section-block {
  margin-top: 50px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a2a4a;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.title-bar {
  width: 5px;
  height: 24px;
  border-radius: 3px;
}

.more-link {
  font-size: 14px;
  color: #78909c;
  display: flex;
  align-items: center;
  gap: 4px;
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: #2c3e6b;
  }
}

/* ==================== 快捷入口 ==================== */
.quick-entry {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-top: -30px;
  position: relative;
  z-index: 10;
}

.entry-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px 12px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 8px 24px rgba(44, 62, 107, 0.15);
  }
}

.entry-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.entry-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a2a4a;
}

/* ==================== 文旅动态 ==================== */
.news-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.news-item {
  display: flex;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

    .news-title {
      color: #2c3e6b;
    }
  }
}

.news-cover {
  width: 200px;
  flex-shrink: 0;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
    min-height: 140px;
  }
}

.news-content {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
}

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

/* ==================== 文旅服务 ==================== */
.service-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.service-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 16px;
  text-align: center;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
    border-color: transparent;
  }
}

.service-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 14px;
}

.service-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 6px;
}

.service-desc {
  font-size: 12px;
  color: #90a4ae;
  margin: 0;
}

/* ==================== 文旅时空 ==================== */
.culture-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.culture-card {
  position: relative;
  height: 180px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);

    .culture-content {
      transform: scale(1.05);
    }
  }
}

.culture-bg {
  position: absolute;
  inset: 0;
}

.culture-content {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: transform 0.3s ease;
}

.culture-name {
  font-size: 18px;
  font-weight: 700;
  margin: 12px 0 6px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.culture-desc {
  font-size: 13px;
  opacity: 0.9;
  margin: 0;
}

/* ==================== 畅游寿阳 ==================== */
.travel-section {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.travel-tabs {
  :deep(.el-tabs__item) {
    font-size: 15px;
    height: 44px;
  }

  :deep(.el-tabs__active-bar) {
    background-color: #66bb6a;
  }
}

.travel-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 10px;
}

.travel-card {
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

    .travel-name {
      color: #2c3e6b;
    }
  }
}

.travel-cover {
  position: relative;
  height: 160px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
  }
}

.travel-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255, 152, 0, 0.95);
  color: #fff;
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 4px;
  font-weight: 600;

  &.star {
    background: rgba(255, 193, 7, 0.95);
  }
}

.travel-info {
  padding: 14px;
}

.travel-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}

.travel-addr {
  font-size: 12px;
  color: #78909c;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ==================== 文旅单位 ==================== */
.org-scroll {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.org-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
    border-color: #26a69a;
  }
}

.org-logo {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: #e0f2f1;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.org-info {
  flex: 1;
  min-width: 0;
}

.org-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.org-type {
  font-size: 12px;
  color: #90a4ae;
  margin: 0;
}

/* ==================== 响应式 ==================== */
@media (max-width: 992px) {
  .quick-entry {
    grid-template-columns: repeat(3, 1fr);
  }

  .service-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .culture-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .news-list {
    grid-template-columns: 1fr;
  }

  .org-scroll {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .quick-entry {
    grid-template-columns: repeat(2, 1fr);
  }

  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .travel-grid {
    grid-template-columns: 1fr;
  }

  .org-scroll {
    grid-template-columns: 1fr;
  }

  .news-cover {
    width: 120px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>
