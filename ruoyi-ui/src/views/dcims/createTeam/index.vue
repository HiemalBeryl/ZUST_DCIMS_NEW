<template>
<div>
    <div>
        <h2>创建团队</h2>
        <hr/>
    </div>
      <div>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-row>
            <el-form-item label="所属竞赛" prop="competitionId">
              <el-col :span="6">
                <el-select v-model="year"  placeholder="请选择竞赛所属年份">
                  <el-option
                    v-for="dict in dict.type.dcims_years"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-select
                  v-model="form.competitionId"
                  filterable
                  remote
                  reserve-keyword
                  placeholder="请输入竞赛名称"
                  :remote-method="queryCompetition"
                  :loading="loadingCompetition"
                  :disabled="year == undefined">
                  <el-option
                    v-for="item in optionsCompetition"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-col>
          </el-form-item>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="队伍名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入队伍名称" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
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
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="作品名称" prop="name">
                <el-input v-model="form.worksName" :disabled="worksNameIsNull" placeholder="请输入作品名称" />
                <el-checkbox label="作品名称" @change="changeWorksName()">无</el-checkbox>
              </el-form-item>
            </el-col>
              
          </el-row>
          <el-form-item label="指导教师" prop="teacherId">
            <el-select
              v-model="form.teacherId"
              filterable
              remote
              multiple
              reserve-keyword
              placeholder="请输入教师姓名"
              :remote-method="queryTeacher"
              :loading="loadingTeacher"
              @change="syncTeacherName">
              <el-option
                v-for="item in optionsTeacher"
                :key="item.teacherId"
                :label="item.name"
                :value="item.teacherId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="参赛学生" prop="studentId">
            <el-select
              v-model="form.studentId"
              filterable
              remote
              multiple
              reserve-keyword
              placeholder="请输入学生姓名"
              :remote-method="queryStudent"
              :loading="loadingStudent"
              @change="syncStudentName">
              <el-option
                v-for="item in optionsStudent"
                :key="item.studentId"
                :label="item.studentId + '  ' + item.name"
                :value="item.studentId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="比赛时间" prop="competitionTime">
            <el-date-picker clearable
              v-model="form.competitionTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择比赛时间">
            </el-date-picker>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
    </div>
</div>
</template>

<script>
import { addTeam, updateTeam } from "@/api/dcims/team";
import { listCompetition } from "@/api/dcims/competition"
import { listTeacherDict, listStudentDict } from "@/api/dcims/basicData"
export default {
    name: "CreateTeam",
    dicts: ['dcims_award_type', 'dcims_award_level', 'dcims_declare_award_status', 'dcims_teacher', 'dcims_student', 'dcims_years'],
    data(){
        return {
            // 按钮loading
            buttonLoading: false,
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
            },
            // 是否不填写作品名称
            worksNameIsNull: false,
            // 年份
            year: undefined,
            // 是否正在查找竞赛列表
            loadingCompetition: undefined,
            // 是否正在查找教师列表
            loadingTeacher: undefined,
            // 是否正在查找学生列表
            loadingStudent: undefined,
            // 可选竞赛
            optionsCompetition: [],
            // 可选教师
            optionsTeacher: [],
            // 可选学生
            optionsStudent: [],
        }
    },
    created(){

    },
    methods: {
        // 取消按钮
        cancel() {
            this.reset();
        },
        // 表单重置
        reset() {
            this.form = {
            id: undefined,
            orderNum: undefined,
            competitionId: undefined,
            name: undefined,
            worksName: undefined,
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
        /** 提交按钮 */
        submitForm() {
          // 是否填写作品名称
          if (this.worksNameIsNull){
            this.form.worksName = undefined;
          }
          // 数组对象预处理
          this.form.teacherId = this.form.teacherId.join(",");
          this.form.studentId = this.form.studentId.join(",");
          this.form.teacherName = this.form.teacherName.join(",");
          this.form.studentName = this.form.studentName.join(",");
          this.$refs["form"].validate(valid => {
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
                  this.reset();
                });
              }
            }
          });
        },
        // 根据选中学号同步学生姓名
        syncStudentName(){
          this.form.studentName = [];
          this.form.studentId.forEach(element => {
            this.form.studentName.push(
              this.optionsStudent.filter(function(item){
                if (item.studentId == element){
                  return item.name;
                }
              })[0].name
            );
          });
        },
        // 根据选中工号同步教师姓名
        syncTeacherName(){
          this.form.teacherName = [];
          this.form.teacherId.forEach(element => {
            this.form.teacherName.push(
              this.optionsTeacher.filter(function(item){
                if (item.teacherId == element){
                  return item.name;
                }
              })[0].name
            );
          });
        },
        // 是否填写作品名称
        changeWorksName(){
          this.worksNameIsNull = !this.worksNameIsNull;
        },
        /** 查询竞赛列表 */
        queryCompetition(query) {
          if (query !== '') {
            this.loadingCompetition = true;
            const params = {
              annual: this.year,
              name: query,
            };
            setTimeout(() => {
              listCompetition(params).then(response => {
                this.optionsCompetition = response.rows;
              }).finally(() => {
                this.loadingCompetition = false;
              })
            }, 200);
          } else {
            this.optionsCompetition = [];
          }
        },
        /** 查询教师列表 */
        queryTeacher(query) {
          if (query !== '') {
            this.loadingTeacher = true;
            setTimeout(() => {
              listTeacherDict(query).then(response => {
                this.optionsTeacher = response.rows;
              }).finally(() => {
                this.loadingTeacher = false;
              })
            }, 200);
          } else {
            this.optionsTeacher = [];
          }
        },
        /** 查询学生列表 */
        queryStudent(query) {
          if (query !== '') {
            this.loadingStudent = true;
            setTimeout(() => {
              listStudentDict(query).then(response => {
                this.optionsStudent = response.rows;
              }).finally(() => {
                this.loadingStudent = false;
              })
            }, 200);
          } else {
            this.optionsStudent = [];
          }
        },
    }
}
</script>
