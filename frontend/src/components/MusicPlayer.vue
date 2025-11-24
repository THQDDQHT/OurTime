<template>
  <div class="music-player" :class="{ 'is-playing': musicStore.isPlaying }">
    <audio
      ref="audioRef"
      :src="musicStore.currentSong.url"
      @timeupdate="handleTimeUpdate"
      @ended="handleEnded"
      @loadedmetadata="handleLoadedMetadata"
    ></audio>

    <div class="player-content">
      <!-- 封面/黑胶效果 -->
      <div class="disc-container" :class="{ 'rotating': musicStore.isPlaying }">
        <div class="disc-wrapper">
          <img 
            :src="musicStore.currentSong.cover || '/default-music.png'" 
            class="disc-cover"
            alt="cover"
          />
          <div class="disc-hole"></div>
        </div>
      </div>

      <div class="info-controls">
        <div class="song-info">
          <n-text class="song-title" strong>{{ musicStore.currentSong.title }}</n-text>
          <n-text class="artist-name" depth="3">{{ musicStore.currentSong.artist }}</n-text>
        </div>

        <div class="controls">
          <n-button text class="control-btn" @click="musicStore.prev">
            <template #icon><n-icon size="20"><play-skip-back /></n-icon></template>
          </n-button>
          
          <n-button text circle class="play-btn" @click="togglePlay">
            <template #icon>
              <n-icon size="28">
                <pause v-if="musicStore.isPlaying" />
                <play v-else />
              </n-icon>
            </template>
          </n-button>
          
          <n-button text class="control-btn" @click="musicStore.next">
            <template #icon><n-icon size="20"><play-skip-forward /></n-icon></template>
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { NIcon, NButton, NText } from 'naive-ui'
import { Play, Pause, PlaySkipBack, PlaySkipForward } from '@vicons/ionicons5'
import { useMusicStore } from '@/stores/music'

const musicStore = useMusicStore()
const audioRef = ref<HTMLAudioElement | null>(null)

const togglePlay = () => {
  if (musicStore.isPlaying) {
    audioRef.value?.pause()
    musicStore.pause()
  } else {
    audioRef.value?.play()
    musicStore.play()
  }
}

const handleTimeUpdate = () => {
  if (audioRef.value) {
    musicStore.currentTime = audioRef.value.currentTime
  }
}

const handleLoadedMetadata = () => {
  if (audioRef.value) {
    musicStore.duration = audioRef.value.duration
  }
}

const handleEnded = () => {
  musicStore.next()
}

// 监听播放状态变化（例如从其他地方控制）
watch(() => musicStore.isPlaying, (newVal) => {
  if (newVal) {
    audioRef.value?.play()
  } else {
    audioRef.value?.pause()
  }
})

// 监听切歌
watch(() => musicStore.currentSong, () => {
  // 自动播放下一首
  setTimeout(() => {
    if (musicStore.isPlaying) {
      audioRef.value?.play()
    }
  }, 100)
})

// 监听音量
watch(() => musicStore.volume, (newVal) => {
  if (audioRef.value) {
    audioRef.value.volume = newVal
  }
})
</script>

<style scoped>
.music-player {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  padding: 12px;
  border-radius: 50px;
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05), 
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  max-width: 300px;
}

.music-player:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05),
    0 0 0 1px rgba(0,0,0,0.05);
}

.player-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.disc-container {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.disc-wrapper {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  background: #333;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border: 2px solid #2c3e50;
}

.disc-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.disc-hole {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #fff;
  border-radius: 50%;
  border: 4px solid #2c3e50;
}

.rotating .disc-wrapper {
  animation: rotate 10s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.info-controls {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 120px;
}

.song-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.song-title {
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.artist-name {
  font-size: 10px;
}

.controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.control-btn {
  color: #5d4037;
  opacity: 0.7;
}

.control-btn:hover {
  opacity: 1;
}

.play-btn {
  color: #5d4037;
}

.play-btn:hover {
  background: rgba(93, 64, 55, 0.1);
}
</style>
