<template>
  <div class="item-card" @click="handleClick">
    <!-- 封面图 -->
    <div class="card-cover">
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
            <el-icon :size="32" color="#cfd8dc"><Picture /></el-icon>
          </div>
        </template>
      </el-image>
      <!-- 角标（根据 type 显示） -->
      <span v-if="badgeText" class="card-badge" :class="`badge-${type}`">
        {{ badgeText }}
      </span>
    </div>

    <!-- 文字区 -->
    <div class="card-body">
      <h4 class="card-title">{{ titleText }}</h4>
      <p class="card-desc">{{ descText }}</p>
      <div class="card-meta">
        <!-- 日期（news/activity 等） -->
        <span v-if="metaDate" class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ metaDate }}
        </span>
        <!-- 地址（scenic/hotel 等） -->
        <span v-if="metaAddress" class="meta-item">
          <el-icon><Location /></el-icon>
          {{ metaAddress }}
        </span>
        <!-- 价格（product/ticket 等） -->
        <span v-if="metaPrice !== null && metaPrice !== undefined" class="meta-item price">
          <el-icon><Money /></el-icon>
          ¥{{ metaPrice }}
        </span>
        <!-- 分类（food/heritage 等） -->
        <span v-if="metaCategory" class="meta-item">
          <el-icon><Collection /></el-icon>
          {{ metaCategory }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, Clock, Location, Money, Collection } from '@element-plus/icons-vue'

const props = defineProps({
  // 数据对象
  item: {
    type: Object,
    required: true
  },
  // 卡片类型：news / scenic / food / hotel / product / heritage / pavilion / activity / ticket / venue / org / guide
  type: {
    type: String,
    default: 'news'
  }
})

const emit = defineEmits(['click'])
const router = useRouter()

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/shouyang-default/400/300'

// 封面图 URL
const coverUrl = computed(() => {
  return props.item.coverImage || props.item.image || props.item.cover || props.item.avatar || defaultCover
})

// 标题
const titleText = computed(() => {
  return props.item.title || props.item.name || props.item.nickname || '暂无标题'
})

// 描述
const descText = computed(() => {
  return props.item.description || props.item.summary || props.item.intro || props.item.content || '暂无描述'
})

// 角标文字
const badgeText = computed(() => {
  if (props.type === 'scenic' && props.item.level) return props.item.level
  if (props.type === 'hotel' && props.item.star) return `${props.item.star}星`
  if (props.type === 'activity' && props.item.status) {
    const statusMap = { 0: '未开始', 1: '进行中', 2: '已结束' }
    return statusMap[props.item.status] || ''
  }
  if (props.type === 'product' && props.item.price !== undefined) return '热卖'
  return ''
})

// 元信息：日期
const metaDate = computed(() => {
  const date = props.item.createTime || props.item.date || props.item.startTime || props.item.publishTime
  if (!date) return ''
  return typeof date === 'string' ? date.substring(0, 10) : ''
})

// 元信息：地址
const metaAddress = computed(() => {
  return props.item.address || props.item.location || ''
})

// 元信息：价格
const metaPrice = computed(() => {
  return props.item.price !== undefined ? props.item.price : null
})

// 元信息：分类
const metaCategory = computed(() => {
  return props.item.category || props.item.categoryName || props.item.typeName || ''
})

// 类型对应的详情路由
const routeMap = {
  news: '/news/',
  scenic: '/travel/scenic/',
  food: '/travel/food/',
  hotel: '/travel/hotel/',
  product: '/culture/product/',
  heritage: '/culture/heritage/',
  pavilion: '/culture/pavilion/',
  activity: '/service/activity/',
  ticket: '/service/ticket/',
  venue: '/service/venue/',
  guide: '/travel/guide/',
  org: '/org/'
}

// 点击跳转
const handleClick = () => {
  emit('click', props.item)
  const basePath = routeMap[props.type] || '/'
  if (props.item.id) {
    router.push(basePath + props.item.id)
  }
}
</script>

<style lang="scss" scoped>
.item-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
    border-color: transparent;

    .cover-img {
      transform: scale(1.08);
    }

    .card-title {
      color: #2c3e6b;
    }
  }
}

/* 封面图 */
.card-cover {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 比例 */
  overflow: hidden;
  background: #f5f7fa;
}

.cover-img {
  position: absolute;
  top: 0;
  left: 0;
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

/* 角标 */
.card-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  z-index: 2;

  &.badge-scenic {
    background: rgba(255, 152, 0, 0.95);
  }

  &.badge-hotel {
    background: rgba(255, 193, 7, 0.95);
    color: #5d4037;
  }

  &.badge-activity {
    background: rgba(102, 187, 106, 0.95);
  }

  &.badge-product {
    background: rgba(229, 115, 115, 0.95);
  }
}

/* 文字区 */
.card-body {
  padding: 14px 16px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 8px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}

.card-desc {
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

/* 元信息 */
.card-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #b0bec5;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;

  &.price {
    color: #e57373;
    font-weight: 600;
  }
}
</style>
