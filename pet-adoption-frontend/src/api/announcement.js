import request from '@/utils/request'

// 获取公告列表（公开）
export function getAnnouncements(params) {
  return request.get('/announcements', { params })
}

// 获取所有公告（管理）
export function getAllAnnouncements(params) {
  return request.get('/announcements/all', { params })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return request.get(`/announcements/${id}`)
}

// 添加公告
export function addAnnouncement(data) {
  return request.post('/announcements', data)
}

// 更新公告
export function updateAnnouncement(id, data) {
  return request.put(`/announcements/${id}`, data)
}

// 删除公告
export function deleteAnnouncement(id) {
  return request.delete(`/announcements/${id}`)
}
