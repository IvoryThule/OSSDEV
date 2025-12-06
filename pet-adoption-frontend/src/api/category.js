import request from '@/utils/request'

// 获取所有分类
export function getCategories() {
  return request.get('/categories')
}

// 添加分类
export function addCategory(data) {
  return request.post('/categories', data)
}

// 更新分类
export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

// 删除分类
export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}
