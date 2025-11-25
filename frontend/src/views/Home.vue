<template>
  <div class="home-container">
    <n-layout class="layout-bg">
      <n-layout-header class="header" bordered>
        <div class="header-content">
          <div class="brand">
            <n-text class="brand-text">OurTime</n-text>
          </div>
          <div class="header-actions">
            <n-tooltip trigger="hover">
              <template #trigger>
                <n-button circle quaternary @click="handleCreate">
                  <template #icon>
                    <n-icon><add-outline /></n-icon>
                  </template>
                </n-button>
              </template>
              发布瞬间
            </n-tooltip>

            <n-tooltip trigger="hover">
              <template #trigger>
                <n-button circle quaternary @click="handleLogout">
                  <template #icon>
                    <n-icon><log-out-outline /></n-icon>
                  </template>
                </n-button>
              </template>
              退出
            </n-tooltip>
          </div>
        </div>
      </n-layout-header>

      <n-layout-content class="content" :native-scrollbar="false">
        <div class="main-wrapper">
          <div class="welcome-section">
             <h1 class="welcome-title">欢迎回来，{{ authStore.username }}</h1>
             <p class="welcome-subtitle">选择一本相册，重温那些美好时光</p>
          </div>
          
          <AlbumList />
          
        </div>
      </n-layout-content>
    </n-layout>
    <CreateMomentModal
      v-model:show="showCreateModal"
      @success="handleCreateSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useMessage } from "naive-ui";
import {
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  NButton,
  NText,
  NIcon,
  NTooltip,
} from "naive-ui";
import {
  AddOutline,
  LogOutOutline,
} from "@vicons/ionicons5";
import { useAuthStore } from "@/stores/auth";
import CreateMomentModal from "@/components/CreateMomentModal.vue";
import AlbumList from "@/components/AlbumList.vue";

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();

const showCreateModal = ref(false);

const handleCreate = () => {
  showCreateModal.value = true;
};

const handleCreateSuccess = () => {
  message.success("发布成功！去相册里看看吧");
  // 可以在这里刷新相册列表，或者依靠 AlbumList 自身的加载机制
  // 由于 AlbumList 在挂载时会加载，这里不需要额外操作，
  // 除非我们想要实时更新相册封面（如果新瞬间包含封面图更新逻辑的话）
};

const handleLogout = () => {
  authStore.logout();
  router.push("/login");
};
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.layout-bg {
  background: transparent;
}

.header {
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  background: rgba(253, 252, 248, 0.9) !important;
  backdrop-filter: blur(12px);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.header-content {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-text {
  font-family: "Noto Serif SC", serif;
  font-size: 24px;
  font-weight: 700;
  color: #5d4037;
  letter-spacing: 1px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.content {
  background-color: transparent;
}

.main-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.welcome-section {
  margin-bottom: 40px;
  text-align: center;
  padding: 40px 0 20px;
}

.welcome-title {
  font-family: "Noto Serif SC", serif;
  font-size: 28px;
  color: #3e2723;
  margin-bottom: 8px;
  font-weight: 600;
}

.welcome-subtitle {
  color: #8d6e63;
  font-size: 16px;
}
</style>
