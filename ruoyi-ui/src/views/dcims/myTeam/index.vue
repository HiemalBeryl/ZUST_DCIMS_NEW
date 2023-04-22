<template>
  <div>
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1" @click="myteam" >我的团队</el-menu-item>
      <el-menu-item index="4" @click="newteam">新增团队</el-menu-item>
      <el-menu-item index="8" @click="checkteam">查询团队</el-menu-item>
    </el-menu>
  </div>
</template>

<script>

export default {
  name:'tuandui',
  data() {
    return {
      // users:[],
      search:'',
      tableData: [{
        xuehao: '01',
        name: '1',
        dianhua: '1'
      }, {
        xuehao: '00',
        name: '1',
        dianhua:'2'
      }, {
        xuehao: '00',
        name: '1',
        dianhua: '2'
      }, {
        xuehao: '00',
        name: '1',
        dianhua: '3'
      }, {
        xuehao: '00',
        name: '1',
        dianhua: '3'
      }, {
        xuehao: '00',
        name: '1',
        dianhua: '3'
      }, {
        xuehao: '00',
        name: '1',
        dianhua: '3'
      }]
    }
  },

  //   computed:{
  //     tableDataSearch() {    //筛选对象中的所有key value
  //     var input = this.search;  //username为input中的v-model参数
  //     if (input) {
  //       return this.tableData.filter((data) => {
  //         return Object.keys(data).some((key) => {
  //           return String(data[key]).toLowerCase().indexOf(input) > -1;
  //         });
  //       });
  //     }
  //     return this.tableData;
  //   },
  //        DataSearch() {    //筛选对象中的所有key value
  //     var input = this.search;  //username为input中的v-model参数
  //     if (input) {
  //       return this.users.filter((user) => {
  //         return Object.keys(user).some((key) => {
  //           return String(user[key]).toLowerCase().indexOf(input) > -1;
  //         });
  //       });
  //     }
  //     return this.users;
  //   },
  // },


  methods:{
    //     async getdata(){
    //        const{data:res}= await axios({
    //         method:'GET',
    //         url:'https://api.github.com/search/users?q=test'
    //     });
    //     console.log(res)   //此时的res就是 返回响应报文中的data
    //       let i= res.items.length;
    //     console.log(i)
    //       for(let j=0;j<i;j++){
    //           this.users.unshift({'login':res.items[j].login,'html_url':res.items[j].html_url,'avatar_url':res.items[j].avatar_url})

    //       }
    //     },
    //      showdata(res){
    //       let i= res.items.size;
    //       for(let j=0;j<i;j++){
    //           this.users.unshift({login:res.items[j].login,html_url:res.items[j].html_url,avatar_url:res.items[j].avatar_url})

    //       }
    // },
    myteam(){
      this.$router.push({
        name:'myteam'
      })
    },
    newteam(){
      this.$router.push({
        name:'newteam'
      })
    },
    checkteam(){
      this.$router.push({
        name:'checkteam'
      })
    }
//  async beforeCreate() {
//          const{data:res}= await axios({
//           method:'GET',
//           url:'https://api.github.com/search/users?q=test'
//       });
//       console.log(res)   //此时的res就是 返回响应报文中的data
//         let i= res.items.length;
//       console.log(i)
//         for(let j=0;j<i;j++){
//             this.users.unshift({'login':res.items[j].login,'html_url':res.items[j].html_url,'avatar_url':res.items[j].avatar_url})

//         }
//       },
  }
}
</script>
<style>

</style>
