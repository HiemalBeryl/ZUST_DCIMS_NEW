import vuex from 'vuex'
import Vue from 'vue'

const actions ={
    
}
const mutations={
 
     
}
const getters={
  
}
const state = {
     myteams:[{                    //此部分后续为数据库所有
         竞赛项目:'1',
         项目负责人:'1',
         指导老师:['1','1'],
         参赛学生:['1','1'], 
        队伍名称:''
     },
     {
            竞赛项目:'1',
            项目负责人:'1',
            指导老师:['2','2'],
            参赛学生:['2','2'], 
        队伍名称:''
     }]
}
Vue.use(vuex)
export default new vuex.Store({
    namespaced:true,
    actions:actions,
    mutations:mutations,
    state:state,
})