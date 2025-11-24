import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMusicList } from '@/api/music'
import type { MusicResponse } from '@/api/types'
import { resolveUploadUrl } from '@/utils/url'

export interface Song {
  id: number
  title: string
  artist: string
  url: string
  cover?: string
}

export type PlayMode = 'loop' | 'single' | 'random'

export const useMusicStore = defineStore('music', () => {
  const isPlaying = ref(false)
  const playMode = ref<PlayMode>('loop')
  const currentTime = ref(0)
  const duration = ref(0)
  const volume = ref(0.5)
  const currentIndex = ref(0)
  const showPlayer = ref(true)
  const playlist = ref<Song[]>([])
  const currentSong = ref<Song | null>(null)

  const loadPlaylist = async () => {
    try {
      const musicList = await getMusicList()
      if (musicList.length > 0) {
        playlist.value = musicList.map(m => {
          // 从 url 中解析文件名作为标题
          const filename = m.url.split('/').pop() || 'Unknown Track'
          // 解码 URL
          let title = decodeURIComponent(filename)
          
          // 去掉 uuid 前缀 (假设格式为 uuid_filename)
          if (title.includes('_')) {
            title = title.split('_').slice(1).join('_')
          }
          
          // 去掉文件后缀 (.mp3, .flac 等)
          if (title.lastIndexOf('.') > 0) {
            title = title.substring(0, title.lastIndexOf('.'))
          }
          
          return {
            id: m.id,
            title: title, 
            artist: 'Local Music', // 通用艺术家名
            url: resolveUploadUrl(m.url),
            cover: m.coverUrl ? resolveUploadUrl(m.coverUrl) : undefined
          }
        })
        
        // 如果当前没有播放歌曲，或者当前歌曲不在新的播放列表中，则重置为第一首
        if (!currentSong.value || !playlist.value.find(s => s.id === currentSong.value?.id)) {
          currentSong.value = playlist.value[0]
          currentIndex.value = 0
        }
      } else {
        // 空列表处理
        playlist.value = []
        currentSong.value = null
      }
    } catch (error) {
      console.error('Failed to load music playlist', error)
    }
  }

  const togglePlay = () => {
    if (!currentSong.value) return
    isPlaying.value = !isPlaying.value
  }

  const togglePlayMode = () => {
    const modes: PlayMode[] = ['loop', 'single', 'random']
    const nextIndex = (modes.indexOf(playMode.value) + 1) % modes.length
    playMode.value = modes[nextIndex]
  }

  const play = () => {
    if (!currentSong.value) return
    isPlaying.value = true
  }

  const pause = () => {
    isPlaying.value = false
  }

  const next = () => {
    if (playlist.value.length === 0) return
    if (playMode.value === 'random') {
      let nextIndex = Math.floor(Math.random() * playlist.value.length)
      if (playlist.value.length > 1 && nextIndex === currentIndex.value) {
        nextIndex = (nextIndex + 1) % playlist.value.length
      }
      currentIndex.value = nextIndex
    } else {
      currentIndex.value = (currentIndex.value + 1) % playlist.value.length
    }
    currentSong.value = playlist.value[currentIndex.value]
    isPlaying.value = true
  }

  const prev = () => {
    if (playlist.value.length === 0) return
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
    playMode,
    loadPlaylist,
    togglePlayMode,
    togglePlay,
    play,
    pause,
    next,
    prev
  }
})

