import request from '@/utils/request'

// 查询竞赛赛事基本信息列表
export function listCompetition(query) {
  return request({
    url: '/dcims/competition/list',
    method: 'get',
    params: query
  })
}

// 查询竞赛赛事基本信息详细
export function getCompetition(id) {
  return request({
    url: '/dcims/competition/' + id,
    method: 'get'
  })
}

// 新增竞赛赛事基本信息
export function addCompetition(data) {
  return request({
    url: '/dcims/competition',
    method: 'post',
    data: data
  })
}

// 修改竞赛赛事基本信息
export function updateCompetition(data) {
  return request({
    url: '/dcims/competition',
    method: 'put',
    data: data
  })
}

// 删除竞赛赛事基本信息
export function delCompetition(id) {
  return request({
    url: '/dcims/competition/' + id,
    method: 'delete'
  })
}
