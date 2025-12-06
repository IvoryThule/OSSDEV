import request from '@/utils/request'

// 获取宠物列表
export function getPetList(params) {
  return request.get('/pets', { params })
}

// 获取宠物详情
export function getPetDetail(id) {
  return request.get(`/pets/${id}`)
}

// 添加宠物
export function addPet(data) {
  return request.post('/pets', data)
}

// 更新宠物
export function updatePet(id, data) {
  return request.put(`/pets/${id}`, data)
}

// 删除宠物
export function deletePet(id) {
  return request.delete(`/pets/${id}`)
}

// 更新宠物状态
export function updatePetStatus(id, status) {
  return request.put(`/pets/${id}/status`, null, { params: { status } })
}

// 获取我发布的宠物
export function getMyPets(params) {
  return request.get('/pets/my', { params })
}
