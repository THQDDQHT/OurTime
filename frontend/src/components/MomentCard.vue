<template>
  <div class="moment-card">
    <div class="card-header">
      <div class="meta-info">
        <span class="date">{{ formatDate(moment.happenedAt) }}</span>
        <div class="meta-extras">
          <span v-if="moment.location" class="location">
            <n-icon size="12"><location-outline /></n-icon>
            {{ moment.location }}
          </span>
          <span
            v-if="moment.albumName"
            class="album-tag"
            :class="{ clickable: !!moment.albumId }"
            @click.stop="handleAlbumClick"
          >
            <n-icon size="12"><folder-outline /></n-icon> {{ moment.albumName }}
          </span>
        </div>
      </div>
      <n-popconfirm
        @positive-click="handleDelete"
        positive-text="删除"
        negative-text="取消"
      >
        <template #trigger>
          <n-button text class="delete-btn">
            <template #icon>
              <n-icon><trash-outline /></n-icon>
            </template>
          </n-button>
        </template>
        确定要删除这段回忆吗？
      </n-popconfirm>
    </div>

    <div v-if="moment.content" class="card-content">
      <p>{{ moment.content }}</p>
    </div>

    <div v-if="moment.photos && moment.photos.length > 0" class="card-photos">
      <n-image-group>
        <div class="photo-grid" :class="getGridClass(moment.photos.length)">
          <div
            v-for="photo in moment.photos"
            :key="photo.id"
            class="photo-wrapper"
          >
            <n-image
              :src="resolveUploadUrl(photo.filePath)"
              :alt="moment.content"
              object-fit="cover"
              preview-disabled
              class="photo-img"
            />
          </div>
        </div>
      </n-image-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  NButton,
  NImage,
  NImageGroup,
  NIcon,
  NPopconfirm,
  useMessage,
} from "naive-ui";
import {
  TrashOutline,
  LocationOutline,
  FolderOutline,
} from "@vicons/ionicons5";
import { deleteMoment } from "@/api/moment";
import type { MomentResponse } from "@/api/types";
import { resolveUploadUrl } from "@/utils/url";
import { useRouter } from "vue-router";

const props = defineProps<{
  moment: MomentResponse;
}>();

const emit = defineEmits<{
  deleted: [id: number];
}>();

const message = useMessage();
const router = useRouter();

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  const options: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  };
  return date.toLocaleDateString("zh-CN", options);
};

const getGridClass = (count: number) => {
  if (count === 1) return "grid-1";
  if (count === 2) return "grid-2";
  if (count === 4) return "grid-2-2";
  return "grid-3";
};

const handleAlbumClick = () => {
  if (props.moment.albumId) {
    router.push(`/albums/${props.moment.albumId}`);
  }
};

const handleDelete = async () => {
  try {
    await deleteMoment(props.moment.id);
    message.success("回忆已移除");
    emit("deleted", props.moment.id);
  } catch (error: any) {
    message.error(error.message || "删除失败");
  }
};
</script>

<style scoped>
.moment-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05),
    0 10px 20px -5px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid rgba(0, 0, 0, 0.02);
  position: relative;
}

.moment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 15px 25px -5px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #eee;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date {
  font-family: "Noto Serif SC", serif;
  font-size: 1.1rem;
  font-weight: 600;
  color: #3e2723;
}

.meta-extras {
  display: flex;
  gap: 12px;
  font-size: 0.85rem;
  color: #90a4ae;
}

.location,
.album-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.album-tag.clickable {
  cursor: pointer;
  color: #5d4037;
  transition: color 0.2s, text-decoration-color 0.2s;
}

.album-tag.clickable:hover {
  color: #3e2723;
  text-decoration: underline;
}

.delete-btn {
  opacity: 0.4;
  transition: opacity 0.2s;
}

.delete-btn:hover {
  opacity: 1;
  color: #d32f2f;
}

.card-content {
  margin-bottom: 16px;
  color: #455a64;
  line-height: 1.6;
  font-size: 1rem;
  white-space: pre-wrap;
}

.photo-grid {
  display: grid;
  gap: 8px;
  border-radius: 8px;
  overflow: hidden;
}

.grid-1 {
  grid-template-columns: 1fr;
}

.grid-2 {
  grid-template-columns: repeat(2, 1fr);
}

/* Special 2x2 layout for 4 images */
.grid-2-2 {
  grid-template-columns: repeat(2, 1fr);
}

.grid-3 {
  grid-template-columns: repeat(3, 1fr); /* Default for 3, 5, 6, etc. */
}

.photo-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1; /* Square photos for uniform grid */
  overflow: hidden;
  background: #f5f5f5;
}

.grid-1 .photo-wrapper {
  aspect-ratio: auto; /* Let single photo keep its ratio or be wider */
  max-height: 500px;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.photo-wrapper:hover .photo-img {
  transform: scale(1.05);
}
</style>
