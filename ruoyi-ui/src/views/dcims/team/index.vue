<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="竞赛名称" prop="competitionName">
        <el-input
          v-model="queryParams.competitionName"
          placeholder="请输入竞赛名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事年份" prop="annual">
        <el-input
          v-model="queryParams.annual"
          placeholder="请输入赛事年份（如2024）"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="队伍名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入队伍名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      <el-form-item label="指导教师工号" prop="teacherId">
        <el-input
          v-model="queryParams.teacherId"
          placeholder="请输入指导教师工号"
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
      <el-form-item label="参赛学生学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入参赛学生学号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛学生姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入参赛学生姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交状态" prop="audit">
        <el-select v-model="queryParams.audit" placeholder="请选择提交状态" clearable>
          <el-option
            v-for="dict in dict.type.dcims_declare_award_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dcims:team:add']"
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
          v-hasPermi="['dcims:team:edit']"
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
          v-hasPermi="['dcims:team:remove']"
        >删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:team:export']"
        >下载获奖信息表</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport2"
          v-hasPermi="['dcims:team:export']"
        >下载佐证材料</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="teamList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="竞赛名称" align="center" prop="competition.name" />
      <el-table-column label="队伍名称" align="center" prop="name" />
      <el-table-column label="作品名称" align="center" prop="worksName" />
      <el-table-column label="比赛类型" align="center" prop="competitionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
        </template>
      </el-table-column>
      <el-table-column label="奖项等级" align="center" prop="awardLevel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="指导教师工号" align="center" prop="teacherId" />
      <el-table-column label="指导教师姓名" align="center" prop="teacherName" />
      <el-table-column label="参赛学生学号" align="center" prop="studentId" />
      <el-table-column label="参赛学生姓名" align="center" prop="studentName" />
      <el-table-column label="佐证材料" align="center" prop="oss.url">
        <template slot-scope="scope">
          <!-- <ImagePreview
            v-if="previewListResource && scope.row.oss != null && checkFileSuffix(scope.row.oss.fileSuffix)"
            :width=100 :height=100
            :src="scope.row.oss.url"
            :preview-src-list="[scope.row.oss.url]"/>
          <span v-text="scope.row.oss.url"
                v-if="(scope.row.oss != null) && (!checkFileSuffix(scope.row.oss.fileSuffix) || !previewListResource)"/> -->
                <i class="el-icon-document" style="font-size: 20px;"></i>
                <br/>
                <el-button v-if="(scope.row.oss != null)" type="text" @click.native="openNewTab(scope.row.oss.url)">在新窗口打开</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="removeReward(scope.row)"
            v-hasPermi="['dcims:teamAudit:removeOne']"
          >退回获奖信息</el-button>
        </template>
      </el-table-column>
      <!-- <el-table-column label="审核状态" align="center" prop="audit">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_declare_award_status" :value="scope.row.audit"/>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:team:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:team:remove']"
          >删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改参赛团队对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="竞赛id" prop="competitionId">
          <el-input v-model="form.competitionId" placeholder="请输入竞赛id" />
        </el-form-item>
        <el-form-item label="队伍名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入队伍名称" />
        </el-form-item>
        <el-form-item label="作品名称" prop="name">
            <el-input v-model="form.worksName" :disabled="worksNameIsNull" placeholder="请输入作品名称" />
            <el-checkbox label="作品名称" @change="changeWorksName()">无</el-checkbox>
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
        <el-form-item label="指导教师工号" prop="teacherId">
          <el-input v-model="form.teacherId" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="指导教师姓名" prop="teacherName">
          <el-input v-model="form.teacherName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="参赛学生学号" prop="studentId">
          <el-input v-model="form.studentId" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="参赛学生姓名" prop="studentName">
          <el-input v-model="form.studentName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="比赛时间" prop="competitionTime">
          <el-date-picker clearable
            v-model="form.competitionTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择比赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker clearable
            v-model="form.awardTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择获奖时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="佐证材料" prop="supportMaterial">
          <file-upload v-model="form.supportMaterial"/>
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
import { listTeam, getTeam, delTeam, addTeam, updateTeam, removeOne } from "@/api/dcims/team";

export default {
  name: "Team",
  dicts: ['dcims_award_type', 'dcims_award_level', 'dcims_declare_award_status'],
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
      // 参赛团队表格数据
      teamList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否不填写作品名称
      worksNameIsNull: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        competitionName: undefined,
        annual: undefined,
        name: undefined,
        competitionType: undefined,
        awardLevel: undefined,
        teacherId: undefined,
        teacherName: undefined,
        studentId: undefined,
        studentName: undefined,
        audit: '2',
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
        competitionId: [
          { required: true, message: "竞赛id不能为空", trigger: "blur" }
        ],
        teacherId: [
          { required: true, message: "指导教师工号不能为空", trigger: "blur" }
        ],
        teacherName: [
          { required: true, message: "指导教师姓名不能为空", trigger: "blur" }
        ],
        studentId: [
          { required: true, message: "参赛学生学号不能为空", trigger: "blur" }
        ],
        studentName: [
          { required: true, message: "参赛学生姓名不能为空", trigger: "blur" }
        ],
        supportMaterial: [
          { required: true, message: "佐证材料不能为空", trigger: "blur" }
        ],
      },
      // 预览列表图片
      previewListResource: true,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询参赛团队列表 */
    getList() {
      this.loading = true;
      listTeam(this.queryParams).then(response => {
        this.teamList = response.rows;
        this.total = response.total;
        console.log(this.teamList);

        this.teamList.forEach(element => {
        element.teacherId = element.teacherId.join();
        element.studentId = element.studentId.join();
        element.teacherName = element.teacherName.join();
        element.studentName = element.studentName.join();
        });

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
        competitionId: undefined,
        name: undefined,
        competitionType: undefined,
        awardLevel: undefined,
        teacherId: undefined,
        teacherName: undefined,
        studentId: undefined,
        studentName: undefined,
        advancedAwardNumber: undefined,
        competitionTime: undefined,
        awardTime: undefined,
        supportMaterial: undefined,
        audit: undefined,
        nextAuditId: undefined,
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
      this.title = "添加参赛团队";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getTeam(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改参赛团队";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        // 是否填写作品名称
        if (this.worksNameIsNull){
          this.form.worksName = undefined;
        }
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateTeam(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addTeam(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除参赛团队编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delTeam(ids);
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
      this.download('dcims/team/export', {
        ...this.queryParams
      }, `获奖信息表${new Date().getTime()}.xlsx`)
    },
    /** 导出按钮操作 */
    handleExport2() {
      this.download('dcims/team/download', {
        ...this.queryParams
      }, `获奖佐证材料附件${new Date().getTime()}.zip`)
    },
    /** 是否填写作品名称 */
    changeWorksName(){
      this.worksNameIsNull = !this.worksNameIsNull;
    },
    checkFileSuffix(fileSuffix) {
      let arr = ["png", "jpg", "jpeg"];
      return arr.some(type => {
        return fileSuffix.indexOf(type) > -1;
      });
    },
    /** 打开新窗口 */
    openNewTab(url) {
      window.open(url, '_blank');
    },
    /** 退回获奖信息 */
    removeReward(row) {
      const configIds = row.configId || this.ids;
      const competitionName = row.competition.name
      const teamName = row.name
      this.$modal.confirm('是否确认退回竞赛：' + competitionName + ' 的 ' + teamName + ' 获奖信息？').then(() => {
          this.loading = true;
          return removeOne(row.id);
        }).then(() => {
          this.getList();
          this.loading = false;
          this.$modal.msgSuccess("退回成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
    }
  }
};
</script>
