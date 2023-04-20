<template>
   <div>
 <!-- <el-descriptions class="margin-top" title="我的团队1" :column="3"  border>
   
    <el-descriptions-item>
      <template slot="label">
        竞赛名称
      </template>
      1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
        团队负责人
      </template>
       1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
       团队人数
      </template>
       1
    </el-descriptions-item>
 
    <el-descriptions-item>
      <template slot="label">
        指导老师1
      </template>
         1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
        学生团队
      </template>
      1
    </el-descriptions-item>
  </el-descriptions>


<el-descriptions class="margin-top" title="我的团队2" :column="3"  border>
   
    <el-descriptions-item>
      <template slot="label">
        竞赛名称
      </template>
      1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
        团队负责人
      </template>
       1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
       团队人数
      </template>
       1
    </el-descriptions-item>
 
    <el-descriptions-item>
      <template slot="label">
        指导老师1
      </template>
         1
    </el-descriptions-item>
    <el-descriptions-item>
      <template slot="label">
        学生团队
      </template>
      1
    </el-descriptions-item>
  </el-descriptions> -->
  

  <!--这里 myteams是一个对象数组  p为数组中的每个元素 -->
  <!--这里 value1是一个对象中的每个属性值 既可以是单个属性 也可以是数组 key1是属性名 -->
  <!--展示的值可以设置为el-button格式 方便查看 -->
   <el-descriptions class="margin-top" v-for="(p,index) in myteams" :key="index"  :title="`我的团队${index}`" :column="3"  border>


       <el-descriptions-item v-for=" (value1,key1,index) in p" :key="index">
      <template slot="label">
        {{key1}}
      </template>
      <!-- <el-button type="text">{{value1.toString()}}</el-button> -->
      {{value1.toString()}}
      </el-descriptions-item>

     
       <el-descriptions-item>
          <template slot="label">
         是否获奖
      </template>
        <el-button type="text">查看</el-button>
         
       </el-descriptions-item>



   </el-descriptions>

   </div>
</template>

<script>
export default {
    name:'myteam',
    data(){
        return{
          
        }
    },
    computed:{
     myteams(){
     return this.$store.state.myteams
     }
    }
}
</script>

<style>

</style>