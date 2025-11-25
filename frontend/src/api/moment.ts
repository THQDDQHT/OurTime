import request from './request'
import type { MomentRequest, MomentResponse, PageResponse } from './types'

export const createMoment = (data: MomentRequest): Promise<MomentResponse> => {
  return request.post('/moments', data)
}

export const getMoments = (page: number = 0, size: number = 20, albumId?: number): Promise<PageResponse<MomentResponse>> => {
  return request.get('/moments', {
    params: { page, size, albumId }
  })
}

export const getMomentById = (id: number): Promise<MomentResponse> => {
  return request.get(`/moments/${id}`)
}

export const deleteMoment = (id: number): Promise<void> => {
  return request.delete(`/moments/${id}`)
}

