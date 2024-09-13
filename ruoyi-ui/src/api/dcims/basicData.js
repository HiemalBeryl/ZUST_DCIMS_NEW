import request from '@/utils/request'

// 查询教师信息列表
export function listTeacherDict(name, exact) {
  return request({
    url: '/dcims/basicData/listTeacherDict?name=' + name + '&exact=' + exact,
    method: 'get',
  })
}

// 查询学生信息列表
export function listStudentDict(name, exact) {
    return request({
      url: '/dcims/basicData/listStudentDict?name=' + name + '&exact=' + exact,
      method: 'get',
    })
}

// 查询登录用户的教师信息
export function queryLoginTeacher() {
  return request({
    url: '/dcims/basicData/queryLoginTeacher',
    method: 'get',
  })
}
