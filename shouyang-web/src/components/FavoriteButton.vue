<template>
  <el-button
    :type="favorited ? 'danger' : 'default'"
    :plain="!favorited"
    :icon="favorited ? StarFilled : Star"
    :loading="loading"
    class="favorite-button"
    @click="handleClick"
  >
    {{ favorited ? '已收藏' : '收藏' }}
  </el-button>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { addFavorite, deleteFavorite, checkFavorite } from '@/api/favorite'

const props = defineProps({
  targetType: {
    type: String,
    required: true
  },
  targetId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['change'])

const router = useRouter()
const userStore = useUserStore()
const favorited = ref(false)
const loading = ref(false)

const checkStatus = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await checkFavorite(props.targetType, props.targetId)
    favorited.value = res.data?.favorited || false
  } catch (e) {
    console.error('检查收藏状态失败:', e)
  }
}

const handleClick = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再收藏')
    router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  loading.value = true
  try {
    if (favorited.value) {
      await deleteFavorite(props.targetType, props.targetId)
      favorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(props.targetType, props.targetId)
      favorited.value = true
      ElMessage.success('收藏成功')
    }
    emit('change', favorited.value)
  } catch (e) {
    console.error('收藏操作失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => { checkStatus() })
</script>

<style lang="scss" scoped>
.favorite-button {
  min-width: 100px;
}
</style>
