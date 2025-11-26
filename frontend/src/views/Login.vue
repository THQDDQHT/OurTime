<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="cyber-grid"></div>
    <div class="scan-overlay"></div>

    <div class="login-content">
      <div class="brand-area">
        <h1 class="brand-title">OurTime</h1>
        <p class="brand-subtitle">时空终端 // 需要访问权限</p>
      </div>

      <n-card class="login-card" :bordered="false" size="huge">
        <div class="card-header">
          <div class="status-light" :class="{ active: !loading }"></div>
          <n-text class="card-title">{{
            isRegisterMode ? "新用户注册程序" : "身份验证程序"
          }}</n-text>
        </div>

        <n-form ref="formRef" :model="form" :rules="rules" size="large">
          <n-form-item path="username" :show-label="false">
            <n-input
              v-model:value="form.username"
              placeholder="输入用户名"
              :disabled="loading"
              @keyup.enter="handleSubmit"
              class="cyber-input"
            >
              <template #prefix>
                <span class="input-icon">></span>
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="password" :show-label="false">
            <n-input
              v-model:value="form.password"
              type="password"
              placeholder="输入访问密钥"
              :disabled="loading"
              @keyup.enter="handleSubmit"
              class="cyber-input"
            >
              <template #prefix>
                <span class="input-icon">#</span>
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
            @click="handleSubmit"
            class="cyber-btn"
          >
            {{ isRegisterMode ? "启动注册" : "连接核心" }}
          </n-button>

          <div class="toggle-mode">
            <span @click="toggleMode" class="cyber-link">
              {{ isRegisterMode ? "[ 切换至登录 ]" : "[ 创建新身份 ]" }}
            </span>
          </div>
        </div>
      </n-card>

      <div class="footer">
        <n-text depth="3" class="copyright"
          >系统版本 3.0 // 记忆核心在线</n-text
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useMessage } from "naive-ui";
import { NCard, NForm, NFormItem, NInput, NButton, NText } from "naive-ui";
import { login, register } from "@/api/auth";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();

const formRef = ref();
const loading = ref(false);
const isRegisterMode = ref(false);
const form = ref({
  username: "",
  password: "",
});

const rules = {
  username: {
    required: true,
    message: "需要身份ID",
    trigger: "blur",
  },
  password: {
    required: true,
    message: "需要密钥",
    trigger: "blur",
  },
};

const handleLogin = async () => {
  try {
    await formRef.value?.validate();
    loading.value = true;

    const response = await login({
      username: form.value.username,
      password: form.value.password,
    });
    authStore.setAuth(response);
    message.success("访问已授权");
    router.push("/");
  } catch (error: any) {
    message.error("访问被拒绝");
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  try {
    await formRef.value?.validate();
    loading.value = true;

    const response = await register({
      username: form.value.username,
      password: form.value.password,
    });
    authStore.setAuth(response);
    message.success("身份已创建");
    router.push("/");
  } catch (error: any) {
    message.error("创建失败");
  } finally {
    loading.value = false;
  }
};

const handleSubmit = () => {
  if (isRegisterMode.value) {
    handleRegister();
  } else {
    handleLogin();
  }
};

const toggleMode = () => {
  isRegisterMode.value = !isRegisterMode.value;
  form.value = { username: "", password: "" };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@500;700&display=swap');

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #050b14;
  position: relative;
  overflow: hidden;
}

/* 动态网格背景 */
.cyber-grid {
  position: absolute;
  width: 200%; height: 200%;
  background-image: 
    linear-gradient(rgba(0, 243, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  transform: perspective(500px) rotateX(60deg) translateY(-100px) translateZ(-200px);
  animation: grid-scroll 20s linear infinite;
  opacity: 0.2;
}

@keyframes grid-scroll {
  0% { transform: perspective(500px) rotateX(60deg) translateY(0) translateZ(-200px); }
  100% { transform: perspective(500px) rotateX(60deg) translateY(50px) translateZ(-200px); }
}

.login-content {
  width: 100%;
  max-width: 460px;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.brand-area { text-align: center; }
.brand-title {
  font-family: "Orbitron", sans-serif;
  font-size: 3.5rem;
  color: #fff;
  text-shadow: 0 0 15px #00f3ff;
  letter-spacing: 4px;
  margin-bottom: 5px;
}
.brand-subtitle {
  font-family: "Share Tech Mono", monospace;
  color: #00f3ff;
  letter-spacing: 2px;
  font-size: 0.8rem;
  opacity: 0.8;
}

.login-card {
  /* Note: Background handled by global override, but we add specific glowing borders here */
  border: 1px solid rgba(0, 243, 255, 0.3);
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 1px solid rgba(0, 243, 255, 0.2);
  padding-bottom: 15px;
}

.status-light {
  width: 8px; height: 8px;
  background: #333;
  border-radius: 50%;
}
.status-light.active {
  background: #00f3ff;
  box-shadow: 0 0 8px #00f3ff;
  animation: blink 2s infinite;
}

.card-title {
  font-family: "Share Tech Mono", monospace;
  font-size: 1.2rem;
  color: #fff;
}

.cyber-input {
  font-family: "Share Tech Mono";
}
.input-icon { color: #00f3ff; }

.cyber-btn {
  font-family: "Orbitron", sans-serif;
  letter-spacing: 2px;
  height: 50px;
  font-size: 1rem;
}

.toggle-mode {
  text-align: center;
  margin-top: 15px;
}

.cyber-link {
  font-family: "Share Tech Mono";
  color: rgba(0, 243, 255, 0.6);
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}
.cyber-link:hover {
  color: #00f3ff;
  text-shadow: 0 0 5px #00f3ff;
}

.copyright {
  font-family: "Share Tech Mono";
  font-size: 0.7rem;
  color: rgba(255,255,255,0.3);
}

@keyframes blink { 50% { opacity: 0.5; } }
</style>
