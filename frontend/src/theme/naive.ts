/**
 * Naive UI 赛博朋克主题覆盖配置
 * 基于 Naive UI 的 GlobalThemeOverrides API
 */

import type { GlobalThemeOverrides } from 'naive-ui'

export const cyberThemeOverrides: GlobalThemeOverrides = {
  common: {
    // 主色调 - 霓虹蓝
    primaryColor: '#00f3ff',
    primaryColorHover: '#33f5ff',
    primaryColorPressed: '#00d4e0',
    primaryColorSuppl: '#bc13fe',

    // 语义色
    infoColor: '#00f3ff',
    infoColorHover: '#33f5ff',
    infoColorPressed: '#00d4e0',
    successColor: '#00ff88',
    successColorHover: '#33ff99',
    successColorPressed: '#00dd77',
    warningColor: '#ffaa00',
    warningColorHover: '#ffbb22',
    warningColorPressed: '#ee9900',
    errorColor: '#ff0055',
    errorColorHover: '#ff3377',
    errorColorPressed: '#dd0044',

    // 文字颜色
    textColorBase: '#e0f7fa',
    textColor1: '#ffffff',
    textColor2: 'rgba(224, 247, 250, 0.82)',
    textColor3: 'rgba(224, 247, 250, 0.52)',

    // 背景色
    bodyColor: '#050b14',
    cardColor: 'rgba(5, 11, 20, 0.8)',
    modalColor: 'rgba(5, 11, 20, 0.9)',
    popoverColor: 'rgba(5, 11, 20, 0.95)',
    tableColor: 'rgba(5, 11, 20, 0.6)',
    hoverColor: 'rgba(0, 243, 255, 0.08)',

    // 边框和分割线
    borderColor: 'rgba(0, 243, 255, 0.2)',
    dividerColor: 'rgba(0, 243, 255, 0.15)',

    // 圆角
    borderRadius: '4px',
    borderRadiusSmall: '2px',

    // 字体
    fontFamily: '"Rajdhani", sans-serif',
    fontFamilyMono: '"Share Tech Mono", monospace',
    fontSize: '16px',
    fontSizeMini: '12px',
    fontSizeTiny: '12px',
    fontSizeSmall: '14px',
    fontSizeMedium: '16px',
    fontSizeLarge: '18px',
    fontSizeHuge: '20px',

    // 阴影
    boxShadow1: '0 0 10px rgba(0, 0, 0, 0.3)',
    boxShadow2: '0 0 15px rgba(0, 243, 255, 0.1)',
    boxShadow3: '0 0 20px rgba(0, 243, 255, 0.15)',
  },

  Button: {
    // Primary 按钮
    colorPrimary: 'rgba(0, 243, 255, 0.15)',
    colorHoverPrimary: '#00f3ff',
    colorPressedPrimary: '#00d4e0',
    colorFocusPrimary: 'rgba(0, 243, 255, 0.2)',
    
    textColorPrimary: '#00f3ff',
    textColorHoverPrimary: '#000',
    textColorPressedPrimary: '#000',
    textColorFocusPrimary: '#00f3ff',
    
    border: '1px solid rgba(0, 243, 255, 0.3)',
    borderHover: '1px solid #00f3ff',
    borderPressed: '1px solid #00d4e0',
    borderFocus: '1px solid #00f3ff',
    
    borderPrimary: '1px solid #00f3ff',
    borderHoverPrimary: '1px solid #00f3ff',
    borderPressedPrimary: '1px solid #00d4e0',
    borderFocusPrimary: '1px solid #00f3ff',

    // Ghost 按钮
    colorGhost: 'transparent',
    colorHoverGhost: 'rgba(0, 243, 255, 0.1)',
    colorPressedGhost: 'rgba(0, 243, 255, 0.15)',
    textColorGhost: '#00f3ff',
    textColorHoverGhost: '#00f3ff',
    textColorPressedGhost: '#00d4e0',

    // 通用
    fontFamily: '"Share Tech Mono", monospace',
    fontWeight: '600',
    borderRadiusMedium: '4px',
  },

  Card: {
    color: 'rgba(5, 11, 20, 0.8)',
    colorModal: 'rgba(5, 11, 20, 0.9)',
    colorTarget: 'rgba(5, 11, 20, 0.8)',
    colorEmbedded: 'rgba(5, 11, 20, 0.6)',
    
    borderColor: 'rgba(0, 243, 255, 0.3)',
    borderRadius: '4px',
    
    titleTextColor: '#00f3ff',
    titleFontWeight: '700',
    titleFontSize: '18px',
    
    textColor: '#e0f7fa',
    
    boxShadow: '0 0 10px rgba(0, 0, 0, 0.3), 0 0 20px rgba(0, 243, 255, 0.05)',
    
    paddingMedium: '20px',
    paddingLarge: '24px',
    paddingHuge: '28px',
  },

  Input: {
    color: 'rgba(0, 0, 0, 0.3)',
    colorFocus: 'rgba(0, 0, 0, 0.4)',
    colorDisabled: 'rgba(0, 0, 0, 0.2)',
    
    textColor: '#ffffff',
    textColorDisabled: 'rgba(255, 255, 255, 0.3)',
    placeholderColor: 'rgba(255, 255, 255, 0.3)',
    placeholderColorDisabled: 'rgba(255, 255, 255, 0.2)',
    
    border: '1px solid rgba(0, 243, 255, 0.3)',
    borderHover: '1px solid #00f3ff',
    borderFocus: '1px solid #00f3ff',
    borderError: '1px solid #ff0055',
    borderWarning: '1px solid #ffaa00',
    
    boxShadowFocus: '0 0 8px rgba(0, 243, 255, 0.3)',
    
    caretColor: '#00f3ff',
    
    fontFamily: '"Share Tech Mono", monospace',
    fontSizeMedium: '14px',
    borderRadius: '4px',
  },

  Modal: {
    color: 'rgba(5, 11, 20, 0.95)',
    textColor: '#e0f7fa',
    titleTextColor: '#00f3ff',
    titleFontWeight: '700',
    titleFontSize: '20px',
    
    boxShadow: '0 0 30px rgba(0, 243, 255, 0.2)',
    
    borderRadius: '4px',
    
    headerBorderBottom: '1px solid rgba(0, 243, 255, 0.2)',
    footerBorderTop: '1px solid rgba(0, 243, 255, 0.2)',
  },

  Dialog: {
    color: 'rgba(5, 11, 20, 0.95)',
    textColor: '#e0f7fa',
    titleTextColor: '#00f3ff',
    titleFontWeight: '700',
    titleFontSize: '20px',
    
    boxShadow: '0 0 30px rgba(0, 243, 255, 0.2)',
    borderRadius: '4px',
  },

  Upload: {
    draggerColor: 'rgba(0, 0, 0, 0.2)',
    draggerColorHover: 'rgba(0, 243, 255, 0.05)',
    draggerBorder: '1px dashed rgba(0, 243, 255, 0.2)',
    draggerBorderHover: '1px dashed #00f3ff',
    
    borderRadius: '4px',
    
    itemColorHover: 'rgba(0, 243, 255, 0.05)',
    itemColorHoverError: 'rgba(255, 0, 85, 0.05)',
    
    itemTextColor: '#e0f7fa',
    itemTextColorError: '#ff0055',
    itemTextColorSuccess: '#00ff88',
  },

  Select: {
    peers: {
      InternalSelection: {
        color: 'rgba(0, 0, 0, 0.3)',
        colorActive: 'rgba(0, 0, 0, 0.4)',
        colorDisabled: 'rgba(0, 0, 0, 0.2)',
        
        textColor: '#ffffff',
        placeholderColor: 'rgba(255, 255, 255, 0.3)',
        
        border: '1px solid rgba(0, 243, 255, 0.3)',
        borderHover: '1px solid #00f3ff',
        borderActive: '1px solid #00f3ff',
        borderFocus: '1px solid #00f3ff',
        
        boxShadowActive: '0 0 8px rgba(0, 243, 255, 0.3)',
        boxShadowFocus: '0 0 8px rgba(0, 243, 255, 0.3)',
        
        arrowColor: '#00f3ff',
      },
    },
  },

  DatePicker: {
    peers: {
      Input: {
        color: 'rgba(0, 0, 0, 0.3)',
        colorFocus: 'rgba(0, 0, 0, 0.4)',
        textColor: '#ffffff',
        placeholderColor: 'rgba(255, 255, 255, 0.3)',
        border: '1px solid rgba(0, 243, 255, 0.3)',
        borderHover: '1px solid #00f3ff',
        borderFocus: '1px solid #00f3ff',
        boxShadowFocus: '0 0 8px rgba(0, 243, 255, 0.3)',
        caretColor: '#00f3ff',
      },
    },
  },

  Form: {
    labelTextColor: '#00f3ff',
    labelFontWeight: '600',
    labelFontSizeTopMedium: '14px',
    
    feedbackTextColor: '#e0f7fa',
    feedbackTextColorError: '#ff0055',
    feedbackTextColorWarning: '#ffaa00',
    
    asteriskColor: '#ff0055',
  },

  Message: {
    color: 'rgba(5, 11, 20, 0.95)',
    colorInfo: 'rgba(0, 243, 255, 0.15)',
    colorSuccess: 'rgba(0, 255, 136, 0.15)',
    colorWarning: 'rgba(255, 170, 0, 0.15)',
    colorError: 'rgba(255, 0, 85, 0.15)',
    
    textColor: '#e0f7fa',
    textColorInfo: '#00f3ff',
    textColorSuccess: '#00ff88',
    textColorWarning: '#ffaa00',
    textColorError: '#ff0055',
    
    borderRadius: '4px',
    boxShadow: '0 0 15px rgba(0, 243, 255, 0.2)',
  },

  Notification: {
    color: 'rgba(5, 11, 20, 0.95)',
    textColor: '#e0f7fa',
    titleTextColor: '#00f3ff',
    titleFontWeight: '700',
    
    borderRadius: '4px',
    boxShadow: '0 0 20px rgba(0, 243, 255, 0.2)',
    
    border: '1px solid rgba(0, 243, 255, 0.3)',
  },

  Popconfirm: {
    peers: {
      Button: {
        textColorPrimary: '#00f3ff',
        colorPrimary: 'rgba(0, 243, 255, 0.15)',
        colorHoverPrimary: '#00f3ff',
        textColorHoverPrimary: '#000',
      },
    },
  },

  Timeline: {
    lineColor: 'rgba(0, 243, 255, 0.2)',
    circleBorder: '2px solid #00f3ff',
    titleTextColor: '#00f3ff',
    contentTextColor: '#e0f7fa',
    metaTextColor: 'rgba(224, 247, 250, 0.6)',
    iconColor: '#00f3ff',
  },

  Tabs: {
    tabTextColorLine: 'rgba(255, 255, 255, 0.6)',
    tabTextColorActiveLine: '#00f3ff',
    tabTextColorHoverLine: '#00f3ff',
    
    barColor: '#00f3ff',
    
    tabBorderColor: 'rgba(0, 243, 255, 0.2)',
    tabBorderRadius: '4px',
    
    tabFontWeightActive: '700',
    
    // Segment type
    tabColorSegment: 'transparent',
    tabTextColorSegment: 'rgba(255, 255, 255, 0.6)',
    tabTextColorActiveSegment: '#00f3ff',
    tabTextColorHoverSegment: '#00f3ff',
    
    paneTextColor: '#e0f7fa',
  },

  Tag: {
    color: 'rgba(0, 243, 255, 0.1)',
    colorBordered: 'transparent',
    
    textColor: '#00f3ff',
    
    border: '1px solid rgba(0, 243, 255, 0.3)',
    borderRadius: '12px',
    
    closeIconColor: '#00f3ff',
    closeIconColorHover: '#ff0055',
    closeIconColorPressed: '#dd0044',
  },

  Empty: {
    textColor: 'rgba(255, 255, 255, 0.5)',
    iconColor: 'rgba(0, 243, 255, 0.3)',
    iconSize: '64px',
    fontFamily: '"Share Tech Mono", monospace',
  },

  Spin: {
    color: '#00f3ff',
    textColor: '#00f3ff',
  },

  List: {
    color: 'transparent',
    
    borderColor: 'rgba(0, 243, 255, 0.1)',
    borderRadius: '4px',
    
    itemColorHover: 'rgba(0, 243, 255, 0.05)',
    
    itemTextColor: '#e0f7fa',
  },

  Popover: {
    color: 'rgba(5, 11, 20, 0.95)',
    textColor: '#e0f7fa',
    
    borderRadius: '4px',
    boxShadow: '0 0 15px rgba(0, 243, 255, 0.2)',
    
    arrowColor: 'rgba(5, 11, 20, 0.95)',
  },

  Layout: {
    color: 'transparent',
    textColor: '#e0f7fa',
    
    headerColor: 'rgba(5, 11, 20, 0.8)',
    headerBorderColor: 'rgba(0, 243, 255, 0.2)',
    
    footerColor: 'rgba(5, 11, 20, 0.8)',
    footerBorderColor: 'rgba(0, 243, 255, 0.2)',
    
    siderColor: 'rgba(5, 11, 20, 0.8)',
    siderBorderColor: 'rgba(0, 243, 255, 0.2)',
  },

  Avatar: {
    color: 'rgba(0, 243, 255, 0.15)',
    textColor: '#00f3ff',
    border: '1px solid rgba(0, 243, 255, 0.3)',
  },

  Image: {
    toolbarColor: 'rgba(5, 11, 20, 0.9)',
    toolbarIconColor: '#00f3ff',
    toolbarBorderRadius: '4px',
  },
}

