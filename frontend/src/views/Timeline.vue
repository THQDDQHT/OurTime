<template>
  <div
    class="timeline-container w-screen h-screen bg-dark-bg overflow-hidden relative font-rajdhani text-neon-blue flex flex-col"
  >
    <!-- 背景层 (复用 DesktopHome 风格) -->
    <div class="perspective-grid"></div>
    <div class="stars"></div>
    <div class="scanlines"></div>
    <div class="vignette"></div>

    <!-- 顶部导航 -->
    <div
      class="timeline-header h-[60px] px-8 flex justify-between items-center bg-black/80 border-b border-neon-blue/30 z-[100]"
    >
      <div
        class="back-btn cursor-pointer flex items-center gap-2.5 font-share-tech-mono text-white/70 transition-colors duration-300 hover:text-neon-blue hover:text-shadow-glow-blue-sm"
        @click="router.back()"
      >
        <span class="icon-arrow"><</span>
        <span>RETURN_TO_DESKTOP // 返回桌面</span>
      </div>
      <div
        class="header-title font-share-tech-mono text-lg tracking-[2px] text-neon-blue"
      >
        CHRONO_ARCHIVE // 时间轴
      </div>
    </div>

    <!-- 时间轴主体 -->
    <div
      class="timeline-content flex-1 relative overflow-y-auto overflow-x-hidden py-10 z-10"
      ref="scrollContainer"
    >
      <div
        class="moments-list w-full max-w-[1000px] mx-auto relative min-h-full pb-24"
        v-if="moments.length > 0"
      >
        <!-- 极简激光中轴线 -->
        <div class="central-line"></div>
        <div
          v-for="(moment, index) in moments"
          :key="moment.id"
          class="moment-item relative w-1/2 px-10 mb-16 box-border flex flex-col opacity-0 transition-all duration-[800ms]"
          :class="{
            left: index % 2 === 0,
            right: index % 2 !== 0,
            visible: visibleItems.has(moment.id),
          }"
          :ref="(el) => observeItem(el, moment.id)"
        >
          <!-- 时间点指示器 -->
          <div class="time-node absolute top-0 flex items-center z-20">
            <div
              class="node-dot w-5 h-5 flex items-center justify-center relative bg-dark-bg rounded-full z-[2]"
            >
              <div class="dot-core"></div>
              <div class="dot-ring"></div>
            </div>
            <div
              class="node-date font-share-tech-mono text-neon-blue text-sm bg-dark-bg/80 px-2.5 py-1 rounded-xl border border-neon-blue/20 whitespace-nowrap transition-all duration-300 text-shadow-glow-blue-sm hover:bg-neon-blue/10 hover:border-neon-blue hover:shadow-glow-blue-sm"
            >
              {{ formatDate(moment.createdAt) }}
            </div>
          </div>

          <!-- 内容卡片 -->
          <div
            class="moment-card bg-gradient-to-br from-dark-bg/90 to-[rgba(10,21,37,0.9)] border border-neon-blue/30 rounded p-4 w-4/5 max-w-[400px] relative transition-all duration-300 mt-10 shadow-[0_4px_15px_rgba(0,0,0,0.5)] backdrop-blur-[10px] hover:border-neon-blue hover:shadow-glow-blue-sm hover:-translate-y-0.5"
          >
            <div class="card-body text-base text-white leading-relaxed">
              <p class="moment-text mb-2.5 break-all">{{ moment.content }}</p>

              <!-- 图片展示 (如果有) -->
              <div
                class="moment-photos grid grid-cols-[repeat(auto-fit,minmax(80px,1fr))] gap-2 mt-2.5"
                v-if="moment.photos && moment.photos.length > 0"
              >
                <div
                  v-for="photo in moment.photos"
                  :key="photo.id"
                  class="photo-wrapper aspect-square overflow-hidden border border-neon-blue/30 rounded-sm"
                >
                  <img
                    :src="resolveUploadUrl(photo.filePath)"
                    loading="lazy"
                    alt="Memory capture"
                    class="w-full h-full object-cover transition-transform duration-500 hover:scale-110"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state flex justify-center mt-24" v-else>
        <div class="empty-text font-share-tech-mono text-white/30 text-lg">
          NO_DATA_FOUND // 暂无时间记录
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import type { ComponentPublicInstance } from "vue";
import { getMoments } from "@/api/moment";
import { resolveUploadUrl } from "@/utils/url";
import type { MomentResponse } from "@/api/types";

const router = useRouter();
const moments = ref<MomentResponse[]>([]);
const visibleItems = ref(new Set<number>());
const scrollContainer = ref<HTMLElement | null>(null);

// 观察器逻辑
const observeItem = (
  el: Element | ComponentPublicInstance | null,
  id: number
) => {
  if (!el) return;

  // 简单的 IntersectionObserver
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          visibleItems.value.add(id);
          observer.unobserve(entry.target);
        }
      });
    },
    {
      threshold: 0.2,
      root: scrollContainer.value, // 指定滚动容器
    }
  );

  observer.observe(el as Element);
};

const fetchMoments = async () => {
  try {
    // 获取最近的动态，假设最多获取 100 条用于展示
    const res = await getMoments(0, 100);
    moments.value = res.content || [];
  } catch (error) {
    console.error("Failed to fetch timeline:", error);
  }
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return "UNKNOWN_DATE";
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const time = `${String(date.getHours()).padStart(2, "0")}:${String(
    date.getMinutes()
  ).padStart(2, "0")}`;
  return `${year}.${month}.${day} ${time}`;
};

onMounted(() => {
  fetchMoments();
});
</script>

<style scoped>
/* --- Background Layers (Copied & Simplified) --- */
.perspective-grid {
  position: absolute;
  width: 200%;
  height: 200%;
  left: -50%;
  top: -50%;
  background-image: linear-gradient(rgba(0, 243, 255, 0.2) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.2) 1px, transparent 1px);
  background-size: 60px 60px;
  transform: perspective(500px) rotateX(60deg);
  opacity: 0.4;
  z-index: 0;
  pointer-events: none;
}
.stars {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(white 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.1;
  z-index: 0;
  pointer-events: none;
}
.scanlines {
  position: absolute;
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0) 0px,
    rgba(0, 0, 0, 0) 2px,
    rgba(0, 0, 0, 0.2) 3px,
    rgba(0, 0, 0, 0.2) 4px
  );
  pointer-events: none;
  z-index: 5;
}
.vignette {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, transparent 50%, rgba(0, 0, 0, 0.8) 100%);
  pointer-events: none;
  z-index: 5;
}

/* --- Central Line (Ethereal Line) --- */
.central-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 1px;
  background: rgba(255, 255, 255, 0.1); /* 极淡的白色 */
  box-shadow: 0 0 5px rgba(0, 243, 255, 0.1); /* 极微弱的蓝光晕 */
  transform: translateX(-50%);
  z-index: 1;
  pointer-events: none;
}

@keyframes galaxy-flow {
  0% {
    background-position: 0 0, 20px 40px, -10px 60px;
  }
  100% {
    background-position: 0 1000px, 20px 1040px, -10px 1060px;
  }
}

.dot-core {
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 8px #fff, 0 0 15px #00f3ff;
  z-index: 2;
  animation: star-pulse 2s infinite alternate;
}

/* Star Rays */
.dot-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 0;
  animation: star-spin 20s linear infinite;
}
.dot-ring::before,
.dot-ring::after {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  background: linear-gradient(to bottom, transparent, #fff, transparent);
  transform: translate(-50%, -50%);
}
.dot-ring::before {
  width: 1px;
  height: 30px;
}
.dot-ring::after {
  width: 30px;
  height: 1px;
  background: linear-gradient(to right, transparent, #fff, transparent);
}

@keyframes star-pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}
@keyframes star-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  border-bottom: 1px dashed rgba(0, 243, 255, 0.3);
  padding-bottom: 5px;
}
.moment-id {
  font-family: "Share Tech Mono";
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}
/* Tailwind 类样式 */
.moment-item.left {
  left: 0;
  align-items: flex-end;
  text-align: right;
  transform: translateX(-50px);
}

.moment-item.right {
  left: 50%;
  align-items: flex-start;
  text-align: left;
  transform: translateX(50px);
}

.moment-item.visible {
  opacity: 1;
  transform: translateX(0);
}

.moment-item.left .time-node {
  right: -10px;
  flex-direction: row-reverse;
}

.moment-item.right .time-node {
  left: -10px;
  flex-direction: row;
}

.moment-item.left .node-date {
  margin-right: 25px;
}
.moment-item.right .node-date {
  margin-left: 25px;
}

/* Mobile Adaption */
@media (max-width: 768px) {
  .moment-item {
    width: 100%;
    padding-left: 60px;
    padding-right: 20px;
    text-align: left !important;
    align-items: flex-start !important;
    left: 0 !important;
    transform: none !important;
  }
  .moment-item.visible {
    transform: none !important;
  }

  .moment-item.left .time-node,
  .moment-item.right .time-node {
    left: 10px;
    right: auto;
    flex-direction: row;
    top: 0;
  }

  .moment-item.left .node-date,
  .moment-item.right .node-date {
    margin-left: 25px !important;
    margin-right: 0 !important;
    font-size: 12px;
  }

  .moment-card {
    width: 100%;
    max-width: none;
  }
}
</style>
