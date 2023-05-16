import request from '@/utils/request'

// 查询待审核团队列表
export function listTeamAudit(query) {
  return request({
    url: '/dcims/team/audit/list',
    method: 'get',
    params: query
  })
}

// 通过团队审核
export function permitAudit(params) {
  return request({
    url: '/dcims/teamAudit/',
    method: 'post',
    data: params
  })
}

// 退回团队审核
export function refuseAudit(params) {
  return request({
    url: '/dcims/teamAudit/',
    method: 'delete',
    data: params
  })
}
