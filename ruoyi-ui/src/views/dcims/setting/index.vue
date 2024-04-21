<template>
  <div class="app-container">
<template>
  <el-select v-model="selectedYear" placeholder="请选择年份" @change="getDetail">
    <el-option
      v-for="item in yearOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
  </el-select>
  <el-input v-model="inputYear" placeholder="请输入新年份" style="width: 15%; margin-left: 30px;"></el-input>
  <el-button type="button" @click="addAnnual">创建新年份</el-button>
</template>

<el-divider></el-divider>

<h2>竞赛立项设置：</h2>
<span class="establish">是否允许立项</span>
<el-tag>
  <el-switch
  v-model="establishSwitch"
  active-color="#13ce66"
  inactive-color="#ff4949">
</el-switch>
</el-tag>
<template>
  <div class="block">
    <span class="demonstration">设置截止时间</span>
    <el-date-picker
      v-model="establishTime"
      type="datetimerange"
      align="right"
      unlink-panels
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      format="yyyy-MM-dd HH:mm:ss"
      value-format="yyyy-MM-dd HH:mm:ss"
      :picker-options="pickerOptions">
    </el-date-picker>
    <el-button type="primary" icon="el-icon-check" circle @click="changeDetail"></el-button>
  </div>
</template>


<h1></h1>

<h2>获奖申报设置：</h2>
<span class="establish">是否允许进行获奖申报信息录入</span>
<el-tag>
  <el-switch
  v-model="teamSwitch"
  active-color="#13ce66"
  inactive-color="#ff4949">
</el-switch>
</el-tag>
<template>
  <div class="block">
    <span class="demonstration">设置截止时间</span>
    <el-date-picker
      v-model="teamTime"
      type="datetimerange"
      align="right"
      unlink-panels
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      format="yyyy-MM-dd HH:mm:ss"
      value-format="yyyy-MM-dd HH:mm:ss"
      :picker-options="pickerOptions">
    </el-date-picker>
    <el-button type="primary" icon="el-icon-check" circle @click="changeDetail"></el-button>
  </div>
</template>

<h1></h1>
<el-button type="button" @click="deleteAnnual">删除该年份</el-button>

  </div>
</template>

<script>
import { list, del, add, getDetail, update} from "@/api/dcims/globalSetting";


export default {
  name: 'setting',
  data(){
    return {
      id: '',
      inputYear: '',
      selectedYear: '',
      yearOptions: [],
      establishSwitch: false,
      establishTime: ["", ""],
      teamSwitch: false,
      teamTime: "",
      pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
    }
  },
  created(){
    this.getAnnualList()
  },
  methods: {
    /** 获取可选年份列表 */
    getAnnualList(){
      // 清空选项
      this.yearOptions = []
      this.selectedYear = ''
      // 调用api查询
      list().then(response => {
        console.log(response)
        response.forEach(element => {
          this.yearOptions.push({
            value: element,
            label: element
          })
        });
      })
    },
    /** 删除某一年份 */
    deleteAnnual(){
      del(this.selectedYear).then(response => {
        if(response.data == true){
          this.$modal.msgSuccess("删除成功");
        }
      })
      this.getAnnualList()
    },
    /** 添加某一年份*/
    addAnnual(){
      add(this.inputYear).then(response => {
        if(response.data == true){
          this.$modal.msgSuccess("添加成功");
        }
      })
      this.getAnnualList()
    },
    /** 根据所选年份查询起止时间等内容 */
    getDetail(){
      this.id = ""
      this.establishSwitch = false
      this.establishTime = ["", ""]
      this.teamSwitch = false
      this.teamTime = ["", ""]

      getDetail(this.selectedYear).then(response => {
        this.id = response.id
        this.establishSwitch = response.establishCompetition
        // this.$set(this.establishTime, 0, response.competitionStartTime);
        // this.$set(this.establishTime, 1, response.competitionEndTime);
        // this.establishTime[0] = response.competitionStartTime
        // this.establishTime[1] = response.competitionEndTime
        this.teamSwitch = response.createTeam
        this.daterangeChange([response.competitionStartTime,response.competitionEndTime],[response.teamStartTime,response.teamEndTime])
        // this.$set(this.teamTime, 0, response.teamStartTime);
        // this.$set(this.teamTime, 1, response.teamEndTime);
        // this.teamTime[0] = response.teamStartTime
        // this.teamTime[1] = response.teamEndTime
      })
    },
    /** 修改起止时间 */
    changeDetail(){
      let data = {
        id: this.id,
        establishCompetition: this.establishSwitch,
        competitionStartTime: this.establishTime[0],
        competitionEndTime: this.establishTime[1],
        createTeam: this.teamSwitch,
        teamStartTime: this.teamTime[0],
        teamEndTime: this.teamTime[1]
      }
      update(data).then(response => {
        console.log(response);
        this.$modal.msgSuccess("修改成功");
      })
    },
    /**
 * 开始日期-结束日期改变
 * **/
daterangeChange(e1,e2){
    let _this =this
    _this.$nextTick(() => {
        _this.$set(_this, "establishTime", [e1[0], e1[1]]);
        _this.$set(_this, "teamTime", [e2[0], e2[1]]);
        _this.$forceUpdate();
    });
},
  }
}
</script>

<style>

</style>
