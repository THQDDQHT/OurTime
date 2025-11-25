import request from './request'
import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from './types'

export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return request.post('/auth/login', data)
}

export const register = (data: RegisterRequest): Promise<RegisterResponse> => {
  return request.post('/auth/register', data)
}

