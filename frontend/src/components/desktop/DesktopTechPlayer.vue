<template>
  <div class="tech-reactor-player">
    <!-- 反应堆主体 -->
    <div class="reactor-assembly">
      <!-- 能量光环 (进度条) -->
      <div class="energy-containment-ring">
        <svg class="progress-ring-svg" viewBox="0 0 200 200">
          <circle class="progress-ring-bg" cx="100" cy="100" r="90" />
          <circle
            class="progress-ring-bar"
            cx="100"
            cy="100"
            r="90"
            :style="{ strokeDashoffset: progressOffset }"
          />
        </svg>

        <!-- 刻度装饰 -->
        <div class="scale-marks">
          <div
            class="mark"
            v-for="i in 36"
            :key="i"
            :style="{ transform: `rotate(${i * 10}deg)` }"
          ></div>
        </div>
      </div>

      <!-- 3D 旋转环 (装饰) -->
      <div class="gyro-stabilizer">
        <div class="ring ring-x"></div>
        <div class="ring ring-y"></div>
        <div class="ring ring-z"></div>
      </div>

      <!-- 核心 (播放/暂停控制) -->
      <div
        class="reactor-core"
        :class="{ 'is-active': musicStore.isPlaying }"
        @click="togglePlay"
      >
        <div class="core-plasma"></div>
        <!-- <div class="core-icon">
          <div class="play-triangle" v-if="!musicStore.isPlaying"></div>
          <div class="pause-bars" v-else>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
        </div> -->
      </div>
    </div>

    <!-- HUD 控制面板 -->
    <div class="hud-controls">
      <!-- 上一首 -->
      <div class="hud-btn prev" @click.stop="musicStore.prev">
        <div class="arrow-left"></div>
      </div>

      <!-- 下一首 -->
      <div class="hud-btn next" @click.stop="musicStore.next">
        <div class="arrow-right"></div>
      </div>

      <!-- 模式切换 -->
      <div class="hud-btn mode" @click.stop="musicStore.togglePlayMode">
        <span class="mode-text">{{ modeAbbr }}</span>
      </div>

      <!-- 列表按钮 -->
      <div class="hud-btn list" @click.stop="showManageModal = true">
        <div class="menu-grid">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
      </div>
    </div>

    <audio
      ref="audioRef"
      :src="musicStore.currentSong?.url"
      @timeupdate="handleTimeUpdate"
      @ended="handleEnded"
      @loadedmetadata="handleLoadedMetadata"
    ></audio>

    <!-- 歌单管理弹窗 (复用之前的逻辑，样式适配科技风) -->
    <n-modal
      v-model:show="showManageModal"
      preset="card"
      title="数据矩阵 // DATA_MATRIX"
      class="cyber-player-modal"
      size="huge"
      style="width: 800px; max-width: 90vw"
    >
      <n-tabs type="segment">
        <n-tab-pane name="list" tab="当前序列">
          <n-list hoverable clickable>
            <n-list-item v-for="song in musicStore.playlist" :key="song.id">
              <template #prefix>
                <div class="tech-avatar">
                  <img :src="song.cover || '/default-music.svg'" />
                </div>
              </template>
              <div class="song-list-item" @click="playSong(song)">
                <div class="song-list-info">
                  <n-text strong>{{ song.title }}</n-text>
                  <n-text depth="3" style="font-size: 12px">{{
                    song.artist
                  }}</n-text>
                </div>
                <div class="song-list-action">
                  <div
                    class="status-indicator"
                    :class="{ active: musicStore.currentSong?.id === song.id }"
                  ></div>
                </div>
              </div>
              <template #suffix>
                <n-button
                  size="tiny"
                  type="error"
                  ghost
                  @click.stop="handleDelete(song.id)"
                  >删除</n-button
                >
              </template>
            </n-list-item>
            <n-empty
              v-if="musicStore.playlist.length === 0"
              description="NO_DATA"
            />
          </n-list>
        </n-tab-pane>

        <n-tab-pane name="add" tab="注入数据">
          <n-form ref="formRef" :model="form" :rules="rules">
            <n-form-item label="音频数据 (MP3/FLAC)" path="url">
              <n-upload
                action="#"
                :custom-request="handleUploadMusic"
                :show-file-list="false"
                accept=".mp3,.wav,.m4a,.flac"
              >
                <n-button v-if="!form.url" ghost type="primary" color="#00f3ff"
                  >选择文件</n-button
                >
                <n-text v-else type="success"
                  >已加载: {{ form.url.split("/").pop() }}</n-text
                >
              </n-upload>
            </n-form-item>
            <n-form-item label="全息封面 (可选)" path="coverUrl">
              <n-upload
                action="#"
                :custom-request="handleUploadCover"
                :show-file-list="false"
                accept="image/*"
              >
                <div class="upload-cover-preview" v-if="form.coverUrl">
                  <img :src="resolveUploadUrl(form.coverUrl)" />
                </div>
                <n-button v-else ghost type="primary" color="#00f3ff"
                  >上传图像</n-button
                >
              </n-upload>
            </n-form-item>

            <div class="form-actions">
              <n-button
                type="primary"
                ghost
                block
                @click="handleSubmit"
                :loading="submitting"
                color="#00f3ff"
                >执行注入</n-button
              >
            </div>
          </n-form>
        </n-tab-pane>
      </n-tabs>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useMusicStore, type Song } from "@/stores/music";
import {
  NModal,
  NButton,
  NTabs,
  NTabPane,
  NList,
  NListItem,
  NEmpty,
  NForm,
  NFormItem,
  NUpload,
  NText,
  useMessage,
  type UploadCustomRequestOptions,
} from "naive-ui";
import { createMusic, deleteMusic } from "@/api/music";
import { uploadFile } from "@/api/upload";
import { resolveUploadUrl } from "@/utils/url";

const musicStore = useMusicStore();
const message = useMessage();
const audioRef = ref<HTMLAudioElement | null>(null);
const showManageModal = ref(false);
const submitting = ref(false);
const formRef = ref();

const form = ref({
  url: "",
  coverUrl: "",
});

const rules = {
  url: { required: true, message: "必须上传音频文件", trigger: "blur" },
};

const modeAbbr = computed(() => {
  switch (musicStore.playMode) {
    case "single":
      return "1";
    case "random":
      return "随";
    case "loop":
      return "循";
    default:
      return "循";
  }
});

// Progress Ring Calculation
const progressOffset = computed(() => {
  const r = 90; // Radius
  const c = 2 * Math.PI * r;
  const percent =
    musicStore.duration > 0 ? musicStore.currentTime / musicStore.duration : 0;
  return c * (1 - percent);
});

const togglePlay = () => {
  if (!musicStore.currentSong && musicStore.playlist.length > 0) {
    musicStore.currentSong = musicStore.playlist[0];
  }

  if (musicStore.isPlaying) {
    audioRef.value?.pause();
    musicStore.pause();
  } else {
    audioRef.value?.play();
    musicStore.play();
  }
};

const playSong = (song: Song) => {
  musicStore.currentSong = song;
  musicStore.play();
};

const handleDelete = async (id: number) => {
  try {
    await deleteMusic(id);
    message.success("已删除");
    await musicStore.loadPlaylist();
  } catch (error) {
    message.error("删除失败");
  }
};

const handleUploadMusic = async ({
  file,
  onFinish,
  onError,
}: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return;
    const res = await uploadFile(file.file);
    form.value.url = res.url;
    onFinish();
    message.success("上传完成");
  } catch (e) {
    onError();
    message.error("上传失败");
  }
};

const handleUploadCover = async ({
  file,
  onFinish,
  onError,
}: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return;
    const res = await uploadFile(file.file);
    form.value.coverUrl = res.url;
    onFinish();
    message.success("上传完成");
  } catch (e) {
    onError();
    message.error("上传失败");
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    await createMusic(form.value);
    message.success("数据注入成功");
    form.value = { url: "", coverUrl: "" };
    await musicStore.loadPlaylist();
  } catch (e) {
    message.error("注入失败");
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  musicStore.loadPlaylist();
});

// --- Audio Event Handlers ---
const handleTimeUpdate = () => {
  if (audioRef.value) musicStore.currentTime = audioRef.value.currentTime;
};
const handleLoadedMetadata = () => {
  if (audioRef.value) musicStore.duration = audioRef.value.duration;
};
const handleEnded = () => {
  if (musicStore.playMode === "single") {
    audioRef.value?.play();
  } else {
    musicStore.next();
  }
};

watch(
  () => musicStore.isPlaying,
  (val) => {
    if (val) setTimeout(() => audioRef.value?.play(), 50);
    else audioRef.value?.pause();
  }
);

watch(
  () => musicStore.currentSong,
  (newSong) => {
    if (!newSong) return;
    setTimeout(() => {
      if (musicStore.isPlaying) audioRef.value?.play();
    }, 100);
  }
);
</script>

<style scoped>
.tech-reactor-player {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: "Share Tech Mono", monospace;
  perspective: 1000px;
}

.reactor-assembly {
  position: relative;
  width: 220px;
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 能量核心 */
.reactor-core {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  position: relative;
  z-index: 10;
  cursor: pointer;
  background: radial-gradient(
    circle,
    #00f3ff 0%,
    rgba(0, 243, 255, 0.2) 60%,
    rgba(0, 0, 0, 0.8) 100%
  );
  box-shadow: 0 0 20px rgba(0, 243, 255, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.reactor-core:hover {
  transform: scale(1.05);
  box-shadow: 0 0 40px rgba(0, 243, 255, 0.6);
}

.reactor-core.is-active .core-plasma {
  animation: plasma-pulse 2s infinite;
}

.core-plasma {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.8) 0%,
    transparent 70%
  );
  opacity: 0.5;
}

.core-icon {
  z-index: 11;
  pointer-events: none;
}

.play-triangle {
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 10px 0 10px 16px;
  border-color: transparent transparent transparent #050b14;
  margin-left: 4px;
}

.pause-bars {
  display: flex;
  gap: 4px;
}

.pause-bars .bar {
  width: 4px;
  height: 20px;
  background-color: #050b14;
}

/* 陀螺仪稳定环 */
.gyro-stabilizer {
  position: absolute;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  pointer-events: none;
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 1px solid rgba(0, 243, 255, 0.3);
  box-shadow: 0 0 5px rgba(0, 243, 255, 0.1);
}

.ring-x {
  width: 140px;
  height: 140px;
  animation: rotate-x 8s linear infinite;
}

.ring-y {
  width: 170px;
  height: 170px;
  animation: rotate-y 12s linear infinite;
}

.ring-z {
  width: 200px;
  height: 200px;
  border: 1px dashed rgba(0, 243, 255, 0.2);
  animation: rotate-z 20s linear infinite;
}

.is-active .gyro-stabilizer .ring {
  border-color: rgba(0, 243, 255, 0.6);
}

/* 进度条环 */
.energy-containment-ring {
  position: absolute;
  width: 220px;
  height: 220px;
  z-index: 5;
  pointer-events: none;
}

.progress-ring-svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.progress-ring-bg {
  fill: transparent;
  stroke: rgba(0, 243, 255, 0.1);
  stroke-width: 4;
}

.progress-ring-bar {
  fill: transparent;
  stroke: #00f3ff;
  stroke-width: 4;
  stroke-dasharray: 565.48; /* 2 * PI * 90 */
  stroke-dashoffset: 565.48;
  transition: stroke-dashoffset 0.1s linear;
  filter: drop-shadow(0 0 5px #00f3ff);
}

.scale-marks {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.mark {
  position: absolute;
  top: 0;
  left: 50%;
  width: 2px;
  height: 6px;
  background: rgba(0, 243, 255, 0.3);
  transform-origin: bottom center;
  margin-left: -1px;
}

/* HUD Controls */
.hud-controls {
  margin-top: 40px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.hud-btn {
  width: 40px;
  height: 40px;
  border: 1px solid rgba(0, 243, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: rgba(5, 11, 20, 0.4);
  clip-path: polygon(10% 0, 100% 0, 100% 90%, 90% 100%, 0 100%, 0 10%);
}

.hud-btn:hover {
  background: rgba(0, 243, 255, 0.2);
  border-color: #00f3ff;
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.2);
}

.hud-btn:active {
  transform: scale(0.95);
}

.arrow-left {
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-right: 10px solid #00f3ff;
}

.arrow-right {
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 10px solid #00f3ff;
}

.mode-text {
  font-size: 12px;
  color: #00f3ff;
  font-weight: bold;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 4px);
  gap: 4px;
}

.menu-grid .dot {
  width: 4px;
  height: 4px;
  background: #00f3ff;
}

/* Animations */
@keyframes plasma-pulse {
  0%,
  100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.2);
  }
}

@keyframes rotate-x {
  0% {
    transform: translate(-50%, -50%) rotateX(0deg) rotateY(60deg);
  }
  100% {
    transform: translate(-50%, -50%) rotateX(360deg) rotateY(60deg);
  }
}

@keyframes rotate-y {
  0% {
    transform: translate(-50%, -50%) rotateY(0deg) rotateX(60deg);
  }
  100% {
    transform: translate(-50%, -50%) rotateY(360deg) rotateX(60deg);
  }
}

@keyframes rotate-z {
  0% {
    transform: translate(-50%, -50%) rotateZ(0deg);
  }
  100% {
    transform: translate(-50%, -50%) rotateZ(360deg);
  }
}

/* Modal Styles (Scoped to this component usage) */
.tech-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #00f3ff;
}
.tech-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #333;
  margin-right: 10px;
}
.status-indicator.active {
  background: #00f3ff;
  box-shadow: 0 0 5px #00f3ff;
}

.upload-cover-preview {
  width: 60px;
  height: 60px;
  border: 1px solid #00f3ff;
  overflow: hidden;
}
.upload-cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
