<template>
  <div style="height: 100%">
    <div>
      <el-row>
        <el-col :span="24"
          ><div class="grid-content bg-purple-light" style="height: 20px"></div
        ></el-col>
      </el-row>
    </div>

    <!-- 用于显示共有多少工作量需要分配，剩余多少工作量可以分配，分配截至日期 -->
    <div>
      <el-row :gutter="20">
        <el-col :span="3"><div class="grid-content"></div></el-col>

        <!-- 总共有多少工作量需要分配 -->
        <el-col :span="6"
          ><div class="grid-content">
            <h3>
              您共有<span style="color: red">{{ workTimeTotal }}</span
              >个工作量需要分配
            </h3>
          </div></el-col
        >

        <!-- 目前还剩余多少钱可以分配 -->
        <el-col :span="6"
          ><div class="grid-content">
            <h3>
              您目前剩余<span style="color: red">{{ workTimeRemain }}</span
              >个工作量可以分配
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

    <!-- 用于对各个老师的工作量进行分配 -->
    <div>
      <el-row :gutter="20">
        <el-col :span="2"><div class="grid-content"></div></el-col>

        <!-- table 用于勾选不同的教师，来分配他们的工作量 -->
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
              <el-table-column prop="teamName" label="团队名称" width="150">
              </el-table-column>
              <el-table-column prop="projectName" label="赛事名称" width="150">
              </el-table-column>
              <el-table-column prop="projectLevel" label="赛事级别" width="150">
              </el-table-column>
              <el-table-column prop="teamPrice" label="获得奖项" width="150">
              </el-table-column>
              <el-table-column prop="teacherName" label="教师姓名" width="120">
              </el-table-column>
              <el-table-column prop="workTimeGet" label="工作量" width="120">
              </el-table-column>
              <el-table-column fixed="right" label="工作量分配" width="120">
                <el-button type="text" @click="typeWorkTime"
                  >设置工作量</el-button
                >
              </el-table-column>
            </el-table>
            <div style="margin-top: 20px">
              <el-button @click="toggleSelection()">取消选择</el-button>
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
  name: "gongZuoLiangFenPei",
  data() {
    return {
      // 工作量分配资料
      gongZuoLiangFenPei: [
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
        {
          teamName: "Zust Team I",
          projectName: "蓝桥杯",
          projectLevel: "A类",
          teacherName: "XXX",
          teamPrice: "国一",
        },
      ],
      workTimeTotal: 50,
      workTimeRemain: 50,
      assignmentEndDate: "2023-06-10 12:00:00",
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
    typeWorkTime() {
      this.$prompt("请输入工作量", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.$message({
            type: "success",
            message: "你输入的工作量是: " + value,
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
      this.$confirm("此操作将重置所有已经分配的工作量, 是否继续?", "警告", {
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
      this.$confirm("此操作将保存所有已经分配的工作量, 是否继续?", "提示", {
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
      this.$confirm("此操作将提交所有已经勾选分配的工作量, 是否继续?", "提示", {
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