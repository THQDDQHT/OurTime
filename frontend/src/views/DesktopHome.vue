<template>
  <div class="desktop-container">
    <!-- 背景层：Deep Space Grid -->
    <div class="perspective-grid"></div>
    <div class="stars"></div>
    <div class="scanlines"></div>
    <div class="vignette"></div>

    <!-- 顶部状态栏 -->
    <div class="status-bar">
      <div class="status-item">
        <span class="status-label">USER:</span>
        <span class="status-value">{{
          authStore.user?.username || "GUEST"
        }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">NET:</span>
        <span class="status-value connected">ONLINE</span>
      </div>
      <div class="logout-trigger" @click="handleLogout">
        <div class="icon-power"></div>
        <span>断开连接</span>
      </div>
    </div>

    <!-- 主控制台布局 (Grid Layout) -->
    <div class="main-console">
      <!-- 左翼：数据档案 (Left Wing) -->
      <div class="console-column left-wing">
        <div class="panel-header">
          <span class="panel-title">ARCHIVE_DATA // 记忆库</span>
          <div class="panel-deco"></div>
        </div>
        <div class="panel-content album-list-wrapper">
          <DesktopMemoryHelix @open-archive="showAlbumModal = true" />
        </div>
      </div>

      <!-- 中央核心：时空罗盘 (Center Core) -->
      <div class="console-column center-core" @click="router.push('/timeline')">
        <div class="core-ring-outer"></div>
        <div class="core-ring-inner"></div>

        <div class="chrono-dial">
          <div class="dial-ticks"></div>
          <div
            class="seconds-scanner"
            :style="{ transform: `rotate(${secondsDeg}deg)` }"
          ></div>

          <div class="time-display">
            <div class="time-label">CURRENT_CYCLE</div>
            <div class="time-value">{{ timeString }}</div>
            <div class="date-value">{{ dateString }}</div>
          </div>
        </div>

        <div class="core-status">
          <span class="status-text">TEMPORAL FLUX: STABLE</span>
        </div>
      </div>

      <!-- 右翼：声波反应堆 (Right Wing) -->
      <div class="console-column right-wing">
        <div class="panel-header">
          <span class="panel-title">SONIC_REACTOR // 播放器</span>
          <div class="panel-deco"></div>
        </div>
        <div class="panel-content player-wrapper">
          <DesktopTechPlayer />
        </div>
      </div>
    </div>

    <!-- 底部指令台 (Bottom Console) -->
    <div class="bottom-console" @click="showCreateModal = true">
      <div class="console-frame">
        <div class="console-label">INPUT_TERMINAL // 等待指令</div>
        <div class="input-area">
          <span class="prompt">></span>
          <span class="cursor">_</span>
          <span class="placeholder">点击此处录入新的时空节点数据...</span>
        </div>
        <div class="console-stats">
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
      class="cyber-modal"
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
import { useMessage, NModal } from "naive-ui";
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
@import url("https://fonts.googleapis.com/css2?family=Rajdhani:wght@500;700&family=Share+Tech+Mono&display=swap");

.desktop-container {
  --neon-blue: #00f3ff;
  --neon-purple: #bc13fe;
  --neon-red: #ff0055;
  --dark-bg: #050b14;
  --panel-bg: rgba(5, 11, 20, 0.6);

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

/* --- Deep Space Background --- */
.perspective-grid {
  position: absolute;
  width: 200%;
  height: 200%;
  left: -50%;
  top: -50%;
  background-image: linear-gradient(rgba(0, 243, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.1) 1px, transparent 1px);
  background-size: 60px 60px;
  transform: perspective(500px) rotateX(60deg) translateY(0) translateZ(-100px);
  animation: grid-fly 20s linear infinite;
  opacity: 0.2;
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
  background: radial-gradient(circle, transparent 60%, black 100%);
  pointer-events: none;
  z-index: 800;
}

/* --- Status Bar --- */
.status-bar {
  height: 40px;
  width: 100%;
  background: rgba(0, 0, 0, 0.8);
  border-bottom: 1px solid rgba(0, 243, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  gap: 20px;
  z-index: 100;
  font-family: "Share Tech Mono";
  font-size: 12px;
}

.status-item {
  display: flex;
  gap: 5px;
}
.status-label {
  color: rgba(255, 255, 255, 0.5);
}
.status-value {
  color: #fff;
}
.status-value.connected {
  color: #0f0;
  text-shadow: 0 0 5px #0f0;
}

.logout-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--neon-red);
  border: 1px solid rgba(255, 0, 85, 0.3);
  padding: 2px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}
.logout-trigger:hover {
  background: rgba(255, 0, 85, 0.1);
  box-shadow: 0 0 10px rgba(255, 0, 85, 0.3);
}
.icon-power {
  width: 12px;
  height: 12px;
  border: 2px solid var(--neon-red);
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
  background: var(--neon-red);
}

/* --- Main Console Grid --- */
.main-console {
  flex: 1;
  display: grid;
  grid-template-columns: 300px 1fr 300px; /* 三栏布局 */
  gap: 20px;
  padding: 40px;
  perspective: 1000px;
  z-index: 10;
}

.console-column {
  position: relative;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  border-bottom: 2px solid rgba(0, 243, 255, 0.3);
  padding-bottom: 5px;
}
.panel-title {
  font-family: "Share Tech Mono";
  font-size: 14px;
  color: rgba(0, 243, 255, 0.7);
}
.panel-deco {
  width: 20px;
  height: 20px;
  background: repeating-linear-gradient(
    45deg,
    var(--neon-blue),
    var(--neon-blue) 2px,
    transparent 2px,
    transparent 4px
  );
}

.panel-content {
  flex: 1;
  background: var(--panel-bg);
  border: 1px solid rgba(0, 243, 255, 0.2);
  backdrop-filter: blur(5px);
  position: relative;
  transition: all 0.3s;
}
.panel-content:hover {
  border-color: var(--neon-blue);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.1);
}

/* Left Wing: Album List Wrapper */
.album-list-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
  cursor: pointer;
}

/* Center Core: Chrono Dial */
.center-core {
  align-items: center;
  justify-content: center;
  cursor: pointer;
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
  border-top: 2px solid var(--neon-blue);
  box-shadow: 0 -5px 10px var(--neon-blue);
  transition: transform 1s linear;
}
.time-display {
  text-align: center;
  z-index: 10;
}
.time-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 2px;
}
.time-value {
  font-size: 64px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 0 20px var(--neon-blue);
  line-height: 1;
  font-variant-numeric: tabular-nums;
}
.date-value {
  font-size: 18px;
  color: var(--neon-blue);
}

.core-status {
  margin-top: 40px;
  padding: 5px 15px;
  background: rgba(0, 243, 255, 0.1);
  border: 1px solid var(--neon-blue);
  border-radius: 20px;
}
.status-text {
  font-family: "Share Tech Mono";
  font-size: 12px;
  letter-spacing: 2px;
}

/* Right Wing: Player */
.player-wrapper {
  padding: 10px;
  overflow: hidden;
}

/* Bottom Console */
.bottom-console {
  height: 100px;
  padding: 0 40px 20px 40px;
  z-index: 10;
}
.console-frame {
  width: 100%;
  height: 100%;
  background: var(--panel-bg);
  border: 1px solid rgba(0, 243, 255, 0.3);
  border-top: 2px solid var(--neon-blue);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  padding: 0 30px;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.3s;
}
.console-frame:hover {
  background: rgba(5, 11, 20, 0.8);
  box-shadow: 0 -5px 20px rgba(0, 243, 255, 0.1);
}

.console-label {
  font-family: "Share Tech Mono";
  color: rgba(0, 243, 255, 0.5);
  font-size: 12px;
  writing-mode: vertical-lr;
  transform: rotate(180deg);
  border-left: 1px solid rgba(0, 243, 255, 0.3);
  padding-left: 5px;
}

.input-area {
  flex: 1;
  margin-left: 20px;
  font-family: "Share Tech Mono";
  font-size: 18px;
  color: var(--neon-blue);
}
.prompt {
  margin-right: 10px;
}
.placeholder {
  color: rgba(255, 255, 255, 0.3);
  font-size: 14px;
}

.console-stats {
  display: flex;
  flex-direction: column;
  font-family: "Share Tech Mono";
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  border-left: 1px solid rgba(255, 255, 255, 0.1);
  padding-left: 10px;
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
  } /* 移动端隐藏底部控制台，或简化 */
}

@keyframes rotate-slow {
  100% {
    transform: rotate(360deg);
  }
}
@keyframes blink {
  50% {
    opacity: 0;
  }
}
</style>
