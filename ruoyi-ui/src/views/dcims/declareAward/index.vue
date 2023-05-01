<template>
<el-form :model="form" :rules="rules" ref="form" label-width="100px" class="form">

  <el-form-item label="导入信息" >
    <el-select v-model="form.id" placeholder="请选择团队">
      <el-option label="团队1652996017461710849" value="1652996017461710849"></el-option>
      <el-option label="团队2" value="2"></el-option>
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

  <el-form-item label="审核人工号" prop="nextAuditId">
            <el-input v-model="form.nextAuditId" placeholder="请输入审核人工号" />
  </el-form-item>
  
  

  <el-form-item>
    <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
    <el-button @click="resetForm('form')">重置</el-button>
  </el-form-item>
</el-form>
</template>
<script>
  import { declareAward } from "@/api/dcims/team";
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
              supportMaterial: [
                { required: true, message: "佐证材料不能为空", trigger: "blur" }
              ],
              nextAuditId: [
                { required: true, message: "审核人工号不能为空", trigger: "blur"}
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
                declareAward(this.form).then(response => {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.resetForm('form');
                }).finally(() => {
                  this.buttonLoading = false;
                });
              } else {
                
              }
            }
          });
        },

    }
}
</script>