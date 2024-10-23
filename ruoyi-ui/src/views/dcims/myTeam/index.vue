<template>
  <div class="app-container">
    <el-table v-loading="loading" :data="teamList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="竞赛名称" align="center" prop="competition.name" />
      <el-table-column label="队伍名称" align="center" prop="name" />
      <el-table-column label="作品名称" align="center" prop="worksName" />
      <el-table-column label="比赛类型" align="center" prop="competitionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_type" :value="scope.row.competitionType"/>
        </template>
      </el-table-column>
      <el-table-column label="奖项等级" align="center" prop="awardLevel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="指导教师工号" align="center" prop="teacherId" />
      <el-table-column label="指导教师姓名" align="center" prop="teacherName" />
      <el-table-column label="参赛学生学号" align="center" prop="studentId" />
      <el-table-column label="参赛学生姓名" align="center" prop="studentName" />
      <el-table-column label="类型" align="center" prop="createBy">
        <template slot-scope="scope">
          <el-tag type="primary" v-if="scope.row.createBy == $store.state.user.name">我创建的</el-tag>
          <el-tag type="success" v-if="scope.row.createBy != $store.state.user.name">我管理的</el-tag>
<!--          {{$store.state.user.name}},-->
<!--          {{scope.row.teacherId}},-->
<!--          {{scope.row.createBy}}-->
        </template>
      </el-table-column>
      <el-table-column label="佐证材料" align="center" prop="oss.url" >
        <template slot-scope="scope">
          <ImagePreview
            v-if="previewListResource && scope.row.oss != null && checkFileSuffix(scope.row.oss.fileSuffix)"
            :width=100 :height=100
            :src="scope.row.oss.url"
            :preview-src-list="[scope.row.oss.url]"/>
          <span v-text="scope.row.oss.originalName"
                v-if="(scope.row.oss != null) && (!checkFileSuffix(scope.row.oss.fileSuffix) || !previewListResource)"/>
          <el-button v-if="(scope.row.oss != null)" type="text" @click.native="openNewTab(scope.row.oss.url)">在新窗口打开</el-button>

        </template>
      </el-table-column>
      <el-table-column label="评估状态" align="center" fixed="right" prop="audit">
        <template slot-scope="scope">
          <el-tooltip v-if="scope.row.auditDetail != null" class="item" effect="dark" :content="scope.row.auditDetail.reason" placement="top-end">
            <el-tag type="primary" v-if="scope.row.audit == 0">等待添加佐证材料</el-tag>
            <el-tag type="primary" v-if="scope.row.audit == 1 && scope.row.nextAuditId != 102099">学院评估中</el-tag>
            <el-tag type="primary" v-if="scope.row.audit == 1 && scope.row.nextAuditId == 102099">教务处评估中</el-tag>
            <el-tag type="success" v-if="scope.row.audit == 2">通过评估</el-tag>
            <el-tag type="danger" v-if="scope.row.audit == 3">评估不通过</el-tag>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" content="已经通过评估的获奖信息不允许修改，如需修改，请选择删除或联系管理员" placement="top" :disabled="scope.row.audit == 0 || scope.row.audit == 1 || scope.row.audit == 3">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              :disabled="scope.row.audit == 2"
              v-hasPermi="['dcims:competition:edit']"
            >修改</el-button>
          </el-tooltip>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:team:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改参赛团队对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="竞赛id" prop="competitionId">
            <el-input v-model="form.competitionId" placeholder="请输入竞赛id" />
          </el-form-item>
          <el-form-item label="竞赛名称" prop="competitionName">
            <el-input v-model="form.competitionName" placeholder="请输入竞赛名称" />
          </el-form-item>
          <el-form-item label="队伍名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入队伍名称" />
          </el-form-item>
          <el-form-item label="作品名称" prop="name">
            <el-input v-model="form.worksName" :disabled="worksNameIsNull" placeholder="请输入作品名称" />
            <el-checkbox label="作品名称" @change="changeWorksName()">无</el-checkbox>
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
          <el-form-item label="指导教师" prop="teacherId">{{ Array.isArray(form.teacherName) ? form.teacherName.join(', ') : form.teacherName }}
<!--            <el-select-->
<!--              v-model="form.teacherId"-->
<!--              filterable-->
<!--              remote-->
<!--              multiple-->
<!--              :reserve-keyword="false"-->
<!--              placeholder="请输入教师姓名"-->
<!--              :remote-method="queryTeacher"-->
<!--              :loading="loadingTeacher"-->
<!--              @change="syncTeacherName">-->
<!--              <el-option-->
<!--                v-for="item in optionsTeacher"-->
<!--                :key="item.teacherId"-->
<!--                :label="item.name"-->
<!--                :value="item.teacherId">-->
<!--              </el-option>-->
<!--            </el-select>-->
          </el-form-item>
        <el-form-item label="指导教师工号" prop="teacherId">{{ Array.isArray(form.teacherId) ? form.teacherId.join(', ') : form.teacherId }}<el-button size="mini" @click="editPeople('teacher')">修改</el-button></el-form-item>
        <el-form-item label="参赛学生" prop="studentName">{{ Array.isArray(form.studentName) ? form.studentName.join(', ') : form.studentName }}
<!--            <el-select-->
<!--              v-model="form.studentId"-->
<!--              filterable-->
<!--              remote-->
<!--              multiple-->
<!--              :reserve-keyword="false"-->
<!--              placeholder="请输入学生姓名"-->
<!--              :remote-method="queryStudent"-->
<!--              :loading="loadingStudent"-->
<!--              @change="syncStudentName">-->
<!--              <el-option-->
<!--                v-for="item in optionsStudent"-->
<!--                :key="item.studentId"-->
<!--                :label="item.studentId + '  ' + item.name"-->
<!--                :value="item.studentId">-->
<!--              </el-option>-->
<!--            </el-select>-->
          </el-form-item>
        <el-form-item label="参赛学生学号" prop="studentId">{{ Array.isArray(form.studentId) ? form.studentId.join(', ') : form.studentId }}<el-button size="mini" @click="editPeople('student')">修改</el-button></el-form-item>
        <el-form-item label="比赛时间" prop="competitionTime">
            <el-date-picker clearable
              v-model="form.competitionTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择比赛时间">
            </el-date-picker>
          </el-form-item>
        <el-form-item label="佐证材料" prop="supportMaterial">
          <file-upload v-model="form.supportMaterial"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注内容" />
        </el-form-item>

        </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="dialogVisible = true">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>




      <!-- 二次确认是否修改团队信息 -->
      <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="30%"
        :before-close="handleClose"
        append-to-body>
        <span>您确定要保存修改吗？这会导致获奖信息需要被重新评估！</span>
        <span slot="footer" class="dialog-footer">
              <el-button @click="dialogVisible = false">取 消</el-button>
              <el-button type="primary" @click="submitForm">确 定</el-button>
          </span>
      </el-dialog>
    </el-dialog>


    <!-- 修改指导教师或参赛学生，同批量导入的重名修改界面-->
    <el-dialog :title="DuplicatedDetail.title" :visible.sync="DuplicatedNameChangeWindowIsVisible" :before-close="CloseDuplicateWindow" style="text-align: center;">
      <el-table :data="DuplicatedDetail.entity" style="width: 100%">
        <el-table-column prop="name" label="名称" width="250">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.name" placeholder="请输入名称"></el-input>
            <div v-else class="txt">{{scope.row.name}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="number" label="学号/工号" width="250">
          <template slot-scope="scope">
            <el-select v-model="scope.row.number" placeholder="请选择" @click.native="queryPeopleNew(scope.$index)">
              <el-option
                v-for="item in scope.row.options"
                :key="item.id"
                :label="item.id + '   ' + item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column prop="number" label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="deleteOne(scope.$index)">删除</el-button>
            <el-button type="text" @click="saveName(scope.$index)">确定</el-button>
            <el-button type="text" @click="addOne(scope.$index, 1)">在下方插入一行</el-button>
            <el-button type="text" @click="addOne(scope.$index, -1)">在上方插入一行</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" @click="saveDuplicateWindow()" :loading="loading" style="margin-top: 30px">确定并保存</el-button>
      <el-button @click="CloseDuplicateWindow()" :loading="loading" style="margin-top: 30px">取消</el-button>
    </el-dialog>
  </div>
</template>

<script>
import { listTeamByTeacherId, getTeam, delTeam, addTeam, updateTeam } from "@/api/dcims/team";
import { listCompetition } from "@/api/dcims/competition"
import { listTeacherDict, listStudentDict } from "@/api/dcims/basicData"

export default {
  name: "Team",
  dicts: ['dcims_award_type', 'dcims_award_level', 'dcims_declare_award_status'],
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
      // 参赛团队表格数据
      teamList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 二次确认是否修改
      dialogVisible: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
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
      },
      // 是否不填写作品名称
      worksNameIsNull: false,
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
      // 预览列表图片
      previewListResource: true,
      // 展示重名修改窗口
      DuplicatedNameChangeWindowIsVisible: false,
      // 重名修改窗口内容
      DuplicatedDetail:{
        index: -1,
        title: "教师/学生重名核验窗口",
        errMsg: "我是错误信息",
        entity: []
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询参赛团队列表 */
    getList() {
      this.loading = true;
      listTeamByTeacherId(this.queryParams).then(response => {
        this.teamList = response.rows;
        this.total = response.total;

        this.teamList.forEach(element => {
        element.teacherId = element.teacherId.join();
        element.studentId = element.studentId.join();
        element.teacherName = element.teacherName.join();
        element.studentName = element.studentName.join();
        });

        this.loading = false;
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
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加参赛团队";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getTeam(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.form.competitionName = response.data.competition.name;
        this.open = true;
        this.title = "修改参赛团队";
        // 调用姓名查询回显用
        // this.form.teacherName.forEach((element) => {
        //   this.queryTeacher(element);
        // })
        // this.form.studentName.forEach((element) => {
        //   this.queryStudent(element);
        // })
        // syncStudentName();
        // syncTeacherName();
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.dialogVisible = false;
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          // 将数组类型的教师学生信息转换为字符串
          this.form.teacherId = this.form.teacherId.join(",");
          this.form.teacherName = this.form.teacherName.join(",");
          this.form.studentId = this.form.studentId.join(",");
          this.form.studentName = this.form.studentName.join(",");
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
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除参赛团队编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delTeam(ids);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dcims/team/export', {
        ...this.queryParams
      }, `team_${new Date().getTime()}.xlsx`)
    },
    /** 是否填写作品名称 */
    changeWorksName(){
      this.worksNameIsNull = !this.worksNameIsNull;
      if(this.worksNameIsNull){
        this.form.worksName = null;
      }
    },
    /** 关闭二次确认窗口 */
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },
    // 根据选中学号同步学生姓名
    syncStudentName(){
      console.log(this.optionsStudent);
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
          listTeacherDict(query, false).then(response => {
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
          listStudentDict(query, false).then(response => {
            this.optionsStudent = response.rows;
          }).finally(() => {
            this.loadingStudent = false;
          })
        }, 200);
      } else {
        this.optionsStudent = [];
      }
    },
    checkFileSuffix(fileSuffix) {
      let arr = ["png", "jpg", "jpeg"];
      return arr.some(type => {
        return fileSuffix.indexOf(type) > -1;
      });
    },
    /** 打开新窗口 */
    openNewTab(url) {
      window.open(url, '_blank');
    },
    /** 修改指导教师或学生面板 */
    editPeople(type){
      let NamesArray = [];
      let idsArray = [];
      switch (type){
        case "teacher":{
          this.DuplicatedDetail.title = "编辑指导教师";
          console.log(this.form.teacherName)
          console.log(typeof this.form.teacherName)

          NamesArray = Array.isArray(this.form.teacherName) ? this.form.teacherName : [this.form.teacherName];
          idsArray = Array.isArray(this.form.teacherId) ? this.form.teacherId : [this.form.teacherId];
          break;
        }
        case "student":{
          this.DuplicatedDetail.title = "编辑参赛学生";
          NamesArray = Array.isArray(this.form.studentName) ? this.form.studentName : [this.form.studentName];
          idsArray = Array.isArray(this.form.studentId) ? this.form.studentId : [this.form.studentId];
          break;
        }
      }

      for (let i = 0; i < NamesArray.length; i++){
        let tmp = {
          name: NamesArray[i],
          number: idsArray[i],
          options: {}
        }

        this.DuplicatedDetail.entity.push(tmp)
        if (this.DuplicatedDetail.title === "编辑指导教师"){
          this.queryTeacherNew(NamesArray[i], i)
        }else{
          this.queryStudentNew(NamesArray[i], i)
        }
      }
      console.log(this.DuplicatedDetail.entity)
      this.DuplicatedNameChangeWindowIsVisible = true
    },
    /** 关闭重名核验窗口 */
    CloseDuplicateWindow() {
      this.DuplicatedDetail.title = "";
      this.DuplicatedDetail.index = -1;
      this.DuplicatedDetail.errMsg = "";
      this.DuplicatedDetail.entity = [];

      this.DuplicatedNameChangeWindowIsVisible = false;
    },
    /** 远程搜索教师工号 */
    queryTeacherNew(query, index) {
      var optionsTeacher = undefined;
      if (query !== '') {
        // 去除query中的```符号
        query = query.replace(/`/g, "")
        listTeacherDict(query, true).then(response => {
          optionsTeacher = response.rows;
        }).finally(() => {
          for(let i = 0; i<optionsTeacher.length; i++){
            optionsTeacher[i]['id'] = optionsTeacher[i].teacherId
          }
          this.DuplicatedDetail.entity[index].options = optionsTeacher
          // 添加校外教师
          this.DuplicatedDetail.entity[index].options.push({id: "校外教师", name: query})
        })
      } else {
        optionsTeacher = [];
      }
    },

    /** 远程搜索学生学号 */
    queryStudentNew(query, index) {
      var optionsStudent = undefined
      if (query !== '') {
        // 去除query中的```符号
        query = query.replace(/`/g, "")
        listStudentDict(query, true).then(response => {
          optionsStudent = response.rows;
        }).finally(() => {
          for(let i = 0; i<optionsStudent.length; i++){
            optionsStudent[i]['id'] = optionsStudent[i].studentId
          }
          this.DuplicatedDetail.entity[index].options = optionsStudent
          // 添加研究生与校外学生
          this.DuplicatedDetail.entity[index].options.push({id: "校外学生", name: query})
          this.DuplicatedDetail.entity[index].options.push({id: "研究生", name: query})
        })
      } else {
        optionsStudent = [];
      }
    },
    /** 保存重名核验窗口内容 */
    saveDuplicateWindow(){
      // 判断是否都填写
      for(let j = 0; j<this.DuplicatedDetail.entity.length; j++){
        if(!this.isEmptyString(this.DuplicatedDetail.entity[j].name) && !this.isEmptyString(this.DuplicatedDetail.entity[j].number)){

        }else{
          let mStart = this.DuplicatedDetail.title.substring(4)
          this.$modal.msgError(mStart+this.DuplicatedDetail.entity[j].name+"姓名填写错误或未选择工号，请修改后重新保存！")
          return
        }
      }


      // 将教师或学生的姓名与学号更改保存到原先的数组中
      const title = this.DuplicatedDetail.title
      console.log(this.DuplicatedDetail.entity)
      switch (title){
        case "编辑指导教师":{
          let name = [];
          let id = [];
          for (let j =0; j<this.DuplicatedDetail.entity.length; j++){
            name.push(this.DuplicatedDetail.entity[j].name);
            id.push(this.DuplicatedDetail.entity[j].number)
          }
          this.form.teacherId = id;
          this.form.teacherName = name;
          break;
        }
        case "编辑参赛学生":{
          let name = [];
          let id = [];
          for (let j =0; j<this.DuplicatedDetail.entity.length; j++){
            name.push(this.DuplicatedDetail.entity[j].name);
            id.push(this.DuplicatedDetail.entity[j].number)
          }
          this.form.studentId = id;
          this.form.studentName = name;
          break;
        }
      }



      this.CloseDuplicateWindow();
    },
    /** 删除一位学生或老师 */
    deleteOne(index){
      console.log("要删除的行为" + index)
      this.DuplicatedDetail.entity.splice(index,1)
    },

    /** 添加一位学生或老师 */
    addOne(index, position){
      let item = {
        name: '',
        number: '',
        options: {},
        edit: true
      }
      this.DuplicatedDetail.entity.splice(index + position, 0, item)
    },

    /** 确定学生姓名 */
    saveName(index){
      this.DuplicatedDetail.entity[index].edit = false;
    },

    /** 查询学生或老师 */
    queryPeopleNew(index){
      switch (this.DuplicatedDetail.title){
        case "编辑指导教师":{
          this.queryTeacherNew(this.DuplicatedDetail.entity[index].name, index)
          break;
        }case "编辑参赛学生":{
          this.queryStudentNew(this.DuplicatedDetail.entity[index].name, index)
          break;
        }
      }
    },

    /** 字符串判空 */
    isEmptyString(s){
      if(s == null || s === ''){
        return true;
      }
      return false;
    }
  }
};
</script>
