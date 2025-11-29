<template>
  <div
    class="w-screen h-screen bg-dark-bg overflow-hidden relative font-rajdhani text-neon-blue flex flex-col"
  >
    <!-- 背景层：Deep Space Grid -->
    <div class="perspective-grid"></div>
    <div class="stars"></div>
    <div class="scanlines"></div>
    <div class="vignette"></div>

    <!-- 顶部状态栏 -->
    <div
      class="h-10 w-full bg-black/80 border-b border-neon-blue/30 flex items-center justify-end px-5 gap-5 z-[100] font-share-tech-mono text-xs"
    >
      <div
        class="flex items-center gap-2 cursor-pointer text-neon-red border border-neon-red/30 px-2 py-0.5 rounded transition-all duration-300 hover:bg-neon-red/10 hover:shadow-glow-red"
        @click="handleLogout"
      >
        <div class="icon-power"></div>
        <span>断开连接</span>
      </div>
    </div>

    <!-- 主控制台布局 (Grid Layout) -->
    <div
      class="flex-1 grid grid-cols-[300px_1fr_300px] gap-5 p-10 perspective-[1000px] z-10"
    >
      <!-- 左翼：数据档案 (Left Wing) -->
      <div class="relative flex flex-col">
        <div
          class="flex justify-between items-center mb-2.5 border-b-2 border-neon-blue/30 pb-1"
        >
          <span
            class="font-share-tech-mono text-sm text-neon-blue/70"
            >ARCHIVE_DATA // 记忆库</span
          >
          <div class="panel-deco"></div>
        </div>
        <div
          class="flex-1 bg-panel-bg border border-neon-blue/20 backdrop-blur-[5px] relative transition-all duration-300 flex flex-col justify-between p-5 cursor-pointer hover:border-neon-blue hover:shadow-glow-blue-sm"
        >
          <DesktopMemoryHelix @open-archive="showAlbumModal = true" />
          <div
            class="mt-auto w-full pt-4 border-t border-neon-blue/10"
          >
            <n-button
              class="font-share-tech-mono font-bold tracking-wide transition-all duration-300"
              ghost
              block
              @click.stop="showAlbumModal = true"
            >
              <template #icon>
                <n-icon><AlbumsOutline /></n-icon>
              </template>
              打开档案库
            </n-button>
          </div>
        </div>
      </div>

      <!-- 中央核心：时空罗盘 (Center Core) -->
      <div
        class="relative flex flex-col items-center justify-center cursor-pointer"
        @click="router.push('/timeline')"
      >
        <div class="core-ring-outer"></div>
        <div class="core-ring-inner"></div>

        <div class="chrono-dial">
          <div class="dial-ticks"></div>
          <div
            class="seconds-scanner"
            :style="{ transform: `rotate(${secondsDeg}deg)` }"
          ></div>

          <div class="text-center z-10">
            <div class="text-xs text-white/50 tracking-[2px]">
              CURRENT_CYCLE
            </div>
            <div
              class="text-[64px] font-bold text-white text-shadow-glow-white leading-none tabular-nums"
            >
              {{ timeString }}
            </div>
            <div class="text-lg text-neon-blue">
              {{ dateString }}
            </div>
          </div>
        </div>

        <div
          class="mt-10 py-1 px-4 bg-neon-blue/10 border border-neon-blue rounded-[20px]"
        >
          <span class="font-share-tech-mono text-xs tracking-[2px]"
            >TEMPORAL FLUX: STABLE</span
          >
        </div>
      </div>

      <!-- 右翼：声波反应堆 (Right Wing) -->
      <div class="relative flex flex-col">
        <div
          class="flex justify-between items-center mb-2.5 border-b-2 border-neon-blue/30 pb-1"
        >
          <span
            class="font-share-tech-mono text-sm text-neon-blue/70"
            >SONIC_REACTOR // 播放器</span
          >
          <div class="panel-deco"></div>
        </div>
        <div
          class="flex-1 bg-panel-bg border border-neon-blue/20 backdrop-blur-[5px] relative transition-all duration-300 p-2.5 overflow-hidden hover:border-neon-blue hover:shadow-glow-blue-sm"
        >
          <DesktopTechPlayer />
        </div>
      </div>
    </div>

    <!-- 底部指令台 (Bottom Console) -->
    <div
      class="h-24 px-10 pb-5 z-10"
      @click="showCreateModal = true"
    >
      <div
        class="w-full h-full bg-panel-bg border border-neon-blue/30 backdrop-blur-[5px] flex items-center px-8 justify-between cursor-pointer transition-all duration-300 hover:bg-dark-bg/80 hover:shadow-[0_-5px_20px_rgba(0,243,255,0.1)]"
      >
        <div
          class="flex-1 ml-5 font-share-tech-mono text-lg text-neon-blue"
        >
          <span class="mr-2.5">></span>
          <span>_</span>
          <span class="text-white/30 text-sm">
            点击此处录入新的时空节点数据...</span
          >
        </div>
        <div
          class="flex flex-col font-share-tech-mono text-[10px] text-white/40 border-l border-white/10 pl-2.5"
        >
          <span>MEM: 64TB</span>
          <span>CPU: 12%</span>
        </div>
      </div>
    </div>

    <!-- 弹窗组件 -->
    <CreateMomentModal
      v-model:show="showCreateModal"
      @success="handleCreateSuccess"
    />

    <n-modal
      v-model:show="showAlbumModal"
      preset="card"
      size="huge"
      title="记忆档案库"
    >
      <AlbumList is-modal />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useMessage, NModal, NButton, NIcon } from "naive-ui";
import { AlbumsOutline } from "@vicons/ionicons5";
import { useAuthStore } from "@/stores/auth";
import DesktopTechPlayer from "@/components/desktop/DesktopTechPlayer.vue";
import DesktopMemoryHelix from "@/components/desktop/DesktopMemoryHelix.vue";
import CreateMomentModal from "@/components/CreateMomentModal.vue";
import AlbumList from "@/components/AlbumList.vue";

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();

const showCreateModal = ref(false);
const showAlbumModal = ref(false);

// --- 时间控制逻辑 ---
const timeString = ref("");
const dateString = ref("");
const secondsDeg = ref(0);
let timer: number | null = null;

const updateTime = () => {
  const now = new Date();
  // 格式化时间: 12:34
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  timeString.value = `${hours}:${minutes}`;

  // 格式化日期: MON 12.25
  const days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const date = String(now.getDate()).padStart(2, "0");
  dateString.value = `${days[now.getDay()]} ${month}.${date}`;

  // 计算秒针角度 (平滑移动可增加 transition，这里用每秒跳动)
  const seconds = now.getSeconds();
  // 秒针一圈是 360度，60秒。为了做成扫描雷达效果，可以让它一直转
  // 这里简单映射：0s = 0deg, 60s = 360deg
  secondsDeg.value = seconds * 6;
};

const handleCreateSuccess = () => {
  message.success("数据上传完成");
};

const handleLogout = () => {
  message.info("正在启动弹出程序...");
  authStore.logout();
  setTimeout(() => {
    router.push("/login");
  }, 1000);
};

onMounted(() => {
  updateTime();
  timer = setInterval(updateTime, 1000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
/* --- Deep Space Background --- */
.perspective-grid {
  position: absolute;
  width: 200%;
  height: 200%;
  left: -50%;
  top: -50%;
  background-image: linear-gradient(rgba(0, 243, 255, 0.2) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.2) 1px, transparent 1px);
  background-size: 60px 60px;
  transform: perspective(500px) rotateX(60deg) translateY(0) translateZ(-100px);
  animation: grid-fly 20s linear infinite;
  opacity: 0.4;
  pointer-events: none;
  z-index: 0;
}

@keyframes grid-fly {
  0% {
    transform: perspective(500px) rotateX(60deg) translateY(0)
      translateZ(-100px);
  }
  100% {
    transform: perspective(500px) rotateX(60deg) translateY(60px)
      translateZ(-100px);
  }
}

.stars {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(white 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.1;
  z-index: 0;
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
  z-index: 900;
}

.vignette {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, transparent 50%, rgba(0, 0, 0, 0.8) 100%);
  pointer-events: none;
  z-index: 5;
}

.icon-power {
  width: 12px;
  height: 12px;
  border: 2px solid #ff0055;
  border-radius: 50%;
  position: relative;
}
.icon-power::after {
  content: "";
  position: absolute;
  top: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 6px;
  background: #ff0055;
}
.panel-deco {
  width: 20px;
  height: 20px;
  background: repeating-linear-gradient(
    45deg,
    #00f3ff,
    #00f3ff 2px,
    transparent 2px,
    transparent 4px
  );
}
.core-ring-outer {
  position: absolute;
  width: 400px;
  height: 400px;
  border: 1px solid rgba(0, 243, 255, 0.2);
  border-radius: 50%;
  animation: rotate-slow 60s linear infinite;
}
.core-ring-inner {
  position: absolute;
  width: 360px;
  height: 360px;
  border: 2px dashed rgba(0, 243, 255, 0.1);
  border-radius: 50%;
  animation: rotate-slow 40s linear infinite reverse;
}

.chrono-dial {
  position: relative;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 0, 0, 0.8) 0%, transparent 70%);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 50px rgba(0, 243, 255, 0.1);
}
.dial-ticks {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 1px solid rgba(0, 243, 255, 0.3);
}
.seconds-scanner {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  border-top: 2px solid #00f3ff;
  box-shadow: 0 -5px 10px #00f3ff;
  transition: transform 1s linear;
}

/* Mobile Adaption */
@media (max-width: 1024px) {
  .main-console {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
    overflow-y: auto;
    padding: 20px;
  }
  .center-core {
    order: -1;
    margin-bottom: 40px;
  }
  .console-column {
    min-height: 200px;
  }
  .bottom-console {
    display: none;
  }
}

@keyframes rotate-slow {
  100% {
    transform: rotate(360deg);
  }
}
</style>
