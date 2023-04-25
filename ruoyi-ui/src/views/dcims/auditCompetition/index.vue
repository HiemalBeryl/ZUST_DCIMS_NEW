<template>
    <div style="height:100%">
        <div>
            <el-row>
              <el-col :span="24" ><div class="grid-content bg-purple-light" style="height:20px"></div></el-col>
            </el-row>
          </div>

        <!-- 用于存放第一行的筛选按钮 -->
        <div class="juZhong">
            <el-row :gutter="20">
                <el-col :span="2"><div class="grid-content "></div></el-col>

                <!-- 用于存放等级的筛选框 -->
                <el-col :span="5"><div class="grid-content ">
                    <div>
                        赛事等级:
                    </div>
                    <div>
                        <el-select v-model="value" placeholder="请选择赛事等级">
                            <el-option
                              v-for="item in levelOption"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value">
                            </el-option>
                          </el-select>
                    </div>
                </div></el-col>

                <!-- 用于存放时间筛选框 -->
                <el-col :span="10"><div class="grid-content ">
                    <span>申请时间:</span>
                      <div class="block">

                        <el-date-picker
                          v-model="date1"
                          type="daterange"
                          align="right"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          :picker-options="dateOptions">
                        </el-date-picker>
                      </div>
                </div></el-col>

                <!-- 用于存放关键词的搜索 -->
                <el-col :span="5"><div class="grid-content">
                    <div>关键词搜索:</div>
                    <div>
                        <el-input clearable="true" v-model="keyWord" placeholder="请键入关键词"></el-input>

                    </div>
                </div></el-col>

                <el-col :span="2"><div class="grid-content"></div></el-col>
            </el-row>

            <!-- 用于存放勾选需要审核的项目 -->
            <div>
                <el-row :gutter="20">
                  <el-col :span="2"><div class="grid-content"></div></el-col>

                  <!-- table 用于勾选项目 -->
                  <el-col :span="20"
                    ><div class="grid-content">
                      <el-table
                        v-loading="loading"
                        ref="multipleTable"
                        :data="competitionList"
                        tooltip-effect="dark"
                        style="width: 100%"
                        max-height="500px"
                        @selection-change="handleSelectionChange"
                      >
                        <el-table-column type="selection" width="55"> </el-table-column>
                        <el-table-column prop="name" label="赛事名称" width="150">
                        </el-table-column>
                        <el-table-column prop="term" label="立项届次" width="150">
                        </el-table-column>
                        <el-table-column prop="level" label="竞赛等级" width="150">
                        </el-table-column>
                        <el-table-column prop="responsiblePersonName" label="竞赛负责人" width="150">
                        </el-table-column>
                        <el-table-column prop="budget" label="申报经费（万元）" width="120">
                        </el-table-column>
                        <el-table-column prop="shenQingShiJian" label="申请时间" width="120">
                        </el-table-column>
                        <el-table-column fixed="right" label="查看详情" width="120">
                          <el-button type="text" @click="getOneDetail(scope.row)"
                            >查看详情</el-button
                          >
                        </el-table-column>
                      </el-table>

                      <!-- 最底部按钮，提交 退回 备注 -->
                      <div style="margin-top: 20px">
                        <div style="float:left">
                            <el-button @click="toggleSelection()">取消选择</el-button>
                        </div>

                        <div style="float: right">
                          <el-button type="primary" @click="submitWarn">提交</el-button>
                        </div>
                        <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                        <div style="float: right">
                          <el-button type="warning" @click="returnWarn">退回</el-button>
                        </div>
                        <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                        <div style="float: right">
                            <!-- <el-button type="info" @click="note">备注</el-button> -->
                        </div>
                      </div>
                    </div></el-col
                  >
                  <el-col :span="2"><div class="grid-content"></div></el-col>
                </el-row>
              </div>




        </div>
    </div>
</template>

<script>
import {listCompetitionAudit, getCompetitionAudit} from "@/api/dcims/competitionAudit";

  export default {
    name:"liXiangShenHe",
    data() {
      return {
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10
        },
        // 竞赛赛事基本信息表格数据
        competitionList: [],
        // 遮罩层
        loading: true,
        // 表单参数
        form: {},
        dateOptions: {
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
        date1: '',
        levelOption: [{
          value: '选项1',
          label: 'A'
        }, {
          value: '选项2',
          label: 'B'
        }, {
          value: '选项3',
          label: 'C'
        }, {
          value: '选项4',
          label: '其他'
        }],
        value: '',
        keyWord:'',

    };
    },
    created() {
      this.getList();
      console.log(competitionList);
    },
    methods: {
      /** 查询竞赛赛事基本信息列表 */
      getList() {
        this.loading = true;
        listCompetitionAudit(this.queryParams).then(response => {
          this.competitionList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      /** 修改按钮操作 */
      getOneDetail(row) {
        this.loading = true;
        //this.reset();
        const id = row.id || this.ids
        console.log(id)
        getCompetitionAudit(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改竞赛赛事基本信息";
        });
      },
      // 表单重置
      reset() {
        this.form = {
          id: undefined,
          deptId: undefined,
          userId: undefined,
          orderNum: undefined,
          name: undefined,
          level: undefined,
          pastName: undefined,
          term: undefined,
          annual: undefined,
          organizer: undefined,
          responsiblePersonId: undefined,
          responsiblePersonName: undefined,
          innerTime: undefined,
          provinceTime: undefined,
          nationalTime: undefined,
          stopTime: undefined,
          concentrationOfTeachingHours: undefined,
          budget: undefined,
          goal: undefined,
          appropriation: undefined,
          moneyAggregate: undefined,
          workloadAggregate: undefined,
          state: undefined,
          version: undefined,
          createTime: undefined,
          createBy: undefined,
          updateTime: undefined,
          updateBy: undefined,
          delFlag: undefined
        };
        this.resetForm("form");
      },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    checkDetail(){
        this.$alert('<strong>这里将会用于显示所有在立项时输入的信息</strong>', '详细信息', {
          dangerouslyUseHTMLString: true
        });
    }
    ,
    // returnWarn() {
    //   this.$confirm("此操作将退回该申请, 是否继续?", "警告", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(() => {
    //       this.$message({
    //         type: "success",
    //         message: "退回成功!",
    //       });
    //     })
    //     .catch(() => {
    //       this.$message({
    //         type: "info",
    //         message: "已取消退回",
    //       });
    //     });
    // },
    // submitWarn() {
    //   this.$confirm("此操作将提交勾选的立项申请, 是否继续?", "提示", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(() => {
    //       this.$message({
    //         type: "success",
    //         message: "提交成功!",
    //       });
    //     })
    //     .catch(() => {
    //       this.$message({
    //         type: "info",
    //         message: "已取消提交",
    //       });
    //     });
    // },
     returnWarn() {
    const h = this.$createElement;
this.$prompt(h(
	'div', null, [
		h('div', { style: "display:flex;align-items: center" }, [
			h('span',{style:"width: 70px"}, '审核人id:'),
			h('el-input',null)
		]),
      ]), 
		'退回提示', 
		{
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputPlaceholder: '退回备注',
			inputType:'textarea'
		}).then(({ value }) => {
			//  todo .....
		}).catch();
    },
    submitWarn() {
    
const h = this.$createElement;
this.$prompt(h(
	'div', null, [
		h('div', { style: "display:flex;align-items: center" }, [
			h('span',{style:"width: 70px"}, '审核人id:'),
			h('el-input',null)
		]),
      ]), 
		'上交提示', 
		{
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputPlaceholder: '备注消息',
			inputType:'textarea'
		}).then(({ value }) => {
			//  todo .....
		}).catch();

    },
        note() {
        this.$prompt('请输入备注', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          }).then(({ value }) => {
          this.$message({
            type: 'success',
            message: '备注成功'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消输入'
          });
        });
      }

  },
  };
</script>

<style>
.juZhong{
    text-align: center;
}
.el-row {
  margin-bottom: 20px;
}
 .el-row:last-child {
    margin-bottom: 0;
  }
  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
</style>
