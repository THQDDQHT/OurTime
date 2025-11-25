<template>
  <div class="album-list-container">
    <div class="section-header">
      <n-text class="section-title">我的时光集</n-text>
      <n-button type="primary" dashed @click="showCreateModal = true">
        <template #icon>
          <n-icon><add-outline /></n-icon>
        </template>
        新建相册
      </n-button>
    </div>

    <div v-if="errorMessage" class="error-state">
      <n-empty description="加载失败">
        <template #extra>
          <div class="error-actions">
            <n-text type="error">{{ errorMessage }}</n-text>
            <n-button size="small" @click="loadAlbums">重试</n-button>
          </div>
        </template>
      </n-empty>
    </div>

    <n-spin :show="loading" v-else>
      <div v-if="albums.length > 0" class="albums-grid">
        <n-card
          v-for="album in albums"
          :key="album.id"
          class="album-card"
          hoverable
          @click="handleView(album)"
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
                  <images-outline />
                </n-icon>
              </div>
              <div class="album-hover-overlay">
                <n-button class="enter-btn" secondary round type="primary">
                  进入相册
                </n-button>
              </div>
            </div>
          </template>

          <div class="album-info">
            <n-text strong class="album-name">{{ album.name }}</n-text>
            <n-text depth="3" class="date">
              {{ formatDate(album.createdAt) }}
            </n-text>
          </div>

          <template #action>
            <div class="album-actions">
              <n-text depth="3" class="description" v-if="album.description">
                {{ album.description }}
              </n-text>
              <div class="action-buttons">
                <n-button size="small" text @click.stop="handleEdit(album)">
                  <template #icon><create-outline /></template>
                </n-button>
                <n-button
                  size="small"
                  text
                  type="error"
                  @click.stop="handleDelete(album)"
                >
                  <template #icon><trash-outline /></template>
                </n-button>
              </div>
            </div>
          </template>
        </n-card>
      </div>
      <div v-else class="empty-state">
        <n-empty description="还没有创建相册" size="large">
          <template #extra>
            <n-button type="primary" @click="showCreateModal = true">
              创建第一个相册
            </n-button>
          </template>
        </n-empty>
      </div>
    </n-spin>

    <!-- 创建/编辑相册模态框 -->
    <n-modal
      v-model:show="showCreateModal"
      preset="card"
      style="width: 600px; max-width: 90vw"
      size="huge"
      :title="editingAlbum ? '编辑相册' : '创建相册'"
      :bordered="false"
    >
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="name" label="相册名称">
          <n-input v-model:value="form.name" placeholder="给这段回忆起个名字" />
        </n-form-item>
        <n-form-item path="description" label="描述">
          <n-input
            v-model:value="form.description"
            type="textarea"
            placeholder="写点什么来描述这个相册..."
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
                <div class="dragger-content">
                  <n-icon size="32" :depth="3">
                    <add-outline />
                  </n-icon>
                  <n-text depth="3">上传封面</n-text>
                </div>
              </n-upload-dragger>
            </n-upload>
          </div>
        </n-form-item>
      </n-form>

      <template #footer>
        <div class="modal-actions">
          <n-button @click="handleCancel">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ editingAlbum ? "保存" : "创建" }}
          </n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useMessage, useDialog } from "naive-ui";
import {
  NCard,
  NButton,
  NText,
  NImage,
  NIcon,
  NSpin,
  NEmpty,
  NModal,
  NForm,
  NFormItem,
  NInput,
  NUpload,
  NUploadDragger,
  type UploadCustomRequestOptions,
} from "naive-ui";
import {
  ImagesOutline,
  CameraOutline,
  AddOutline,
  CreateOutline,
  TrashOutline,
} from "@vicons/ionicons5";
import { getAlbums, createAlbum, updateAlbum, deleteAlbum } from "@/api/album";
import { uploadFile } from "@/api/upload";
import { resolveUploadUrl } from "@/utils/url";
import type { AlbumResponse, AlbumRequest } from "@/api/types";

const router = useRouter();
const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const submitting = ref(false);
const albums = ref<AlbumResponse[]>([]);
const showCreateModal = ref(false);
const editingAlbum = ref<AlbumResponse | null>(null);
const formRef = ref();
const errorMessage = ref("");

const form = ref<AlbumRequest>({
  name: "",
  description: "",
  coverUrl: "",
});

const rules = {
  name: {
    required: true,
    message: "请输入相册名称",
    trigger: "blur",
  },
};

const handleUpload = async ({
  file,
  onFinish,
  onError,
}: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return;
    const response = await uploadFile(file.file);
    form.value.coverUrl = response.url;
    onFinish();
    message.success("封面上传成功");
  } catch (error) {
    onError();
    message.error("封面上传失败");
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "short", // shorter month format
    day: "numeric",
  });
};

const loadAlbums = async () => {
  try {
    loading.value = true;
    errorMessage.value = "";
    console.log("开始加载相册列表...");
    albums.value = await getAlbums();
    console.log("相册列表加载成功:", albums.value);
  } catch (error: any) {
    console.error("加载相册失败:", error);
    errorMessage.value = error.message || "加载失败，请检查网络或后端服务";
    message.error(errorMessage.value);
  } finally {
    loading.value = false;
  }
};

const handleView = (album: AlbumResponse) => {
  router.push(`/albums/${album.id}`);
};

const handleEdit = (album: AlbumResponse) => {
  editingAlbum.value = album;
  form.value = {
    name: album.name,
    description: album.description || "",
    coverUrl: album.coverUrl || "",
  };
  showCreateModal.value = true;
};

const handleDelete = (album: AlbumResponse) => {
  dialog.warning({
    title: "确认删除",
    content: `确定要删除相册"${album.name}"吗？相册内的回忆不会被删除，但将不再属于任何相册。`,
    positiveText: "删除",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        await deleteAlbum(album.id);
        message.success("删除成功");
        loadAlbums();
      } catch (error: any) {
        message.error(error.message || "删除失败");
      }
    },
  });
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;

    if (editingAlbum.value) {
      await updateAlbum(editingAlbum.value.id, form.value);
      message.success("更新成功");
    } else {
      await createAlbum(form.value);
      message.success("创建成功");
    }

    showCreateModal.value = false;
    resetForm();
    loadAlbums();
  } catch (error: any) {
    message.error(error.message || "操作失败");
  } finally {
    submitting.value = false;
  }
};

const handleCancel = () => {
  showCreateModal.value = false;
  resetForm();
};

const resetForm = () => {
  editingAlbum.value = null;
  form.value = {
    name: "",
    description: "",
    coverUrl: "",
  };
  formRef.value?.restoreValidation();
};

onMounted(() => {
  loadAlbums();
});
</script>

<style scoped>
.album-list-container {
  padding: 20px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
}

.section-title {
  font-family: "Noto Serif SC", serif;
  font-size: 20px;
  font-weight: 600;
  color: #4e342e;
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.album-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.album-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.15);
}

.album-cover {
  width: 100%;
  height: 200px;
  position: relative;
  background: #f5f5f5;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.album-card:hover .cover-image {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafc;
}

.album-hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.album-card:hover .album-hover-overlay {
  opacity: 1;
}

.album-info {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
}

.album-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.date {
  font-size: 12px;
}

.album-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 24px;
}

.description {
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
  margin-right: auto;
}

.action-buttons {
  display: flex;
  gap: 4px;
  opacity: 0; /* Hidden by default */
  transition: opacity 0.2s;
}

.album-card:hover .action-buttons {
  opacity: 1;
}

/* Modal Styles */
.cover-upload-container {
  width: 100%;
}

.cover-dragger {
  background-color: #fafafc;
  border-radius: 8px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #e0e0e0;
}

.dragger-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.cover-preview {
  position: relative;
  width: 100%;
  height: 200px;
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

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.empty-state {
  padding: 60px 0;
}

.error-state {
  padding: 40px 0;
  display: flex;
  justify-content: center;
}

.error-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
</style>
