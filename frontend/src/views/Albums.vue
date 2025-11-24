<template>
  <div class="albums-container">
    <n-card title="相册管理" class="albums-card">
      <template #header-extra>
        <div class="header-actions">
          <n-button @click="handleBack">返回</n-button>
          <n-button type="primary" @click="showCreateModal = true">
            创建相册
          </n-button>
        </div>
      </template>
      
      <n-spin :show="loading">
        <div v-if="albums.length > 0" class="albums-grid">
          <n-card
            v-for="album in albums"
            :key="album.id"
            class="album-card"
            hoverable
            @click="handleEdit(album)"
          >
            <template #cover>
              <div class="album-cover">
                <n-image
                  v-if="album.coverUrl"
                  :src="resolveUploadUrl(album.coverUrl)"
                  object-fit="cover"
                  preview-disabled
                  class="cover-image"
                />
                <div v-else class="cover-placeholder">
                  <n-icon size="48" :depth="3">
                    <image-outline />
                  </n-icon>
                </div>
              </div>
            </template>
            
            <div class="album-info">
              <n-text strong>{{ album.name }}</n-text>
              <n-text v-if="album.description" depth="3" class="description">
                {{ album.description }}
              </n-text>
              <n-text depth="3" class="date">
                {{ formatDate(album.createdAt) }}
              </n-text>
            </div>
            
            <template #action>
              <div class="album-actions">
                <n-button
                  text
                  type="primary"
                  @click.stop="handleEdit(album)"
                >
                  编辑
                </n-button>
                <n-button
                  text
                  type="error"
                  @click.stop="handleDelete(album)"
                >
                  删除
                </n-button>
              </div>
            </template>
          </n-card>
        </div>
        <n-empty v-else description="还没有相册，创建一个吧！" />
      </n-spin>
    </n-card>
    
    <!-- 创建/编辑相册模态框 -->
    <n-modal v-model:show="showCreateModal" preset="card" :title="editingAlbum ? '编辑相册' : '创建相册'" class="album-modal">
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="name" label="相册名称">
          <n-input v-model:value="form.name" placeholder="输入相册名称" />
        </n-form-item>
        <n-form-item path="description" label="描述">
          <n-input
            v-model:value="form.description"
            type="textarea"
            placeholder="输入相册描述（可选）"
            :rows="3"
          />
        </n-form-item>
        <n-form-item path="coverUrl" label="封面图">
          <div class="cover-upload-container">
            <n-upload
              action="#"
              :custom-request="handleUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <div v-if="form.coverUrl" class="cover-preview">
                <img :src="resolveUploadUrl(form.coverUrl)" alt="cover" />
                <div class="cover-mask">
                  <n-icon size="24" color="#fff"><camera-outline /></n-icon>
                  <span>更换封面</span>
                </div>
              </div>
              <n-upload-dragger v-else class="cover-dragger">
                <div style="margin-bottom: 12px">
                  <n-icon size="48" :depth="3">
                    <image-outline />
                  </n-icon>
                </div>
                <n-text style="font-size: 16px">
                  点击或拖拽上传封面
                </n-text>
              </n-upload-dragger>
            </n-upload>
          </div>
        </n-form-item>
      </n-form>
      
      <template #footer>
        <div class="modal-actions">
          <n-button @click="handleCancel">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ editingAlbum ? '保存' : '创建' }}
          </n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, useDialog } from 'naive-ui'
import {
  NCard, NButton, NText, NImage, NIcon, NSpin, NEmpty, NModal, NForm, NFormItem, NInput,
  NUpload, NUploadDragger, type UploadCustomRequestOptions
} from 'naive-ui'
import { ImageOutline, CameraOutline } from '@vicons/ionicons5'
import { getAlbums, createAlbum, updateAlbum, deleteAlbum } from '@/api/album'
import { uploadFile } from '@/api/upload'
import { resolveUploadUrl } from '@/utils/url'
import type { AlbumResponse, AlbumRequest } from '@/api/types'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const submitting = ref(false)
const albums = ref<AlbumResponse[]>([])
const showCreateModal = ref(false)
const editingAlbum = ref<AlbumResponse | null>(null)
const formRef = ref()

const form = ref<AlbumRequest>({
  name: '',
  description: '',
  coverUrl: ''
})

const rules = {
  name: {
    required: true,
    message: '请输入相册名称',
    trigger: 'blur'
  }
}

const handleUpload = async ({ file, onFinish, onError }: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return
    const response = await uploadFile(file.file)
    form.value.coverUrl = response.url
    onFinish()
    message.success('封面上传成功')
  } catch (error) {
    onError()
    message.error('封面上传失败')
  }
}


const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const loadAlbums = async () => {
  try {
    loading.value = true
    albums.value = await getAlbums()
  } catch (error: any) {
    message.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = (album: AlbumResponse) => {
  editingAlbum.value = album
  form.value = {
    name: album.name,
    description: album.description || '',
    coverUrl: album.coverUrl || ''
  }
  showCreateModal.value = true
}

const handleDelete = (album: AlbumResponse) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除相册"${album.name}"吗？此操作不可恢复。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteAlbum(album.id)
        message.success('删除成功')
        loadAlbums()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    
    if (editingAlbum.value) {
      await updateAlbum(editingAlbum.value.id, form.value)
      message.success('更新成功')
    } else {
      await createAlbum(form.value)
      message.success('创建成功')
    }
    
    showCreateModal.value = false
    resetForm()
    loadAlbums()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  showCreateModal.value = false
  resetForm()
}

const handleBack = () => {
  router.push('/')
}

const resetForm = () => {
  editingAlbum.value = null
  form.value = {
    name: '',
    description: '',
    coverUrl: ''
  }
  formRef.value?.restoreValidation()
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped>
.albums-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.albums-card {
  margin-top: 20px;
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.album-card {
  cursor: pointer;
}

.album-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.album-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.description {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.date {
  font-size: 12px;
}

.album-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.album-modal {
  width: 600px;
}

.cover-upload-container {
  width: 100%;
}

.cover-dragger {
  background-color: #fafafc;
  border-radius: 8px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-preview {
  position: relative;
  width: 100%;
  height: 240px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  gap: 8px;
}

.cover-preview:hover .cover-mask {
  opacity: 1;
}
</style>

