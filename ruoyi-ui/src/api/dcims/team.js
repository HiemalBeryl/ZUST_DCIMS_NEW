import request from '@/utils/request'

// 查询参赛团队列表
export function listTeam(query) {
  return request({
    url: '/dcims/team/list',
    method: 'get',
    params: query
  })
}

// 根据登录人的工号查询参赛团队列表
export function listTeamByTeacherId(query) {
  return request({
    url: '/dcims/team/listTeamByTeacherId',
    method: 'get',
    params: query
  })
}

// 查询参赛团队详细
export function getTeam(id) {
  return request({
    url: '/dcims/team/' + id,
    method: 'get'
  })
}

// 新增参赛团队
export function addTeam(data) {
  return request({
    url: '/dcims/team',
    method: 'post',
    data: data
  })
}

// 修改参赛团队
export function updateTeam(data) {
  return request({
    url: '/dcims/team/put',
    method: 'post',
    data: data
  })
}

// 为团队申报huojiangxinxi
export function declareAward(data) {
  return request({
    url: '/dcims/team/award/put',
    method: 'post',
    data: data
  })
}

// 删除参赛团队
export function delTeam(id) {
  return request({
    url: '/dcims/team/delete/' + id,
    method: 'post'
  })
}
