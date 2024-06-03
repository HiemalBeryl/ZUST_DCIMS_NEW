import request from '@/utils/request'

// 查询工作量分配竞赛列表
export function listWorktimeAllocationCompetition(query) {
  return request({
    url: '/dcims/worktimeAllocationCompetition/list',
    method: 'get',
    params: query
  })
}

// 根据用户工号查询工作量分配竞赛列表
export function listWorktimeAllocationCompetitionByTeacherId() {
  return request({
    url: '/dcims/worktimeAllocationCompetition/listByTeacherId',
    method: 'get',
  })
}

// 查询工作量分配竞赛详细
export function getWorktimeAllocationCompetition(id) {
  return request({
    url: '/dcims/worktimeAllocationCompetition/' + id,
    method: 'get'
  })
}

// 新增工作量分配竞赛
export function addWorktimeAllocationCompetition(data) {
  return request({
    url: '/dcims/worktimeAllocationCompetition',
    method: 'post',
    data: data
  })
}

// 修改工作量分配竞赛
export function updateWorktimeAllocationCompetition(data) {
  return request({
    url: '/dcims/worktimeAllocationCompetition/put',
    method: 'post',
    data: data
  })
}

// 删除工作量分配竞赛
export function delWorktimeAllocationCompetition(id) {
  return request({
    url: '/dcims/worktimeAllocationCompetition/delete/' + id,
    method: 'post'
  })
}
