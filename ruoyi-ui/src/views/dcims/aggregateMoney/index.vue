<template>
  <div style="height: 100%">
    <div>
      <el-row>
        <el-col :span="24"
          ><div class="grid-content bg-purple-light" style="height: 20px"></div
        ></el-col>
      </el-row>
    </div>

    <!-- 用于显示共有多少钱需要分配，剩余多少钱可以分配，分配截至日期 -->
    <div>
      <el-row :gutter="20">
        <el-col :span="3"><div class="grid-content"></div></el-col>

        <!-- 总共有多少钱需要分配 -->
        <el-col :span="6"
          ><div class="grid-content">
            <h3>
              您共有<span style="color: red">{{ jiangJinTotal }}</span
              >元需要分配
            </h3>
          </div></el-col
        >

        <!-- 目前还剩余多少钱可以分配 -->
        <el-col :span="6"
          ><div class="grid-content">
            <h3>
              您目前剩余<span style="color: red">{{ jiangjinRemain }}</span
              >元可以分配
            </h3>
          </div></el-col
        >

        <!-- 本次奖金分配截止日期 -->
        <el-col :span="9"
          ><div class="grid-content" style="text-align: right">
            <h3>
              本次奖金分配截止日期<span>{{ assignmentEndDate }}</span>
            </h3>
          </div></el-col
        >
      </el-row>
    </div>

    <!-- 用于对各个团队进行金额的分配 -->
    <div>
      <el-row :gutter="20">
        <el-col :span="2"><div class="grid-content"></div></el-col>

        <!-- 用于存放table -->
        <el-col :span="20"
          ><div class="grid-content">
            <div style="height: 500px">
              <el-table
                :data="jiangJinFenPei"
                style="width: 100%"
                max-height="500px"
              >
                <el-table-column
                  fixed
                  prop="teamName"
                  label="团队名称"
                  width="120"
                >
                </el-table-column>
                <el-table-column
                  prop="projectName"
                  label="参加赛事"
                  width="120"
                >
                </el-table-column>
                <el-table-column
                  prop="projectLevel"
                  label="赛事级别"
                  width="120"
                >
                </el-table-column>
                <el-table-column prop="teamNumber" label="团队成员" width="230">
                </el-table-column>
                <el-table-column
                  prop="guideTeacher"
                  label="指导教师"
                  width="120"
                >
                </el-table-column>
                <el-table-column prop="teamPrice" label="获得奖项" width="120">
                </el-table-column>
                <el-table-column prop="moneyGet" label="奖金" width="60">
                </el-table-column>
                <el-table-column fixed="right" label="奖金分配(元)" width="120">
                  <el-button type="text" @click="typeMoney">设置奖金</el-button>
                </el-table-column>
              </el-table>
            </div>
          </div></el-col
        >

        <el-col :span="2"><div class="grid-content"></div></el-col>
      </el-row>
    </div>

    <!-- 提交按钮 -->
    <div>
      <el-row :gutter="20">
        <el-col :span="2"><div class="grid-content"></div></el-col>
        <el-col :span="20"
          ><div class="grid-content">
            <!-- 底部 重置 保存 提交三个按钮 -->
            <div style="float: right">
              <el-button type="primary" @click="submitWarn">提交</el-button>
            </div>
            <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
            <div style="float: right">
              <el-button type="info" @click="saveWarn">保存</el-button>
            </div>
            <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
            <div style="float: right">
              <el-button type="warning" @click="resetWarn">重置</el-button>
            </div>
          </div></el-col
        >
        <el-col :span="2"><div class="grid-content"></div></el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "jiangJinFenPei",
  data() {
    return {
      // 奖金分配资料
      jiangJinFenPei: [
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          teamNumber: "XXX,XXX,XX",
          guideTeacher: "XXX",
          projectLevel: "A类",
          teamPrice: "国一",
        },
      ],
      jiangJinTotal: 50000,
      jiangjinRemain: 50000,
      assignmentEndDate: "2023-06-10 12:00:00",
    };
  },
  methods: {
    typeMoney() {
      this.$prompt("请输入金额", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.$message({
            type: "success",
            message: "你输入的金额是: " + value,
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消输入",
          });
        });
    },
    resetWarn() {
      this.$confirm("此操作将重置所有已经分配的奖金, 是否继续?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "重置成功!",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消重置",
          });
        });
    },
    saveWarn() {
      this.$confirm("此操作将保存所有已经分配的奖金, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "保存成功!",
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消保存",
          });
        });
    },
    submitWarn() {
      this.$confirm("此操作将提交所有已经分配的奖金, 是否继续?", "提示", {
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
  },
};
</script>

<style scoped>
.juZhong {
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