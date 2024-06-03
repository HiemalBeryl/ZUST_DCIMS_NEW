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

// 退回已经通过审核的团队
export function removeOne(id) {
  return request({
    url: '/dcims/teamAudit/deleteOne/' + id,
    method: 'post'
  })
}

// 获取批量导入模板
export function getImportTemplate(annual) {
  return request({
    url: '/dcims/team/importTemplate?annual=' + annual,
    method: 'get',
  })
}

// 上传批量导入压缩文件
export function uploadTemplate(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/dcims/team/uploadTemplate',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 手动修改后的批量导入信息
export function editImportData(id, data) {
  return request({
    url: '/dcims/team/editImportData?id=' + id,
    method: 'post',
    data: data
  })
}

// 追加批量导入信息
export function appendImportData(id, type, file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/dcims/team/appendImportData?id=' + id  + "&type=" + type,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 确认批量导入信息，保存进入数据库
export function submitImportData(id) {
  return request({
    url: '/dcims/team/submitImportData?id=' + id,
    method: 'post'
  })
}

