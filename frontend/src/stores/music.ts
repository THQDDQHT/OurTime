import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Song {
  id: number
  title: string
  artist: string
  url: string
  cover?: string
}

export const useMusicStore = defineStore('music', () => {
  const isPlaying = ref(false)
  const currentTime = ref(0)
  const duration = ref(0)
  const volume = ref(0.5)
  const currentIndex = ref(0)
  const showPlayer = ref(true)

  // 预设一些符合氛围的轻音乐
  // 注意：实际项目中应替换为可靠的CDN链接或后端存储的链接
  const playlist = ref<Song[]>([
    {
      id: 1,
      title: 'Gymnopédie No.1',
      artist: 'Erik Satie',
      url: 'https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=gymnopedie-no-1-106464.mp3',
      cover: 'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=200&h=200&fit=crop'
    },
    {
      id: 2,
      title: 'River Flows In You',
      artist: 'Yiruma (Cover)',
      url: 'https://cdn.pixabay.com/download/audio/2022/02/10/audio_fc06c71453.mp3?filename=river-flows-in-you-14260.mp3', // 示例链接
      cover: 'https://images.unsplash.com/photo-1513883049090-d0b7439799bf?w=200&h=200&fit=crop'
    }
  ])

  const currentSong = ref<Song>(playlist.value[0])

  const togglePlay = () => {
    isPlaying.value = !isPlaying.value
  }

  const play = () => {
    isPlaying.value = true
  }

  const pause = () => {
    isPlaying.value = false
  }

  const next = () => {
    currentIndex.value = (currentIndex.value + 1) % playlist.value.length
    currentSong.value = playlist.value[currentIndex.value]
    isPlaying.value = true
  }

  const prev = () => {
    currentIndex.value = (currentIndex.value - 1 + playlist.value.length) % playlist.value.length
    currentSong.value = playlist.value[currentIndex.value]
    isPlaying.value = true
  }

  return {
    isPlaying,
    currentTime,
    duration,
    volume,
    playlist,
    currentSong,
    showPlayer,
    togglePlay,
    play,
    pause,
    next,
    prev
  }
})

