import request from '@/utils/request'

// 查询全局设置年份列表
export function list() {
    return request({
      url: '/dcims/setting/list',
      method: 'get',
    })
  }
  
  // 删除某一年份
  export function del(year) {
    return request({
      url: '/dcims/setting/del?year=' + year,
      method: 'get'
    })
  }
  
  // 添加年份
  export function add(year) {
    return request({
      url: '/dcims/setting/add?year=' + year,
      method: 'get'
    })
  }
  
  // 查询年份内业务的起止时间
  export function getDetail(year){
    return request({
      url: '/dcims/setting/listDetail?year=' + year,
      method: 'get'
    })
  }

  // 修改年份内业务的起止时间
  export function update(data){
    return request({
      url: '/dcims/setting/update',
      method: 'post',
      data: data
    })
  }
  