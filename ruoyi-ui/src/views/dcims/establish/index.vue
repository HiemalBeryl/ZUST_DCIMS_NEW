<style lang="scss" scoped>

/* 自定义数字输入框append  */
.mo-input--number {
  border: 1px solid #DCDFE6;
  width: 100%;
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
<template> <!--赛事立项 -->
  <div>
    <div style="border-bottom: 1px solid #dbdbdb;height:60px;width:600px ;">
      <span slot="label">
        <h1>竞赛立项
          <el-tooltip class="item" effect="dark" content="作为竞赛负责人，您可以在本页面提交当年的竞赛申报材料。" placement="right">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </h1>
      </span>
    </div>

    <!-- 添加或修改竞赛赛事基本信息对话框 -->
    <div v-if="dict.type.dcims_years.length > 0">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <div>
          <h3>基本信息</h3>
          <el-row>
          <el-col :span="8">
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入赛事名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="赛事届次" prop="term">
             <el-input v-model="form.term" placeholder="请输入赛事届次（如：12）" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="赛事年份" prop="annual">
              <el-select v-model="form.annual"  placeholder="请选择竞赛所属年份">
                <el-option
                  v-for="dict in dict.type.dcims_years"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="主办单位" prop="organizer">
              <el-input v-model.number="form.organizer" placeholder="请输入主办单位" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
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
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="竞赛负责人工号" prop="responsiblePersonId">
<!--              <el-input v-model="form.responsiblePersonId" :disabled="true" placeholder="请输入竞赛负责人工号" />-->
              {{ form.responsiblePersonId }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="竞赛负责人" prop="responsiblePersonName">
<!--              <el-input v-model="form.responsiblePersonName" :disabled="true" placeholder="请输入竞赛负责人" />-->
              {{ form.responsiblePersonName }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="负责人手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否是单人赛" prop="singleRace">
              <el-tooltip class="item" effect="dark" content="此选项作为工作量和奖金计算依据，既有单人赛也有团队赛的，建议选择“团队赛“" placement="right">
                <i class="el-icon-question"></i>
              </el-tooltip>
              <el-select v-model="form.singleRace" placeholder="请选择比赛性质" clearable>
                <el-option key="50" label="单人赛" value="50"></el-option>
                <el-option key="100" label="团队赛" value="100"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="校内选拔时间" prop="innerTime">
              <el-date-picker clearable
                v-model="form.innerTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :disabled="innerTimeIsNull"
                placeholder="请选择校内选拔时间">
              </el-date-picker>
              <el-checkbox label="校内选拔时间" @change="changeInnerTime()">无校赛</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="省赛时间" prop="provinceTime">
              <el-date-picker clearable
                v-model="form.provinceTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :disabled="provinceTimeIsNull"
                placeholder="请选择省赛时间">
              </el-date-picker>
              <el-checkbox label="省赛时间" @change="changeProvinceTime()">无省赛</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="国赛时间" prop="nationalTime">
              <el-date-picker clearable
                v-model="form.nationalTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                :disabled="nationalTimeIsNull"
                placeholder="请选择国赛时间">
              </el-date-picker>
              <el-checkbox label="国赛时间" @change="changeNationalTime()">无国赛</el-checkbox>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="竞赛申报书" prop="attachment">
              <file-upload v-model="form.attachment"/>
            </el-form-item>
          </el-col>
        </el-row>
        </div>

        <el-divider></el-divider>

        <div>
        <h3>额外信息</h3>
        <el-row>
          <el-col :span="8">
            <el-form-item label="往届名称" prop="pastName">
             <el-input v-model="form.pastName" placeholder="请输入往届名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="赛事官网" prop="website">
              <el-input v-model="form.website" placeholder="请输入赛事官网" />
            </el-form-item>
          </el-col>


        </el-row>
        <el-row>
          <el-col :span="4">
            <el-form-item label="本年度申报经费" prop="budget">
              <div  class="mo-input--number">
                <el-input-number v-model="form.budget" controls-position="right" :precision="2" :step="0.1" :min="0" :max="100"></el-input-number>
                <div class="define-append">万元</div>
              </div>

            </el-form-item>
          </el-col>
        </el-row>
        </div>
        <el-row>
            <el-form-item label="集中授课教师">
                <!-- 渲染教师列表 -->
                <section v-for="(value, index) in teacherIds" :key="index">
                  <section v-if="index === 0">
                    <el-row>
                      <el-col :span="3">
                        <el-select
                          v-model="teacherIds[index]"
                          filterable
                          remote
                          reserve-keyword
                          placeholder="请输入教师姓名"
                          :remote-method="queryTeacher"
                          :loading="loadingTeacher">
                          <el-option
                            v-for="item in options"
                            :key="item.teacherId"
                            :label="item.name"
                            :value="item.teacherId">
                          </el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="4">
                        <el-input v-model="teachingHour[index]" placeholder="请输入集中授课时数" clearable @keyup.enter.native="addlastitems(index, '1')"/>
                      </el-col>
                      <el-col :span="4">
                        <el-button type="primary" icon="el-icon-plus" plain style="margin-left:10px;" circle @click="addlastitems(index, '1')"/>
                      </el-col>
                    </el-row>
                  </section>
                  <section v-if="index > 0">
                    <!-- 添加的子项目 -->
                    <el-row>
                      <el-col :span="3">
                          <el-select
                          v-model="teacherIds[index]"
                          filterable
                          remote
                          reserve-keyword
                          placeholder="请输入教师姓名"
                          :remote-method="queryTeacher"
                          :loading="loadingTeacher">
                          <el-option
                            v-for="item in options"
                            :key="item.teacherId"
                            :label="item.name"
                            :value="item.teacherId">
                          </el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="4">
                        <el-input v-model="teachingHour[index]" placeholder="请输入授课工时" clearable @keyup.enter.native="addlastitems(index, '1')"/>
                      </el-col>
                      <el-col :span="4">
                        <el-button type="danger" icon="el-icon-delete" plain style="margin-left:10px;" circle @click="rmlastitems(index, '1')"/>
                      </el-col>
                    </el-row>
                  </section>
                </section>
            </el-form-item>
        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="集中授课安排表附件" prop="teachingHoursAttachment">
              <file-upload v-model="form.teachingHoursAttachment"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="竞赛官方通知文件" prop="redHeaderFile">
              <file-upload v-model="form.redHeaderFile"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="获奖目标" prop="goal">
              <el-input v-model="form.goal" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="赛事简介" prop="introduction">
              <el-input v-model="form.introduction" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">清 空</el-button>
      </div>
    </div>


    <!-- 当年未开启竞赛立项时显示的内容-->
    <div v-if="dict.type.dcims_years.length == 0" style="border-bottom: 1px solid #dbdbdb;height:60px;width:600px ;">
      <span slot="label">
        <h1>本年度竞赛申报还未开启，请耐心等待！
        </h1>
      </span>
    </div>
  </div>
  </template>
  <script>
  import {addCompetition} from "@/api/dcims/competition";
  import {queryLoginTeacher,listTeacherDict} from "@/api/dcims/basicData";

  export default {
    name: "Competition",
    dicts: ['dcims_audit_result', 'dcims_competition_type', 'dcims_teacher', 'dcims_college', 'dcims_years'],
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
            { required: true, message: "赛事届次不能为空", trigger: "blur" },
            { pattern:/^\d{1,5}$/, message: '请输入1~10000之间的数字', trigger: 'blur' }
          ],
          annual: [
            { required: true, message: "赛事年份不能为空", trigger: "blur" },
            { pattern:/^\d{4}$/, message: '请输入年份', trigger: 'blur' }
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
          phone: [
            { required: true, message: "手机号不能为空", trigger: "blur"}
          ],
          // innerTime: [
          //   { required: true, message: "校内选拔时间不能为空", trigger: "blur" }
          // ],
          // provinceTime: [
          //   { required: true, message: "省赛时间不能为空", trigger: "blur" }
          // ],
          // nationalTime: [
          //   { required: true, message: "国赛时间不能为空", trigger: "blur" }
          // ],
          attachment: [
            { required: true, message: "请上传竞赛申报书", trigger: "blur" }
          ],
          singleRace: [
            { required: true, message: "请选择是否是单人赛", trigger: "blur" }
          ],
        },
        // 教师工号
        teacherIds: [''],
        // 对应时长
        teachingHour: [''],
        // 是否正在查找教师列表
        loadingTeacher: undefined,
        // 可选教师
        options: [],
        // 是否不填写校赛时间
        innerTimeIsNull: false,
        // 是否不填写省赛时间
        provinceTimeIsNull: false,
        // 是否不填写国赛时间
        nationalTimeIsNull: false,
        //
        listenChange: undefined
      }
    },
    created(){
      this.form = {
        responsiblePersonId: undefined,
        responsiblePersonName: undefined,
        singleRace: '100'
      };
      this.getLoginTeacher();
      // this.form.singleRace = '100';

      // 检测是否存在上次未填写完成的表单
      var arr = JSON.parse(localStorage.getItem("competitionForm"))
      var userName = this.$store.state.user.nick
      console.log(userName.toString());
      console.log(arr);
      if(Object.keys(arr[1]).length > 2 && userName.toString() == arr[0].toString()){
        this.$confirm('存在上次未填写完成的表单，是否载入数据？')
          .then(_ => {
            console.log("before");
            this.initLocalStorageData(arr);
            console.log("after");
          })
          .catch(_ => {});
      }

      this.getLoginTeacher();
    },
    methods: {
      /** 提交按钮 */
      submitForm() {
        // 将工时转化成键值对存入表单
        const keyValuePairs = this.teacherIds.map((key, index) => key + '=' + this.teachingHour[index]);
        const result = keyValuePairs.join(',');
        this.form.teachingHours = result;
        console.log(this.form.teachingHours);
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            addCompetition(this.form).then(response => {
              this.$modal.msgSuccess("竞赛立项申报成功，请等待学院和教务处评估！");
              this.open = false;
              localStorage.removeItem('competitionForm')
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
          teachingHours: undefined,
          teachingHoursAttachment: undefined,
          goal: undefined,
          introduction: undefined,
          attachment: undefined,
          redHeaderFileType: 0,
          redHeaderFile: undefined,
          moneyAggregate: undefined,
          workloadAggregate: undefined,
          personLimit: undefined,
          teamLimit: undefined,
          state: undefined,
          version: undefined,
          createTime: undefined,
          createBy: undefined,
          updateTime: undefined,
          updateBy: undefined,
          delFlag: undefined,
          singleRace: undefined
        };
        this.teacherIds = [''];
        this.teachingHour = [''];
        this.resetForm("form");
        queryLoginTeacher().then(response => {
        this.form.responsiblePersonId = response.data.teacherId;
        this.form.responsiblePersonName = response.data.name;
      });
      },
      // 添加教师工时输入框
      addlastitems(index, type) {
        if (type === '1') {
          const lastitem1 = this.teacherIds[this.teacherIds.length - 1]
          const lastitem2 = this.teachingHour[this.teachingHour.length - 1]
          if (lastitem1 == null || lastitem2.trim() === '') {
            this.$message.error('请您填写完一项后继续追加')
          } else {
            this.teacherIds.push('')
            this.teachingHour.push('')
          }
        }
      },
      rmlastitems(index, type) {
        switch (type) {
          case '1':
            this.teacherIds.splice(index, 1)
            break
          default:
            break
        }
        this.$message.success('移除成功')
      },
      /** 查询教师列表 */
      queryTeacher(query) {
        if (query !== '') {
          this.loadingTeacher = true;
          setTimeout(() => {
            listTeacherDict(query, false).then(response => {
              this.options = response.rows;
            }).finally(() => {
              this.loadingTeacher = false;
            })
          }, 200);
        } else {
          this.options = [];
        }
      },
      changeInnerTime(){
        this.innerTimeIsNull = !this.innerTimeIsNull
        this.form.innerTime = undefined

      },
      changeProvinceTime(){
        this.provinceTimeIsNull = !this.provinceTimeIsNull
        this.form.provinceTime = undefined
      },
      changeNationalTime(){
        this.nationalTimeIsNull = !this.nationalTimeIsNull
        this.form.nationalTime = undefined
      },
      initLocalStorageData(arr){
        console.log(arr)
          this.form = arr[1];
          this.teacherIds = arr[2];
          this.teachingHour = arr[3];
          console.log(this.form);
          console.log(this.teacherIds);
          console.log(this.teachingHour);
      },
      getLoginTeacher(){
        //填写提交人
        queryLoginTeacher().then(response => {
          this.form.responsiblePersonId = response.data.teacherId;
          this.form.responsiblePersonName = response.data.name;
        });
      }
    },
    watch: {
      form(newValue) {
        if(newValue){
          var tempData = [];
          tempData.push(this.$store.state.user.nick); //保存用户名，区分用户填写的表单
          tempData.push(this.form); //值变化时保存在tempData
          tempData.push(this.teacherIds); //值变化时保存在tempData
          tempData.push(this.teachingHour); //值变化时保存在tempData
          localStorage.setItem("competitionForm", JSON.stringify(tempData)); //本地存储存储的是字符串

        }
      },
      teacherIds(newValue, oldValue) {
        if(newValue != null) {
          var tempData = [];
          tempData.push(this.$store.state.user.nick); //保存用户名，区分用户填写的表单
          tempData.push(this.form); //值变化时保存在tempData
          tempData.push(this.teacherIds); //值变化时保存在tempData
          tempData.push(this.teachingHour); //值变化时保存在tempData
          localStorage.setItem("competitionForm", JSON.stringify(tempData)); //本地存储存储的是字符串
        }
      },
      teachingHour(newValue, oldValue) {
        if(newValue != null) {
          var tempData = [];
          tempData.push(this.$store.state.user.nick); //保存用户名，区分用户填写的表单
          tempData.push(this.form); //值变化时保存在tempData
          tempData.push(this.teacherIds); //值变化时保存在tempData
          tempData.push(this.teachingHour); //值变化时保存在tempData
          localStorage.setItem("competitionForm", JSON.stringify(tempData)); //本地存储存储的是字符串
        }
      },
      deep: true
    },
  }
  </script>
