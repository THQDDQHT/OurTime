import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const logout = () => {
    token.value = null
    localStorage.removeItem('token')
  }
  
  const isAuthenticated = () => {
    return token.value !== null
  }
  
  return {
    token,
    setToken,
    logout,
    isAuthenticated
  }
})

