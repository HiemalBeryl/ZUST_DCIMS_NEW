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

//查导入的竞赛是否需要录入团队赛/个人赛数据
export function needTeamOrPersonal(id) {
  return request({
    url: '/dcims/competition/needTeamOrPersonal/' + id,
    method: 'get'
  })
}

//填写竞赛团队赛/个人赛数据，也就是为single_race列赋值50/100
export function fillSingleRace(query) {
  return request({
    url: '/dcims/competition/putSingleRace?id='+query.id+'&singleRace='+query.singleRace,
    method: 'post'
  })
}

//查询导入的竞赛是否需要录入校赛参与人次
export function needInnerStudentCount(id) {
  return request({
    url: '/dcims/competition/needInnerStudent/' + id,
    method: 'get'
  })
}

//填写校赛参与人次数据，也就是为inner_student_count列赋值1~999
export function fillInnerStudentCount(query) {
  return request({
    url: '/dcims/competition/putInnerStudent?id='+query.id+'&innerStudentCount='+query.singleRace,
    method: 'post'
  })
}
