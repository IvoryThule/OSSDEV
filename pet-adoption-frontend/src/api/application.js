import request from '@/utils/request'

// 提交领养申请
export function submitApplication(data) {
  return request.post('/applications', data)
}

// 获取申请列表（管理）
export function getApplicationList(params) {
  return request.get('/applications', { params })
}

// 获取我的申请列表
export function getMyApplications(params) {
  return request.get('/applications/my', { params })
}

// 获取申请详情
export function getApplicationDetail(id) {
  return request.get(`/applications/${id}`)
}

// 审核通过
export function approveApplication(id) {
  return request.put(`/applications/${id}/approve`)
}

// 审核拒绝
export function rejectApplication(id, reason) {
  return request.put(`/applications/${id}/reject`, null, { params: { reason } })
}

// 取消申请
export function cancelApplication(id) {
  return request.put(`/applications/${id}/cancel`)
}

// 获取我收到的申请列表
export function getReceivedApplications(params) {
  return request.get('/applications/received', { params })
}

// 送养人审核
export function publisherReview(id, status, rejectReason) {
  return request.put(`/applications/${id}/publisher-review`, null, { 
    params: { status, rejectReason } 
  })
}
