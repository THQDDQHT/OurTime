<template>
  <div
    class="album-detail-container min-h-screen bg-dark-bg relative overflow-hidden"
  >
    <div class="cyber-grid-bg"></div>
    <n-layout class="layout-bg">
      <n-layout-header
        class="h-16 px-6 flex items-center backdrop-blur-md bg-black/40 border-b border-neon-blue/20"
        bordered
      >
        <div class="header-content w-full flex justify-between items-center">
          <div class="left-actions flex items-center gap-4">
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
            <div class="page-title-wrapper relative">
              <n-text
                class="page-title font-share-tech-mono text-lg text-neon-blue tracking-wider"
                >档案详情 // 访问模式</n-text
              >
              <div class="scan-line"></div>
            </div>
          </div>
          <div class="header-actions">
            <n-button type="primary" @click="handleCreate">
              <template #icon>
                <n-icon><add-outline /></n-icon>
              </template>
              录入新数据
            </n-button>
          </div>
        </div>
      </n-layout-header>

      <n-layout-content class="content p-6" :native-scrollbar="false">
        <div class="main-wrapper max-w-6xl mx-auto">
          <!-- Album Info Card -->
          <div
            class="album-info relative bg-gradient-to-br from-dark-bg/80 to-[rgba(10,21,37,0.8)] border border-neon-blue/30 rounded-lg p-6 mb-6 backdrop-blur-sm flex gap-6 transition-all duration-300 hover:border-neon-blue hover:shadow-glow-blue-sm"
            v-if="album"
          >
            <div class="corner-deco tl"></div>
            <div class="corner-deco tr"></div>
            <div class="corner-deco bl"></div>
            <div class="corner-deco br"></div>

            <div
              class="album-cover-wrapper w-48 h-48 flex-shrink-0 relative overflow-hidden rounded border border-neon-blue/40 bg-black"
            >
              <n-image
                v-if="album.coverUrl"
                :src="resolveUploadUrl(album.coverUrl)"
                object-fit="cover"
                class="album-cover w-full h-full"
              />
              <div
                v-else
                class="album-cover-placeholder w-full h-full flex items-center justify-center"
              >
                <n-icon size="48" color="#333"><images-outline /></n-icon>
              </div>
              <div class="scan-overlay"></div>
            </div>
            <div class="album-meta flex-1 flex flex-col justify-center">
              <h1
                class="album-name font-rajdhani text-4xl font-bold text-white mb-2 uppercase tracking-wide"
                :data-text="album.name"
              >
                {{ album.name }}
              </h1>
              <p
                class="album-desc text-white/70 text-sm mb-4 font-share-tech-mono"
              >
                {{ album.description || "元数据缺失..." }}
              </p>
              <div class="album-stats">
                <n-tag
                  size="small"
                  class="bg-neon-blue/10 text-neon-blue border border-neon-blue/30"
                  :bordered="false"
                >
                  建立日期: {{ formatDateShort(album.createdAt) }}
                </n-tag>
              </div>
            </div>
          </div>

          <n-tabs
            v-model:value="activeTab"
            type="segment"
            animated
          >
            <n-tab-pane name="timeline" tab="时间轴回溯">
              <div class="timeline-container py-5 max-w-[800px] mx-auto">
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
                  <div
                    v-else
                    class="text-center py-10 flex flex-col items-center gap-4"
                  >
                    <n-empty description="尚未探测到记忆信号" />
                    <n-button type="primary" @click="handleCreate"
                      >启动数据录入</n-button
                    >
                  </div>
                </n-spin>

                <div v-if="hasMore" class="load-more text-center mt-12 pb-8">
                  <n-button
                    text
                    @click="loadMore"
                    :loading="loading"
                    class="text-neon-blue font-share-tech-mono hover:text-shadow-glow-blue-sm"
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
              <div class="photowall-container py-5">
                <n-spin :show="loading">
                  <div
                    v-if="allPhotos.length > 0"
                    class="masonry-grid grid grid-cols-[repeat(auto-fill,minmax(220px,1fr))] gap-4"
                  >
                    <n-image-group>
                      <div
                        v-for="photo in allPhotos"
                        :key="photo.id"
                        class="masonry-item cyber-frame border border-neon-blue/20 bg-black/30 relative overflow-hidden transition-all duration-300"
                      >
                        <div class="image-wrapper relative">
                          <n-image
                            :src="resolveUploadUrl(photo.filePath)"
                            object-fit="cover"
                            preview-disabled
                            class="photo-image w-full block contrast-110 saturate-90"
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

                <div
                  v-if="hasMore && allPhotos.length > 0"
                  class="load-more text-center mt-12 pb-8"
                >
                  <n-button
                    text
                    @click="loadMore"
                    :loading="loading"
                    class="text-neon-blue font-share-tech-mono hover:text-shadow-glow-blue-sm"
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
/* 网格背景 */
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

.cyber-back-btn {
  color: #00f3ff;
}
.cyber-back-btn:hover {
  color: #fff;
  background: rgba(0, 243, 255, 0.2);
}

.scan-line {
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #00f3ff;
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

.corner-deco {
  position: absolute;
  width: 10px;
  height: 10px;
  border: 2px solid #00f3ff;
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

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #050b14;
  border: 2px solid #00f3ff;
  box-shadow: 0 0 5px #00f3ff;
}

.cyber-frame:hover {
  border-color: #00f3ff;
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.2);
  transform: translateY(-5px);
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
</style>
