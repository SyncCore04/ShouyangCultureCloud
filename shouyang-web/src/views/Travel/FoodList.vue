<template>
  <div class="page-placeholder">
    <el-result icon="info" :title="pageTitle" sub-title="页面开发中，敬请期待">
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageTitle = computed(() => route.meta.title || '页面')
</script>

<style lang="scss" scoped>
.page-placeholder {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
</style>
