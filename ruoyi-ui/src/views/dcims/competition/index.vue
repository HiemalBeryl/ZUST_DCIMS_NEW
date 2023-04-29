<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事类别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择赛事类别" clearable>
          <el-option
            v-for="dict in dict.type.dcims_competition_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="往届名称" prop="pastName">
        <el-input
          v-model="queryParams.pastName"
          placeholder="请输入往届名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事届次" prop="term">
        <el-input
          v-model="queryParams.term"
          placeholder="请输入赛事届次"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事年份" prop="annual">
        <el-input
          v-model="queryParams.annual"
          placeholder="请输入赛事年份"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主办单位" prop="organizer">
        <el-input
          v-model="queryParams.organizer"
          placeholder="请输入主办单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="竞赛负责人" prop="responsiblePersonName">
        <el-input
          v-model="queryParams.responsiblePersonName"
          placeholder="请输入竞赛负责人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="校内选拔时间">
        <el-date-picker
          v-model="daterangeInnerTime"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="省赛时间">
        <el-date-picker
          v-model="daterangeProvinceTime"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="国赛时间">
        <el-date-picker
          v-model="daterangeNationalTime"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dcims:competition:add']"
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
          v-hasPermi="['dcims:competition:edit']"
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
          v-hasPermi="['dcims:competition:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:competition:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="competitionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" v-if="true"/>
      <el-table-column label="赛事名称" align="center" prop="name" />
      <el-table-column label="赛事类别" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_competition_type" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="往届名称" align="center" prop="pastName" />
      <el-table-column label="赛事届次" align="center" prop="term" />
      <el-table-column label="赛事年份" align="center" prop="annual" />
      <el-table-column label="主办单位" align="center" prop="organizer" />
      <el-table-column label="竞赛负责人工号" align="center" prop="responsiblePersonId" />
      <el-table-column label="竞赛负责人" align="center" prop="responsiblePersonName" />
      <el-table-column label="校内选拔时间" align="center" prop="innerTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.innerTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="省赛时间" align="center" prop="provinceTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.provinceTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="国赛时间" align="center" prop="nationalTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.nationalTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="本年度拨款" align="center" prop="appropriation" />
      <el-table-column label="个人赛限项" align="center" prop="personLimit" />
      <el-table-column label="团队赛限项" align="center" prop="teamLimit" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:competition:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:competition:remove']"
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

    <!-- 添加或修改竞赛赛事基本信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="赛事名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入赛事名称" />
        </el-form-item>
        <el-form-item label="往届名称" prop="pastName">
          <el-input v-model="form.pastName" placeholder="请输入往届名称" />
        </el-form-item>
        <el-form-item label="赛事官网" prop="website">
          <el-input v-model="form.website" placeholder="请输入赛事官网" />
        </el-form-item>
        <el-form-item label="赛事届次" prop="term">
          <el-input v-model="form.term" placeholder="请输入赛事届次" />
        </el-form-item>
        <el-form-item label="赛事年份" prop="annual">
          <el-input v-model="form.annual" placeholder="请输入赛事年份" />
        </el-form-item>
        <el-form-item label="主办单位" prop="organizer">
          <el-input v-model="form.organizer" placeholder="请输入主办单位" />
        </el-form-item>
        <el-form-item label="竞赛负责人工号" prop="responsiblePersonId">
          <el-input v-model="form.responsiblePersonId" placeholder="请输入竞赛负责人工号" />
        </el-form-item>
        <el-form-item label="竞赛负责人" prop="responsiblePersonName">
          <el-input v-model="form.responsiblePersonName" placeholder="请输入竞赛负责人" />
        </el-form-item>
        <el-form-item label="校内选拔时间" prop="innerTime">
          <el-date-picker clearable
            v-model="form.innerTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择校内选拔时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="省赛时间" prop="provinceTime">
          <el-date-picker clearable
            v-model="form.provinceTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择省赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="国赛时间" prop="nationalTime">
          <el-date-picker clearable
            v-model="form.nationalTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择国赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="本年度申报经费" prop="budget">
          <el-input v-model="form.budget" placeholder="请输入本年度申报经费" />
        </el-form-item>
        <el-form-item label="获奖目标" prop="goal">
          <el-input v-model="form.goal" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="赛事简介" prop="introduction">
          <el-input v-model="form.introduction" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="竞赛申报书" prop="attachment">
          <file-upload v-model="form.attachment"/>
        </el-form-item>
        <el-form-item label="审核人工号" prop="nextAuditId">
          <el-input v-model="form.nextAuditId" placeholder="请输入审核人工号" />
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
import { listCompetition, getCompetition, delCompetition, addCompetition, updateCompetition } from "@/api/dcims/competition";

export default {
  name: "Competition",
  dicts: ['dcims_audit_result', 'dcims_competition_type'],
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
      // 竞赛赛事基本信息表格数据
      competitionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 审核人工号时间范围
      daterangeInnerTime: [],
      // 审核人工号时间范围
      daterangeProvinceTime: [],
      // 审核人工号时间范围
      daterangeNationalTime: [],
      // 审核人工号时间范围
      daterangeStopTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        level: undefined,
        pastName: undefined,
        term: undefined,
        annual: undefined,
        organizer: undefined,
        responsiblePersonName: undefined,
        innerTime: undefined,
        provinceTime: undefined,
        nationalTime: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
        orderNum: [
          { required: true, message: "排序号不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "赛事名称不能为空", trigger: "blur" }
        ],
        level: [
          { required: true, message: "赛事类别不能为空", trigger: "change" }
        ],
        term: [
          { required: true, message: "赛事届次不能为空", trigger: "blur" }
        ],
        annual: [
          { required: true, message: "赛事年份不能为空", trigger: "blur" }
        ],
        organizer: [
          { required: true, message: "主办单位不能为空", trigger: "blur" }
        ],
        responsiblePersonId: [
          { required: true, message: "竞赛负责人工号不能为空", trigger: "blur" }
        ],
        responsiblePersonName: [
          { required: true, message: "竞赛负责人不能为空", trigger: "blur" }
        ],
        attachment: [
          { required: true, message: "竞赛申报书不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询竞赛赛事基本信息列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeInnerTime && '' != this.daterangeInnerTime) {
        this.queryParams.params["beginInnerTime"] = this.daterangeInnerTime[0];
        this.queryParams.params["endInnerTime"] = this.daterangeInnerTime[1];
      }
      if (null != this.daterangeProvinceTime && '' != this.daterangeProvinceTime) {
        this.queryParams.params["beginProvinceTime"] = this.daterangeProvinceTime[0];
        this.queryParams.params["endProvinceTime"] = this.daterangeProvinceTime[1];
      }
      if (null != this.daterangeNationalTime && '' != this.daterangeNationalTime) {
        this.queryParams.params["beginNationalTime"] = this.daterangeNationalTime[0];
        this.queryParams.params["endNationalTime"] = this.daterangeNationalTime[1];
      }
      if (null != this.daterangeStopTime && '' != this.daterangeStopTime) {
        this.queryParams.params["beginStopTime"] = this.daterangeStopTime[0];
        this.queryParams.params["endStopTime"] = this.daterangeStopTime[1];
      }
      listCompetition(this.queryParams).then(response => {
        this.competitionList = response.rows;
        this.total = response.total;
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
        orderNum: undefined,
        name: undefined,
        level: undefined,
        pastName: undefined,
        website: undefined,
        term: undefined,
        annual: undefined,
        organizer: undefined,
        responsiblePersonId: undefined,
        responsiblePersonName: undefined,
        innerTime: undefined,
        provinceTime: undefined,
        nationalTime: undefined,
        stopTime: undefined,
        budget: undefined,
        appropriation: undefined,
        goal: undefined,
        introduction: undefined,
        attachment: undefined,
        moneyAggregate: undefined,
        workloadAggregate: undefined,
        personLimit: undefined,
        teamLimit: undefined,
        nextAuditId: undefined,
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
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeInnerTime = [];
      this.daterangeProvinceTime = [];
      this.daterangeNationalTime = [];
      this.daterangeStopTime = [];
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
      this.title = "添加竞赛赛事基本信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getCompetition(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改竞赛赛事基本信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateCompetition(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addCompetition(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除竞赛赛事基本信息编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delCompetition(ids);
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
      this.download('dcims/competition/export', {
        ...this.queryParams
      }, `competition_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
