<template>
  <div>
    <el-steps :active="stepsActive" align-center>
      <el-step title="1-下载批量导入团队模板" description=""></el-step>
      <el-step title="2-上传模板文件" description=""></el-step>
      <el-step title="3-检查并修改奖项" description=""></el-step>
      <el-step title="4-导入成功" description=""></el-step>
    </el-steps>

    <!--  下载模板界面-->
    <div v-show="stepsActive==0" style="text-align: center;">
        <p>请选择年份，下载模板</p>
        <el-select v-model="selectedYear" placeholder="请选择">
          <el-option
            v-for="dict in dict.type.dcims_years"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value">
          </el-option>
        </el-select>
        <br/>
        <el-button type="primary" @click="downloadTemplate()" :loading="loading" style="margin-top: 30px">下载</el-button>
        <el-button type="primary" @click="nextActive()" :loading="loading" style="margin-top: 30px">我已下载模板，直接进行下一步</el-button>
    </div>

    <!--  上传模板界面-->
    <div v-show="stepsActive==1" style="text-align: center;">
<!--      <h1>本系统仅统计本科生获奖数据，请勿上传研究生及校外学生数据！</h1>-->
      <el-upload
        class="upload"
        ref="upload0"
        action="#"
        :on-remove="removeUploadFile"
        :before-upload="beforeUpload"
        :file-list="uploadFile"
        :limit="1"
        :auto-upload="false"
        :http-request="handleFileUpload">
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="clickUploadButton()" :loading="loading">上传到服务器</el-button>
        <div slot="tip" class="el-upload__tip">请上传附带填写好的模板以及附带获奖材料的压缩包，支持rar/zip格式的压缩包，文件大小需小于500MB</div>
      </el-upload>
      <br/>
      <el-button @click="minusStep">后退</el-button>
    </div>

    <!--  调整数据界面-->
    <div v-show="stepsActive==2" style="text-align: center;">
      <el-table :data="team" style="width: 100%" @cell-mouse-enter="handleCellEnter" @cell-mouse-leave="handleCellLeave">
        <el-table-column prop="year" label="年份" width="180">
          <template slot-scope="scope">
            <el-select v-if="scope.row.edit" v-model="scope.row.year" placeholder="请选择年份" @focus="handleSelectOpened()" @blur="handleSelectClosed()" @change="annualChange(scope.row.year)">
              <el-option
                v-for="dict in dict.type.dcims_years"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value">
              </el-option>
            </el-select>
            <div v-else class="txt">{{scope.row.year}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="competitionName" label="竞赛名称" width="180">
          <template slot-scope="scope">
            <el-tooltip placement="right"
                        v-show="scope.row.hasOwnProperty('competitionNameError') && scope.row.competitionNameError.length > 0">
              <div slot="content">
                <div v-for="(error, index) in scope.row.competitionNameError" style="margin-bottom: 5px;margin-top: 5px;">
                  {{ error.errorMessage }}
                </div>
              </div>
              <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
            </el-tooltip>
            <el-select v-if="scope.row.edit" v-model="scope.row.competitionName" placeholder="请选择竞赛名称" @focus="handleSelectOpened()" @blur="handleSelectClosed()" @change="removeError(scope.row, 'competitionNameError')">
              <el-option
                v-for="item in optionsCompetition"
                :key="item.name"
                :label="item.name"
                :value="item.name">
              </el-option>
            </el-select>
            <div v-else class="txt">{{scope.row.competitionName}}</div>
          </template>
        </el-table-column>

        <el-table-column label="奖项等级" prop="awardLevel" width="180">
          <template slot-scope="scope">
            <el-select v-if="scope.row.edit" v-model="scope.row.awardLevel" placeholder="请选择奖项等级" @focus="handleSelectOpened()" @blur="handleSelectClosed()">
              <el-option
                v-for="dict in dict.type.dcims_award_level"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
            <div v-else class="txt">
              <dict-tag :options="dict.type.dcims_award_level" :value="scope.row.awardLevel"/>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="teacherName" label="指导教师名单" width="180">

          <template slot-scope="scope">
            <!--            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.teacherName" placeholder="请输入内容"></el-input>-->
            <!--            <div v-else class="txt">{{scope.row.teacherName}}</div>-->
            <div @click="OpenDuplicateWindow('检查教师重名情况', '', scope.row.teacherName, scope.row.teacherId, scope.row.index)">
              <el-tooltip placement="right"
                          v-show="scope.row.hasOwnProperty('teacherNameRepeatError') && scope.row.teacherNameRepeatError.length > 0">
                <div slot="content">
                  <div v-for="(error, index) in scope.row.teacherNameRepeatError" style="margin-bottom: 5px;margin-top: 5px;">
                    {{ error.errorMessage }}
                  </div>
                </div>
                <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
              </el-tooltip>
            </div>
            <el-tooltip placement="right"
                        v-show="scope.row.hasOwnProperty('teacherNameNotFoundError') && scope.row.teacherNameNotFoundError.length > 0">
              <div slot="content">
                <div v-for="(error, index) in scope.row.teacherNameNotFoundError" style="margin-bottom: 5px;margin-top: 5px;">
                  {{ error.errorMessage }}
                </div>
              </div>
              <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
            </el-tooltip>
            <div class="txt">{{scope.row.teacherName}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="studentName" label="学生名单" width="180">
          <template slot-scope="scope">
<!--            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.studentName" placeholder="请输入内容"></el-input>-->
<!--            <div v-else class="txt">{{scope.row.studentName}}</div>-->
            <div @click="OpenDuplicateWindow('检查学生重名情况', '', scope.row.studentName, scope.row.studentId, scope.row.index)">
              <el-tooltip placement="right"
                          v-show="scope.row.hasOwnProperty('studentNameRepeatError') && scope.row.studentNameRepeatError.length > 0">
                <div slot="content">
                  <div v-for="(error, index) in scope.row.studentNameRepeatError" style="margin-bottom: 5px;margin-top: 5px;">
                    {{ error.errorMessage }}
                  </div>
                </div>
                <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
              </el-tooltip>
              <el-tooltip placement="right"
                          v-show="scope.row.hasOwnProperty('studentNameNotFoundError') && scope.row.studentNameNotFoundError.length > 0">
                <div slot="content">
                  <div v-for="(error, index) in scope.row.studentNameNotFoundError" style="margin-bottom: 5px;margin-top: 5px;">
                    {{ error.errorMessage }}
                  </div>
                </div>
                <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
              </el-tooltip>
            </div>
            <div class="txt">{{scope.row.studentName}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="队伍名称" width="180">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.name" placeholder="请输入内容"></el-input>
            <div v-else class="txt">{{scope.row.name}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="worksName" label="竞赛作品名" width="180">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.worksName" placeholder="请输入内容"></el-input>
            <div v-else class="txt">{{scope.row.worksName}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" width="180">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.remark" placeholder="请输入内容"></el-input>
            <div v-else class="txt">{{scope.row.remark}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="competitionTime" label="比赛时间" width="180">
          <template slot-scope="scope">
            <el-date-picker clearable
                            v-if="scope.row.edit"
                            v-model="scope.row.competitionTime"
                            type="date"
                            value-format="yyyy-MM-dd"
                            placeholder="请选择获奖时间"
                            @focus="handleSelectOpened()" @blur="handleSelectClosed()">
            </el-date-picker>
            <div v-else class="txt">{{new Date(scope.row.competitionTime).getFullYear() + '-' + (new Date(scope.row.competitionTime).getMonth() + 1) + '-' +  new Date(scope.row.competitionTime).getDate()}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="awardTime" label="获奖时间" width="180">
          <template slot-scope="scope">
            <el-date-picker clearable
                            v-if="scope.row.edit"
                            v-model="scope.row.awardTime"
                            type="date"
                            value-format="yyyy-MM-dd"
                            placeholder="请选择获奖时间"
                            @focus="handleSelectOpened()" @blur="handleSelectClosed()">
            </el-date-picker>
            <div v-else class="txt">{{new Date(scope.row.awardTime).getFullYear() + '-' + (new Date(scope.row.awardTime).getMonth() + 1) + '-' +  new Date(scope.row.awardTime).getDate() }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="supportMaterialFileName" label="获奖佐证材料" width="180">
          <template slot-scope="scope">
            <el-tooltip placement="right"
                        v-show="scope.row.hasOwnProperty('fileNotFoundError') && scope.row.fileNotFoundError.length > 0">
              <div slot="content">
                <div v-for="(error, index) in scope.row.fileNotFoundError" style="margin-bottom: 5px;margin-top: 5px;">
                  {{ error.errorMessage }}
                </div>
              </div>
              <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
            </el-tooltip>
            <ImagePreview
              v-if="previewListResource && scope.row.oss != null && checkFileSuffix(scope.row.oss.fileSuffix)"
              :width=100 :height=100
              :src="scope.row.oss.url"
              :preview-src-list="[scope.row.oss.url]"/>
            <span v-if="(scope.row.oss != null) && (!checkFileSuffix(scope.row.oss.fileSuffix) || !previewListResource)">
            <i class="el-icon-document" style="font-size: 20px;"></i>
            <br/>
          </span>
            <el-button v-if="(scope.row.oss != null)" type="text" @click.native="openNewTab(scope.row.oss.url)">在新窗口打开</el-button>
          </template>
        </el-table-column>

        <el-table-column prop="isSingle" label="是否为单人赛" width="180">
          <template slot-scope="scope">
<!--            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.isSingle" placeholder="请输入内容"></el-input>-->
<!--            <div v-else class="txt">{{scope.row.isSingle}}</div>-->
            <el-tooltip placement="right"
                        v-show="scope.row.hasOwnProperty('tooMuchStudentError') && scope.row.tooMuchStudentError.length > 0">
              <div slot="content">
                <div v-for="(error, index) in scope.row.tooMuchStudentError" style="margin-bottom: 5px;margin-top: 5px;">
                  {{ error.errorMessage }}
                </div>
              </div>
              <i class="el-icon-warning" style="font-size: 18px; color: red"></i>
            </el-tooltip>
            <div class="txt">{{scope.row.isSingle}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="operation" label="操作" width="180">
          <template slot-scope="scope">
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
      <el-button style="margin-left: 10px;" type="success" @click="clickChangeButton()" :loading="loading" >保存修改</el-button>
      <br>

      <el-radio v-model="appendType" label="追加">追加</el-radio>
      <el-radio v-model="appendType" label="覆盖">覆盖</el-radio>
      <el-upload
        class="upload1"
        ref="upload1"
        action="#"
        :on-remove="removeUploadFile"
        :before-upload="beforeUpload"
        :file-list="appendFile"
        :limit="1"
        :auto-upload="false"
        :http-request="appendData">
        <el-button slot="trigger" size="small" type="primary">{{this.appendType}}奖项</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="clickAppendButton()" :loading="loading">上传到服务器</el-button>
        <div slot="tip" class="el-upload__tip">请上传附带填写好的模板以及附带获奖材料的压缩包，文件大小小于500MB</div>
      </el-upload>
      <el-button @click="submit()" style="margin-top: 30px">确认奖项无误，点击提交</el-button>
    </div>

    <!--  上传成功界面-->
    <div v-show="stepsActive==3">
      <el-result icon="success" title="批量导入成功" subTitle="请等待学院和教务处通过">
        <!--      <template slot="extra">-->
        <!--        <el-button type="primary" size="medium">返回</el-button>-->
        <!--      </template>-->
      </el-result>
    </div>

    <!-- 重名修改界面-->
    <el-dialog :title="DuplicatedDetail.title" :visible.sync="DuplicatedNameChangeWindowIsVisible" :before-close="CloseDuplicateWindow" style="text-align: center;">
      <el-table :data="DuplicatedDetail.entity" style="width: 100%">
        <el-table-column prop="name" label="姓名" width="250">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.name" placeholder="请输入姓名"></el-input>
            <div v-else class="txt">{{scope.row.name}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="number" label="学号/工号" width="250">
          <template slot-scope="scope">
            <el-select v-model="scope.row.number" placeholder="请选择">
              <el-option
                v-for="item in scope.row.options"
                :key="item.id"
                :label="item.id + '   ' + item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column prop="number" label="修改提示" width="400">
          <template slot-scope="scope">
            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.name" placeholder="请输入内容"></el-input>
            <div v-if="(scope.row.options.length > 2 && DuplicatedDetail.title === '检查教师重名情况') || (scope.row.options.length > 3  && DuplicatedDetail.title === '检查学生重名情况')" class="txt">
              <i class="el-icon-warning" style="font-size: 18px; color: red">请从重名老师/学生中选择一位</i>
            </div>
            <div v-if="(scope.row.options.length < 2 && DuplicatedDetail.title === '检查教师重名情况') || (scope.row.options.length < 3  && DuplicatedDetail.title === '检查学生重名情况')" class="txt">
              <i class="el-icon-warning" style="font-size: 18px; color: red">请确认该名老师/学生是否属于校内；是否是研究生</i>
            </div>
            <div v-else class="txt"></div>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" @click="saveDuplicateWindow()" :loading="loading" style="margin-top: 30px">确定并保存</el-button>
      <el-button @click="CloseDuplicateWindow()" :loading="loading" style="margin-top: 30px">取消</el-button>
    </el-dialog>

  </div>
</template>
<script>
import {uploadTemplate, editImportData, appendImportData, submitImportData} from '@/api/dcims/team'
import {listCompetition} from "@/api/dcims/competition";
import {listStudentDict, listTeacherDict} from "@/api/dcims/basicData";
export default {
  name: 'TeamBatchImport',
  dicts: ['dcims_years', 'dcims_award_level', 'dcims_award_level'],
  data() {
    return {
      stepsActive: 0,
      // stepsActive: 1,
      selectedYear: '',
      loading: false,
      uploadFile: [],
      appendFile: [],
      team: [],
      teamIndex: undefined,
      formColumns: [
        { prop: 'year', label: '年份', width: '180' },
        { prop: 'competitionName', label: '竞赛名称', width: '180' },
        { prop: 'studentName', label: '学生名单', width: '180' },
        { prop: 'teacherName', label: '指导教师名单', width: '180' },
        { prop: 'name', label: '队伍名称', width: '180' },
        { prop: 'worksName', label: '竞赛作品名', width: '180' },
        { prop: 'competitionTime', label: '比赛时间', width: '180' },
        { prop: 'awardTime', label: '获奖时间', width: '180' },
        { prop: 'supportMaterialFileName', label: '获奖佐证材料', width: '180' },
        { prop: 'isSingle', label: '是否为单人赛', width: '180' },
        { prop: 'operation', label: '操作(删除)', width: '180'}
      ],
      appendType: '追加',
      selectedOpen: false,
      // 可选竞赛
      optionsCompetition: [],
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
    }
  },
  created() {

  },
  methods: {
    // 推进到下一个步骤
    nextActive() {
      console.log("active:" + this.stepsActive)
      if (this.stepsActive <= 3) {
        this.stepsActive++;
        this.loading = false
      }
    },
    // 回退到上一个步骤
    minusStep() {
      this.stepsActive--;
    },
    // 检查上传文件是否合规
    beforeUpload(file){
      // 必须是压缩文件，大小小于500MB
      const fileExtensionRegex = /\.(zip|rar|7z|tar\.gz|tar\.bz2|tar\.xz|tar|gz|bz2|xz)$/i;
      const isCompressedFile = fileExtensionRegex.test(file.name);
      const fileSize = file.size / 1024 / 1024 < 500;

      if(!isCompressedFile){
        this.$message.warning("请检查文件类型是否是压缩文件！")
      }
      if(!fileSize){
        this.$message.warning("请检查文件是否超出大小限制！")
      }
      return isCompressedFile && fileSize
    },
    // 下载按钮被点击，执行下载模板的动作
    downloadTemplate(){
      if (this.selectedYear === ''){
        this.$message.warning("请先选择一个年份后再下载模板！")
      }else {
        this.loading = true

        this.download('/dcims/team/importTemplate?annual=' + this.selectedYear, {
          ...this.queryParams
        }, `${this.selectedYear}年科技竞赛批量导入获奖团队模板.xlsx`)
        this.nextActive()
      }
    },
    // 页面内的上传按钮被点击
    clickUploadButton(){
      this.$refs.upload0.submit()
    },
    // 处理文件上传
    handleFileUpload(file){
      var flag1 = false
      this.loading = true
      // 调用后端服务器的接口
      uploadTemplate(file.file).then((resp) => {
        this.team = resp.data
        this.teamIndex = resp.id
        this.nextActive()
      }).catch((e) => {
        this.$message.error(e.message);
      }).finally(() => {
        this.uploadFile = []
        this.classifyErrorType()
        console.log(this.team)
        this.loading = false
      })
    },
    // 移除上传文件
    removeUploadFile(){
      this.uploadFile = []
      this.appendFile = []
    },
    // 页面内的追加按钮被点击
    clickAppendButton(){
      this.$refs.upload1.submit()
    },
    // 处理文件追加
    appendData(file){
      this.loading = true
      var app = ""
      if (this.appendType === "追加")
        app = "append"
      if (this.appendType === "覆盖")
        app = "cover"
      if (app !== "" && this.teamIndex !== undefined){
        appendImportData(this.teamIndex, app, file.file).then(resp => {
          this.team = resp.data
          this.teamIndex = resp.id
        }).catch((e) => {
          this.$message.error(e.message);
        }).finally(() => {
          this.appendFile = []
          this.classifyErrorType()
          this.loading = false
        })
      }else{
        this.$message.warning("出错了，请确认数据是否存在！")
      }
    },
    // 提交数据
    submit(){
      if (this.teamIndex !== undefined){
        submitImportData(this.teamIndex).then(resp => {
          this.$message.success("提交成功！")
          this.nextActive()
        })
      }else{
        this.$message.warning("出错了，请确认数据是否存在！")
      }
    },
    /** 鼠标移入cell */
    handleCellEnter (row, column, cell, event) {
      row.edit = true
      console.log(row)
    },
    /** 鼠标移出cell */
    handleCellLeave (row, column, cell, event) {
      if (this.selectedOpen){

      }else {
        row.edit = false
      }
    },
    handleSelectOpened(){
      this.selectedOpen = true
    },
    handleSelectClosed(){
      this.selectedOpen = false
    },


    /** 年份变化 */
    annualChange(year){
      console.log(year)
      this.queryCompetition(year)
    },

    /** 查询竞赛列表 */
    queryCompetition(year) {
      console.log(year)
      const params = {
        annual: year
      };
      setTimeout(() => {
        listCompetition(params).then(response => {
          this.optionsCompetition = response.rows;
        }).finally(() => {
          this.loadingCompetition = false;
        })
        console.log(this.optionsCompetition)
      }, 200);
    },
    /** 删除按钮操作 */
    handleDelete(row){
      console.log(row)
      console.log(this.team)
      var new_arr = []
      for(let i = 0; i < this.team.length; i++){
        this.team[i].index != row.index && new_arr.push(this.team[i])
      }
      this.team = new_arr
      console.log(this.team)
      this.clickChangeButton();
    },
    /** 保存对表格的修改 */
    clickChangeButton(){
      editImportData(this.teamIndex, this.team).then(resp => {
        this.team = resp.data
        this.teamIndex = resp.id
      }).catch((e) => {
        this.$message.error(e.message);
      }).finally(() => {
        this.appendFile = []
        this.classifyErrorType()
        let tempTeam = this.team
        this.$set(this, 'team', [])
        this.$set(this, 'team', tempTeam)
        this.loading = false
        this.$modal.msgSuccess("修改成功");
      })
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
    /** 对错误类型进行分类 */
    classifyErrorType() {
      this.team.forEach(entity => {
        for(let i = 0; i < entity.errors.length; i++){
          let err = entity.errors[i]
          switch (err.errorType){
            case "teacherNameRepeatError":
              if (!entity.hasOwnProperty("teacherNameRepeatError")){
                this.$set(entity, "teacherNameRepeatError", [])
              }
              entity.teacherNameRepeatError.push(err)
              break;
            case "teacherNameNotFoundError":
              if (!entity.hasOwnProperty("teacherNameNotFoundError")){
                this.$set(entity, "teacherNameNotFoundError", [])
              }
              entity.teacherNameNotFoundError.push(err)
              break;
            case "studentNameRepeatError":
              if (!entity.hasOwnProperty("studentNameRepeatError")){
                this.$set(entity, "studentNameRepeatError", [])
              }
              entity.studentNameRepeatError.push(err)
              break;
            case "studentNameNotFoundError":
              if (!entity.hasOwnProperty("studentNameNotFoundError")){
                this.$set(entity, "studentNameNotFoundError", [])
              }
              entity.studentNameNotFoundError.push(err)
              break;
            case "competitionNameError":
              if (!entity.hasOwnProperty("competitionNameError")){
                this.$set(entity, "competitionNameError", [])
              }
              entity.competitionNameError.push(err)
              break;
            case "isSingleError":
              if (!entity.hasOwnProperty("isSingleError")){
                this.$set(entity, "isSingleError", [])
              }
              entity.isSingleError.push(err)
              break;
            case "awardLevelError":
              if (!entity.hasOwnProperty("awardLevelError")){
                this.$set(entity, "awardLevelError", [])
              }
              entity.awardLevelError.push(err)
              break;
            case "fileNotFoundError":
              if (!entity.hasOwnProperty("fileNotFoundError")){
                this.$set(entity, "fileNotFoundError", [])
              }
              entity.fileNotFoundError.push(err)
              break;
            case "tooMuchStudentError":
              if (!entity.hasOwnProperty("tooMuchStudentError")){
                this.$set(entity, "tooMuchStudentError", [])
              }
              entity.tooMuchStudentError.push(err)
              break;
          }
        }
      })
      this.$set(this, 'team', this.team)
    },
    /** 打开重名核验窗口 */
    OpenDuplicateWindow(title, errMsg, names, ids, index) {
      this.DuplicatedNameChangeWindowIsVisible = true;
      this.DuplicatedDetail.index = index;
      this.DuplicatedDetail.title = title;
      this.DuplicatedDetail.errMsg = errMsg;

      const NamesArray = names.split(',');
      const idsArray = ids.split(',');
      for (let i = 0; i < NamesArray.length; i++){
        let tmp = {
          name: NamesArray[i],
          number: idsArray[i],
          options: {}
        }

        this.DuplicatedDetail.entity.push(tmp)
        if (title === "检查教师重名情况"){
          this.queryTeacher(NamesArray[i], i)
        }else{
          this.queryStudent(NamesArray[i], i)
        }
      }
      console.log(this.DuplicatedDetail.entity)
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
    queryTeacher(query, index) {
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
    queryStudent(query, index) {
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
      // 检查是否都修改正确
      for (let i = 0; i< this.DuplicatedDetail.entity.length; i++){
        if (this.DuplicatedDetail.entity[i].number == "-1"){
          console.log(this.DuplicatedDetail.entity[i].number)
          console.log(typeof this.DuplicatedDetail.entity[i].number)
          this.$message.warning("请全部按照提示修改后再进行保存！")
          return false;
        }
      }



      for (let i = 0; i< this.team.length; i++){
        if (this.team[i].index === this.DuplicatedDetail.index){
          let n = ""
          let v = ""
          for(let j = 0; j<this.DuplicatedDetail.entity.length; j++){
            let e = this.DuplicatedDetail.entity[j]
            n = n + e.name + ","
            v = v + e.number + ","
          }

          if (this.DuplicatedDetail.title === "检查教师重名情况"){
            this.team[i].teacherName = n.slice(0, n.length - 1)
            this.team[i].teacherId = v.slice(0, v.length - 1)
          }else{
            this.team[i].studentName = n.slice(0, n.length - 1)
            this.team[i].studentId = v.slice(0, v.length - 1)
          }
          this.CloseDuplicateWindow();
          break;
        }
      }


      this.clickChangeButton();
    },
    /** 前端移除某条错误,也就是移除row对象中的错误属性 */
    removeError(row, errorType){
      this.$delete(row, errorType)
    }

  }
}
</script>
