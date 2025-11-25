<template>
  <div class="album-detail-container">
    <n-layout class="layout-bg">
      <n-layout-header class="header" bordered>
        <div class="header-content">
          <div class="left-actions">
            <n-button circle quaternary @click="handleBack">
              <template #icon>
                <n-icon><arrow-back-outline /></n-icon>
              </template>
            </n-button>
            <n-text class="page-title">相册详情</n-text>
          </div>
          <div class="header-actions">
             <n-button type="primary" ghost @click="handleCreate">
               <template #icon>
                 <n-icon><add-outline /></n-icon>
               </template>
               添加瞬间
             </n-button>
          </div>
        </div>
      </n-layout-header>

      <n-layout-content class="content" :native-scrollbar="false">
        <div class="main-wrapper">
          
          <!-- Album Info Card -->
          <div class="album-info" v-if="album">
             <div class="album-cover-wrapper">
                <n-image 
                  v-if="album.coverUrl" 
                  :src="resolveUploadUrl(album.coverUrl)" 
                  object-fit="cover"
                  class="album-cover"
                />
                <div v-else class="album-cover-placeholder">
                  <n-icon size="48" color="#ccc"><images-outline /></n-icon>
                </div>
             </div>
             <div class="album-meta">
                <h1 class="album-name">{{ album.name }}</h1>
                <p class="album-desc">{{ album.description || '暂无描述' }}</p>
                <div class="album-stats">
                   <n-tag size="small" :bordered="false">创建于 {{ formatDateShort(album.createdAt) }}</n-tag>
                </div>
             </div>
          </div>

          <n-tabs
            v-model:value="activeTab"
            type="segment"
            animated
            class="custom-tabs"
          >
            <n-tab-pane name="timeline" tab="时间轴">
              <div class="timeline-container">
                <n-spin :show="loading">
                  <n-timeline v-if="moments.length > 0" size="large">
                    <n-timeline-item
                      v-for="moment in moments"
                      :key="moment.id"
                      type="default"
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
                    <n-empty description="这个相册还没有瞬间，快来添加吧" />
                    <n-button
                      class="create-btn"
                      type="primary"
                      @click="handleCreate"
                      >添加瞬间</n-button
                    >
                  </div>
                </n-spin>

                <div v-if="hasMore" class="load-more">
                  <n-button text @click="loadMore" :loading="loading">
                    <template #icon>
                      <n-icon><reload-outline /></n-icon>
                    </template>
                    浏览更多回忆
                  </n-button>
                </div>
              </div>
            </n-tab-pane>

            <n-tab-pane name="photowall" tab="照片墙">
              <div class="photowall-container">
                <n-spin :show="loading">
                  <div v-if="allPhotos.length > 0" class="masonry-grid">
                    <n-image-group>
                      <div
                        v-for="photo in allPhotos"
                        :key="photo.id"
                        class="masonry-item"
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
                        </div>
                      </div>
                    </n-image-group>
                  </div>
                  <n-empty v-else description="相册空空如也" />
                </n-spin>

                <div v-if="hasMore && allPhotos.length > 0" class="load-more">
                  <n-button text @click="loadMore" :loading="loading">
                    <template #icon>
                      <n-icon><reload-outline /></n-icon>
                    </template>
                    加载更多
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
  ImagesOutline
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
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString();
}

const loadAlbumInfo = async () => {
    try {
        album.value = await getAlbumById(albumId);
    } catch (error: any) {
        message.error("加载相册信息失败");
        router.push('/albums');
    }
}

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
  router.push("/albums");
};

onMounted(() => {
  if (!albumId) {
      message.error("相册ID无效");
      router.push('/albums');
      return;
  }
  loadAlbumInfo();
  loadMoments(true);
});
</script>

<style scoped>
.album-detail-container {
  min-height: 100vh;
}

.layout-bg {
  background: transparent;
}

.header {
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  background: rgba(253, 252, 248, 0.9) !important;
  backdrop-filter: blur(12px);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.header-content {
  width: 100%;
  max-width: 900px;
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

.page-title {
  font-family: "Noto Serif SC", serif;
  font-size: 18px;
  font-weight: 600;
  color: #5d4037;
}

.content {
  background-color: transparent;
}

.main-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.album-info {
    display: flex;
    gap: 24px;
    margin-bottom: 40px;
    background: white;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.album-cover-wrapper {
    width: 120px;
    height: 120px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
}

.album-cover {
    width: 100%;
    height: 100%;
    display: block;
}

.album-cover-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: #f0f0f0;
}

.album-meta {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.album-name {
    font-family: "Noto Serif SC", serif;
    font-size: 24px;
    font-weight: 700;
    color: #2c3e50;
    margin: 0 0 8px 0;
}

.album-desc {
    color: #666;
    font-size: 14px;
    margin: 0 0 16px 0;
    line-height: 1.6;
}

.custom-tabs {
  margin-bottom: 32px;
}

.timeline-container {
  padding: 20px 0;
  max-width: 700px;
  margin: 0 auto;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #d7ccc8;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #d7ccc8;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.load-more {
  text-align: center;
  margin-top: 48px;
  padding-bottom: 32px;
}

.photowall-container {
  padding: 20px 0;
}

.masonry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.masonry-item {
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
  break-inside: avoid;
  width: 100%;
  background: #fff;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.image-wrapper {
  position: relative;
  overflow: hidden;
}

.masonry-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.masonry-item :deep(.n-image) {
  width: 100%;
  display: block;
}

.masonry-item :deep(.n-image-wrapper) {
  width: 100%;
  padding-top: 0 !important;
}

.masonry-item :deep(.n-image img) {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
}

.photo-image {
  width: 100%;
}
</style>

