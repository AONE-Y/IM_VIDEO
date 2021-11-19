import request from '@/utils/request'

// 查询参数列表
export function addVideoChatInfo(data) {
  return request({
    url: '/chat/videoChat/add',
    method: 'post',
    data: data
  })
}

// 查询参数详细
export function deleteVideoChatInfo(username) {
  return request({
    url: '/chat/videoChat/' ,
    method: 'get',
    params: username
  })
}

