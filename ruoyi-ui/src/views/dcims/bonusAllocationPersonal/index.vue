<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item> -->
    </el-form>
 <!-- 用于显示共有多少钱需要分配，剩余多少钱可以分配，分配截至日期 -->
 <div>
    <el-row>
      <el-col :span="24">
        <el-select v-model="allocationId" placeholder="请选择" @change="renew(allocationId)">
          <el-option
            v-for="item in allocationOption"
            :key="item.id"
            :label="item.years+'年奖金分配,学院序号'+item.college"
            :value="item.id">
          </el-option>
        </el-select>
      </el-col>
    </el-row>
      <el-row type="flex" justify="space-center">
        <!-- <el-col :span="1"><div class="grid-content"></div></el-col> -->

          <!-- 总共有多少钱需要分配 -->
          <el-col :span="8">
            <div class="grid-content">
              <h3>
                您共有 <span style="color: red">{{ jiangJinTotal }}</span> 元需要分配
              </h3>
            </div>
          </el-col>

          <!-- 目前还剩余多少钱可以分配 -->
          <el-col :span="8" class="juZhong">
            <div class="grid-content">
              <h3>
                您目前剩余 <span style="color: red">{{ jiangjinRemain }}</span> 元可以分配
              </h3>
            </div>
          </el-col>

          <!-- 目前已分配多少钱 -->
          <el-col :span="8" class="youDuiQi">
            <div class="grid-content" >
              <h3>
                您目前已分配 <span style="color: red">{{ jiangJinTotal-jiangjinRemain }}</span> 元
              </h3>
            </div>
          </el-col>

          
        
      </el-row >
       
      <el-row >
        <!-- 留存比例 -->
          <el-col :span="12">
            <div class="grid-content">
              <h3>
                目前留存比例 <span style="color: red">{{ retentionRatio }}%</span> 
              </h3>
            </div>
          </el-col>
          
          <!-- 本次奖金分配截止日期 -->
          <el-col :span="12" class="youDuiQi">
            <div class="grid-content" style="text-align: right">
              <h3>
                本次奖金分配截止日期 <span>{{ assignmentEndDate }}</span>
              </h3>
            </div>
          </el-col>
      </el-row>
      
    </div>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dcims:bonusAllocationPersonal:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['dcims:bonusAllocationPersonal:edit']"
        >修改</el-button>
      </el-col> 
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['dcims:bonusAllocationPersonal:remove']"
        >删除</el-button>
      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:bonusAllocationPersonal:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bonusAllocationPersonalList" @selection-change="handleSelectionChange">
      <!--<el-table-column type="selection" width="55" align="center" />
       <el-table-column label="主键" align="center" prop="id" v-if="true"/>
      <el-table-column label="年份" align="center" prop="years" /> -->
      <el-table-column label="获得人" align="center" prop="gainerDetail.name" />
      <el-table-column label="负责竞赛" align="center" prop="competitionDetail.name" />
      <el-table-column label="获得奖金数" align="center" prop="bonus" />
      <el-table-column label="分配时间" align="center" prop="allocateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.allocateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="分配者" align="center" prop="teacherInChargeDetail.name" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:bonusAllocationPersonal:edit']"
          >修改</el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:bonusAllocationPersonal:remove']"
          >删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改奖金分配个人对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- <el-form-item label="年份" prop="years">
          <el-input v-model="form.years" placeholder="请输入年份" />
        </el-form-item>
        <el-form-item label="获得人" prop="gainer">
          <el-input v-model="form.gainer" placeholder="请输入获得人" />
        </el-form-item>
        <el-form-item label="负责竞赛" prop="competition">
          <el-input v-model="form.competition" placeholder="请输入负责竞赛" />
        </el-form-item> -->
        <el-form-item label="获得奖金" prop="bonus">
          <el-input v-model="form.bonus" placeholder="请输入获得奖金数" />
        </el-form-item>
        <!-- <el-form-item label="分配时间" prop="allocateTime">
          <el-date-picker clearable
            v-model="form.allocateTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择分配时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="分配者" prop="teacherInCharge">
          <el-input v-model="form.teacherInCharge" placeholder="请输入分配者" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBonusAllocationPersonal,
  getBonusAllocationPersonal,
  delBonusAllocationPersonal,
  addBonusAllocationPersonal,
  updateBonusAllocationPersonal,
  getBonusAllocationCollegeTotal,
} from "@/api/dcims/bonusAllocationPersonal";

import {
  listByTeacherId,
} from "@/api/dcims/bonusAllocation";

export default {
  name: "BonusAllocationPersonal",
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 奖金分配个人表格数据
      bonusAllocationPersonalList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: 0
      },
      // 奖金分配选项
      allocationOption: [],
      // 选中的项目
      allocationId: undefined,
      // 奖金总数，奖金剩余，留存比例
      jiangJinTotal: 0,
      jiangjinRemain: 0,
      retentionRatio: 0,
      assignmentEndDate: "8888-88-88 88:88:88",
      // 用于预存原本获得的金额
      add: 0,
      
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
        years: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        gainer: [
          { required: true, message: "获得人不能为空", trigger: "blur" }
        ],
        competition: [
          { required: true, message: "负责竞赛不能为空", trigger: "blur" }
        ],
        bonus: [
          { required: true, message: "获得奖金数不能为空", trigger: "blur" }
        ],
        allocateTime: [
          { required: true, message: "分配时间不能为空", trigger: "blur" }
        ],
        teacherInCharge: [
          { required: true, message: "分配者不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.initAllocationOptions();
  },
  methods: {
    /** 查询奖金分配个人列表 */
    getList() {
      this.loading = true;
      this.queryParams.id = this.allocationId;
      listBonusAllocationPersonal(this.queryParams).then(response => {
        console.log(response)
        this.bonusAllocationPersonalList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    // 更新数据
    renew(id) {
      getBonusAllocationCollegeTotal(id).then(response => {
        this.jiangJinTotal = response.data.totalAmount;
        this.jiangjinRemain = response.data.unallocated;
        this.assignmentEndDate = response.data.endTime;
        this.retentionRatio = response.data.retentionRatio*100;
      })
      this.getList();
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        years: undefined,
        gainer: undefined,
        competition: undefined,
        bonus: undefined,
        allocateTime: undefined,
        teacherInCharge: undefined,
        version: undefined,
        createTime: undefined,
        createBy: undefined,
        updateTime: undefined,
        updateBy: undefined,
        delFlag: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加奖金分配个人";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getBonusAllocationPersonal(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改奖金分配个人";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateBonusAllocationPersonal(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              //更新上方数据
              this.renew(this.allocationId);

              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addBonusAllocationPersonal(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");

              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除奖金分配个人编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delBonusAllocationPersonal(ids);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dcims/bonusAllocationPersonal/export', {
        ...this.queryParams
      }, `bonusAllocationPersonal_${new Date().getTime()}.xlsx`)
    },
    // 初始化奖金分配选择
    initAllocationOptions(){
      listByTeacherId().then(response => {
        this.allocationOption = response;
        console.log(this.allocationOption);
        // 初始化最上面的信息
        if(this.allocationOption[0] != undefined){
          this.allocationId = this.allocationOption[0].id;
          this.renew(this.allocationOption[0].id);
        }
      });
    }
  }
};
</script>

<style scoped>
.juZhong {
  text-align: center;
}
.youDuiQi{
  text-align: right;
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