<template>
<el-form :model="form" :rules="rules" ref="form" label-width="100px" class="form">
  <div>
    <h2>获奖申报</h2>
  </div>
  <el-divider></el-divider>
  <el-form-item label="选择团队" prop="id">
    <el-select v-model="form.id" placeholder="请选择团队">
      <el-option
        v-for="team in teamList"
        :key="team.name"
        :label="team.name"
        :value="team.id"
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


  <el-form-item label="获奖时间" prop="awardTime">
            <el-date-picker clearable
              v-model="form.awardTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择获奖时间">
            </el-date-picker>
  </el-form-item>


  <el-form-item label="佐证材料" prop="supportMaterial">
    <el-row>
      <el-col :span="8">
        <template v-if="uploadType == 'image'">
          <image-upload v-model="form.supportMaterial"/>
        </template>
      </el-col>
      <el-col :span="8">
        <template v-if="uploadType == 'file'">
          <file-upload v-model="form.supportMaterial"/>
        </template>
      </el-col>
      <el-col :span="4">
        <el-button type="small" @click="switchType">切换文件类型</el-button>
      </el-col>
    </el-row>
  </el-form-item>



  <el-form-item>
    <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
    <el-button @click="resetForm('form')">重置</el-button>
  </el-form-item>
</el-form>
</template>
<script>
  import { listTeamByTeacherId,declareAward } from "@/api/dcims/team";
export default {
    name: "CreateTeam",
    dicts: ['dcims_award_type', 'dcims_award_level', 'dcims_declare_award_status'],
    data(){
        return {
            // 按钮loading
            buttonLoading: false,
            // 遮罩层
            loading: true,
            // 表单参数
            form: {},
            // 总条数
            total: 0,
            // 参赛团队表格数据
            teamList: [],
            // 查询参数
            queryParams: {
              pageNum: undefined,
              pageSize: undefined,
              competitionId: undefined,
              name: undefined,
              competitionType: undefined,
              awardLevel: undefined,
              teacherId: undefined,
              teacherName: undefined,
              studentId: undefined,
              studentName: undefined,
              audit: undefined,
            },
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
            // 佐证材料类型
            uploadType: "image",
        }
    },
    created(){
      this.getList();
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
                  this.getList();
                  this.buttonLoading = false;
                });
              } else {

              }
            }
          });
        },
        /** 查询参赛团队列表 */
        getList() {
          this.loading = true;
          listTeamByTeacherId(this.queryParams).then(response => {
            this.teamList = response.rows;
            this.total = response.total;
            // 过滤掉已经提交佐证材料或已通过审核的团队
            // const filtersList = this.teamList.filter(element => element.audit === 0);
            // this.teamList = filtersList;
            // 过滤掉已经被作为父级的团队，选择没有材料的的||有材料但没被排除的，其它判断逻辑交给后端
            const filtersList = [];
            const excludeIds = [];
            this.teamList.forEach(element => {
              if(element.advancedAwardNumber != null){
                excludeIds.push(element.advancedAwardNumber)
              }
            });
            this.teamList.forEach(element => {
              var flag = false;
              excludeIds.forEach(e => {
                if(element.id == e){
                  console.log("找到了排除对象"+element);
                  flag = true;
                }
              })
              if(flag == false){
                filtersList.push(element);
              }
            });
            this.teamList = filtersList;
            this.loading = false;
          });
        },
        /** 切换佐证材料类型 */
        switchType() {
          if (this.uploadType == "image") this.uploadType = "file";
          else this.uploadType = "image";
        }

    }
}
</script>
