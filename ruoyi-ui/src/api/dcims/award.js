import request from '@/utils/request'

// 查询获奖基本信息列表
export function listAward(query) {
  return request({
    url: '/dcims/award/list',
    method: 'get',
    params: query
  })
}

// 查询获奖基本信息详细
export function getAward(id) {
  return request({
    url: '/dcims/award/' + id,
    method: 'get'
  })
}

// 新增获奖基本信息
export function addAward(data) {
  return request({
    url: '/dcims/award',
    method: 'post',
    data: data
  })
}

// 修改获奖基本信息
export function updateAward(data) {
  return request({
    url: '/dcims/award/put',
    method: 'post',
    data: data
  })
}

// 删除获奖基本信息
export function delAward(id) {
  return request({
    url: '/dcims/award/delete/' + id,
    method: 'post'
  })
}
