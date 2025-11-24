// 直接返回路径，利用 Vite 代理或 Nginx 处理
export const resolveUploadUrl = (path?: string | null) => {
  if (!path) return ''
  // 如果已经是完整URL，直接返回
  if (path.startsWith('http')) {
    return path
  }
  // 兼容旧格式：/uploads/xxx.jpg -> /api/v1/uploads/xxx.jpg
  if (path.startsWith('/uploads/')) {
    return '/api/v1' + path
  }
  // 新格式或其他情况直接返回，由 Vite 代理或 Nginx 处理
  return path
}

