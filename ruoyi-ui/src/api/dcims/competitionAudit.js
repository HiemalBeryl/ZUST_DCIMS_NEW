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
