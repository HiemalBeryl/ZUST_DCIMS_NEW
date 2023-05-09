<template> <!--赛事立项 -->
<div>
  <div style="border-bottom: 1px solid #dbdbdb;height:60px;width:600px ;">
    <el-tooltip class="item" effect="dark" content="作为竞赛负责人，您可以在本页面提交当年的竞赛申报材料。" placement="right">
      <h1>竞赛立项</h1>
    </el-tooltip>
  </div>

  <!-- 添加或修改竞赛赛事基本信息对话框 -->
  <div>
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
      <el-form-item label="所属学院" prop="college">
        <el-select v-model="form.college" placeholder="请选择所属学院" clearable>
          <el-option
            v-for="dict in dict.type.dcims_college"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      <el-form-item label="审核人" prop="nextAuditId">
        <el-select filterable v-model="form.nextAuditId" placeholder="请选择审核人">
          <el-option
            v-for="dict in dict.type.dcims_teacher"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
import {addCompetition} from "@/api/dcims/competition";
export default {
  name: "Competition",
  dicts: ['dcims_audit_result', 'dcims_competition_type', 'dcims_teacher', 'dcims_college'],
  data(){
    return{
      // 按钮loading
      buttonLoading: false,
      // 表单参数
      form: {},
      // 是否显示弹出层
      open: false,
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
        college: [
          { required: true, message: "所属学院不能为空", trigger: "blur"}
        ],
        attachment: [
          { required: true, message: "竞赛申报书不能为空", trigger: "blur" }
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
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          addCompetition(this.form).then(response => {
            this.$modal.msgSuccess("竞赛立项成功，请等待审核！");
            this.open = false;
          }).finally(() => {
            this.buttonLoading = false;
            this.reset();
          });
        }
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
        name: undefined,
        level: undefined,
        pastName: undefined,
        website: undefined,
        term: undefined,
        annual: undefined,
        organizer: undefined,
        responsiblePersonId: undefined,
        responsiblePersonName: undefined,
        college: undefined,
        innerTime: undefined,
        provinceTime: undefined,
        nationalTime: undefined,
        stopTime: undefined,
        budget: undefined,
        appropriation: undefined,
        goal: undefined,
        introduction: undefined,
        attachment: undefined,
        moneyAggregate: undefined,
        workloadAggregate: undefined,
        personLimit: undefined,
        teamLimit: undefined,
        nextAuditId: undefined,
        state: undefined,
        version: undefined,
        createTime: undefined,
        createBy: undefined,
        updateTime: undefined,
        updateBy: undefined,
        delFlag: undefined
      };
      this.resetForm("form");
    }
  }
}
</script>
