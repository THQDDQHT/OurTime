<template>
  <div class="moment-card">
    <div class="card-header">
      <div class="meta-info">
        <span class="date">时间戳: {{ formatDate(moment.happenedAt) }}</span>
        <div class="meta-extras">
          <span v-if="moment.location" class="location">
            <n-icon size="12"><location-outline /></n-icon>
            坐标: {{ moment.location }}
          </span>
          <span
            v-if="moment.albumName"
            class="album-tag"
            :class="{ clickable: !!moment.albumId }"
            @click.stop="handleAlbumClick"
          >
            <n-icon size="12"><folder-outline /></n-icon> 索引:
            {{ moment.albumName }}
          </span>
        </div>
      </div>

      <n-popconfirm
        @positive-click="handleDelete"
        positive-text="确认"
        negative-text="取消"
      >
        <template #trigger>
          <n-button text class="delete-btn">
            <template #icon>
              <n-icon><trash-outline /></n-icon>
            </template>
          </n-button>
        </template>
        <span style="font-family: 'Share Tech Mono'">清除此记忆碎片?</span>
      </n-popconfirm>
    </div>

    <div v-if="moment.content" class="card-content">
      <div class="content-text">> {{ moment.content }}</div>
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
            <div class="img-overlay"></div>
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
  // 格式化为更科技感的日期：2023.12.25 - 14:30:00
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const min = String(date.getMinutes()).padStart(2, "0");
  return `${yyyy}.${mm}.${dd} // ${hh}:${min}`;
};

const getGridClass = (count: number) => {
  if (count === 1) return "grid-1";
  if (count === 2) return "grid-2";
  if (count === 4) return "grid-2-2";
  return "grid-3";
};

const handleAlbumClick = () => {
  if (props.moment.albumId) {
    if (
      router.currentRoute.value.name === "AlbumDetail" &&
      Number(router.currentRoute.value.params.id) === props.moment.albumId
    ) {
      return;
    }
    router.push(`/albums/${props.moment.albumId}`);
  }
};

const handleDelete = async () => {
  try {
    await deleteMoment(props.moment.id);
    message.success("碎片已清除");
    emit("deleted", props.moment.id);
  } catch (error: any) {
    message.error(error.message || "删除错误");
  }
};
</script>

<style scoped>
.moment-card {
  background: rgba(5, 11, 20, 0.6);
  border: 1px solid rgba(0, 243, 255, 0.15);
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 24px;
  position: relative;
  backdrop-filter: blur(5px);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  transition: all 0.3s;
}

.moment-card:hover {
  border-color: #00f3ff;
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.1);
  transform: translateY(-2px);
}

.moment-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #00f3ff;
  opacity: 0.5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed rgba(0, 243, 255, 0.2);
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date {
  font-family: "Share Tech Mono";
  font-size: 0.9rem;
  color: #00f3ff;
  letter-spacing: 1px;
}

.meta-extras {
  display: flex;
  gap: 12px;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
  font-family: "Share Tech Mono";
}

.location,
.album-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.album-tag.clickable {
  cursor: pointer;
  color: #bc13fe;
}
.album-tag.clickable:hover {
  text-decoration: underline;
  text-shadow: 0 0 5px #bc13fe;
}

.delete-btn {
  color: rgba(255, 255, 255, 0.3);
}
.delete-btn:hover {
  color: #ff0055;
  text-shadow: 0 0 5px #ff0055;
}

.card-content {
  margin-bottom: 16px;
  color: #fff;
  font-family: "Rajdhani";
  font-size: 1.1rem;
  line-height: 1.5;
}

.content-text {
  border-left: 2px solid rgba(255, 255, 255, 0.1);
  padding-left: 10px;
}

.photo-grid {
  display: grid;
  gap: 4px;
  border: 1px solid rgba(0, 243, 255, 0.1);
  padding: 4px;
  background: rgba(0, 0, 0, 0.3);
}

.grid-1 {
  grid-template-columns: 1fr;
}
.grid-2 {
  grid-template-columns: repeat(2, 1fr);
}
.grid-2-2 {
  grid-template-columns: repeat(2, 1fr);
}
.grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.photo-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  cursor: pointer;
}

.grid-1 .photo-wrapper {
  aspect-ratio: auto;
  max-height: 400px;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: contrast(1.1) saturate(0.9);
  transition: all 0.3s;
}

.img-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 243, 255, 0.1);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.photo-wrapper:hover .photo-img {
  transform: scale(1.05);
  filter: contrast(1.2);
}
.photo-wrapper:hover .img-overlay {
  opacity: 1;
}
</style>
