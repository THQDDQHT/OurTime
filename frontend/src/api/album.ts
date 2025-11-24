import request from './request'
import type { AlbumRequest, AlbumResponse } from './types'

export const getAlbums = (): Promise<AlbumResponse[]> => {
  return request.get('/albums')
}

export const getAlbumById = (id: number): Promise<AlbumResponse> => {
  return request.get(`/albums/${id}`)
}

export const createAlbum = (data: AlbumRequest): Promise<AlbumResponse> => {
  return request.post('/albums', data)
}

export const updateAlbum = (id: number, data: AlbumRequest): Promise<AlbumResponse> => {
  return request.put(`/albums/${id}`, data)
}

export const deleteAlbum = (id: number): Promise<void> => {
  return request.delete(`/albums/${id}`)
}

