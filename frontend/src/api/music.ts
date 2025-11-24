import request from './request'
import type { MusicRequest, MusicResponse } from './types'

export const getMusicList = (): Promise<MusicResponse[]> => {
  return request.get('/music')
}

export const createMusic = (data: MusicRequest): Promise<MusicResponse> => {
  return request.post('/music', data)
}

export const deleteMusic = (id: number): Promise<void> => {
  return request.delete(`/music/${id}`)
}

