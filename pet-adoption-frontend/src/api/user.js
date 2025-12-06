import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
  return request.get('/users', { params })
}

// 获取用户详情
export function getUserDetail(id) {
  return request.get(`/users/${id}`)
}

// 更新用户信息
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request.put(`/users/${id}/status`, null, { params: { status } })
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

// 修改密码
export function changePassword(data) {
  return request.put('/users/password', null, { params: data })
}

// 获取统计数据
export function getStatistics() {
  return request.get('/statistics')
}
