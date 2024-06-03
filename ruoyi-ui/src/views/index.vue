<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" style="padding-left: 20px" :span="24">
        <h2>{{ this.$store.state.user.nick }}，欢迎进入竞赛信息管理系统</h2>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :span="6">
        <div>
          <el-statistic title="竞赛申报">
            <template slot="formatter">
              <el-result v-if="todoList.A" icon="success" title="进行中"/>
              <el-result v-if="!todoList.A" icon="warning" title="已截止"/>
            </template>
          </el-statistic>
        </div>
      </el-col>
      <el-col :span="6">
        <div>
          <el-statistic title="获奖申报">
            <template slot="formatter">
              <el-result v-if="todoList.B" icon="success" title="进行中"/>
              <el-result v-if="!todoList.B" icon="warning" title="已截止"/>
            </template>
          </el-statistic>
        </div>
      </el-col>
      <el-col :span="6" v-if="$store.state.user.roles.includes('AcademicAffairsOffice') || $store.state.user.roles.includes('AcademyCompetitionHead')">
        <div>
          <el-statistic title="学院/教务处待提交竞赛">
            <template slot="formatter">
              <el-progress v-if="todoList.C" type="circle" :percentage="100" status="success"></el-progress>
              <el-progress v-if="!todoList.C" type="circle" :percentage="50" status="warning"></el-progress>

            </template>
          </el-statistic>
        </div>
      </el-col>
      <el-col :span="6" v-if="$store.state.user.roles.includes('AcademicAffairsOffice') || $store.state.user.roles.includes('AcademyCompetitionHead')">
        <div>
          <el-statistic title="学院/教务处待提交获奖">
            <template slot="formatter">
              <el-progress v-if="todoList.D" type="circle" :percentage="100" status="success"></el-progress>
              <el-progress v-if="!todoList.D" type="circle" :percentage="50" status="warning"></el-progress>
            </template>
          </el-statistic>
        </div>
      </el-col>
    </el-row>
  </div>
</template>


<script>
import {getTeacherTodo} from "@/api/dcims/globalSetting";
import {listTeamAudit} from "@/api/dcims/teamAudit";
import {listCompetitionAudit} from "@/api/dcims/competitionAudit";


export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "4.6.0",
      queryParams: {
        pageNum: 1,
        pageSize: 500
      },
      todoList: {
        A: false,
        B: false,
        C: false,
        D: false,
      }
    };
  },
  created() {
    this.getMyWorkload();
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    getMyWorkload() {
      getTeacherTodo().then((res) => {
        console.log(res)
        this.todoList.A = res.A;
        this.todoList.B = res.B;
      });
      listTeamAudit(this.queryParams).then(response => {
        if(response.total == 0) {
          this.todoList.D = true;
        }
      });
      listCompetitionAudit(this.queryParams).then(response => {
        if(response.total == 0) {
          this.todoList.C = true;
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

