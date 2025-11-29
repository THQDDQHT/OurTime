<template>
  <div class="login-container min-h-screen flex items-center justify-center bg-dark-bg relative overflow-hidden">
    <!-- 动态背景 -->
    <div class="cyber-grid"></div>
    <div class="scan-overlay"></div>

    <div class="login-content w-full max-w-[460px] z-10 flex flex-col gap-5">
      <div class="brand-area text-center">
        <h1 class="brand-title font-orbitron text-6xl text-white text-shadow-glow-blue tracking-[4px] mb-1">NEURAL</h1>
        <p class="brand-subtitle font-share-tech-mono text-neon-blue tracking-[2px] text-xs opacity-80">时空终端 // 需要访问权限</p>
      </div>

      <n-card class="login-card border border-neon-blue/30 shadow-glow-blue" :bordered="false" size="huge">
        <div class="card-header flex items-center justify-center gap-2.5 mb-7 border-b border-neon-blue/20 pb-4">
          <div class="status-light w-2 h-2 bg-gray-700 rounded-full" :class="{ active: !loading }"></div>
          <n-text class="card-title font-share-tech-mono text-xl text-white">{{
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
              class="cyber-input font-share-tech-mono"
            >
              <template #prefix>
                <span class="input-icon text-neon-blue">></span>
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="password" :show-label="false">
            <n-input
              v-model:value="form.password"
              show-password-on="click"
              type="password"
              placeholder="输入访问密钥"
              :disabled="loading"
              @keyup.enter="handleSubmit"
              class="cyber-input font-share-tech-mono"
            >
              <template #prefix>
                <span class="input-icon text-neon-blue">#</span>
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
            class="cyber-btn font-orbitron tracking-[2px] h-[50px] text-base"
          >
            {{ isRegisterMode ? "启动注册" : "连接核心" }}
          </n-button>

          <div class="toggle-mode text-center mt-4">
            <span @click="toggleMode" class="cyber-link font-share-tech-mono text-neon-blue/60 cursor-pointer text-sm transition-all duration-300 hover:text-neon-blue hover:text-shadow-glow-blue-sm">
              {{ isRegisterMode ? "[ 切换至登录 ]" : "[ 创建新身份 ]" }}
            </span>
          </div>
        </div>
      </n-card>

      <div class="footer">
        <n-text depth="3" class="copyright font-share-tech-mono text-[0.7rem] text-white/30"
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
/* 动态网格背景 */
.cyber-grid {
  position: absolute;
  width: 200%;
  height: 200%;
  background-image: 
    linear-gradient(rgba(0, 243, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: grid-scroll 20s linear infinite;
  opacity: 0.2;
}

@keyframes grid-scroll {
  0% { transform: perspective(500px) rotateX(60deg) translateY(0) translateZ(-200px); }
  100% { transform: perspective(500px) rotateX(60deg) translateY(50px) translateZ(-200px); }
}

.status-light.active {
  background: #00f3ff;
  box-shadow: 0 0 8px #00f3ff;
  animation: blink 2s infinite;
}

@keyframes blink { 
  50% { opacity: 0.5; } 
}
</style>
