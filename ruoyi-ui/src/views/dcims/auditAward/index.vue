<template>
  <div style="height:100%">
    <!-- 顶部 -->
    <div>
      <el-row>
        <el-col :span="24" ><div class="grid-content bg-purple-light" style="height:20px"></div></el-col>
      </el-row>
    </div>


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
          placeholder="请输入赛事年份"
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

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 导出相关文档-->
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
    <el-col>
      <el-tag :span="1" type="danger">请在上方进行筛选条件选择(下方复选框无效)</el-tag>
    </el-col>

    <!-- 用于存放第一行的筛选按钮 -->
    <div class="juZhong">
      <h2>待提交列表</h2>
      <!-- <el-row :gutter="20">
          <el-col :span="2"><div class="grid-content "></div></el-col>


          <el-col :span="5"><div class="grid-content ">
              <div>
                  赛事等级:
              </div>
              <div>
                  <el-select placeholder="请选择赛事等级">
                      <el-option>A</el-option>
                      <el-option>B</el-option>
                      <el-option>C</el-option>
                    </el-select>
              </div>
          </div></el-col>


          <el-col :span="10"><div class="grid-content ">
              <span>申请时间:</span>
                <div class="block">

                  <el-date-picker
                    type="daterange"
                    align="right"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :picker-options="dateOptions">
                  </el-date-picker>
                </div>
          </div></el-col>


          <el-col :span="5"><div class="grid-content">
              <div>关键词搜索:</div>
              <div>
                  <el-input clearable placeholder="请键入关键词"></el-input>

              </div>
          </div></el-col>

          <el-col :span="2"><div class="grid-content"></div></el-col>
      </el-row>-->

      <!-- 用于存放勾选需要审核的获奖信息 -->
      <div>
          <el-row :gutter="20">
            <el-col :span="2"><div class="grid-content"></div></el-col>

            <!-- table 用于勾选项目 -->
            <el-col :span="20"
              ><div class="grid-content">
                <el-table
                  v-loading="loading"
                  ref="multipleTable"
                  :data="teamList"
                  tooltip-effect="dark"
                  style="width: 100%"
                  max-height="600px"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column type="selection" width="55"> </el-table-column>
                  <el-table-column prop="competition.name" label="赛事名称" width="200">
                  </el-table-column>
                  <el-table-column prop="name" label="团队名称" width="100">
                  </el-table-column>
                  <el-table-column prop="competitionType" label="比赛类型" width="80">
                    <template slot-scope="scope">
                     <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="awardLevel" label="获奖等级" width="110">
                    <template slot-scope="scope">
                     <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="studentName" label="参赛学生" width="130">
                  </el-table-column>
                  <el-table-column prop="teacherName" label="指导教师" width="130">
                  </el-table-column>
                  <el-table-column prop="awardTime" label="获奖时间" width="120">
                  </el-table-column>
                  <el-table-column prop="supportMaterial" label="佐证材料" width="100">
                    <template slot-scope="scope">
                      <el-tag type="primary" v-if="scope.row.supportMaterial != null">有</el-tag>
                      <el-tag type="error" v-else>无</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column fixed="right" label="查看详情" min-width="120" align="center">
                    <template slot-scope="scope">
                      <el-button type="text" @click="checkDetail(scope.row)">查看详情</el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <!-- 最底部按钮，提交 退回 备注 -->
                <div style="margin-top: 20px">
                  <div style="float:left">
                      <el-button @click="toggleSelection()">取消选择</el-button>
                  </div>

                  <div style="float: right">
                    <el-button type="primary" @click="submitOrRefuse(1)">通过并提交</el-button>
                  </div>
                  <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                  <div style="float: right">
                    <el-button type="warning" @click="submitOrRefuse(0)">退回修改</el-button>
                  </div>
                  <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                  <div style="float: right">

                  </div>
                </div>
              </div></el-col
            >
            <el-col :span="2"><div class="grid-content"></div></el-col>
          </el-row>
      </div>
  </div>


    <!-- 流程跟踪列表，下方的表格 -->
    <div class="juZhong" style="margin-top: 50px">
      <h2>流程跟踪</h2>
      <div>
        <el-row :gutter="20">
          <el-col :span="2"><div class="grid-content"></div></el-col>
          <!-- table 用于勾选项目 -->
          <el-col :span="20"
          ><div class="grid-content">
            <el-table
              v-loading="loading"
              ref="multipleTable"
              :data="teamProcessingList"
              tooltip-effect="dark"
              style="width: 100%"
              max-height="600px"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55"> </el-table-column>
              <el-table-column prop="competition.name" label="赛事名称" width="200">
              </el-table-column>
              <el-table-column prop="name" label="团队名称" width="100">
              </el-table-column>
              <el-table-column prop="competitionType" label="比赛类型" width="80">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
                </template>
              </el-table-column>
              <el-table-column prop="awardLevel" label="获奖等级" width="110">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
                </template>
              </el-table-column>
              <el-table-column prop="studentName" label="参赛学生" width="130">
              </el-table-column>
              <el-table-column prop="teacherName" label="指导教师" width="130">
              </el-table-column>
<!--              <el-table-column prop="" label="退回原因" width="130">-->
<!--              </el-table-column>-->
              <el-table-column label="提交状态" align="center" fixed="right" prop="audit">
                <template slot-scope="scope">
                  <el-tag type="primary" v-if="scope.row.audit == 0">等待负责人修改材料</el-tag>
                  <el-tag type="success" v-if="scope.row.audit == 1">等待教务处通过</el-tag>
                  <el-tag type="danger" v-if="scope.row.audit == 2">通过</el-tag>
                  <el-tag type="danger" v-if="scope.row.audit == 3">材料被退回，等待负责人修改</el-tag>
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="查看详情" min-width="120" align="center">
                <template slot-scope="scope">
                  <el-button type="text" @click="checkDetail(scope.row)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div></el-col>
          <el-col :span="2"><div class="grid-content"></div></el-col>
        </el-row>
      </div>
    </div>




  <!-- 提交和退回审核信息对话框-->
  <div>
    <el-dialog :title="title" :visible.sync="open2" width="500px" append-to-body>
    <el-form ref="form2" :model="submitt" label-width="100px">
        <el-input v-model="submitt.reason" type="textArea" placeholder="备注原因" />
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button :loading="buttonLoading" type="primary" @click="submitForm2">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
    </el-dialog>
  </div>

  <!-- 查看详情对话框 -->
  <el-dialog title="查看详情" :visible.sync="openDetail" width="500px" append-to-body>
    <el-descriptions title="团队信息">
    <el-descriptions-item label="所属竞赛">{{ this.detailForm.competition.name }}</el-descriptions-item>
    <el-descriptions-item label="队伍名称">{{ this.detailForm.name }}</el-descriptions-item>
    <el-descriptions-item label="作品名称">
      <span v-if="this.detailForm.worksName != null">
        {{ this.detailForm.worksName }}
      </span>
      <span v-else>
        无
      </span>
    </el-descriptions-item>
    <el-descriptions-item label="比赛类型">
      <dict-tag :options="dict.type.dcims_award_type" :value="detailForm.competitionType"/>
    </el-descriptions-item>
    <el-descriptions-item label="奖项等级">
      <dict-tag :options="dict.type.dcims_award_level" :value="detailForm.awardLevel"/>
    </el-descriptions-item>
    <el-descriptions-item label="指导教师">{{ this.detailForm.teacherName }}</el-descriptions-item>
    <el-descriptions-item label="参赛学生">{{ this.detailForm.studentName }}</el-descriptions-item>
    <el-descriptions-item label="是否存在更高级奖项" :span="2">
      <span v-if="this.detailForm.advancedAwardNumber != null">
        <el-tag size="small">是</el-tag>
      </span>
      <span v-else>
        <el-tag size="small">否</el-tag>
      </span>
    </el-descriptions-item>
    <el-descriptions-item label="佐证材料" v-if="openDetail == true">
          <ImagePreview
            v-if="checkFileSuffix(detailForm.fileSuffix) && openDetail == true"
            :width=300 :height=300
            :src="this.detailForm.supportMaterialURL[0]"
            :preview-src-list="[this.detailForm.supportMaterialURL[0]]"/>
          <span v-if="!checkFileSuffix(detailForm.fileSuffix) && openDetail == true">
            {{ this.detailForm.originalName[0] }}
            <el-button size="small">
              <a :href="this.detailForm.supportMaterialURL[0]" target="_blank" style="text-decoration: none">
		          下载
	            </a>
            </el-button>
          </span>
          <el-button v-if="(detailForm.supportMaterialURL[0] != null)" type="text" @click.native="openNewTab(detailForm.supportMaterialURL[0])">在新窗口打开</el-button>
    </el-descriptions-item>
    </el-descriptions>
  </el-dialog>


  </div>
</template>

<script>
import {listTeamAudit, listTeamInProcessing, permitAudit, refuseAudit} from "@/api/dcims/teamAudit";
import {getTeam, updateTeam,listTeam} from "@/api/dcims/team"
import { listByIds } from "@/api/system/oss"
import {listCompetitionInProcessing} from "@/api/dcims/competitionAudit";
import download from '@/plugins/download.js';

  export default {
    name:"tuanDuiHuoJiangShenHe",
    dicts: ['dcims_award_type', 'dcims_award_level'],
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
        // 第二个表格数据总数
        totalProcessing: 0,
      // 竞赛赛事基本信息表格数据
      teamList: [],
      // 第二个表格的数据
      teamProcessingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层1
      open1: false,
      // 是否显示弹出层2
      open2: false,
      // 提交或退回标志
      flag: 0,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 500,
          competitionName: undefined,
          name: undefined,
          annual: undefined,
          competitionType: undefined,
          awardLevel: undefined,
          studentName: undefined,
          teacherName: undefined,
          awardTime: undefined,
          supportMaterial: undefined,
          teacherId: undefined,
          next_audit_id: this.$store.state.user.name,
          audit: '1'
        },
        // 表单参数
        form: {},
        // 提交与退回表单
        submittForm: [],
        // 单个提交与退回信息
        submitt: {
          teamId: undefined,// 团队id
          reason: undefined,// 退回或提交原因
          nextTeacherId: undefined// 下一级审核教师的工号
        },
        // 查看详情对话框是否显示
        openDetail: false,
        // 查看详情内容
        detailForm: {
          fileSuffix: [],
          competition: {
            name: undefined
          }
        },
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
    created() {
      this.getList();
      this.getListInProcessing();
      console.log(this.teamList);
    },
    methods: {
      /** 查询竞赛赛事基本信息列表 */
      getList() {
        this.loading = true;
        listTeamAudit(this.queryParams).then(response => {
          this.teamList = response.rows;
          this.total = response.total;

          console.log(this.teamList)
          this.teamList.forEach(e => {

            e.teacherName = e.teacherName.join("，");
            e.studentName = e.studentName.join("，");
          })

          this.loading = false;
        });
      },

      getListTwo() {
        this.loading = true;
        listTeam(this.queryParams).then(response => {
          this.teamList = response.rows;
          this.total = response.total;

          console.log(this.teamList)
          this.teamList.forEach(e => {

            e.teacherName = e.teacherName.join("，");
            e.studentName = e.studentName.join("，");
          })

          this.loading = false;
        });
      },
      /** 查询已经通过审核的获奖信息，现在的状态 **/
      getListInProcessing() {
        this.loading = true;
        listTeamInProcessing(this.queryParams).then(response => {
          this.teamProcessingList = response.rows;
          this.totalProcessing = response.total;

          this.teamProcessingList.forEach(e => {
            e.teacherName = e.teacherName.join("，");
            e.studentName = e.studentName.join("，");
          })

          this.loading = false;
        })
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getTeam(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open1 = true;
        });
      },
      // 表单重置
      reset() {
        this.form = {
          id: undefined,
          deptId: undefined,
          userId: undefined,
          orderNum: undefined,
          name: undefined,
          level: undefined,
          pastName: undefined,
          term: undefined,
          annual: undefined,
          organizer: undefined,
          responsiblePersonId: undefined,
          responsiblePersonName: undefined,
          innerTime: undefined,
          provinceTime: undefined,
          nationalTime: undefined,
          stopTime: undefined,
          concentrationOfTeachingHours: undefined,
          budget: undefined,
          goal: undefined,
          appropriation: undefined,
          moneyAggregate: undefined,
          workloadAggregate: undefined,
          state: undefined,
          version: undefined,
          createTime: undefined,
          createBy: undefined,
          updateTime: undefined,
          updateBy: undefined,
          delFlag: undefined
        };
        this.submitt = {
          teamId: undefined,// 竞赛id
          reason: undefined,// 退回或提交原因
          nextTeacherId: undefined// 下一级审核教师的工号
        };
        this.resetForm("form");
        this.resetForm("form2");
      },
       /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getListTwo();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.getList();
    },
     // 多选框选中数据
     handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    // 取消按钮
    cancel() {
      this.open1 = false;
      this.open2 = false;
      this.openDetail = false;
      this.reset();
      this.detailForm = {};
    },
    /** 提交竞赛信息按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateTeam(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open1 = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {

          }
        }
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
    /** 提交审核信息按钮 */
    submitForm2() {
            this.buttonLoading = true;
            // 对审核信息进行封装
            for(let i = 0; i < this.ids.length; i++){
              const submitt1 = Object.assign({}, this.submitt);
              submitt1.teamId = this.ids[i];
              this.submittForm.push(submitt1);
            }
            console.log(this.submittForm);
            if(this.flag){
              // 执行通过审核逻辑
              permitAudit(this.submittForm).then(response => {
                this.$modal.msgSuccess("通过选中竞赛成功");
                this.submittForm = [];
                this.open2 = false;
                this.getList();
              }).catch((err) => {
                this.submittForm = [];
              }).finally(() => {
              this.buttonLoading = false;
            });
            }else{
              // 执行退回审核逻辑
              refuseAudit(this.submittForm).then(response => {
                this.$modal.msgError("退回选中竞赛成功");
                this.submittForm = [];
                this.open2 = false;
                this.getList();
              }).catch((err) => {
                this.submittForm = [];
              }).finally(() => {
              this.buttonLoading = false;
            });
            }
    },
    /** 激活竞赛审核信息窗口 */
    submitOrRefuse(flag){
      this.loading = true;
      this.reset();
      // flag为0，表示退回；flag为1，表示通过
      this.flag = flag;
      this.loading = false;
      this.open2 = true;
    },
    /** 查看详情 */
    checkDetail(row){
      this.detailForm = row;
      this.detailForm.supportMaterialURL = [];
      this.detailForm.fileSuffix = [];
      this.detailForm.originalName = [];
      listByIds(this.detailForm.supportMaterial).then(response => {
        response.data.forEach(entity => {
          this.detailForm.supportMaterialURL.push(entity.url);
          this.detailForm.fileSuffix.push(entity.fileSuffix);
          this.detailForm.originalName.push(entity.originalName);
          this.detailForm.competition.push(entity.competition);
        })
      }).finally(() => {
        this.openDetail = true;
      });
    },
    /** 检查附件是否为图片 */
    checkFileSuffix(fileSuffix) {
      let arr = [".png", ".jpg", ".jpeg", ".webp"];
      return arr.some(type => {
        return fileSuffix.indexOf(type) > -1;
      });
    },
    /** 打开新窗口 */
    openNewTab(url) {
      window.open(url, '_blank');
    },
     returnWarn() {
const h = this.$createElement;
this.$prompt(h(
	'div', null, [
		h('div', { style: "display:flex;align-items: center" }, [
			h('span',{style:"width: 100px"}, '审核人工号:'),
			h('el-input',null)
		]),
      ]),
		'退回提示',
		{
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputPlaceholder: '退回备注',
			inputType:'textarea'
		}).then(({ value }) => {
			//  todo .....
		}).catch();
    },
    submitWarn() {

const h = this.$createElement;
this.$prompt(h(
	'div', null, [
		h('div', { style: "display:flex;align-items: center" }, [
			h('span',{style:"width: 100px"}, '审核人工号:'),
			h('el-input',null)
		]),
      ]),
		'上交提示',
		{
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputPlaceholder: '备注消息',
			inputType:'textarea'
		}).then(({ value }) => {
			//  todo .....
      console.log(value);
      permitAudit().then(response => {

      })
		}).catch();

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
      }

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
