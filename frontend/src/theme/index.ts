/**
 * 主题系统入口
 * 统一导出所有主题相关配置
 */

import { darkTheme } from 'naive-ui'
import { cyberThemeOverrides } from './naive'

// 导出主题配置
export { cyberThemeOverrides }

/**
 * 获取 Naive UI 主题配置
 * 赛博朋克暗色主题
 */
export function useNaiveTheme() {
  return {
    theme: darkTheme,
    themeOverrides: cyberThemeOverrides,
  }
}

