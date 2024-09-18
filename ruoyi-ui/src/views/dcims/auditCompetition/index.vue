<style lang="scss" scoped>

/* 自定义数字输入框append  */
.mo-input--number {
  border: 1px solid #DCDFE6;
  width: 60%;
  display: flex;
  border-radius: 4px;
  .el-input-number--mini{
    flex: 1;
  }
  ::v-deep .el-input__inner{
    border: none!important;
  }
}

.define-append{
  width: 40px;
  display: inline-block;
  background: #F5F7FA;
  padding: 0px 3px;
  border-left: none;
  height: 32px;
  line-height: 32px;
  color: #909399;
  font-size: 12px;
  text-align: center;
}
</style>
<template>
    <div style="height:100%">
      <!-- 待审核列表，上方的表格 -->
      <div class="juZhong">
        <h2>待提交列表</h2>
          <!-- 用于存放勾选需要审核的项目 -->
          <div>
              <el-row :gutter="20">
                <el-col :span="2"><div class="grid-content"></div></el-col>
                <!-- table 用于勾选项目 -->
                <el-col :span="20"
                  ><div class="grid-content">
                    <el-table
                      v-loading="loading"
                      ref="multipleTable"
                      :data="competitionList"
                      tooltip-effect="dark"
                      style="width: 100%"
                      max-height="500px"
                      @selection-change="handleSelectionChange"
                    >
                      <el-table-column type="selection" width="55"> </el-table-column>
                      <el-table-column prop="name" label="赛事名称" width="200">
                      </el-table-column>
                      <el-table-column prop="term" label="立项届次" width="100">
                      </el-table-column>
                      <el-table-column prop="level" label="竞赛类别" width="100">
                      </el-table-column>
                      <el-table-column prop="responsiblePersonName" label="竞赛负责人" width="100">
                      </el-table-column>
                      <el-table-column prop="collegeName" label="所属学院" width="200">
                      </el-table-column>
                      <el-table-column prop="budget" label="申报经费（万元）" width="120">
                        <template slot-scope="scope">
                          <span>{{scope.row.budget / 1 | rounding}}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="提交状态" align="center" fixed="right" prop="state">
                        <template slot-scope="scope">
                          <el-tag type="primary" v-if="scope.row.state == 0">待通过</el-tag>
                          <el-tag type="success" v-if="scope.row.state == 1">通过</el-tag>
                          <el-tag type="danger" v-if="scope.row.state == 2">被退回</el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column prop="createTime" label="申请时间" width="120">
                        <template slot-scope="scope">
                          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') || '无' }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column fixed="right" label="竞赛申报书" align="center" prop="oss.url" width="140">
                        <template slot-scope="scope">
                          <el-button v-if="(scope.row.oss != null)"
                          type="text"
                          @click.native="handleDownload(scope.row.oss.ossId)"
                          >在新窗口打开
                          </el-button>
                        </template>
                      </el-table-column>
                      <el-table-column fixed="right" label="集中授课安排表" align="center" prop="teachingHoursAttachmentOss.url" width="140">
                        <template slot-scope="scope">
                          <el-button v-if="(scope.row.teachingHoursAttachment != null)"
                                     type="text"
                                     @click.native="handleDownload(scope.row.teachingHoursAttachmentOss.ossId)"
                          >在新窗口打开
                          </el-button>
                          <el-button v-if="(scope.row.teachingHoursAttachment == null)"
                                     type="text"
                                     disabled="true"
                          >无
                          </el-button>
                        </template>
                      </el-table-column>
                      <el-table-column fixed="right" label="查看详情" min-width="100">
                        <template slot-scope="scope">
                          <el-button type="text" @click="handleUpdate(scope.row)">查看详情</el-button>
                        </template>
                      </el-table-column>
                    </el-table>

                    <!-- 最底部按钮，提交 退回 备注 -->
                    <div style="margin-top: 20px">
                      <div style="float:left">
                          <el-button @click="toggleSelection()">取消选择</el-button>
                      </div>

                      <div style="float: right">
                        <el-tooltip class="item" effect="dark" content="请先勾选需要通过并提交的竞赛" placement="top" :disabled="!ids.length == 0">
                          <el-button type="primary" :disabled="ids.length == 0" @click="submitOrRefuse(1)">通过并提交</el-button>
                        </el-tooltip>
                      </div>
                      <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                      <div style="float: right">
                        <el-tooltip class="item" effect="dark" content="请先勾选需要退回修改的竞赛" placement="top" :disabled="!ids.length == 0">
                          <el-button type="warning" :disabled="ids.length == 0" @click="submitOrRefuse(0)">退回修改</el-button>
                        </el-tooltip>
                      </div>
                      <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                      <div style="float: right">
                          <!-- <el-button type="info" @click="note">备注</el-button> -->
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
                v-loading="loading2"
                :data="competitionProcessingList"
                tooltip-effect="dark"
                style="width: 100%"
                max-height="500px"
              >
                <el-table-column prop="name" label="赛事名称" width="200">
                </el-table-column>
                <el-table-column prop="annual" label="赛事年份" width="200">
                </el-table-column>
                <el-table-column prop="responsiblePersonName" label="竞赛负责人" width="200">
                </el-table-column>
                <el-table-column label="提交状态" align="center" fixed="right" prop="state">
                  <template slot-scope="scope">
                    <el-tag type="primary" v-if="scope.row.state == 0">等待教务处通过</el-tag>
                    <el-tag type="success" v-if="scope.row.state == 1">通过</el-tag>
                    <el-tag type="danger" v-if="scope.row.state == 2">竞赛被退回，等待负责人修改</el-tag>
                  </template>
                </el-table-column>
                <el-table-column fixed="right" label="竞赛申报书" align="center" prop="oss.url" width="160">
                  <template slot-scope="scope">
                    <el-button v-if="(scope.row.oss != null)"
                               type="text"
                               @click.native="handleDownload(scope.row.oss.ossId)"
                    >在新窗口打开
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div></el-col>
            <el-col :span="2"><div class="grid-content"></div></el-col>
          </el-row>
        </div>
      </div>

      <!-- 查看竞赛详情对话框 -->
    <el-dialog title="修改竞赛赛事基本信息" :visible.sync="open1" width="500px" append-to-body>
      <!--  教务处看到的 -->
      <el-form v-if="userInfo == 'AcademicAffairsOffice' || userInfo == 'admin'" ref="form" :model="form" :rules="rules" label-width="80px">
      <div>
        <h2>以下是校级管理员（教务处）需要填写的信息</h2>
        <el-form-item label="赛事类别" prop="level">
          <el-select v-model="form.level" placeholder="请选择赛事类别">
            <el-option
              v-for="dict in dict.type.dcims_competition_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="本年度追加经费" prop="appropriation">
          <div  class="mo-input--number">
            <el-input-number v-model="form.appropriation" controls-position="right" :precision="2" :step="0.1" :min="0" :max="100"></el-input-number>
            <div class="define-append">万元</div>
          </div>
        </el-form-item>
        <el-form-item label="个人赛限项" prop="personLimit">
          <el-input v-model="form.personLimit" placeholder="请输入个人赛限项" />
        </el-form-item>
        <el-form-item label="团队赛限项" prop="teamLimit">
          <el-input v-model="form.teamLimit" placeholder="请输入团队赛限项" />
        </el-form-item>
      <hr/>
      </div>
      <h2>以下是竞赛基本信息</h2>
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
        <el-form-item label="竞赛负责人工号" prop="responsiblePersonId" >
          <el-input v-model="form.responsiblePersonId" placeholder="请输入竞赛负责人工号" :disabled="true"/>
        </el-form-item>
        <el-form-item label="竞赛负责人" prop="responsiblePersonName">
          <el-input v-model="form.responsiblePersonName" placeholder="请输入竞赛负责人" :disabled="true"/>
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="所属学院" :disabled="true"/>
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
          <div  class="mo-input--number">
            <el-input-number v-model="form.budget" controls-position="right" :precision="2" :step="0.1" :min="0" :max="100"></el-input-number>
            <div class="define-append">万元</div>
          </div>
        </el-form-item>
        <el-form-item label="获奖目标" prop="goal">
          <el-input v-model="form.goal" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="赛事简介" prop="introduction">
          <el-input v-model="form.introduction" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="集中授课安排表" prop="teachingHoursAttachment">
          <file-upload v-model="form.teachingHoursAttachment"/>
        </el-form-item>
        <el-form-item label="竞赛申报书" prop="attachment">
          <file-upload v-model="form.attachment"/>
        </el-form-item>
      </el-form>

      <!--  学院竞赛负责人看到的 -->
      <el-form v-if="userInfo.indexOf('AcademyCompetitionHead') != -1" ref="form" :model="form" :rules="academyRules" label-width="80px">
      <h2>以下是竞赛基本信息</h2>
        <el-form-item label="赛事名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入赛事名称"  disabled="true"/>
        </el-form-item>
        <el-form-item label="往届名称" prop="pastName">
          <el-input v-model="form.pastName" placeholder="请输入往届名称"  disabled="true"/>
        </el-form-item>
        <el-form-item label="赛事官网" prop="website">
          <el-input v-model="form.website" placeholder="请输入赛事官网"  disabled="true"/>
        </el-form-item>
        <el-form-item label="赛事届次" prop="term">
          <el-input v-model="form.term" placeholder="请输入赛事届次"  disabled="true"/>
        </el-form-item>
        <el-form-item label="赛事年份" prop="annual">
          <el-input v-model="form.annual" placeholder="请输入赛事年份"  disabled="true"/>
        </el-form-item>
        <el-form-item label="主办单位" prop="organizer">
          <el-input v-model="form.organizer" placeholder="请输入主办单位"  disabled="true"/>
        </el-form-item>
        <el-form-item label="竞赛负责人工号" prop="responsiblePersonId">
          <el-input v-model="form.responsiblePersonId" placeholder="请输入竞赛负责人工号" :disabled="true"/>
        </el-form-item>
        <el-form-item label="竞赛负责人" prop="responsiblePersonName">
          <el-input v-model="form.responsiblePersonName" placeholder="请输入竞赛负责人" :disabled="true"/>
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="所属学院" :disabled="true"/>
        </el-form-item>
        <el-form-item label="校内选拔时间" prop="innerTime">
          <el-date-picker clearable
            v-model="form.innerTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled="true"
            placeholder="请选择校内选拔时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="省赛时间" prop="provinceTime">
          <el-date-picker clearable
            v-model="form.provinceTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled="true"
            placeholder="请选择省赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="国赛时间" prop="nationalTime">
          <el-date-picker clearable
            v-model="form.nationalTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled="true"
            placeholder="请选择国赛时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="本年度申报经费" prop="budget">
          <el-input v-model="form.budget" placeholder="请输入本年度申报经费"  disabled="true"/>
        </el-form-item>
        <el-form-item label="获奖目标" prop="goal">
          <el-input v-model="form.goal" type="textarea" placeholder="请输入内容"  disabled="true"/>
        </el-form-item>
        <el-form-item label="赛事简介" prop="introduction">
          <el-input v-model="form.introduction" type="textarea" placeholder="请输入内容"  disabled="true"/>
        </el-form-item>
        <el-form-item label="集中授课安排表" prop="teachingHoursAttachment">
          <file-upload v-model="form.teachingHoursAttachment" disabled="true"/>
        </el-form-item>
        <el-form-item label="竞赛申报书" prop="attachment">
          <file-upload v-model="form.attachment" disabled="true"/>
        </el-form-item>
      </el-form>


      <!--  对话框下方公共部分 -->
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 提交和退回审核信息对话框-->
    <el-dialog :title="title" :visible.sync="open2" width="500px" append-to-body>
    <el-form ref="form2" :model="submitt" label-width="100px">
        <el-input v-model="submitt.reason" type="textArea" placeholder="备注提交或退回原因（非必填）" />
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button :loading="buttonLoading" type="primary" @click="submitForm2">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
    </el-dialog>
    </div>
</template>

<script>
import {listCompetitionAudit, listCompetitionInProcessing, permitAudit, refuseAudit, updateAuditCompetition} from "@/api/dcims/competitionAudit";
import {getCompetition} from "@/api/dcims/competition"
import {getInfo} from "@/api/login"
import download from '@/plugins/download.js';

  export default {
    name:"liXiangShenHe",
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
      // 第二个表格的数据
      competitionProcessingList: [],
      // 第二个表格是否加载
      loading2: true,
      // 第二个表格数据总数
      totalProcessing: 0,
      // 弹出层标题
      title: "",
      // 是否显示弹出层1
      open1: false,
      // 是否显示弹出层2
      open2: false,
      // 提交或退回标志
      flag: 0,
      // 用户信息
      userInfo: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 500
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
      },
      // 表单校验2
      academyRules: {
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
      },
      // 提交与退回表单
      submittForm: [],
      // 单个提交与退回信息
      submitt: {
        competitionId: undefined,// 竞赛id
        reason: undefined,// 退回或提交原因
        nextTeacherId: undefined// 下一级审核教师的工号
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
      this.getUserInfo();
      this.getListInProcessing();
    },
    filters: {
      rounding (value) {
        return value.toFixed(2)
      }
    },
    methods: {
      /** 查询竞赛赛事基本信息列表 */
      getList() {
        this.loading = true;
        listCompetitionAudit(this.queryParams).then(response => {
          this.competitionList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      /** 查询已经通过审核的竞赛，现在的状态 **/
      getListInProcessing() {
        this.loading2 = true;
        listCompetitionInProcessing(this.queryParams).then(response => {
          this.competitionProcessingList = response.rows;
          this.totalProcessing = response.total;
          this.loading2 = false;
        })
      },
      /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getCompetition(id).then(response => {
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
          collegeName: undefined,
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
          competitionId: undefined,// 竞赛id
          reason: undefined,// 退回或提交原因
          nextTeacherId: undefined// 下一级审核教师的工号
        };
        this.resetForm("form");
        this.resetForm("form2");
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
      this.reset();
    },
    /** 提交竞赛信息按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateAuditCompetition(this.form).then(response => {
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
    /** 提交审核信息按钮 */
    submitForm2() {
            this.buttonLoading = true;
            // 对审核信息进行封装
            for(let i = 0; i < this.ids.length; i++){
              const submitt1 = Object.assign({}, this.submitt);
              submitt1.competitionId = this.ids[i];
              this.submittForm.push(submitt1);
            }
            console.log(this.submittForm);
            if(this.flag){
              // 判断是否填写了竞赛类别
              if(this.userInfo == "AcademicAffairsOffice" && this.validateLevel() == false){
                console.log("不允许提交")
                this.buttonLoading = false;
                this.$modal.msgError("选中的竞赛中存在未确定类别的竞赛，不允许提交！");
                this.submittForm = [];
                return;
              }
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
              this.getListInProcessing();
            });
            }else{
              // 执行退回审核逻辑
              refuseAudit(this.submittForm).then(response => {
                this.$modal.msgSuccess("退回选中竞赛成功");
                this.submittForm = [];
                this.open2 = false;
                this.getList();
              }).catch((err) => {
                this.submittForm = [];
              }).finally(() => {
              this.buttonLoading = false;
              this.getListInProcessing();
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
    /** 获取角色权限 */
    getUserInfo(){
      getInfo().then(response =>{
        if (response.data.roles.indexOf('AcademyCompetitionHead') != -1){
          this.userInfo = response.data.roles[1];
        }else{
          this.userInfo = response.data.roles[0];
        }
      })
    },
    /** 判断审核通过竞赛是否添加了类别(ABC) */
    validateLevel(){
      let flag = true;
      this.submittForm.forEach((item) =>{
        this.competitionList.forEach((com) => {
          if(com.id == item.competitionId){
            // console.log("2:"+ com+ item)
            // console.log("2:"+ com.id+ item.competitionId)
            // console.log("2:"+ com.level)
            if(com.level == null || com.level.trim() === ''){
              console.log("出现空类型竞赛" + com.level)
              flag = false
            }
          }
        })
      })
      console.log(flag);
      if (flag == false){
        return flag;
      }
      return true;
    },
    /** 打开新窗口 */
    openNewTab(url) {
      window.open(url, '_blank');
    },
      // 下载按钮操作
      handleDownload(ossId) {
        download.oss(ossId);
      }
    }
  }




</script>

<style>
.juZhong{
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
