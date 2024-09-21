import request from '@/utils/request'

// 查询竞赛赛事基本信息列表
export function listCompetition(query) {
  return request({
    url: '/dcims/competition/list',
    method: 'get',
    params: query
  })
}

// 根据登录用户教师工号查询竞赛赛事基本信息列表
export function listCompetitionByTeacherId(query) {
  return request({
    url: '/dcims/competition/listByTeacherId',
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
    url: '/dcims/competition/put',
    method: 'post',
    data: data
  })
}

// 删除竞赛赛事基本信息
export function delCompetition(id) {
  return request({
    url: '/dcims/competition/delete/' + id,
    method: 'post'
  })
}

// 根据竞赛id查询指导教师
export function getTutor(id) {
  return request({
    url: '/dcims/competition/tutor/' + id,
    method: 'get',
  })
}

// 为竞赛添加指导教师
export function addTutor(competitionId, teacherIds) {
  return request({
    url: '/dcims/competition/tutor/competitionId/'+competitionId+'/teacherIds/'+teacherIds,
    method: 'post',
  })
}

// 删除竞赛赛事基本信息
export function deleteTutor(id) {
  return request({
    url: '/dcims/competition/tutor/delete/' + id,
    method: 'post'
  })
}
