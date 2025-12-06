import request from '@/utils/request'

export function getPosts(params) {
  return request({
    url: '/forum/posts',
    method: 'get',
    params
  })
}

export function getPostDetail(id) {
  return request({
    url: `/forum/posts/${id}`,
    method: 'get'
  })
}

export function createPost(data) {
  return request({
    url: '/forum/posts',
    method: 'post',
    data
  })
}

export function updatePost(data) {
  return request({
    url: '/forum/post',
    method: 'put',
    data
  })
}

export function deletePost(id) {
  return request({
    url: `/forum/post/${id}`,
    method: 'delete'
  })
}

export function getComments(postId) {
  return request({
    url: `/forum/posts/${postId}/comments`,
    method: 'get'
  })
}

export function createComment(data) {
  return request({
    url: '/forum/comments',
    method: 'post',
    data
  })
}

export function deleteComment(id) {
  return request({
    url: `/forum/comment/${id}`,
    method: 'delete'
  })
}

export function toggleLike(data) {
  return request({
    url: '/forum/like',
    method: 'post',
    data
  })
}

export function getLikeStatus(targetId, type) {
  return request({
    url: `/forum/like/status`,
    method: 'get',
    params: { targetId, type }
  })
}
