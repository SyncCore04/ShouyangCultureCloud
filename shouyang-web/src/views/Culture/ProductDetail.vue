<template>
  <div class="product-detail-page">
    <div class="page-container">
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/culture/product' }">文创商城</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div v-if="loading" class="detail-skeleton"><el-skeleton :rows="10" animated /></div>

      <template v-else-if="product">
        <!-- 商品主信息区 -->
        <div class="product-main">
          <!-- 左侧图片区 -->
          <div class="product-gallery">
            <div class="main-image">
              <el-image :src="currentImage" fit="contain" class="main-img" :preview-src-list="imageList" />
            </div>
            <div class="thumb-list" v-if="imageList.length > 1">
              <div
                v-for="(img, index) in imageList"
                :key="index"
                class="thumb-item"
                :class="{ active: currentIndex === index }"
                @click="currentIndex = index"
              >
                <el-image :src="img" fit="cover" class="thumb-img" />
              </div>
            </div>
          </div>

          <!-- 右侧信息区 -->
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            <p class="product-desc">{{ product.description }}</p>

            <div class="price-box">
              <div class="price-row">
                <span class="price-label">促销价</span>
                <span class="price-current">¥{{ formatPrice(product.price) }}</span>
                <span v-if="product.originalPrice && product.originalPrice > product.price" class="price-original">
                  ¥{{ formatPrice(product.originalPrice) }}
                </span>
              </div>
              <div v-if="product.originalPrice && product.originalPrice > product.price" class="discount-row">
                <span class="discount-badge">限时特惠</span>
                <span class="discount-text">立省 ¥{{ formatPrice(product.originalPrice - product.price) }}</span>
              </div>
            </div>

            <div class="info-list">
              <div class="info-item">
                <span class="info-label">库存</span>
                <span class="info-value">{{ product.stock }} 件</span>
              </div>
              <div class="info-item">
                <span class="info-label">浏览量</span>
                <span class="info-value">{{ product.viewCount || 0 }} 次</span>
              </div>
            </div>

            <div class="action-buttons">
              <el-button type="primary" size="large" class="action-btn" @click="handleAddCart">
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button type="danger" size="large" class="action-btn buy-btn" @click="handleBuyNow">
                <el-icon><CreditCard /></el-icon>
                立即购买
              </el-button>
            </div>

            <el-alert
              title="本商城为毕业设计演示项目，暂不支持真实交易，所有商品仅作展示。"
              type="info"
              :closable="false"
              show-icon
              class="tip-alert"
            />
          </div>
        </div>

        <!-- 商品详情 -->
        <div class="product-detail">
          <div class="detail-header">
            <h3 class="detail-title">商品详情</h3>
          </div>
          <div class="detail-content">
            <div class="article-content" v-html="product.content || '<p>暂无详细介绍</p>'"></div>
          </div>
        </div>
      </template>

      <el-empty v-else description="商品不存在" class="empty-state" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, CreditCard } from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/culture'

const route = useRoute()
const loading = ref(true)
const product = ref(null)
const currentIndex = ref(0)

// 图片列表（封面图 + 多图）
const imageList = computed(() => {
  if (!product.value) return []
  const images = [product.value.coverImage]
  if (product.value.images) {
    try {
      const extra = JSON.parse(product.value.images)
      if (Array.isArray(extra)) {
        images.push(...extra.filter(img => img && img !== product.value.coverImage))
      }
    } catch (e) {
      // 非JSON格式忽略
    }
  }
  return images.filter(Boolean)
})

const currentImage = computed(() => {
  return imageList.value[currentIndex.value] || product.value?.coverImage
})

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getProductDetail(id)
    product.value = res.data
    currentIndex.value = 0
  } catch (e) {
    console.error('加载商品详情失败:', e)
  } finally {
    loading.value = false
  }
}

const handleAddCart = () => {
  ElMessage.info('加入购物车功能开发中，敬请期待~')
}

const handleBuyNow = () => {
  ElMessage.info('立即购买功能开发中，本项目为演示项目，暂不支持真实交易~')
}

onMounted(() => { loadDetail() })
</script>

<style lang="scss" scoped>
.product-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.breadcrumb-bar { margin-bottom: 20px;
  :deep(.el-breadcrumb__inner) { color: #78909c; font-size: 13px; }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) { color: #2c3e6b; font-weight: 500; }
}

.product-main {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  display: flex;
  gap: 40px;
}

/* 图片区 */
.product-gallery { width: 420px; flex-shrink: 0; }
.main-image {
  width: 100%;
  height: 420px;
  border-radius: 10px;
  overflow: hidden;
  background: #f5f7fa;
  border: 1px solid #eceff1;
}
.main-img { width: 100%; height: 100%; }
.thumb-list { display: flex; gap: 10px; margin-top: 14px; }
.thumb-item {
  width: 72px;
  height: 72px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  &:hover { border-color: #a8bddb; }
  &.active { border-color: #2c3e6b; }
}
.thumb-img { width: 100%; height: 100%; }

/* 信息区 */
.product-info { flex: 1; min-width: 0; }
.product-title { font-size: 24px; font-weight: 700; color: #1a2a4a; margin: 0 0 10px; line-height: 1.4; }
.product-desc { font-size: 14px; color: #78909c; line-height: 1.7; margin: 0 0 20px; }

.price-box {
  background: linear-gradient(135deg, #fff5f5 0%, #fff0f0 100%);
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 24px;
}
.price-row { display: flex; align-items: baseline; gap: 12px; }
.price-label { font-size: 14px; color: #90a4ae; }
.price-current { font-size: 36px; font-weight: 700; color: #f56c6c; line-height: 1; }
.price-original { font-size: 16px; color: #b0bec5; text-decoration: line-through; }
.discount-row { display: flex; align-items: center; gap: 10px; margin-top: 10px; }
.discount-badge {
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}
.discount-text { font-size: 13px; color: #f56c6c; }

.info-list { margin-bottom: 28px; }
.info-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;
}
.info-label { width: 80px; font-size: 14px; color: #90a4ae; }
.info-value { font-size: 14px; color: #1a2a4a; font-weight: 500; }

.action-buttons { display: flex; gap: 16px; margin-bottom: 20px; }
.action-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
}
.buy-btn { background: #f56c6c; border-color: #f56c6c; }
.tip-alert { margin-top: 8px; }

/* 商品详情 */
.product-detail {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}
.detail-header {
  padding: 20px 30px;
  border-bottom: 2px solid #2c3e6b;
}
.detail-title { font-size: 18px; font-weight: 600; color: #2c3e6b; margin: 0; }
.detail-content { padding: 30px; }

.article-content {
  font-size: 15px;
  line-height: 1.9;
  color: #37474f;
  word-break: break-word;
  :deep(h1), :deep(h2), :deep(h3) { color: #1a2a4a; margin: 24px 0 12px; font-weight: 600; }
  :deep(p) { margin: 0 0 16px; }
  :deep(img) { max-width: 100%; height: auto; border-radius: 8px; margin: 16px auto; display: block; }
}

.detail-skeleton { background: #fff; border-radius: 12px; padding: 30px; }
.empty-state { background: #fff; border-radius: 12px; padding: 80px 0; }

@media (max-width: 992px) {
  .product-main { flex-direction: column; }
  .product-gallery { width: 100%; }
  .main-image { height: 360px; }
}
</style>
