<template>
  <n-modal
    v-model:show="showModal"
    preset="card"
    title="数据录入终端"
    style="width: 800px; max-width: 90vw"
    :bordered="false"
    size="huge"
    :segmented="{
      content: 'soft',
      footer: 'soft',
    }"
    class="cyber-modal"
  >
    <n-form ref="formRef" :model="form" :rules="rules">
      <n-form-item path="content" label="记忆数据串">
        <n-input
          v-model:value="form.content"
          type="textarea"
          placeholder="输入原始数据..."
          :rows="5"
          class="cyber-input"
        />
      </n-form-item>

      <div class="grid-row">
        <n-form-item path="happenedAt" label="时间戳">
          <n-date-picker
            v-model:value="form.happenedAt"
            type="datetime"
            placeholder="选择日期"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item path="location" label="地理坐标">
          <n-input v-model:value="form.location" placeholder="经纬度 或 城市名" class="cyber-input" />
        </n-form-item>
      </div>

      <n-form-item path="albumId" label="归档文件夹">
        <n-select
          v-model:value="form.albumId"
          placeholder="选择目标路径"
          :options="albumOptions"
          clearable
        />
      </n-form-item>

      <n-form-item label="视觉附件">
        <n-upload
          v-model:file-list="fileList"
          multiple
          :max="9"
          accept="image/*"
          :custom-request="handleUpload"
          list-type="image-card"
          @remove="handleRemove"
        >
          <n-upload-dragger>
            <div class="upload-content">
              <n-icon size="48" :depth="3" class="upload-icon">
                 <!-- 上传图标 -->
                 <div class="upload-symbol">+</div>
              </n-icon>
            </div>
          </n-upload-dragger>
        </n-upload>
      </n-form-item>
    </n-form>

    <template #footer>
      <div class="actions">
        <n-button ghost @click="handleClose" class="cancel-btn">中止</n-button>
        <n-button type="primary" :loading="loading" @click="handleSubmit" class="cyber-btn">
          上传数据包
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useMessage } from "naive-ui";
import {
  NModal,
  NForm,
  NFormItem,
  NInput,
  NDatePicker,
  NSelect,
  NUpload,
  NButton,
  NIcon,
  NUploadDragger,
} from "naive-ui";
import type { UploadFileInfo, UploadCustomRequestOptions } from "naive-ui";
import { createMoment } from "@/api/moment";
import { uploadFile } from "@/api/upload";
import { getAlbums } from "@/api/album";
import type { AlbumResponse, PhotoRequest } from "@/api/types";

const props = defineProps<{
  show: boolean;
  initialAlbumId?: number;
}>();

const emit = defineEmits<{
  (e: "update:show", value: boolean): void;
  (e: "success"): void;
}>();

const message = useMessage();

const showModal = computed({
  get: () => props.show,
  set: (val) => emit("update:show", val),
});

const formRef = ref();
const loading = ref(false);
const fileList = ref<UploadFileInfo[]>([]);
const photos = ref<PhotoRequest[]>([]);
const albums = ref<AlbumResponse[]>([]);
const albumOptions = ref<Array<{ label: string; value: number }>>([]);

const form = ref({
  content: "",
  happenedAt: Date.now(),
  location: "",
  albumId: null as number | null,
  albumId: props.initialAlbumId || null, // fix: ensure initial value
});

// 移除验证规则，改为在提交时手动检查
const rules = {};

// 监听 modal 打开，重置表单
watch(
  () => props.show,
  (newVal) => {
    if (newVal) {
      resetForm();
      loadAlbums();
    }
  }
);

const resetForm = () => {
  form.value = {
    content: "",
    happenedAt: Date.now(),
    location: "",
    albumId: props.initialAlbumId || null,
  };
  fileList.value = [];
  photos.value = [];
};

const loadAlbums = async () => {
  try {
    albums.value = await getAlbums();
    albumOptions.value = albums.value.map((album) => ({
      label: album.name,
      value: album.id,
    }));
  } catch (error) {
    console.error("加载相册失败", error);
  }
};

// 压缩图片
const compressImage = (file: File): Promise<File> => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (e) => {
      const img = new Image();
      img.src = e.target?.result as string;
      img.onload = () => {
        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        const maxWidth = 1920;
        const maxHeight = 1920;
        let width = img.width;
        let height = img.height;

        if (width > maxWidth || height > maxHeight) {
          if (width > height) {
            height = (height * maxWidth) / width;
            width = maxWidth;
          } else {
            width = (width * maxHeight) / height;
            height = maxHeight;
          }
        }

        canvas.width = width;
        canvas.height = height;

        ctx?.drawImage(img, 0, 0, width, height);

        canvas.toBlob(
          (blob) => {
            if (blob) {
              const compressedFile = new File([blob], file.name, {
                type: file.type,
                lastModified: Date.now(),
              });
              resolve(compressedFile);
            } else {
              resolve(file);
            }
          },
          file.type,
          0.8
        );
      };
    };
  });
};

const handleUpload = async (options: UploadCustomRequestOptions) => {
  const { file } = options;
  try {
    const compressedFile = await compressImage(file.file as File);
    const response = await uploadFile(compressedFile);

    photos.value.push({
      filePath: response.url,
      width: response.width,
      height: response.height,
      size: response.size,
    });

    options.onFinish();
  } catch (error: any) {
    message.error("上传失败: " + (error.message || "未知错误"));
    options.onError();
  }
};

const handleRemove = (options: {
  file: UploadFileInfo;
  fileList: UploadFileInfo[];
}) => {
  const index = fileList.value.findIndex((f) => f.id === options.file.id);
  if (index > -1) {
    photos.value.splice(index, 1);
  }
};

const handleSubmit = async () => {
  try {
    if (photos.value.length === 0) {
      message.warning("需要视觉数据");
      return;
    }

    if (!form.value.happenedAt) {
      message.warning("需要时间戳");
      return;
    }

    await formRef.value?.validate();

    loading.value = true;

    const date = new Date(form.value.happenedAt);
    const happenedAt =
      date.getFullYear() +
      "-" +
      String(date.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(date.getDate()).padStart(2, "0") +
      "T" +
      String(date.getHours()).padStart(2, "0") +
      ":" +
      String(date.getMinutes()).padStart(2, "0") +
      ":" +
      String(date.getSeconds()).padStart(2, "0");

    await createMoment({
      content: form.value.content,
      happenedAt: happenedAt,
      location: form.value.location,
      albumId: form.value.albumId || undefined,
      photos: photos.value,
    });

    message.success("数据包已上传");
    emit("success");
    showModal.value = false;
  } catch (error: any) {
    message.error(error.message || "上传失败");
  } finally {
    loading.value = false;
  }
};

const handleClose = () => {
  showModal.value = false;
};

onMounted(() => {
  loadAlbums();
});
</script>

<style scoped>
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-symbol {
  font-size: 40px;
  color: var(--neon-blue);
  font-weight: bold;
}

.grid-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.cyber-btn {
  letter-spacing: 2px;
}

.cancel-btn {
  color: rgba(255,255,255,0.6);
}
.cancel-btn:hover {
  color: #fff;
}
</style>
