/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // 赛博朋克主题颜色系统
      colors: {
        'neon-blue': '#00f3ff',
        'neon-purple': '#bc13fe',
        'neon-red': '#ff0055',
        'dark-bg': '#050b14',
        'glass-bg': 'rgba(0, 243, 255, 0.05)',
        'glass-border': 'rgba(0, 243, 255, 0.2)',
        'text-main': '#e0f7fa',
        'text-dim': 'rgba(224, 247, 250, 0.6)',
        'panel-bg': 'rgba(5, 11, 20, 0.6)',
      },
      
      // 字体家族
      fontFamily: {
        'rajdhani': ['Rajdhani', 'sans-serif'],
        'share-tech-mono': ['Share Tech Mono', 'monospace'],
        'orbitron': ['Orbitron', 'sans-serif'],
      },
      
      // 自定义阴影（霓虹光晕效果）
      boxShadow: {
        'glow-blue': '0 0 15px rgba(0, 243, 255, 0.5)',
        'glow-blue-sm': '0 0 5px rgba(0, 243, 255, 0.3)',
        'glow-blue-lg': '0 0 30px rgba(0, 243, 255, 0.5)',
        'glow-purple': '0 0 15px rgba(188, 19, 254, 0.5)',
        'glow-red': '0 0 15px rgba(255, 0, 85, 0.5)',
      },
      
      // 文字阴影
      textShadow: {
        'glow-blue': '0 0 15px rgba(0, 243, 255, 0.8)',
        'glow-blue-sm': '0 0 5px rgba(0, 243, 255, 0.5)',
        'glow-white': '0 0 20px rgba(255, 255, 255, 0.8)',
      },
      
      // 自定义动画
      keyframes: {
        'grid-fly': {
          '0%': { transform: 'perspective(500px) rotateX(60deg) translateY(0) translateZ(-100px)' },
          '100%': { transform: 'perspective(500px) rotateX(60deg) translateY(60px) translateZ(-100px)' },
        },
        'grid-scroll': {
          '0%': { transform: 'perspective(500px) rotateX(60deg) translateY(0) translateZ(-200px)' },
          '100%': { transform: 'perspective(500px) rotateX(60deg) translateY(50px) translateZ(-200px)' },
        },
        'blink': {
          '50%': { opacity: '0.5' },
        },
        'rotate-slow': {
          '100%': { transform: 'rotate(360deg)' },
        },
        'star-pulse': {
          '0%': { transform: 'scale(1)', opacity: '1' },
          '100%': { transform: 'scale(1.2)', opacity: '0.8' },
        },
        'star-spin': {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' },
        },
        'galaxy-flow': {
          '0%': { backgroundPosition: '0 0, 20px 40px, -10px 60px' },
          '100%': { backgroundPosition: '0 1000px, 20px 1040px, -10px 1060px' },
        },
      },
      
      animation: {
        'grid-fly': 'grid-fly 20s linear infinite',
        'grid-scroll': 'grid-scroll 20s linear infinite',
        'blink': 'blink 2s infinite',
        'rotate-slow': 'rotate-slow 60s linear infinite',
        'rotate-slow-reverse': 'rotate-slow 40s linear infinite reverse',
        'star-pulse': 'star-pulse 2s infinite alternate',
        'star-spin': 'star-spin 20s linear infinite',
        'galaxy-flow': 'galaxy-flow 60s linear infinite',
      },
      
      // 背景图案
      backgroundImage: {
        'cyber-grid': 'linear-gradient(rgba(0, 243, 255, 0.2) 1px, transparent 1px), linear-gradient(90deg, rgba(0, 243, 255, 0.2) 1px, transparent 1px)',
        'stars': 'radial-gradient(white 1px, transparent 1px)',
        'scanlines': 'repeating-linear-gradient(0deg, rgba(0, 0, 0, 0) 0px, rgba(0, 0, 0, 0) 2px, rgba(0, 0, 0, 0.2) 3px, rgba(0, 0, 0, 0.2) 4px)',
        'vignette': 'radial-gradient(circle, transparent 50%, rgba(0, 0, 0, 0.8) 100%)',
      },
      
      // 背景尺寸
      backgroundSize: {
        'grid': '60px 60px',
        'grid-sm': '50px 50px',
        'stars': '50px 50px',
      },
      
      // 自定义滤镜
      backdropBlur: {
        'xs': '2px',
      },
    },
  },
  plugins: [
    // 添加文字阴影插件
    function({ addUtilities }) {
      const newUtilities = {
        '.text-shadow-glow-blue': {
          textShadow: '0 0 15px rgba(0, 243, 255, 0.8)',
        },
        '.text-shadow-glow-blue-sm': {
          textShadow: '0 0 5px rgba(0, 243, 255, 0.5)',
        },
        '.text-shadow-glow-white': {
          textShadow: '0 0 20px rgba(255, 255, 255, 0.8)',
        },
        '.preserve-3d': {
          transformStyle: 'preserve-3d',
        },
        '.backface-hidden': {
          backfaceVisibility: 'hidden',
        },
      }
      addUtilities(newUtilities)
    }
  ],
}

