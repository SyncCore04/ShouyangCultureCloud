<template>
  <div class="placeholder-page">
    <el-result icon="info" :title="title" sub-title="该模块正在开发中，敬请期待">
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回仪表盘</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title || '页面开发中')
</script>

<style lang="scss" scoped>
.placeholder-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
</style>
