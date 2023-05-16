<template>
    <div style="height:100%">
      <!-- 顶部 -->
        <div>
            <el-row>
              <el-col :span="24" ><div class="grid-content bg-purple-light" style="height:20px"></div></el-col>
            </el-row>
          </div>

        <!-- 用于存放第一行的筛选按钮 -->
        <div class="juZhong">
            <el-row :gutter="20">
                <el-col :span="2"><div class="grid-content "></div></el-col>

                <!-- 用于存放等级的筛选框 -->
                <el-col :span="5"><div class="grid-content ">
                    <div>
                        赛事等级:
                    </div>
                    <div>
                        <el-select v-model="value" placeholder="请选择赛事等级">
                            <el-option
                              v-for="item in levelOption"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value">
                            </el-option>
                          </el-select>
                    </div>
                </div></el-col>

                <!-- 用于存放时间筛选框 -->
                <el-col :span="10"><div class="grid-content ">
                    <span>申请时间:</span>
                      <div class="block">
                        <el-date-picker
                          v-model="date1"
                          type="daterange"
                          align="right"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          :picker-options="dateOptions">
                        </el-date-picker>
                      </div>
                </div></el-col>

                <!-- 用于存放关键词的搜索 -->
                <el-col :span="5"><div class="grid-content">
                    <div>关键词搜索:</div>
                    <div>
                        <el-input clearable v-model="keyWord" placeholder="请键入关键词"></el-input>

                    </div>
                </div></el-col>

                <el-col :span="2"><div class="grid-content"></div></el-col>
            </el-row>

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
                        <el-table-column prop="name" label="赛事名称" width="150">
                        </el-table-column>
                        <el-table-column prop="term" label="立项届次" width="150">
                        </el-table-column>
                        <el-table-column prop="level" label="竞赛等级" width="150">
                        </el-table-column>
                        <el-table-column prop="responsiblePersonName" label="竞赛负责人" width="150">
                        </el-table-column>
                        <el-table-column prop="budget" label="申报经费（万元）" width="120">
                        </el-table-column>
                        <el-table-column prop="shenQingShiJian" label="申请时间" width="120">
                        </el-table-column>
                        <el-table-column fixed="right" label="查看详情" width="120">
                          <template slot-scope="scope">
                            <el-button type="text" @click="handleUpdate(scope.row)">编辑</el-button>
                          </template>
                        </el-table-column>
                      </el-table>

                      <!-- 最底部按钮，提交 退回 备注 -->
                      <div style="margin-top: 20px">
                        <div style="float:left">
                            <el-button @click="toggleSelection()">取消选择</el-button>
                        </div>

                        <div style="float: right">
                          <el-button type="primary" @click="submitOrRefuse(1)">提交</el-button>
                        </div>
                        <div style="float: right"><p style="width: 20px">&nbsp;</p></div>
                        <div style="float: right">
                          <el-button type="warning" @click="submitOrRefuse(0)">退回</el-button>
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

      <!-- 修改竞赛赛事基本信息对话框 -->
    <el-dialog title="修改竞赛赛事基本信息" :visible.sync="open1" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
        <el-form-item label="本年度拨款" prop="appropriation">
          <el-input v-model="form.appropriation" placeholder="请输入本年度拨款" />
        </el-form-item>
        <el-form-item label="个人赛限项" prop="personLimit">
          <el-input v-model="form.personLimit" placeholder="请输入个人赛限项" />
        </el-form-item>
        <el-form-item label="团队赛限项" prop="teamLimit">
          <el-input v-model="form.teamLimit" placeholder="请输入团队赛限项" />
        </el-form-item>
      <hr/>
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 提交和退回审核信息对话框-->
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
</template>

<script>
import {listCompetitionAudit, permitAudit, refuseAudit, updateAuditCompetition} from "@/api/dcims/competitionAudit";
import {getCompetition} from "@/api/dcims/competition"

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
          pageSize: 10
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
      console.log(this.competitionList);
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
                this.$modal.msgSuccess("退回选中竞赛成功");
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
