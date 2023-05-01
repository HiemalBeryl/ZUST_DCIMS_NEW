<template>
<div>
    <div>
        <h2>创建团队</h2>
        <hr/>
    </div>
        <div>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="竞赛id" prop="competitionId">
            <el-input v-model="form.competitionId" placeholder="请输入竞赛id" />
          </el-form-item>
          <el-form-item label="队伍名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入队伍名称" />
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
export default {
    name: "CreateTeam",
    dicts: ['dcims_award_type', 'dcims_award_level', 'dcims_declare_award_status'],
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
            }
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

    }
}
</script>
