import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  id: number
  username: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'))
  
  const setAuth = (authData: { token: string; userId: number; username: string; role: string }) => {
    token.value = authData.token
    user.value = {
      id: authData.userId,
      username: authData.username,
      role: authData.role
    }
    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const isAuthenticated = () => {
    return token.value !== null
  }

  return {
    token,
    user,
    setAuth,
    setToken,
    logout,
    isAuthenticated,
  }
})

