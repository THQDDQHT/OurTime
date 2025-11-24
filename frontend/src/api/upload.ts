import request from './request'
import type { UploadResponse } from './types'

export const uploadFile = (file: File): Promise<UploadResponse> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

