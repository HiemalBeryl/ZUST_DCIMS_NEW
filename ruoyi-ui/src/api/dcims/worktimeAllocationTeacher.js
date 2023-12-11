import request from '@/utils/request'

// 查询竞赛列表
export function getWorktimeAllocationCompetitionList() {
  return request({
    url: '/dcims/worktimeAllocationTeacher/competition',
    method: 'get'
  })
}

// 查询工作量分配列表
export function listWorktimeAllocationTeacher(query) {
  return request({
    url: '/dcims/worktimeAllocationTeacher/list',
    method: 'get',
    params: query
  })
}

// 查询工作量分配详细
export function getWorktimeAllocationTeacher(id) {
  return request({
    url: '/dcims/worktimeAllocationTeacher/' + id,
    method: 'get'
  })
}

// 新增工作量分配
export function addWorktimeAllocationTeacher(data) {
  return request({
    url: '/dcims/worktimeAllocationTeacher',
    method: 'post',
    data: data
  })
}

// 修改工作量分配
export function updateWorktimeAllocationTeacher(data) {
  return request({
    url: '/dcims/worktimeAllocationTeacher/put',
    method: 'post',
    data: data
  })
}

// 删除工作量分配
export function delWorktimeAllocationTeacher(id) {
  return request({
    url: '/dcims/worktimeAllocationTeacher/delete/' + id,
    method: 'post'
  })
}
