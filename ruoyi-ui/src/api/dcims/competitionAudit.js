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
export function permitAudit(params) {
  return request({
    url: '/dcims/competitionAudit/',
    method: 'post',
    data: params
  })
}

// 退回竞赛审核
export function refuseAudit(params) {
  return request({
    url: '/dcims/competitionAudit/delete/',
    method: 'post',
    data: params
  })
}

// 编辑竞赛类型等部分信息
export function updateAuditCompetition(data){
  return request({
    url: '/dcims/competitionAudit/put',
    method: 'post',
    data: data
  })
}
