<template>
  <div class="memory-helix-container w-full h-full relative flex flex-col overflow-hidden perspective-[800px] font-share-tech-mono">
    <div class="helix-viewport flex-1 relative flex justify-center items-center preserve-3d">
      <div
        class="helix-structure"
        :style="{ transform: `rotateY(${rotation}deg)` }"
      >
        <!-- 螺旋节点 -->
        <div
          v-for="(node, index) in nodes"
          :key="node.id"
          class="helix-node"
          :style="getNodeStyle(index)"
          @click="$emit('open-archive')"
        >
          <div class="node-content">
            <div class="node-image">
              <img :src="node.url" loading="lazy" />
            </div>
            <div class="node-label" v-if="node.title !== 'LOST_SIGNAL'">
              {{ node.title }}
            </div>
            <div class="node-connector"></div>
          </div>
        </div>

        <!-- 中心轴 -->
        <div class="central-axis"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { getAlbums } from "@/api/album";
import { getMoments } from "@/api/moment";
import { resolveUploadUrl } from "@/utils/url";

interface HelixNode {
  id: number | string;
  url: string;
  title?: string;
}

const nodes = ref<HelixNode[]>([]);
const rotation = ref(0);
let animationFrameId: number;

// 星球 SVG (作为占位符)
const PLANET_SVG = `data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj4KICA8Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDBmM2ZmIiBzdHJva2Utd2lkdGg9IjIiIG9wYWNpdHk9IjAuMyIvPgogIDxjaXJjbGUgY3g9IjUwIiBjeT0iNTAiIHI9IjI1IiBmaWxsPSJyZ2JhKDAsIDI0MywgMjU1LCAwLjEpIiBzdHJva2U9IiMwMGYzZmYiIHN0cm9rZS13aWR0aD0iMiIvPgogIDxlbGxpcHNlIGN4PSI1MCIgY3k9IjUwIiByeD0iNDUiIHJ5PSIxMCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDBmM2ZmIiBzdHJva2Utd2lkdGg9IjEiIHRyYW5zZm9ybT0icm90YXRlKC0zMCA1MCA1MCkiIG9wYWNpdHk9IjAuNSIvPgo8L3N2Zz4=`;

// 获取第一个相册的照片
const fetchPhotos = async () => {
  try {
    // 1. 获取相册列表
    const albumRes = await getAlbums();
    const albums = albumRes || [];

    let photos: HelixNode[] = [];

    // 2. 如果有相册，获取第一个相册的 moments
    if (albums.length > 0) {
      const firstAlbum = albums[0];
      // 增加获取数量到 50
      const momentRes = await getMoments(0, 50, firstAlbum.id);
      const moments = momentRes.content || [];

      // 3. 提取照片
      moments.forEach((m) => {
        if (m.photos && m.photos.length > 0) {
          m.photos.forEach((p) => {
            photos.push({
              id: p.id,
              url: resolveUploadUrl(p.filePath),
              title: m.content || "",
            });
          });
        }
      });
    }

    // 4. 双螺旋需要更多节点，设置最小值为 24
    const MIN_NODES = 26;
    while (photos.length < MIN_NODES) {
      photos.push({
        id: `planet-${photos.length}`,
        url: PLANET_SVG,
        title: "LOST_SIGNAL",
      });
    }

    // 限制数量，展示前 30 个 (确保是偶数以保持对称)
    const DISPLAY_COUNT = 30;
    nodes.value = photos.slice(0, DISPLAY_COUNT);
  } catch (e) {
    console.error(e);
    // 出错也用星球填充
    const fallback: HelixNode[] = [];
    for (let i = 0; i < 26; i++) {
      fallback.push({
        id: `planet-${i}`,
        url: PLANET_SVG,
        title: "LOST_SIGNAL",
      });
    }
    nodes.value = fallback;
  }
};

// 计算每个节点的3D位置 (双螺旋逻辑)
const getNodeStyle = (index: number) => {
  const total = nodes.value.length;
  
  // 两个为一组（层）
  const layerIndex = Math.floor(index / 2);
  const isSecondStrand = index % 2 === 1;
  
  const totalLayers = Math.ceil(total / 2);
  const ySpacing = 22; // 减小垂直间距，更紧凑
  
  // 垂直位置
  const yOffset = (layerIndex - totalLayers / 2) * ySpacing;
  
  // 旋转角度: 每一层旋转一定角度
  const angleStep = 25; 
  let angle = layerIndex * angleStep;
  
  // 第二条链相位偏移 180 度
  if (isSecondStrand) {
    angle += 180;
  }

  const radius = 90; // 螺旋半径

  return {
    transform: `translateY(${yOffset}px) rotateY(${angle}deg) translateZ(${radius}px)`,
  };
};

// 自动旋转动画
const animate = () => {
  rotation.value += 0.3; // 稍微加快一点旋转速度
  animationFrameId = requestAnimationFrame(animate);
};

onMounted(async () => {
  await fetchPhotos();
  animate();
});

onUnmounted(() => {
  cancelAnimationFrame(animationFrameId);
});
</script>

<style scoped>
/* 3D 螺旋结构 - 保留所有动画效果 */
.helix-structure {
  position: relative;
  width: 0;
  height: 0;
  transform-style: preserve-3d;
}

.central-axis {
  position: absolute;
  top: -200px;
  left: 0;
  width: 2px;
  height: 400px;
  background: linear-gradient(to bottom, transparent, #00f3ff, transparent);
  box-shadow: 0 0 10px #00f3ff;
  transform: translateX(-50%);
  opacity: 0.3;
}

.helix-node {
  position: absolute;
  top: 0;
  left: 0;
  width: 50px;
  height: 50px;
  transform-style: preserve-3d;
  cursor: pointer;
  transition: filter 0.3s;
}

.helix-node:hover {
  filter: brightness(1.5) drop-shadow(0 0 5px #00f3ff);
  z-index: 100;
}

.node-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 面向屏幕 */
  backface-visibility: visible;
}

.node-image {
  width: 36px;
  height: 36px;
  border: 1px solid #00f3ff;
  background: rgba(5, 11, 20, 0.8);
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.3);
}

.node-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.node-label {
  position: absolute;
  left: 100%;
  margin-left: 8px;
  color: #00f3ff;
  font-size: 9px;
  white-space: nowrap;
  text-shadow: 0 0 2px #00f3ff;
  opacity: 0.6;
  pointer-events: none;
  background: rgba(0, 0, 0, 0.5);
  padding: 1px 3px;
}

/* 连接线: 指向中心 */
.node-connector {
  position: absolute;
  right: 100%;
  top: 50%;
  width: 90px; /* 连接到中心，半径是90 */
  height: 1px;
  background: linear-gradient(to left, rgba(0, 243, 255, 0.5), transparent);
  opacity: 0.2;
  transform-origin: right center;
  transform: rotateY(-90deg); 
  pointer-events: none;
}
</style>
