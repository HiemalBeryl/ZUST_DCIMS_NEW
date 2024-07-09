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
    </div>

    <!--  上传模板界面-->
    <div v-show="stepsActive==1" style="text-align: center;">
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
        <div slot="tip" class="el-upload__tip">请上传附带填写好的模板以及附带获奖材料的压缩包，文件大小小于100MB</div>
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
            <el-select v-if="scope.row.edit" v-model="scope.row.competitionName" placeholder="请选择竞赛名称" @focus="handleSelectOpened()" @blur="handleSelectClosed()">
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
            <div class="txt">{{scope.row.teacherName}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="studentName" label="学生名单" width="180">
          <template slot-scope="scope">
<!--            <el-input v-if="scope.row.edit" class="item" v-model="scope.row.studentName" placeholder="请输入内容"></el-input>-->
<!--            <div v-else class="txt">{{scope.row.studentName}}</div>-->
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

        <el-table-column prop="competitionTime" label="比赛时间" width="180">
          <template slot-scope="scope">
            <el-date-picker clearable
                            v-if="scope.row.edit"
                            v-model="scope.row.competitionTime"
                            type="datetime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择获奖时间"
                            @focus="handleSelectOpened()" @blur="handleSelectClosed()">
            </el-date-picker>
            <div v-else class="txt">{{scope.row.competitionTime}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="awardTime" label="获奖时间" width="180">
          <template slot-scope="scope">
            <el-date-picker clearable
                            v-if="scope.row.edit"
                            v-model="scope.row.awardTime"
                            type="datetime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择获奖时间"
                            @focus="handleSelectOpened()" @blur="handleSelectClosed()">
            </el-date-picker>
            <div v-else class="txt">{{scope.row.awardTime}}</div>
          </template>
        </el-table-column>

        <el-table-column prop="supportMaterialFileName" label="获奖佐证材料" width="180">
          <template slot-scope="scope">
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
      <el-button style="margin-left: 10px;" type="success" @click="clickChangeButton()" :loading="loading" style="margin-bottom: 30px">保存修改</el-button>
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
        <div slot="tip" class="el-upload__tip">请上传附带填写好的模板以及附带获奖材料的压缩包，文件大小小于100MB</div>
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
  </div>
</template>
<script>
import {uploadTemplate, editImportData, appendImportData, submitImportData} from '@/api/dcims/team'
import {listCompetition} from "@/api/dcims/competition";
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
      // 必须是压缩文件，大小小于100MB
      const fileExtensionRegex = /\.(zip|rar|7z|tar\.gz|tar\.bz2|tar\.xz|tar|gz|bz2|xz)$/i;
      const isCompressedFile = fileExtensionRegex.test(file.name);
      const fileSize = file.size / 1024 / 1024 < 100;

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
      this.$modal.msgSuccess("删除成功");
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
        this.loading = false
        this.$modal.msgSuccess("保存成功");
      })
    },
    checkFileSuffix(fileSuffix) {
      console.log(fileSuffix)
      let arr = ["png", "jpg", "jpeg"];
      return arr.some(type => {
        return fileSuffix.indexOf(type) > -1;
      });
    },
    /** 打开新窗口 */
    openNewTab(url) {
      window.open(url, '_blank');
    },
  }
}
</script>
