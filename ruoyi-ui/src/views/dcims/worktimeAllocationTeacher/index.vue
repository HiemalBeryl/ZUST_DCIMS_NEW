<template>
  <div class="app-container">
    <div>
      <el-row>
        <el-col :span="18">
          请选择竞赛：
            <el-select v-model="queryParams.level" @change="selectionChange" placeholder="请选择竞赛" clearable>
             <el-option
               v-for="competition in competitionList"
                :key="competition.id"
               :label="competition.competitionName"
               :value="competition.id"
             />
            </el-select>
        </el-col>
        <el-col :span="6">
          <h3>您目前剩余<span style="color: red">{{ competitionList[0].remain }}</span>单位的工作量可以分配</h3>
        </el-col>
      </el-row>
      
    </div>
    <el-divider></el-divider>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dcims:worktimeAllocationTeacher:add']"
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
          v-hasPermi="['dcims:worktimeAllocationTeacher:edit']"
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
          v-hasPermi="['dcims:worktimeAllocationTeacher:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:worktimeAllocationTeacher:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="worktimeAllocationTeacherList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" v-if="true"/>
      <el-table-column label="工作量竞赛id" align="center" prop="worktimeCompetitionId" />
      <el-table-column label="教师工号" align="center" prop="teacherId" />
      <el-table-column label="教师应得工作量" align="center" prop="worktime" />
      <el-table-column label="审核状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:worktimeAllocationTeacher:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:worktimeAllocationTeacher:remove']"
          >删除</el-button>
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

    <!-- 添加或修改工作量分配对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="教师工号" prop="teacherId">
          <el-input v-model="form.teacherId" :disabled="true" placeholder="请输入教师工号" />
        </el-form-item>
        <el-form-item label="教师应得工作量" prop="worktime">
          <el-input v-model="form.worktime" placeholder="请输入教师应得工作量" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWorktimeAllocationTeacher, getWorktimeAllocationTeacher, delWorktimeAllocationTeacher, addWorktimeAllocationTeacher, updateWorktimeAllocationTeacher } from "@/api/dcims/worktimeAllocationTeacher";
import { listWorktimeAllocationCompetitionByTeacherId} from "@/api/dcims/worktimeAllocationCompetition";
export default {
  name: "WorktimeAllocationTeacher",
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
      worktimeAllocationTeacherList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        worktimeCompetitionId: undefined,
        teacherId: undefined,
        worktime: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
      },
      // 教师负责的竞赛列表
      competitionList: [],
      // 竞赛总数
      competitionTotal: undefined,
      // 剩余工作量
      workTimeRemain: undefined,
    };
  },
  created() {
    this.getCompetitionList();
    this.queryParams.worktimeCompetitionId = this.competitionList[0].id;
    this.getList();
  },
  methods: {
    /** 查询待分配工作量的竞赛列表 */
    getCompetitionList(){
      this.loading = true;
      listWorktimeAllocationCompetitionByTeacherId().then(response => {
        this.competitionList = response.rows;
        this.ompetitionTotal = response.total;
        this.loading = false;
      })
    },
    /** 查询工作量分配列表 */
    getList() {
      this.loading = true;
      listWorktimeAllocationTeacher(this.queryParams).then(response => {
        this.worktimeAllocationTeacherList = response.rows;
        this.competitionTotal = response.total;
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
        worktimeCompetitionId: undefined,
        teacherId: undefined,
        worktime: undefined,
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
      this.title = "添加工作量分配";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getWorktimeAllocationTeacher(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改工作量分配";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateWorktimeAllocationTeacher(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addWorktimeAllocationTeacher(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工作量分配编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delWorktimeAllocationTeacher(ids);
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
      this.download('dcims/worktimeAllocationTeacher/export', {
        ...this.queryParams
      }, `worktimeAllocationTeacher_${new Date().getTime()}.xlsx`)
    },
    /** 下拉框选中竞赛变化 */
    selectionChange(key){
      let obj = {};
      obj = this.competitionList.find((item)=>{
          return item.id === key;
      });
      this.queryParams.worktimeCompetitionId = obj.id;
      this.getList();
    },
  }
};
</script>
