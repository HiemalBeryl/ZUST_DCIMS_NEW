import request from '@/utils/request'

// 查询test列表
export function listTest(query) {
  return request({
    url: '/dcims/test/list',
    method: 'get',
    params: query
  })
}

// 查询test详细
export function getTest(id) {
  return request({
    url: '/dcims/test/' + id,
    method: 'get'
  })
}

// 新增test
export function addTest(data) {
  return request({
    url: '/dcims/test',
    method: 'post',
    data: data
  })
}

// 修改test
export function updateTest(data) {
  return request({
    url: '/dcims/test',
    method: 'put',
    data: data
  })
}

// 删除test
export function delTest(id) {
  return request({
    url: '/dcims/test/' + id,
    method: 'delete'
  })
}
