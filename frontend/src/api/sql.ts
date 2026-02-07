import { http } from './request'

/**
 * SQL 生成 API
 */
export const sqlApi = {
  /**
   * 生成 SQL
   */
  generateSql(data: FormData) {
    return http.upload<string>('/sql/generate', data)
  },

  /**
   * 下载 Excel 模板
   */
  downloadTemplate() {
    return `${import.meta.env.VITE_API_BASE_URL || '/api'}/sql/download/template`
  }
}
