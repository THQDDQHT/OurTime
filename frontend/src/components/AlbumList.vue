<template>
  <div class="album-list-container py-5">
    <div
      class="section-header flex justify-between items-center mb-6 px-2 border-b border-neon-blue/20 pb-2.5"
    >
      <n-text
        class="section-title font-share-tech-mono text-2xl text-neon-blue text-shadow-glow-blue-sm"
        >归档模块 // 相册集</n-text
      >
      <n-button type="primary" dashed @click="showCreateModal = true">
        <template #icon>
          <n-icon><add-outline /></n-icon>
        </template>
        新建归档
      </n-button>
    </div>

    <div v-if="errorMessage">
      <n-empty description="数据损坏">
        <template #extra>
          <div>
            <n-text type="error">{{ errorMessage }}</n-text>
            <n-button size="small" @click="loadAlbums">重试连接</n-button>
          </div>
        </template>
      </n-empty>
    </div>

    <n-spin :show="loading" v-else>
      <div
        v-if="albums.length > 0"
        class="albums-grid grid grid-cols-[repeat(auto-fill,minmax(260px,1fr))] gap-6"
      >
        <div
          v-for="album in albums"
          :key="album.id"
          class="cyber-card relative bg-dark-bg/60 border border-neon-blue/30 cursor-pointer overflow-hidden transition-all duration-300 hover:border-neon-blue hover:shadow-glow-blue-sm hover:-translate-y-1"
          @click="handleView(album)"
        >
          <!-- 角落装饰 -->
          <div class="corner-deco tl"></div>
          <div class="corner-deco tr"></div>
          <div class="corner-deco bl"></div>
          <div class="corner-deco br"></div>

          <div
            class="card-cover w-full h-[180px] relative bg-black overflow-hidden"
          >
            <n-image
              v-if="album.coverUrl"
              :src="resolveUploadUrl(album.coverUrl)"
              object-fit="cover"
              preview-disabled
              class="cover-image w-full h-full opacity-80 grayscale-[40%] transition-all duration-500"
            />
            <div v-else class="cover-placeholder">
              <n-icon size="48" :depth="3" class="text-gray-700">
                <images-outline />
              </n-icon>
            </div>

            <!-- 扫描覆盖层 -->
            <div class="scan-overlay"></div>
            <div
              class="hover-actions absolute top-0 left-0 right-0 bottom-0 bg-black/60 flex items-center justify-center opacity-0 transition-opacity duration-300"
            >
              <n-button class="font-share-tech-mono tracking-[2px]"
                >访问数据</n-button
              >
            </div>
          </div>

          <div class="card-info p-3 border-b border-neon-blue/10">
            <div class="info-header flex justify-between items-center mb-1">
              <n-text
                class="album-name text-white font-rajdhani font-bold text-lg uppercase"
                >{{ album.name }}</n-text
              >
              <n-text
                class="id-tag text-[10px] text-neon-purple font-share-tech-mono"
                >编号:{{ album.id.toString().padStart(4, "0") }}</n-text
              >
            </div>
            <n-text class="date text-[10px] text-white/50"
              >创建于: {{ formatDate(album.createdAt) }}</n-text
            >
          </div>

          <div
            class="card-footer p-2.5 px-3 flex justify-between items-center min-h-[40px]"
          >
            <n-text
              class="description text-xs text-neon-blue/70 whitespace-nowrap overflow-hidden text-ellipsis max-w-[70%]"
              v-if="album.description"
            >
              {{ album.description }}
            </n-text>
            <div class="action-buttons flex gap-1">
              <n-button
                size="tiny"
                text
                @click.stop="handleEdit(album)"
                class="action-icon text-white/50 hover:text-neon-blue"
              >
                <template #icon><create-outline /></template>
              </n-button>
              <n-button
                size="tiny"
                text
                type="error"
                @click.stop="handleDelete(album)"
                class="action-icon delete hover:text-neon-red"
              >
                <template #icon><trash-outline /></template>
              </n-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <n-empty description="未发现归档" size="large">
          <template #extra>
            <n-button type="primary" @click="showCreateModal = true">
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
    >
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="name" label="归档名称">
          <n-input v-model:value="form.name" placeholder="输入名称..." />
        </n-form-item>
        <n-form-item path="description" label="元数据描述">
          <n-input
            v-model:value="form.description"
            type="textarea"
            placeholder="输入元数据..."
            :rows="3"
          />
        </n-form-item>
        <n-form-item path="coverUrl" label="封面图像">
          <div class="w-full">
            <n-upload
              action="#"
              :custom-request="handleUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <div
                v-if="form.coverUrl"
                class="relative w-full h-[200px] border border-neon-blue overflow-hidden"
              >
                <img
                  :src="resolveUploadUrl(form.coverUrl)"
                  alt="cover"
                  class="w-full h-full object-cover"
                />
                <div
                  class="absolute top-0 left-0 w-full h-full bg-black/70 flex flex-col items-center justify-center opacity-0 transition-opacity duration-300 text-neon-blue font-share-tech-mono hover:opacity-100"
                >
                  <n-icon size="24" color="#00f3ff"><camera-outline /></n-icon>
                  <span>更新图像</span>
                </div>
              </div>
              <n-upload-dragger v-else class="cover-dragger">
                <div class="dragger-content">
                  <n-icon
                    size="32"
                    :depth="3"
                    class="upload-icon text-neon-blue"
                  >
                    <add-outline />
                  </n-icon>
                </div>
              </n-upload-dragger>
            </n-upload>
          </div>
        </n-form-item>
      </n-form>

      <template #footer>
        <div class="flex justify-end gap-3">
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
/* 保留动画和特殊效果 */
.cyber-card:hover .cover-image {
  filter: grayscale(0%);
  opacity: 1;
  transform: scale(1.05);
}

.cyber-card:hover .hover-actions {
  opacity: 1;
}

.corner-deco {
  position: absolute;
  width: 8px;
  height: 8px;
  border: 2px solid #00f3ff;
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

/* Tailwind 样式已应用，保留必要的过渡效果 */
</style>
