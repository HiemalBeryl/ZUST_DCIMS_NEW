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
                  <el-select placeholder="请选择赛事等级">
                      <el-option>A</el-option>
                      <el-option>B</el-option>
                      <el-option>C</el-option>
                    </el-select>
              </div>
          </div></el-col>

          <!-- 用于存放时间筛选框 -->
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

          <!-- 用于存放关键词的搜索 -->
          <el-col :span="5"><div class="grid-content">
              <div>关键词搜索:</div>
              <div>
                  <el-input clearable="true" placeholder="请键入关键词"></el-input>

              </div>
          </div></el-col>

          <el-col :span="2"><div class="grid-content"></div></el-col>
      </el-row>

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
                  max-height="500px"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column type="selection" width="55"> </el-table-column>
                  <el-table-column prop="competitionId" label="赛事id" width="150">
                  </el-table-column>
                  <el-table-column prop="name" label="团队名称" width="150">
                  </el-table-column>
                  <el-table-column prop="competitionType" label="比赛类型" width="150">
                    <template slot-scope="scope">
                     <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="awardLevel" label="获奖等级" width="150">
                    <template slot-scope="scope">
                     <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="studentName" label="参赛学生" width="120">
                  </el-table-column>
                  <el-table-column prop="teacherName" label="指导教师" width="120">
                  </el-table-column>
                  <el-table-column prop="awardTime" label="获奖时间" width="120">
                  </el-table-column>
                  <el-table-column prop="supportMaterial" label="佐证材料" width="120">
                  </el-table-column>
                  <el-table-column fixed="right" label="查看详情" width="120">
                    <el-button type="text" @click="checkDetail"
                      >查看详情</el-button
                    >
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
                   
                  </div>
                </div>
              </div></el-col
            >
            <el-col :span="2"><div class="grid-content"></div></el-col>
          </el-row>
        </div>
  </div>


  <div>
    <!-- 提交和退回审核信息对话框-->
    <el-dialog :title="title" :visible.sync="open2" width="500px" append-to-body>
    <el-form ref="form2" :model="submitt" label-width="100px">
      <el-form-item label="审核人工号" prop="nextTeacherId">
        <el-input v-model="submitt.nextTeacherId" placeholder="请输入下一级审核人的工号" />
      </el-form-item>
        <el-input v-model="submitt.reason" type="textArea" placeholder="备注原因" />
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button :loading="buttonLoading" type="primary" @click="submitForm2">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
    </el-dialog>
  </div>
  </div>
</template>

<script>
import {listTeamAudit, permitAudit, refuseAudit} from "@/api/dcims/teamAudit";
import {getTeam, updateTeam} from "@/api/dcims/team"

  export default {
    name:"tuanDuiHuoJiangShenHe",
    dicts: ['dcims_award_type'],
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
      teamList: [],
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
        // 提交与退回表单
        submittForm: [],
        // 单个提交与退回信息
        submitt: {
          teamId: undefined,// 团队id
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
      console.log(this.teamList);
    },
    methods: {
      /** 查询竞赛赛事基本信息列表 */
      getList() {
        this.loading = true;
        listTeamAudit(this.queryParams).then(response => {
          this.teamList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
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
