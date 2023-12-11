import request from '@/utils/request'

// 查询工作量分配列表
export function listWorktimeAllocation(query) {
  return request({
    url: '/dcims/worktimeAllocation/list',
    method: 'get',
    params: query
  })
}

// 查询工作量分配详细
export function getWorktimeAllocation(id) {
  return request({
    url: '/dcims/worktimeAllocation/' + id,
    method: 'get'
  })
}

// 新增工作量分配
export function addWorktimeAllocation(data) {
  return request({
    url: '/dcims/worktimeAllocation',
    method: 'post',
    data: data
  })
}

// 修改工作量分配
export function updateWorktimeAllocation(data) {
  return request({
    url: '/dcims/worktimeAllocation/put',
    method: 'post',
    data: data
  })
}

// 删除工作量分配
export function delWorktimeAllocation(id) {
  return request({
    url: '/dcims/worktimeAllocation/delete/' + id,
    method: 'post'
  })
}
