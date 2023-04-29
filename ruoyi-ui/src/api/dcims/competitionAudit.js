import request from '@/utils/request'

// 查询竞赛赛事基本信息列表
export function listCompetitionAudit(query) {
  return request({
    url: '/dcims/competitionAudit/list',
    method: 'get',
    params: query
  })
}

// 查询竞赛赛事基本信息详细
export function getCompetitionAudit(id) {
  return request({
    url: '/dcims/competitionAudit/' + id,
    method: 'get'
  })
}

// 通过竞赛审核
export function permitAudit(id) {
  return request({
    url: '/dcims/competitionAudit/' + id,
    method: 'get'
  })
}

// 退回竞赛审核
export function refuseAudit(id) {
  return request({
    url: '/dcims/competitionAudit/' + id,
    method: 'get'
  })
}
