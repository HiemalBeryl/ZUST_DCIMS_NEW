import request from '@/utils/request'

// 查询奖金分配个人列表
export function listBonusAllocationPersonal(query) {
  return request({
    url: '/dcims/bonusAllocationPersonal/list',
    method: 'get',
    params: query
  })
}

// 查询奖金分配个人详细
export function getBonusAllocationPersonal(id) {
  return request({
    url: '/dcims/bonusAllocationPersonal/' + id,
    method: 'get'
  })
}

// 新增奖金分配个人
export function addBonusAllocationPersonal(data) {
  return request({
    url: '/dcims/bonusAllocationPersonal',
    method: 'post',
    data: data
  })
}

// 修改奖金分配个人
export function updateBonusAllocationPersonal(data) {
  return request({
    url: '/dcims/bonusAllocationPersonal',
    method: 'put',
    data: data
  })
}

// 删除奖金分配个人
export function delBonusAllocationPersonal(id) {
  return request({
    url: '/dcims/bonusAllocationPersonal/' + id,
    method: 'delete'
  })
}
