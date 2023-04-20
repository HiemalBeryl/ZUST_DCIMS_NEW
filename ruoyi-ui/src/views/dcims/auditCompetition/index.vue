<template>
  <div style="height:100%">
    <!-- 顶部 -->
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
      
      <!-- 用于存放勾选需要审核的获奖信息 -->
      <div>
          <el-row :gutter="20">
            <el-col :span="2"><div class="grid-content"></div></el-col>
    
            <!-- table 用于勾选项目 -->
            <el-col :span="20"
              ><div class="grid-content">
                <el-table
                  ref="multipleTable"
                  :data="gongZuoLiangFenPei"
                  tooltip-effect="dark"
                  style="width: 100%"
                  max-height="500px"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column type="selection" width="55"> </el-table-column>
                  <el-table-column prop="saiShiMingCheng" label="赛事名称" width="150">
                  </el-table-column>
                  <el-table-column prop="jingSaiDengJi" label="竞赛等级" width="150">
                  </el-table-column>
                  <el-table-column prop="liXiangJieCi" label="赛事团队" width="150">
                  </el-table-column>
                  <el-table-column prop="jingSaiFuZeRen" label="竞赛负责人" width="150">
                  </el-table-column>
                  <el-table-column prop="shenBaoJingFei" label="获奖等级" width="120">
                  </el-table-column>
                  <el-table-column prop="shenQingShiJian" label="申请时间" width="120">
                  </el-table-column>
                  <el-table-column fixed="right" label="附件" width="120">
                    <el-button type="text" @click="checkDetail"
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
                      <el-button type="info" @click="note">备注</el-button>
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
  export default {
    name:"huoJiangShenHe",
    data() {
      return {
        gongZuoLiangFenPei: [
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
        {
            saiShiMingCheng: "服务外包",
            liXiangJieCi: "zustTeamI",
            jingSaiDengJi: "A类",
            jingSaiFuZeRen: "XXX",
            shenBaoJingFei: "国一",
            shenQingShiJian:"2023-01-31"
        },
      ],
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
    methods: {
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
        this.$alert('<strong>这里将会用于显示教师上传的获奖附件</strong>', '详细信息', {
          dangerouslyUseHTMLString: true
        });
    }
    ,
    returnWarn() {
      this.$confirm("此操作将退回该申请, 是否继续?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "退回成功!",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消退回",
          });
        });
    },
    submitWarn() {
      this.$confirm("此操作将提交勾选的获奖申请, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "提交成功!",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消提交",
          });
        });
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
      },
    
  },
  };
</script>

<style>
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