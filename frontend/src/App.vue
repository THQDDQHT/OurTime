<template>
  <n-config-provider :theme="theme" :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
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
import { NConfigProvider, NMessageProvider, NNotificationProvider, NDialogProvider, lightTheme, zhCN, dateZhCN, type GlobalThemeOverrides } from 'naive-ui'
import MusicPlayer from '@/components/MusicPlayer.vue'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const authStore = useAuthStore()
const showPlayer = computed(() => !!authStore.token) // Only show player when logged in

const theme = lightTheme

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#8c7b75',
    primaryColorHover: '#a39088',
    primaryColorPressed: '#756660',
    bodyColor: '#fdfcf8',
    cardColor: '#ffffff',
    textColorBase: '#2c3e50',
    fontFamily: '"Noto Serif SC", "Songti SC", "SimSun", serif', // Use serif for a more literary feel
  },
  Card: {
    borderRadius: '8px',
    borderColor: '#eaddcf',
  },
  Button: {
    borderRadiusMedium: '4px',
    fontWeight: '500',
  },
  Typography: {
    headerFontWeight: '700',
  }
}
</script>

<style scoped>
</style>

