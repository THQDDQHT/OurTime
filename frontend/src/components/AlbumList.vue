<template>
  <div class="album-list-container">
    <div class="section-header">
      <n-text class="section-title">归档模块 // 相册集</n-text>
      <n-button
        type="primary"
        dashed
        @click="showCreateModal = true"
        class="cyber-btn-dashed"
      >
        <template #icon>
          <n-icon><add-outline /></n-icon>
        </template>
        新建归档
      </n-button>
    </div>

    <div v-if="errorMessage" class="error-state">
      <n-empty description="数据损坏">
        <template #extra>
          <div class="error-actions">
            <n-text type="error">{{ errorMessage }}</n-text>
            <n-button size="small" @click="loadAlbums" class="cyber-btn-small"
              >重试连接</n-button
            >
          </div>
        </template>
      </n-empty>
    </div>

    <n-spin :show="loading" v-else>
      <div v-if="albums.length > 0" class="albums-grid">
        <div
          v-for="album in albums"
          :key="album.id"
          class="cyber-card"
          @click="handleView(album)"
        >
          <!-- 角落装饰 -->
          <div class="corner-deco tl"></div>
          <div class="corner-deco tr"></div>
          <div class="corner-deco bl"></div>
          <div class="corner-deco br"></div>

          <div class="card-cover">
            <n-image
              v-if="album.coverUrl"
              :src="resolveUploadUrl(album.coverUrl)"
              object-fit="cover"
              preview-disabled
              class="cover-image"
            />
            <div v-else class="cover-placeholder">
              <n-icon size="48" :depth="3" class="placeholder-icon">
                <images-outline />
              </n-icon>
            </div>

            <!-- 扫描覆盖层 -->
            <div class="scan-overlay"></div>
            <div class="hover-actions">
              <n-button class="enter-btn cyber-btn-glitch">访问数据</n-button>
            </div>
          </div>

          <div class="card-info">
            <div class="info-header">
              <n-text class="album-name">{{ album.name }}</n-text>
              <n-text class="id-tag"
                >编号:{{ album.id.toString().padStart(4, "0") }}</n-text
              >
            </div>
            <n-text class="date"
              >创建于: {{ formatDate(album.createdAt) }}</n-text
            >
          </div>

          <div class="card-footer">
            <n-text class="description" v-if="album.description">
              {{ album.description }}
            </n-text>
            <div class="action-buttons">
              <n-button
                size="tiny"
                text
                @click.stop="handleEdit(album)"
                class="action-icon"
              >
                <template #icon><create-outline /></template>
              </n-button>
              <n-button
                size="tiny"
                text
                type="error"
                @click.stop="handleDelete(album)"
                class="action-icon delete"
              >
                <template #icon><trash-outline /></template>
              </n-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <n-empty description="未发现归档" size="large">
          <template #extra>
            <n-button
              type="primary"
              @click="showCreateModal = true"
              class="cyber-btn"
            >
              初始化首个归档
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
      :title="editingAlbum ? '修改归档配置' : '新建归档协议'"
      :bordered="false"
      class="cyber-modal"
    >
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="name" label="归档名称">
          <n-input
            v-model:value="form.name"
            placeholder="输入名称..."
            class="cyber-input"
          />
        </n-form-item>
        <n-form-item path="description" label="元数据描述">
          <n-input
            v-model:value="form.description"
            type="textarea"
            placeholder="输入元数据..."
            :rows="3"
            class="cyber-input"
          />
        </n-form-item>
        <n-form-item path="coverUrl" label="封面图像">
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
                  <n-icon size="24" color="#00f3ff"><camera-outline /></n-icon>
                  <span>更新图像</span>
                </div>
              </div>
              <n-upload-dragger v-else class="cover-dragger">
                <div class="dragger-content">
                  <n-icon size="32" :depth="3" class="upload-icon">
                    <add-outline />
                  </n-icon>
                </div>
              </n-upload-dragger>
            </n-upload>
          </div>
        </n-form-item>
      </n-form>

      <template #footer>
        <div class="modal-actions">
          <n-button ghost @click="handleCancel" class="cancel-btn"
            >终止</n-button
          >
          <n-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
            class="cyber-btn"
          >
            {{ editingAlbum ? "保存配置" : "执行" }}
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
    message: "请输入名称",
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
    message.success("图像已上传");
  } catch (error) {
    onError();
    message.error("上传失败");
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toISOString().split("T")[0]; // YYYY-MM-DD
};

const loadAlbums = async () => {
  try {
    loading.value = true;
    errorMessage.value = "";
    albums.value = await getAlbums();
  } catch (error: any) {
    errorMessage.value = error.message || "连接错误";
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
    content: `删除归档 "${album.name}"? 数据将解除关联但不会被销毁。`,
    positiveText: "删除",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        await deleteAlbum(album.id);
        message.success("归档已删除");
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
      message.success("配置已更新");
    } else {
      await createAlbum(form.value);
      message.success("归档已创建");
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
  border-bottom: 1px solid rgba(0, 243, 255, 0.2);
  padding-bottom: 10px;
}

.section-title {
  font-family: "Share Tech Mono", monospace;
  font-size: 24px;
  color: var(--neon-blue);
  text-shadow: 0 0 5px rgba(0, 243, 255, 0.5);
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

/* Cyber Card Styles */
.cyber-card {
  position: relative;
  background: rgba(5, 11, 20, 0.6);
  border: 1px solid rgba(0, 243, 255, 0.3);
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cyber-card:hover {
  border-color: var(--neon-blue);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.2);
  transform: translateY(-5px);
}

.corner-deco {
  position: absolute;
  width: 8px;
  height: 8px;
  border: 2px solid var(--neon-blue);
  transition: all 0.3s;
  opacity: 0.5;
  z-index: 2;
}
.cyber-card:hover .corner-deco {
  opacity: 1;
  width: 12px;
  height: 12px;
}
.tl {
  top: 0;
  left: 0;
  border-right: 0;
  border-bottom: 0;
}
.tr {
  top: 0;
  right: 0;
  border-left: 0;
  border-bottom: 0;
}
.bl {
  bottom: 0;
  left: 0;
  border-right: 0;
  border-top: 0;
}
.br {
  bottom: 0;
  right: 0;
  border-left: 0;
  border-top: 0;
}

.card-cover {
  width: 100%;
  height: 180px;
  position: relative;
  background: #000;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  opacity: 0.8;
  filter: grayscale(40%);
  transition: all 0.5s;
}
.cyber-card:hover .cover-image {
  filter: grayscale(0%);
  opacity: 1;
  transform: scale(1.05);
}

.scan-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 243, 255, 0.1) 3px
  );
  pointer-events: none;
}

.hover-actions {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.cyber-card:hover .hover-actions {
  opacity: 1;
}

.cyber-btn-glitch {
  background: transparent;
  border: 1px solid var(--neon-blue);
  color: var(--neon-blue);
  font-family: "Share Tech Mono";
  letter-spacing: 2px;
}
.cyber-btn-glitch:hover {
  background: var(--neon-blue);
  color: #000;
  box-shadow: 0 0 10px var(--neon-blue);
}

.card-info {
  padding: 12px;
  border-bottom: 1px solid rgba(0, 243, 255, 0.1);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.album-name {
  color: #fff;
  font-family: "Rajdhani";
  font-weight: bold;
  font-size: 18px;
  text-transform: uppercase;
}

.id-tag {
  font-size: 10px;
  color: var(--neon-purple);
  font-family: "Share Tech Mono";
}

.date {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.5);
}

.card-footer {
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 40px;
}

.description {
  font-size: 12px;
  color: rgba(0, 243, 255, 0.7);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.action-icon {
  color: rgba(255, 255, 255, 0.5);
}
.action-icon:hover {
  color: var(--neon-blue);
}
.action-icon.delete:hover {
  color: var(--neon-red);
}

/* Modal Styles */
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.cover-upload-container {
  width: 100%;
}
.cover-preview {
  position: relative;
  width: 100%;
  height: 200px;
  border: 1px solid var(--neon-blue);
  overflow: hidden;
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
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: var(--neon-blue);
  font-family: "Share Tech Mono";
}
.cover-preview:hover .cover-mask {
  opacity: 1;
}

.placeholder-icon {
  color: #333;
}
.upload-icon {
  color: var(--neon-blue);
}
</style>
