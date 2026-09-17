<template>
  <div class="upload-image">
    <el-upload
      class="image-uploader"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      accept="image/*"
    >
      <div v-if="modelValue" class="image-preview">
        <el-image :src="modelValue" fit="cover" class="preview-img" :preview-src-list="[modelValue]" />
        <div class="image-mask">
          <el-icon><Edit /></el-icon>
          <span>更换</span>
        </div>
      </div>
      <div v-else class="upload-placeholder">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <span class="upload-text">{{ placeholder }}</span>
      </div>
    </el-upload>
    <div v-if="modelValue" class="image-actions">
      <el-button type="danger" size="small" text @click="handleRemove">
        <el-icon><Delete /></el-icon>
        删除
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getToken } from '@/utils/request'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '上传图片'
  },
  maxSize: {
    type: Number,
    default: 5 // MB
  }
})

const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/admin/upload/image'
const uploadHeaders = {
  Authorization: `Bearer ${getToken()}`
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLtMax = file.size / 1024 / 1024 < props.maxSize
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLtMax) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

const handleSuccess = (response) => {
  if (response.code === 200 && response.data) {
    emit('update:modelValue', response.data)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleRemove = () => {
  emit('update:modelValue', '')
}
</script>

<style lang="scss" scoped>
.upload-image {
  display: inline-block;
}
.image-uploader {
  display: inline-block;
}
.image-preview {
  position: relative;
  width: 178px;
  height: 178px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  &:hover .image-mask {
    opacity: 1;
  }
}
.preview-img {
  width: 100%;
  height: 100%;
}
.image-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
  .el-icon {
    font-size: 20px;
  }
}
.upload-placeholder {
  width: 178px;
  height: 178px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    border-color: #2c3e6b;
    color: #2c3e6b;
  }
}
.upload-icon {
  font-size: 28px;
  color: #8c939d;
}
.upload-text {
  font-size: 12px;
  color: #8c939d;
}
.image-actions {
  margin-top: 8px;
}
</style>
