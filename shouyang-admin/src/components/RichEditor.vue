<template>
  <div class="rich-editor">
    <div
      ref="editorRef"
      class="editor-container"
      :style="{ height: height + 'px' }"
    ></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import '@wangeditor/editor/dist/css/style.css'
import { createEditor, createToolbar } from '@wangeditor/editor'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: Number,
    default: 400
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
let editor = null
let toolbar = null
let isInternalChange = false

const initEditor = () => {
  if (!editorRef.value) return

  // 创建编辑器
  editor = createEditor({
    selector: editorRef.value,
    html: props.modelValue || '',
    config: {
      placeholder: props.placeholder,
      MENU_CONF: {
        uploadImage: {
          // 图片上传配置（使用后台上传接口）
          server: '/api/admin/upload/image',
          fieldName: 'file',
          maxFileSize: 5 * 1024 * 1024,
          headers: {
            Authorization: `Bearer ${localStorage.getItem('shouyang_admin_token')}`
          },
          customInsert(res, insertFn) {
            if (res.code === 200 && res.data) {
              insertFn(res.data, '', '')
            }
          }
        }
      },
      onChange(editor) {
        if (!isInternalChange) {
          emit('update:modelValue', editor.getHtml())
        }
      }
    },
    mode: 'default'
  })
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (editor && newVal !== editor.getHtml()) {
    isInternalChange = true
    editor.setHtml(newVal || '')
    isInternalChange = false
  }
})

onMounted(() => {
  initEditor()
})

onBeforeUnmount(() => {
  if (editor) {
    editor.destroy()
    editor = null
  }
  if (toolbar) {
    toolbar.destroy()
    toolbar = null
  }
})

// 暴露获取内容方法
defineExpose({
  getHtml: () => editor?.getHtml() || '',
  setHtml: (html) => editor?.setHtml(html)
})
</script>

<style lang="scss" scoped>
.rich-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  &:focus-within {
    border-color: #2c3e6b;
  }
}
.editor-container {
  width: 100%;
  overflow-y: auto;
  :deep(.w-e-text-container) {
    z-index: 1;
  }
  :deep(.w-e-toolbar) {
    border-bottom: 1px solid #e4e7ed;
  }
}
</style>
