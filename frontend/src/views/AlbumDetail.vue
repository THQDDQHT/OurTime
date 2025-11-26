<template>
  <div class="album-detail-container">
    <div class="cyber-grid-bg"></div>
    <n-layout class="layout-bg">
      <n-layout-header class="cyber-header" bordered>
        <div class="header-content">
          <div class="left-actions">
            <n-button
              circle
              quaternary
              class="cyber-back-btn"
              @click="handleBack"
            >
              <template #icon>
                <n-icon><arrow-back-outline /></n-icon>
              </template>
            </n-button>
            <div class="page-title-wrapper">
              <n-text class="page-title">档案详情 // 访问模式</n-text>
              <div class="scan-line"></div>
            </div>
          </div>
          <div class="header-actions">
            <n-button type="primary" class="cyber-btn" @click="handleCreate">
              <template #icon>
                <n-icon><add-outline /></n-icon>
              </template>
              录入新数据
            </n-button>
          </div>
        </div>
      </n-layout-header>

      <n-layout-content class="content" :native-scrollbar="false">
        <div class="main-wrapper">
          <!-- Album Info Card -->
          <div class="album-info cyber-panel" v-if="album">
            <div class="corner-deco tl"></div>
            <div class="corner-deco tr"></div>
            <div class="corner-deco bl"></div>
            <div class="corner-deco br"></div>

            <div class="album-cover-wrapper">
              <n-image
                v-if="album.coverUrl"
                :src="resolveUploadUrl(album.coverUrl)"
                object-fit="cover"
                class="album-cover"
              />
              <div v-else class="album-cover-placeholder">
                <n-icon size="48" color="#333"><images-outline /></n-icon>
              </div>
              <div class="scan-overlay"></div>
            </div>
            <div class="album-meta">
              <h1 class="album-name glitch-text" :data-text="album.name">
                {{ album.name }}
              </h1>
              <p class="album-desc">
                {{ album.description || "元数据缺失..." }}
              </p>
              <div class="album-stats">
                <n-tag size="small" class="cyber-tag" :bordered="false">
                  建立日期: {{ formatDateShort(album.createdAt) }}
                </n-tag>
              </div>
            </div>
          </div>

          <n-tabs
            v-model:value="activeTab"
            type="segment"
            animated
            class="custom-tabs cyber-tabs"
          >
            <n-tab-pane name="timeline" tab="时间轴回溯">
              <div class="timeline-container">
                <n-spin :show="loading">
                  <n-timeline v-if="moments.length > 0" size="large">
                    <n-timeline-item
                      v-for="moment in moments"
                      :key="moment.id"
                      type="info"
                      :time="formatDate(moment.happenedAt)"
                    >
                      <template #icon>
                        <div class="timeline-dot"></div>
                      </template>
                      <moment-card
                        :moment="moment"
                        @deleted="handleMomentDeleted"
                      />
                    </n-timeline-item>
                  </n-timeline>
                  <div v-else class="empty-state">
                    <n-empty description="尚未探测到记忆信号" />
                    <n-button
                      class="create-btn cyber-btn"
                      type="primary"
                      @click="handleCreate"
                      >启动数据录入</n-button
                    >
                  </div>
                </n-spin>

                <div v-if="hasMore" class="load-more">
                  <n-button
                    text
                    @click="loadMore"
                    :loading="loading"
                    class="cyber-link-btn"
                  >
                    <template #icon>
                      <n-icon><reload-outline /></n-icon>
                    </template>
                    加载历史数据
                  </n-button>
                </div>
              </div>
            </n-tab-pane>

            <n-tab-pane name="photowall" tab="视觉矩阵">
              <div class="photowall-container">
                <n-spin :show="loading">
                  <div v-if="allPhotos.length > 0" class="masonry-grid">
                    <n-image-group>
                      <div
                        v-for="photo in allPhotos"
                        :key="photo.id"
                        class="masonry-item cyber-frame"
                      >
                        <div class="image-wrapper">
                          <n-image
                            :src="resolveUploadUrl(photo.filePath)"
                            object-fit="cover"
                            preview-disabled
                            class="photo-image"
                            lazy
                          />
                          <div class="image-overlay"></div>
                          <div class="scan-line-vertical"></div>
                        </div>
                      </div>
                    </n-image-group>
                  </div>
                  <n-empty v-else description="无视觉数据" />
                </n-spin>

                <div v-if="hasMore && allPhotos.length > 0" class="load-more">
                  <n-button
                    text
                    @click="loadMore"
                    :loading="loading"
                    class="cyber-link-btn"
                  >
                    <template #icon>
                      <n-icon><reload-outline /></n-icon>
                    </template>
                    加载更多数据
                  </n-button>
                </div>
              </div>
            </n-tab-pane>
          </n-tabs>
        </div>
      </n-layout-content>
    </n-layout>
    <CreateMomentModal
      v-model:show="showCreateModal"
      :initial-album-id="albumId"
      @success="handleCreateSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useMessage } from "naive-ui";
import {
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  NTabs,
  NTabPane,
  NTimeline,
  NTimelineItem,
  NSpin,
  NEmpty,
  NButton,
  NText,
  NImage,
  NIcon,
  NImageGroup,
  NTag,
} from "naive-ui";
import {
  AddOutline,
  ArrowBackOutline,
  ReloadOutline,
  ImagesOutline,
} from "@vicons/ionicons5";
import { getMoments } from "@/api/moment";
import { getAlbumById } from "@/api/album";
import MomentCard from "@/components/MomentCard.vue";
import CreateMomentModal from "@/components/CreateMomentModal.vue";
import type { MomentResponse, PhotoResponse, AlbumResponse } from "@/api/types";
import { resolveUploadUrl } from "@/utils/url";

const route = useRoute();
const router = useRouter();
const message = useMessage();

const albumId = Number(route.params.id);
const album = ref<AlbumResponse | null>(null);

const activeTab = ref("timeline");
const loading = ref(false);
const showCreateModal = ref(false);
const moments = ref<MomentResponse[]>([]);
const page = ref(0);
const size = ref(20);
const hasMore = ref(true);

const allPhotos = computed(() => {
  const photos: Array<PhotoResponse & { id: number }> = [];
  moments.value.forEach((moment) => {
    if (moment.photos && moment.photos.length > 0) {
      moment.photos.forEach((photo) => {
        if (photo.filePath) {
          photos.push({ ...photo, id: photo.id || Math.random() });
        }
      });
    }
  });
  return photos;
});

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatDateShort = (dateStr: string) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString();
};

const loadAlbumInfo = async () => {
  try {
    album.value = await getAlbumById(albumId);
  } catch (error: any) {
    message.error("加载相册信息失败");
    router.push("/albums");
  }
};

const loadMoments = async (reset = false) => {
  if (loading.value) return;

  try {
    loading.value = true;
    const currentPage = reset ? 0 : page.value;
    // Pass albumId to getMoments
    const response = await getMoments(currentPage, size.value, albumId);

    if (reset) {
      moments.value = response.content;
    } else {
      moments.value.push(...response.content);
    }

    page.value = response.number + 1;
    hasMore.value = !response.last;
  } catch (error: any) {
    message.error(error.message || "加载失败");
  } finally {
    loading.value = false;
  }
};

const loadMore = () => {
  loadMoments(false);
};

const handleMomentDeleted = (id: number) => {
  moments.value = moments.value.filter((m) => m.id !== id);
};

const handleCreate = () => {
  showCreateModal.value = true;
};

const handleCreateSuccess = () => {
  loadMoments(true);
};

const handleBack = () => {
  router.push("/");
};

onMounted(() => {
  if (!albumId) {
    message.error("相册ID无效");
    router.push("/albums");
    return;
  }
  loadAlbumInfo();
  loadMoments(true);
});
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Rajdhani:wght@500;700&family=Share+Tech+Mono&display=swap");

.album-detail-container {
  min-height: 100vh;
  background-color: var(--dark-bg);
  position: relative;
  color: #fff;
  font-family: "Rajdhani", sans-serif;
}

.cyber-grid-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(
      rgba(0, 243, 255, 0.05) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(0, 243, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  pointer-events: none;
  z-index: 0;
}

.layout-bg {
  background: transparent;
  position: relative;
  z-index: 1;
}

.cyber-header {
  height: 64px;
  background: rgba(5, 11, 20, 0.8) !important;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 243, 255, 0.2);
  padding: 0 24px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.header-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cyber-back-btn {
  color: var(--neon-blue);
}
.cyber-back-btn:hover {
  color: #fff;
  background: rgba(0, 243, 255, 0.2);
}

.page-title-wrapper {
  position: relative;
}

.page-title {
  font-family: "Share Tech Mono", monospace;
  font-size: 18px;
  color: var(--neon-blue);
  letter-spacing: 1px;
  text-shadow: 0 0 5px rgba(0, 243, 255, 0.5);
}

.scan-line {
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--neon-blue);
  animation: scan-width 2s infinite alternate;
}

@keyframes scan-width {
  0% {
    width: 0;
    opacity: 0;
  }
  100% {
    width: 100%;
    opacity: 1;
  }
}

.content {
  background-color: transparent;
}

.main-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.cyber-panel {
  background: rgba(5, 11, 20, 0.6);
  border: 1px solid rgba(0, 243, 255, 0.3);
  position: relative;
  padding: 24px;
  display: flex;
  gap: 24px;
  margin-bottom: 40px;
  backdrop-filter: blur(5px);
}

.corner-deco {
  position: absolute;
  width: 10px;
  height: 10px;
  border: 2px solid var(--neon-blue);
}
.tl {
  top: -1px;
  left: -1px;
  border-right: 0;
  border-bottom: 0;
}
.tr {
  top: -1px;
  right: -1px;
  border-left: 0;
  border-bottom: 0;
}
.bl {
  bottom: -1px;
  left: -1px;
  border-right: 0;
  border-top: 0;
}
.br {
  bottom: -1px;
  right: -1px;
  border-left: 0;
  border-top: 0;
}

.album-cover-wrapper {
  width: 140px;
  height: 140px;
  flex-shrink: 0;
  border: 1px solid var(--neon-blue);
  position: relative;
  overflow: hidden;
  background: #000;
}

.album-cover {
  width: 100%;
  height: 100%;
  opacity: 0.8;
  filter: grayscale(50%);
}

.album-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111;
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

.album-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.album-name {
  font-family: "Rajdhani", sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.album-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin: 0 0 16px 0;
  font-family: "Share Tech Mono";
}

.cyber-tag {
  background: rgba(0, 243, 255, 0.1);
  color: var(--neon-blue);
  border: 1px solid rgba(0, 243, 255, 0.3);
}

/* Tabs Customization */
:deep(.n-tabs .n-tabs-nav) {
  background: transparent;
}
:deep(.n-tabs .n-tabs-tab) {
  color: rgba(255, 255, 255, 0.5);
  font-family: "Share Tech Mono";
  transition: all 0.3s;
}
:deep(.n-tabs .n-tabs-tab:hover) {
  color: var(--neon-blue);
}
:deep(.n-tabs .n-tabs-tab--active) {
  color: var(--neon-blue) !important;
  text-shadow: 0 0 5px var(--neon-blue);
}
:deep(.n-tabs .n-tabs-bar) {
  background-color: var(--neon-blue);
  box-shadow: 0 0 8px var(--neon-blue);
}
/* Tabs Background Fix */
:deep(.n-tabs-pane-wrapper) {
  background: transparent !important;
}
:deep(.n-tab-pane) {
  background-color: rgba(5, 11, 20, 0.4) !important;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(0, 243, 255, 0.1);
  border-top: none;
  padding: 20px;
  color: #fff;
}

/* Segmented Tabs Override */
:deep(.n-tabs .n-tabs-rail) {
  background-color: rgba(5, 11, 20, 0.8) !important;
  border: 1px solid rgba(0, 243, 255, 0.2);
  padding: 4px;
  border-radius: 4px;
}

/* Target the sliding capsule (background of active tab) */
:deep(.n-tabs .n-tabs-rail .n-tabs-capsule) {
  background-color: rgba(0, 243, 255, 0.15) !important;
  border: 1px solid var(--neon-blue) !important;
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.3) !important;
  border-radius: 4px;
}

/* Target the active tab text */
:deep(.n-tabs .n-tabs-tab.n-tabs-tab--active) {
  color: var(--neon-blue) !important;
  text-shadow: 0 0 8px rgba(0, 243, 255, 0.6);
  font-weight: bold;
  z-index: 1; /* Ensure text is above capsule */
}

/* Target inactive tabs */
:deep(.n-tabs .n-tabs-tab) {
  background-color: transparent !important;
  color: rgba(255, 255, 255, 0.6) !important;
  transition: all 0.3s;
}

:deep(.n-tabs .n-tabs-tab:hover) {
  color: var(--neon-blue) !important;
}

/* Timeline Customization */
:deep(.n-timeline .n-timeline-item-timeline__line) {
  background-color: rgba(0, 243, 255, 0.2) !important;
}

:deep(.n-timeline .n-timeline-item-content__title) {
  color: var(--neon-blue) !important;
  font-family: "Share Tech Mono";
  margin-bottom: 6px !important;
}

:deep(.n-timeline .n-timeline-item-content__time) {
  color: rgba(255, 255, 255, 0.5) !important;
  font-family: "Share Tech Mono";
  font-size: 12px;
}

:deep(.n-timeline .n-timeline-item-content__content) {
  color: rgba(255, 255, 255, 0.8) !important;
}

/* Empty State Customization */
:deep(.n-empty__description) {
  color: rgba(255, 255, 255, 0.5) !important;
  font-family: "Share Tech Mono";
}
:deep(.n-empty__icon) {
  color: rgba(0, 243, 255, 0.3) !important;
  transition: all 0.3s;
}
:deep(.n-empty:hover .n-empty__icon) {
  color: var(--neon-blue) !important;
  filter: drop-shadow(0 0 8px var(--neon-blue));
}

.timeline-container {
  padding: 20px 0;
  max-width: 800px;
  margin: 0 auto;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: var(--dark-bg);
  border: 2px solid var(--neon-blue);
  box-shadow: 0 0 5px var(--neon-blue);
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.cyber-link-btn {
  color: var(--neon-blue);
  font-family: "Share Tech Mono";
}
.cyber-link-btn:hover {
  text-shadow: 0 0 5px var(--neon-blue);
}

/* Photowall */
.photowall-container {
  padding: 20px 0;
}

.masonry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.cyber-frame {
  border: 1px solid rgba(0, 243, 255, 0.2);
  background: rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}
.cyber-frame:hover {
  border-color: var(--neon-blue);
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.2);
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 243, 255, 0.1);
  opacity: 0;
  transition: opacity 0.3s;
}
.cyber-frame:hover .image-overlay {
  opacity: 1;
}

.scan-line-vertical {
  position: absolute;
  top: 0;
  left: 0;
  width: 2px;
  height: 100%;
  background: rgba(0, 243, 255, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
  transform: translateX(-10px);
}
.cyber-frame:hover .scan-line-vertical {
  opacity: 1;
  animation: scan-right 1s linear infinite;
}
@keyframes scan-right {
  0% {
    left: 0;
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

.photo-image {
  width: 100%;
  display: block;
  filter: contrast(1.1) saturate(0.9);
}

.load-more {
  text-align: center;
  margin-top: 48px;
  padding-bottom: 32px;
}
</style>
