<template>
  <div class="timeline-container">
    <!-- 背景层 (复用 DesktopHome 风格) -->
    <div class="perspective-grid"></div>
    <div class="stars"></div>
    <div class="scanlines"></div>
    <div class="vignette"></div>

    <!-- 顶部导航 -->
    <div class="timeline-header">
      <div class="back-btn" @click="router.back()">
        <span class="icon-arrow"><</span>
        <span>RETURN_TO_DESKTOP // 返回桌面</span>
      </div>
      <div class="header-title">CHRONO_ARCHIVE // 时间轴</div>
    </div>

    <!-- 时间轴主体 -->
    <div class="timeline-content" ref="scrollContainer">
      <div class="moments-list" v-if="moments.length > 0">
        <!-- 极简激光中轴线 -->
        <div class="central-line"></div>
        <div
          v-for="(moment, index) in moments"
          :key="moment.id"
          class="moment-item"
          :class="{ 
            'left': index % 2 === 0, 
            'right': index % 2 !== 0,
            'visible': visibleItems.has(moment.id)
          }"
          :ref="(el) => observeItem(el, moment.id)"
        >
          <!-- 时间点指示器 -->
          <div class="time-node">
            <div class="node-dot">
              <div class="dot-core"></div>
              <div class="dot-ring"></div>
            </div>
            <div class="node-date">{{ formatDate(moment.createdAt) }}</div>
          </div>

          <!-- 内容卡片 -->
          <div class="moment-card">
            <!-- <div class="card-header">
              <span class="moment-id">ID: {{ moment.id?.toString().padStart(4, '0') }}</span>
              <div class="card-deco"></div>
            </div> -->
            
            <div class="card-body">
              <p class="moment-text">{{ moment.content }}</p>
              
              <!-- 图片展示 (如果有) -->
              <div class="moment-photos" v-if="moment.photos && moment.photos.length > 0">
                <div 
                  v-for="photo in moment.photos" 
                  :key="photo.id" 
                  class="photo-wrapper"
                >
                  <img 
                    :src="resolveUploadUrl(photo.filePath)" 
                    loading="lazy" 
                    alt="Memory capture"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-else>
        <div class="empty-text">NO_DATA_FOUND // 暂无时间记录</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { ComponentPublicInstance } from 'vue'
import { getMoments } from '@/api/moment'
import { resolveUploadUrl } from '@/utils/url'
import type { MomentResponse } from '@/api/types'

const router = useRouter()
const moments = ref<MomentResponse[]>([])
const visibleItems = ref(new Set<number>())
const scrollContainer = ref<HTMLElement | null>(null)

// 观察器逻辑
const observeItem = (el: Element | ComponentPublicInstance | null, id: number) => {
  if (!el) return
  
  // 简单的 IntersectionObserver
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        visibleItems.value.add(id)
        observer.unobserve(entry.target)
      }
    })
  }, {
    threshold: 0.2,
    root: scrollContainer.value // 指定滚动容器
  })
  
  observer.observe(el as Element)
}

const fetchMoments = async () => {
  try {
    // 获取最近的动态，假设最多获取 100 条用于展示
    const res = await getMoments(0, 100)
    moments.value = res.content || []
  } catch (error) {
    console.error('Failed to fetch timeline:', error)
  }
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return 'UNKNOWN_DATE'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const time = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  return `${year}.${month}.${day} ${time}`
}

onMounted(() => {
  fetchMoments()
})
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Rajdhani:wght@500;700&family=Share+Tech+Mono&display=swap");

.timeline-container {
  --neon-blue: #00f3ff;
  --neon-purple: #bc13fe;
  --dark-bg: #050b14;
  --card-bg: rgba(5, 11, 20, 0.8);

  width: 100vw;
  height: 100vh;
  background-color: var(--dark-bg);
  overflow: hidden;
  position: relative;
  font-family: "Rajdhani", sans-serif;
  color: var(--neon-blue);
  display: flex;
  flex-direction: column;
}

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

/* --- Header --- */
.timeline-header {
  height: 60px;
  padding: 0 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(0, 0, 0, 0.8);
  border-bottom: 1px solid rgba(0, 243, 255, 0.3);
  z-index: 100;
}
.back-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: "Share Tech Mono";
  color: rgba(255, 255, 255, 0.7);
  transition: color 0.3s;
}
.back-btn:hover {
  color: var(--neon-blue);
  text-shadow: 0 0 8px var(--neon-blue);
}
.header-title {
  font-family: "Share Tech Mono";
  font-size: 18px;
  letter-spacing: 2px;
  color: var(--neon-blue);
}

/* --- Content & Timeline --- */
.timeline-content {
  flex: 1;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 40px 0;
  z-index: 10;
}

/* 滚动条样式 */
.timeline-content::-webkit-scrollbar {
  width: 6px;
}
.timeline-content::-webkit-scrollbar-thumb {
  background: rgba(0, 243, 255, 0.3);
  border-radius: 3px;
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

/*
.central-line::after {
  content: '';
  position: absolute;
  top: 0;
  left: -10px;
  right: -10px;
  height: 100%;
  background-image: 
    radial-gradient(white, rgba(255,255,255,.2) 2px, transparent 3px),
    radial-gradient(white, rgba(255,255,255,.15) 1px, transparent 2px),
    radial-gradient(rgba(255,255,255,.4), rgba(255,255,255,.1) 2px, transparent 3px);
  background-size: 250px 250px, 150px 150px, 100px 100px;
  background-position: 0 0, 20px 40px, -10px 60px;
  opacity: 0.6;
  animation: galaxy-flow 60s linear infinite;
}
*/

@keyframes galaxy-flow {
  0% { background-position: 0 0, 20px 40px, -10px 60px; }
  100% { background-position: 0 1000px, 20px 1040px, -10px 1060px; }
}

.moments-list {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  min-height: 100%;
  padding-bottom: 100px; /* 底部留白 */
}

.moment-item {
  position: relative;
  width: 50%;
  padding: 0 40px;
  margin-bottom: 60px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.22, 1, 0.36, 1);
}

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

/* --- Node Indicator (Star System) --- */
.time-node {
  position: absolute;
  top: 0;
  display: flex;
  align-items: center;
  z-index: 20;
}

.moment-item.left .time-node {
  right: -10px; /* Center on the axis */
  flex-direction: row-reverse;
}

.moment-item.right .time-node {
  left: -10px; /* Center on the axis */
  flex-direction: row;
}

.node-dot {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: var(--dark-bg); /* 确保节点背景遮挡中轴线，营造穿过感 */
  border-radius: 50%;
  z-index: 2; /* 确保在光束上方 */
}

.dot-core {
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 
    0 0 8px #fff, 
    0 0 15px var(--neon-blue);
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
.dot-ring::before, .dot-ring::after {
  content: '';
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
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(1.2); opacity: 0.8; }
}
@keyframes star-spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.node-date {
  font-family: "Share Tech Mono";
  color: var(--neon-blue);
  font-size: 14px;
  background: rgba(5, 11, 20, 0.8);
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid rgba(0, 243, 255, 0.2);
  white-space: nowrap;
  transition: all 0.3s;
  text-shadow: 0 0 5px rgba(0, 243, 255, 0.5);
}

.moment-item.left .node-date {
  margin-right: 25px;
}
.moment-item.right .node-date {
  margin-left: 25px;
}

/* Hover effect on date */
.time-node:hover .node-date {
  background: rgba(0, 243, 255, 0.1);
  border-color: var(--neon-blue);
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.2);
}


/* --- Card --- */
.moment-card {
  background: linear-gradient(145deg, rgba(5, 11, 20, 0.9), rgba(10, 21, 37, 0.9));
  border: 1px solid rgba(0, 243, 255, 0.3);
  border-radius: 4px;
  padding: 15px;
  width: 80%;
  max-width: 400px;
  position: relative;
  transition: all 0.3s ease;
  margin-top: 40px; /* Reduced spacing */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
}

.moment-card:hover {
  border-color: var(--neon-blue);
  box-shadow: 0 0 20px rgba(0, 243, 255, 0.15);
  transform: translateY(-2px);
}

/* Connector Lines (Removed) */
/*
.moment-card::before {
  content: '';
  position: absolute;
  top: -40px;
  height: 40px;
  width: 50px;
  pointer-events: none;
  border-bottom: 1px dashed rgba(0, 243, 255, 0.5);
}

.moment-item.left .moment-card::before {
  right: -50px; 
  border-right: 1px dashed rgba(0, 243, 255, 0.5);
  border-bottom-right-radius: 15px;
  background: transparent;
}

.moment-item.right .moment-card::before {
  left: -50px;
  border-left: 1px dashed rgba(0, 243, 255, 0.5);
  border-bottom-left-radius: 15px;
  background: transparent;
}

.moment-card::after {
  content: '';
  position: absolute;
  width: 4px;
  height: 4px;
  background: var(--neon-blue);
  box-shadow: 0 0 5px var(--neon-blue);
  border-radius: 50%;
}
.moment-item.left .moment-card::after {
  top: -2px;
  right: -2px;
}
.moment-item.right .moment-card::after {
  top: -2px;
  left: -2px;
}
*/


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
.card-deco {
  width: 30px;
  height: 4px;
  background: repeating-linear-gradient(
    90deg,
    var(--neon-blue),
    var(--neon-blue) 2px,
    transparent 2px,
    transparent 4px
  );
}

.card-body {
  font-size: 16px;
  color: #fff;
  line-height: 1.5;
}

.moment-text {
  margin-bottom: 10px;
  word-break: break-all;
}

.moment-photos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 8px;
  margin-top: 10px;
}
.photo-wrapper {
  aspect-ratio: 1;
  overflow: hidden;
  border: 1px solid rgba(0, 243, 255, 0.3);
  border-radius: 2px;
}
.photo-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}
.photo-wrapper img:hover {
  transform: scale(1.1);
}

.empty-state {
  display: flex;
  justify-content: center;
  margin-top: 100px;
}
.empty-text {
  font-family: "Share Tech Mono";
  color: rgba(255, 255, 255, 0.3);
  font-size: 18px;
}

/* Mobile Adaption */
@media (max-width: 768px) {
  /* 
  .central-line {
    left: 20px;
    width: 4px;
  }
  */
  .moment-item {
    width: 100%;
    padding-left: 60px; /* More space for axis */
    padding-right: 20px;
    text-align: left !important;
    align-items: flex-start !important;
    left: 0 !important;
    transform: none !important; /* Reset transform */
  }
  .moment-item.visible {
    transform: none !important;
  }
  
  /* Reset Time Node for Mobile */
  .moment-item.left .time-node,
  .moment-item.right .time-node {
    left: 10px; /* Center on the 20px axis (20 - 10 = 10) */
    right: auto;
    flex-direction: row;
    top: 0;
  }
  
  .node-date {
    margin-left: 25px !important;
    margin-right: 0 !important;
    font-size: 12px;
  }
  
  .moment-card {
    width: 100%;
    max-width: none;
    margin-top: 40px;
  }
  
  /* Mobile Connector: Always from left */
  .moment-item.left .moment-card::before,
  .moment-item.right .moment-card::before {
    left: -40px; /* 60px padding - 20px axis = 40px distance */
    right: auto;
    border-left: 1px dashed rgba(0, 243, 255, 0.5);
    border-right: none;
    border-bottom-left-radius: 15px;
    border-bottom-right-radius: 0;
    width: 40px;
  }
  
  .moment-item.left .moment-card::after,
  .moment-item.right .moment-card::after {
    left: -2px;
    right: auto;
  }
}
</style>

