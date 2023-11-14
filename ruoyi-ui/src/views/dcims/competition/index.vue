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
      <el-form-item label="所属学院" prop="college">
        <el-select v-model="queryParams.college" placeholder="请选择所属学院" clearable>
          <el-option
            v-for="dict in dict.type.dcims_college"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      <el-table-column label="主键" align="center" prop="id" v-if="false"/>
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
      <el-table-column label="所属学院" align="center" prop="college">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_college" :value="scope.row.college"/>
        </template>
      </el-table-column>
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

    
  </div>
</template>

<script>
import { listCompetition, getCompetition, delCompetition} from "@/api/dcims/competition";

export default {
  name: "Competition",
  dicts: ['dcims_audit_result', 'dcims_competition_type', 'dcims_college'],
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
        college: undefined,
        responsiblePersonName: undefined,
        innerTime: undefined,
        provinceTime: undefined,
        nationalTime: undefined,
      },
      // 表单参数
      form: {},
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
