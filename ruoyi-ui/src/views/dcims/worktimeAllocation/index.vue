<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="年份" prop="year">
        <el-select v-model="queryParams.year"  placeholder="请选择所属年份">
          <el-option
            v-for="dict in dict.type.dcims_years"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="核算开始时间" prop="startTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.startTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择核算开始时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="核算结束时间" prop="endTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.endTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择核算结束时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item>-->
<!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
<!--      </el-form-item>-->
    </el-form>

    <el-row :gutter="10" class="mb8">
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['dcims:worktimeAllocation:add']"-->
<!--        >新增计算</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['dcims:worktimeAllocation:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:worktimeAllocation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

<!--    <el-table v-loading="loading" :data="worktimeAllocationList" @selection-change="handleSelectionChange">-->
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      &lt;!&ndash; <el-table-column label="主键" align="center" prop="id" v-if="true"/> &ndash;&gt;-->
<!--      <el-table-column label="年份" align="center" prop="year" />-->
<!--      <el-table-column label="核算开始时间" align="center" prop="startTime" width="180">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="核算结束时间" align="center" prop="endTime" width="180">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      &lt;!&ndash; <el-table-column label="计算公式" align="center" prop="fomular" />-->
<!--      <el-table-column label="核算状态" align="center" prop="status" /> &ndash;&gt;-->
<!--      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--            v-hasPermi="['dcims:worktimeAllocation:query']"-->
<!--          >查看</el-button>-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-delete"-->
<!--            @click="handleDelete(scope.row)"-->
<!--            v-hasPermi="['dcims:worktimeAllocation:remove']"-->
<!--          >删除</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--    </el-table>-->

    <el-table v-loading="loading" :data="worktimeAllocationList" @selection-change="handleSelectionChange">
      <el-table-column label="考核年度" align="center" prop="year" />
      <el-table-column label="竞赛名称" align="center" prop="competitionName" />
      <el-table-column label="竞赛类别" align="center" prop="type" />
      <el-table-column label="竞赛时间" align="center" prop="time" />
      <el-table-column label="竞赛方式" align="center" prop="mode" />
      <el-table-column label="获奖项数" align="center" prop="awardNum" />
      <el-table-column label="获奖等级" align="center" prop="awardLevel" />
      <el-table-column label="主办单位" align="center" prop="organizer" />
      <el-table-column label="学时" align="center" prop="credit" />
      <el-table-column label="集中授课学时" align="center" prop="concentratedCredit" />
      <el-table-column label="负责人工号" align="center" prop="leaderId" />
      <el-table-column label="负责人姓名" align="center" prop="leader" />
      <el-table-column label="所属部门" align="center" prop="department" />
      <el-table-column label="备注" align="center" prop="remark" />
    </el-table>

<!--      <pagination-->
<!--      v-show="total>0"-->
<!--      :total="total"-->
<!--      :page.sync="queryParams.pageNum"-->
<!--      :limit.sync="queryParams.pageSize"-->
<!--      @pagination="getList"-->
<!--    />-->

    <!-- 添加或修改工作量分配对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="年份" prop="year">
          <el-select v-model="form.year"  placeholder="请选择所属年份">
          <el-option
            v-for="dict in dict.type.dcims_years"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
        </el-form-item>
        <el-form-item label="核算开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择核算开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="核算结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择核算结束时间">
          </el-date-picker>
        </el-form-item>
        <!-- <el-form-item label="计算公式" prop="fomular">
          <el-input v-model="form.fomular" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="参数键值对" prop="parameter">
          <el-input v-model="form.parameter" type="textarea" placeholder="请输入内容" />
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
import { listWorktimeAllocation, getWorktimeAllocation, delWorktimeAllocation, addWorktimeAllocation, updateWorktimeAllocation } from "@/api/dcims/worktimeAllocation";

export default {
  name: "WorktimeAllocation",
  dicts: ['dcims_years'],
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
      // 工作量分配表格数据
      worktimeAllocationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        year: 2023,
        startTime: undefined,
        endTime: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
        year: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "核算开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "核算结束时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询工作量分配列表 */
    getList() {
      this.loading = true;
      listWorktimeAllocation(this.queryParams.year).then(response => {
        this.worktimeAllocationList = response;
        this.loading = false;
      });
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
        year: undefined,
        startTime: undefined,
        endTime: undefined,
        fomular: undefined,
        parameter: undefined,
        status: undefined,
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
      this.title = "发起工作量汇总";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({ path: "worktimeAllocationCompetition", query: {id: row.id} })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateWorktimeAllocation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addWorktimeAllocation(this.form).then(response => {
              this.$modal.msgSuccess("发起成功");
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
      this.$modal.confirm('是否确认删除工作量分配编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delWorktimeAllocation(ids);
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
      this.download('dcims/worktimeAllocation/export', {
        ...this.queryParams
      }, `工作量汇总${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
