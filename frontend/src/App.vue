<template>
  <n-config-provider
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN"
  >
    <n-message-provider>
      <n-notification-provider>
        <n-dialog-provider>
          <router-view />
          <music-player v-if="showPlayer" />
        </n-dialog-provider>
      </n-notification-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import {
  NConfigProvider,
  NMessageProvider,
  NNotificationProvider,
  NDialogProvider,
  zhCN,
  dateZhCN,
} from "naive-ui";
// import MusicPlayer from "@/components/MusicPlayer.vue";
import { useAuthStore } from "@/stores/auth";
import { computed } from "vue";
import { useNaiveTheme } from "@/theme";

const authStore = useAuthStore();
const showPlayer = computed(() => !!authStore.token);

// 获取赛博朋克主题配置
const { theme, themeOverrides } = useNaiveTheme();
</script>

<style scoped></style>
