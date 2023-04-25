<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="父id" prop="parentId">
        <el-input
          v-model="queryParams.parentId"
          placeholder="请输入父id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事名称" prop="competitionName">
        <el-input
          v-model="queryParams.competitionName"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="队伍名称" prop="teamName">
        <el-input
          v-model="queryParams.teamName"
          placeholder="请输入队伍名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖项等级" prop="awardLevel">
        <el-select v-model="queryParams.awardLevel" placeholder="请选择奖项等级" clearable>
          <el-option
            v-for="dict in dict.type.dcims_award_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="比赛类型" prop="competitionType">
        <el-select v-model="queryParams.competitionType" placeholder="请选择比赛类型" clearable>
          <el-option
            v-for="dict in dict.type.dcims_award_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="团队成员姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入团队成员姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="指导教师姓名" prop="teacherName">
        <el-input
          v-model="queryParams.teacherName"
          placeholder="请输入指导教师姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="比赛时间" prop="competitionTime">
        <el-date-picker clearable
          v-model="queryParams.competitionTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择比赛时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker clearable
          v-model="queryParams.awardTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择获奖时间">
        </el-date-picker>
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
          v-hasPermi="['dcims:award:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="awardList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="父id" prop="parentId" />
      <el-table-column label="赛事名称" align="center" prop="competitionName" />
      <el-table-column label="队伍名称" align="center" prop="teamName" />
      <el-table-column label="奖项等级" align="center" prop="awardLevel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="比赛类型" align="center" prop="competitionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
        </template>
      </el-table-column>
      <el-table-column label="团队成员姓名" align="center" prop="studentName" />
      <el-table-column label="指导教师姓名" align="center" prop="teacherName" />
      <el-table-column label="比赛时间" align="center" prop="competitionTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.competitionTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:award:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['dcims:award:add']"
          >新增</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:award:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改获奖基本信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="父id" prop="parentId">
          <treeselect v-model="form.parentId" :options="awardOptions" :normalizer="normalizer" placeholder="请选择父id" />
        </el-form-item>
        <el-form-item label="赛事名称" prop="competitionName">
          <el-input v-model="form.competitionName" placeholder="请输入赛事名称" />
        </el-form-item>
        <el-form-item label="队伍名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入队伍名称" />
        </el-form-item>
        <el-form-item label="奖项等级" prop="awardLevel">
          <el-select v-model="form.awardLevel" placeholder="请选择奖项等级">
            <el-option
              v-for="dict in dict.type.dcims_award_level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="比赛类型" prop="competitionType">
          <el-select v-model="form.competitionType" placeholder="请选择比赛类型">
            <el-option
              v-for="dict in dict.type.dcims_award_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="团队成员姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入团队成员姓名" />
        </el-form-item>
        <el-form-item label="指导教师姓名" prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="请输入指导教师姓名" />
        </el-form-item>
        <el-form-item label="比赛时间" prop="competitionTime">
          <el-date-picker clearable
            v-model="form.competitionTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择比赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker clearable
            v-model="form.awardTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择获奖时间">
          </el-date-picker>
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
import { listAward, getAward, delAward, addAward, updateAward } from "@/api/dcims/award";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Award",
  dicts: ['dcims_award_level', 'dcims_award_type'],
  components: {
    Treeselect
  },
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 获奖基本信息表格数据
      awardList: [],
      // 获奖基本信息树选项
      awardOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        parentId: undefined,
        competitionName: undefined,
        teamName: undefined,
        awardLevel: undefined,
        competitionType: undefined,
        studentName: undefined,
        teacherName: undefined,
        competitionTime: undefined,
        awardTime: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "数据编号不能为空", trigger: "blur" }
        ],
        parentId: [
          { required: true, message: "父id不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询获奖基本信息列表 */
    getList() {
      this.loading = true;
      listAward(this.queryParams).then(response => {
        this.awardList = this.handleTree(response.data, "id", "parentId");
        this.loading = false;
      });
    },
    /** 转换获奖基本信息数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.id,
        label: node.competitionName,
        children: node.children
      };
    },
	/** 查询获奖基本信息下拉树结构 */
    getTreeselect() {
      listAward().then(response => {
        this.awardOptions = [];
        const data = { id: 0, competitionName: '顶级节点', children: [] };
        data.children = this.handleTree(response.data, "id", "parentId");
        this.awardOptions.push(data);
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
        id: null,
        parentId: null,
        competitionId: null,
        competitionName: null,
        teamName: null,
        awardLevel: null,
        competitionType: null,
        studentName: null,
        studentId: null,
        teacherName: null,
        teacherId: null,
        competitionTime: null,
        awardTime: null,
        audit: null,
        auditInformation: null,
        version: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        delFlag: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.id) {
        this.form.parentId = row.id;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加获奖基本信息";
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
	  this.loading = true;
      this.reset();
      this.getTreeselect();
      if (row != null) {
        this.form.parentId = row.id;
      }
      getAward(row.id).then(response => {
	    this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改获奖基本信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
		  this.buttonLoading = true;
          if (this.form.id != null) {
            updateAward(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addAward(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除获奖基本信息编号为"' + row.id + '"的数据项？').then(() => {
        this.loading = true;
        return delAward(row.id);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    }
  }
};
</script>
