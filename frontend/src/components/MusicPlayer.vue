<template>
  <div class="music-player" :class="{ 'is-playing': musicStore.isPlaying }">
    <audio
      ref="audioRef"
      :src="musicStore.currentSong?.url"
      @timeupdate="handleTimeUpdate"
      @ended="handleEnded"
      @loadedmetadata="handleLoadedMetadata"
    ></audio>

    <div class="player-content">
      <!-- 封面/黑胶效果 -->
      <div class="disc-container" :class="{ 'rotating': musicStore.isPlaying }" @click="showManageModal = true">
        <div class="disc-wrapper">
          <img 
            :src="musicStore.currentSong?.cover || '/default-music.svg'" 
            class="disc-cover"
            alt="cover"
          />
          <div class="disc-hole"></div>
        </div>
      </div>

      <div class="info-controls">
        <div class="song-info" v-if="musicStore.currentSong">
          <n-text class="song-title" strong>{{ musicStore.currentSong.title }}</n-text>
          <n-text class="artist-name" depth="3">{{ musicStore.currentSong.artist }}</n-text>
        </div>
        <div class="song-info" v-else>
          <n-text class="song-title" strong>点击黑胶片添加音乐</n-text>
          <n-text class="artist-name" depth="3">OurTime</n-text>
        </div>

        <div class="controls">
          <n-button text class="control-btn" @click="musicStore.togglePlayMode" :title="modeTitle">
            <template #icon>
              <n-icon size="20" v-if="musicStore.playMode === 'random'">
                <shuffle />
              </n-icon>
              <div v-else style="position: relative; display: flex; align-items: center; justify-content: center; width: 20px; height: 20px;">
                <n-icon size="20">
                  <repeat />
                </n-icon>
                <span v-if="musicStore.playMode === 'single'" style="position: absolute; font-size: 10px; font-weight: bold; top: 50%; left: 50%; transform: translate(-50%, -50%); pointer-events: none;">1</span>
              </div>
            </template>
          </n-button>

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
          
          <n-button text class="control-btn" @click="showManageModal = true">
            <template #icon><n-icon size="16"><list-outline /></n-icon></template>
          </n-button>
        </div>
      </div>
    </div>

    <!-- 歌单管理弹窗 -->
    <n-modal v-model:show="showManageModal" preset="card" title="黑胶唱片架" class="music-modal">
      <n-tabs type="segment">
        <n-tab-pane name="list" tab="当前播放">
          <n-list hoverable clickable>
            <n-list-item v-for="song in musicStore.playlist" :key="song.id">
              <template #prefix>
                <n-avatar :src="song.cover || '/default-music.svg'" size="small" round />
              </template>
              <div class="song-list-item" @click="playSong(song)">
                <div class="song-list-info">
                  <n-text strong>{{ song.title }}</n-text>
                  <n-text depth="3" style="font-size: 12px">{{ song.artist }}</n-text>
                </div>
                <div class="song-list-action">
                  <n-icon v-if="musicStore.currentSong?.id === song.id" color="#8c7b75"><musical-notes /></n-icon>
                  <n-button size="tiny" type="error" ghost @click.stop="handleDelete(song.id)">删除</n-button>
                </div>
              </div>
            </n-list-item>
            <n-empty v-if="musicStore.playlist.length === 0" description="还没有唱片，去添加几张吧" />
          </n-list>
        </n-tab-pane>
        
        <n-tab-pane name="add" tab="添加新歌">
          <n-form ref="formRef" :model="form" :rules="rules">
            <n-form-item label="音乐文件 (MP3 / FLAC)" path="url">
              <n-upload
                action="#"
                :custom-request="handleUploadMusic"
                :show-file-list="false"
                accept=".mp3,.wav,.m4a,.flac"
              >
                <n-button v-if="!form.url">选择文件</n-button>
                <n-text v-else type="success">已上传: {{ form.url.split('/').pop() }}</n-text>
              </n-upload>
            </n-form-item>
            <n-form-item label="封面图 (可选)" path="coverUrl">
              <n-upload
                action="#"
                :custom-request="handleUploadCover"
                :show-file-list="false"
                accept="image/*"
              >
                <div class="upload-cover-preview" v-if="form.coverUrl">
                   <img :src="resolveUploadUrl(form.coverUrl)" />
                </div>
                <n-button v-else>上传封面</n-button>
              </n-upload>
            </n-form-item>
            
            <div class="form-actions">
               <n-button type="primary" block @click="handleSubmit" :loading="submitting">添加到唱片架</n-button>
            </div>
          </n-form>
        </n-tab-pane>
      </n-tabs>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { NIcon, NButton, NText, NModal, NTabs, NTabPane, NList, NListItem, NAvatar, NEmpty, NForm, NFormItem, NInput, NUpload, useMessage, type UploadCustomRequestOptions } from 'naive-ui'
import { Play, Pause, PlaySkipBack, PlaySkipForward, ListOutline, MusicalNotes, Repeat, Shuffle } from '@vicons/ionicons5'
import { useMusicStore, type Song } from '@/stores/music'
import { createMusic, deleteMusic } from '@/api/music'
import { uploadFile } from '@/api/upload'
import { resolveUploadUrl } from '@/utils/url'

const musicStore = useMusicStore()
const message = useMessage()
const audioRef = ref<HTMLAudioElement | null>(null)
const showManageModal = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = ref({
  url: '',
  coverUrl: ''
})

  const rules = {
  url: { required: true, message: '请上传音乐文件', trigger: 'blur' }
}

const modeTitle = computed(() => {
  switch (musicStore.playMode) {
    case 'single': return '单曲循环'
    case 'random': return '随机播放'
    case 'loop': return '列表循环'
    default: return '列表循环'
  }
})

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
  if (musicStore.playMode === 'single') {
    if (audioRef.value) {
      audioRef.value.currentTime = 0
      audioRef.value.play()
    }
  } else {
    musicStore.next()
  }
}

const playSong = (song: Song) => {
  musicStore.currentSong = song
  musicStore.play()
}

const handleDelete = async (id: number) => {
  try {
    await deleteMusic(id)
    message.success('已删除')
    await musicStore.loadPlaylist()
  } catch (error) {
    message.error('删除失败')
  }
}

const handleUploadMusic = async ({ file, onFinish, onError }: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return
    const res = await uploadFile(file.file)
    form.value.url = res.url
    onFinish()
    message.success('音乐上传成功')
  } catch (e) {
    onError()
    message.error('上传失败')
  }
}

const handleUploadCover = async ({ file, onFinish, onError }: UploadCustomRequestOptions) => {
  try {
    if (!file.file) return
    const res = await uploadFile(file.file)
    form.value.coverUrl = res.url
    onFinish()
    message.success('封面上传成功')
  } catch (e) {
    onError()
    message.error('上传失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    await createMusic(form.value)
    message.success('添加成功')
    
    // 重置表单
    form.value = { url: '', coverUrl: '' }
    await musicStore.loadPlaylist()
    
    // 切换回列表tab (简单做法: 暂时不切，或者手动控制activeTab)
  } catch (e) {
    message.error('添加失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  musicStore.loadPlaylist()
})

// 监听播放状态变化（例如从其他地方控制）
watch(() => musicStore.isPlaying, (newVal) => {
  if (newVal) {
    // 必须加 nextTick 或者 setTimeout 确保 src 已经变了
    setTimeout(() => audioRef.value?.play(), 50)
  } else {
    audioRef.value?.pause()
  }
})

// 监听切歌
watch(() => musicStore.currentSong, (newSong) => {
  if (!newSong) return
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
  cursor: pointer;
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
  gap: 8px;
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

.music-modal {
  width: 400px;
}

.song-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 4px 0;
}

.song-list-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin-left: 12px;
}

.song-list-action {
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-cover-preview {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
}

.upload-cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-actions {
  margin-top: 24px;
}
</style>
