export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  role: string
}

export interface RegisterRequest {
  username: string
  password: string
}

export interface RegisterResponse {
  token: string
  userId: number
  username: string
  role: string
}

export interface UploadResponse {
  url: string
  width: number
  height: number
  size: number
}

export interface PhotoRequest {
  filePath: string
  width: number
  height: number
  size: number
}

export interface PhotoResponse {
  id: number
  filePath: string
  width: number
  height: number
  size: number
}

export interface MomentRequest {
  content?: string
  happenedAt: string
  location?: string
  albumId?: number
  photos?: PhotoRequest[]
}

export interface MomentResponse {
  id: number
  content?: string
  happenedAt: string
  location?: string
  albumId?: number
  albumName?: string
  createdAt: string
  photos: PhotoResponse[]
}

export interface AlbumRequest {
  name: string
  description?: string
  coverUrl?: string
}

export interface AlbumResponse {
  id: number
  name: string
  description?: string
  coverUrl?: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  last: boolean
}

export interface MusicRequest {
  title?: string
  url: string
  coverUrl?: string
}

export interface MusicResponse {
  id: number
  title: string
  url: string
  coverUrl?: string
  createdAt: string
}

