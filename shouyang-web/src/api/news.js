/**
 * 资讯模块接口封装
 */
import { get } from '@/utils/request'

/**
 * 获取资讯列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 当前页
 * @param {number} params.size - 每页条数
 * @param {number} [params.categoryId] - 分类ID
 * @param {string} [params.keyword] - 关键词
 * @returns {Promise} 分页结果
 */
export function getNewsList(params) {
  return get('/news/list', params)
}

/**
 * 获取资讯详情
 * @param {number} id - 资讯ID
 * @returns {Promise} 资讯详情
 */
export function getNewsDetail(id) {
  return get(`/news/${id}`)
}

/**
 * 获取资讯分类列表
 * @returns {Promise} 分类列表
 */
export function getNewsCategory() {
  return get('/news/category')
}

/**
 * 获取热门资讯（浏览量前10）
 * @returns {Promise} 热门资讯列表
 */
export function getHotNews() {
  return get('/news/hot')
}

/**
 * 获取相关资讯（同分类下的其他资讯，6条）
 * @param {number} id - 当前资讯ID
 * @returns {Promise} 相关资讯列表
 */
export function getRelatedNews(id) {
  return get(`/news/related/${id}`)
}
