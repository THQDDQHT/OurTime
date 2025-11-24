<template>
  <div class="login-container">
    <div class="login-content">
      <div class="brand-area">
        <h1 class="brand-title">OurTime</h1>
        <p class="brand-subtitle">时光博物馆</p>
      </div>
      
      <n-card class="login-card" :bordered="false" size="huge">
        <div class="card-header">
          <n-text class="card-title">开启记忆之门</n-text>
        </div>
        
        <n-form ref="formRef" :model="form" :rules="rules" size="large">
          <n-form-item path="secret" :show-label="false">
            <n-input
              v-model:value="form.secret"
              type="password"
              placeholder="请输入开启密码"
              :disabled="loading"
              @keyup.enter="handleLogin"
              class="secret-input"
            >
              <template #prefix>
                <span class="input-icon">🔑</span>
              </template>
            </n-input>
          </n-form-item>
        </n-form>
        
        <div class="actions">
          <n-button
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            进入
          </n-button>
        </div>
      </n-card>
      
      <div class="footer">
        <n-text depth="3" class="copyright">© 2023 OurTime. All memories reserved.</n-text>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { NCard, NForm, NFormItem, NInput, NButton, NText } from 'naive-ui'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const message = useMessage()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)
const form = ref({
  secret: ''
})

const rules = {
  secret: {
    required: true,
    message: '请输入开启密码',
    trigger: 'blur'
  }
}

const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    
    const response = await login({ secret: form.value.secret })
    authStore.setToken(response.token)
    message.success('欢迎回来，时光旅人')
    router.push('/')
  } catch (error: any) {
    message.error(error.message || '密钥错误，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f2f0;
  background-image: radial-gradient(#e0dcd9 1px, transparent 1px);
  background-size: 20px 20px;
  padding: 20px;
}

.login-content {
  width: 100%;
  max-width: 420px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

.brand-area {
  text-align: center;
  margin-bottom: 16px;
}

.brand-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 3rem;
  font-weight: 700;
  color: #5d4037;
  letter-spacing: 2px;
  margin-bottom: 8px;
  text-shadow: 2px 2px 0px rgba(255,255,255,0.5);
}

.brand-subtitle {
  font-size: 1.1rem;
  color: #8d6e63;
  letter-spacing: 4px;
  font-weight: 300;
  text-transform: uppercase;
}

.login-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05), 
    0 2px 4px -1px rgba(0, 0, 0, 0.03),
    0 20px 25px -5px rgba(0, 0, 0, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.card-title {
  font-size: 1.2rem;
  color: #5d4037;
  font-weight: 500;
}

.secret-input {
  text-align: center;
  font-size: 1.1rem;
  letter-spacing: 2px;
}

.input-icon {
  font-size: 1.2rem;
}

.actions {
  margin-top: 16px;
}

.login-btn {
  letter-spacing: 4px;
  font-size: 1.1rem;
  height: 48px;
}

.footer {
  text-align: center;
}

.copyright {
  font-size: 0.8rem;
  opacity: 0.6;
}
</style>

