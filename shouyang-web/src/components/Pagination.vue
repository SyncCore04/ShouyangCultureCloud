<template>
  <div class="pagination-wrapper">
    <el-pagination
      :current-page="currentPage"
      :page-size="currentPageSize"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      :background="true"
      :hide-on-single-page="hideOnSinglePage"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  // 总条数
  total: {
    type: Number,
    default: 0
  },
  // 当前页码（支持 v-model:page）
  page: {
    type: Number,
    default: 1
  },
  // 每页条数
  pageSize: {
    type: Number,
    default: 10
  },
  // 每页条数可选项
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  // 布局配置
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  // 只有一页时是否隐藏
  hideOnSinglePage: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:page', 'update:pageSize', 'change'])

// 内部状态
const currentPage = ref(props.page)
const currentPageSize = ref(props.pageSize)

// 监听外部 props 变化
watch(() => props.page, (val) => {
  currentPage.value = val
})

watch(() => props.pageSize, (val) => {
  currentPageSize.value = val
})

// 页码改变
const handlePageChange = (page) => {
  currentPage.value = page
  emit('update:page', page)
  emit('change', { page, pageSize: currentPageSize.value })
}

// 每页条数改变
const handleSizeChange = (size) => {
  currentPageSize.value = size
  emit('update:pageSize', size)
  emit('change', { page: currentPage.value, pageSize: size })
}
</script>

<style lang="scss" scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px 0;

  :deep(.el-pagination) {
    .el-pagination__total {
      color: #78909c;
      font-size: 13px;
    }

    .el-pagination__sizes {
      .el-select .el-input__wrapper {
        border-radius: 6px;
      }
    }

    .btn-prev,
    .btn-next,
    .el-pager li {
      border-radius: 6px;
      min-width: 32px;
      height: 32px;
      line-height: 32px;
      font-size: 13px;
    }

    .el-pager li.is-active {
      background-color: #2c3e6b;
      color: #fff;
    }

    .el-pagination__jump {
      color: #78909c;
      font-size: 13px;

      .el-input__wrapper {
        border-radius: 6px;
      }
    }
  }
}
</style>
