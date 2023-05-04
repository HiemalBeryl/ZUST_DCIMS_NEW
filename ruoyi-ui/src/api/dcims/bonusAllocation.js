import request from '@/utils/request'

// 查询奖金分配总列表
export function listBonusAllocation(query) {
  return request({
    url: '/dcims/bonusAllocation/list',
    method: 'get',
    params: query
  })
}

// 查询奖金分配总详细
export function getBonusAllocation(id) {
  return request({
    url: '/dcims/bonusAllocation/' + id,
    method: 'get'
  })
}

// 新增奖金分配总
export function addBonusAllocation(data) {
  return request({
    url: '/dcims/bonusAllocation',
    method: 'post',
    data: data
  })
}

// 修改奖金分配总
export function updateBonusAllocation(data) {
  return request({
    url: '/dcims/bonusAllocation',
    method: 'put',
    data: data
  })
}

// 删除奖金分配总
export function delBonusAllocation(id) {
  return request({
    url: '/dcims/bonusAllocation/' + id,
    method: 'delete'
  })
}