import request from '@/utils/request'

// 查询教师信息列表
export function listTeacherDict(name) {
  return request({
    url: '/dcims/basicData/listTeacherDict?name=' + name,
    method: 'get',
  })
}
// 查询学生信息列表
export function listStudentDict(name) {
    return request({
      url: '/dcims/basicData/listStudentDict?name=' + name,
      method: 'get',
    })
  }