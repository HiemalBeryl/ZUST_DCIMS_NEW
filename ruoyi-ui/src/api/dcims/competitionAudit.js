import request from '@/utils/request'

// 查询待审核竞赛列表
export function listCompetitionAudit(query) {
  return request({
    url: '/dcims/competition/audit/list',
    method: 'get',
    params: query
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
