<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="年份" prop="years">
        <el-input
          v-model="queryParams.years"
          placeholder="请输入年份"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学院" prop="college">
        <el-select v-model="queryParams.college" placeholder="请选择学院" clearable>
          <el-option
            v-for="dict in dict.type.dcims_college"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dcims:bonusAllocation:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['dcims:bonusAllocation:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['dcims:bonusAllocation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dcims:bonusAllocation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bonusAllocationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键" align="center" prop="id" v-if="true"/> -->
      <el-table-column label="年份" align="center" prop="years" />
      <el-table-column label="学院" align="center" prop="college">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dcims_college" :value="scope.row.college"/>
        </template>
      </el-table-column>
      <el-table-column label="奖金总数" align="center" prop="totalAmount" />
      <!-- <el-table-column label="留存比例" align="center" prop="retentionRatio" />
      <el-table-column label="可分配总额" align="center" prop="distributable" />
      <el-table-column label="已分配金额" align="center" prop="allocated" />
      <el-table-column label="未分配金额" align="center" prop="unallocated" /> -->
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="负责教师" align="center" prop="teacherInCharge" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button 
            size="mini"
            type="text"
            icon="el-icon-s-order"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:bonusAllocation:edit']"
            v-if="isCounted === false"
          >查看详情</el-button>

          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dcims:bonusAllocation:edit']"
            v-if="isCounted === true"
          >修改</el-button>
          
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dcims:bonusAllocation:remove']"
            v-if="isCounted === true"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row>

      <el-col :span="12">
        <el-button  class="countButton" @click="setTime()" >计算 </el-button>
        <el-button  type="warning" class="countButton" @click="upload()" v-show="!countButtonShow" :disabled="uploadDisabled">{{uploadButtonText}}</el-button>
      </el-col>
      <el-col :span="12">
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>

<!-- 
    <el-button>默认按钮</el-button>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    -->
   

    <!-- 查看“奖金分配总”详情的对话框 -->
    <el-dialog :title="title" :visible.sync="openDetail" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="年份" prop="years">
          <el-input v-model="form.years" :disabled="true" placeholder="请输入年份" />
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-select v-model="form.college" :disabled="true" placeholder="请选择学院">
            <el-option
              v-for="dict in dict.type.dcims_college"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="奖金总数" prop="totalAmount">
          <el-input v-model="form.totalAmount" :disabled="true" placeholder="请输入奖金总数" />
        </el-form-item>
        <el-form-item label="留存比例" prop="retentionRatio">
          <el-input v-model="form.retentionRatio" :disabled="true" placeholder="请输入留存比例" />
        </el-form-item>
        <el-form-item label="可分配总额" prop="distributable">
          <el-input v-model="form.distributable" :disabled="true" placeholder="请输入可分配总额" />
        </el-form-item>
        <el-form-item label="已分配金额" prop="allocated">
          <el-input v-model="form.allocated" :disabled="true" placeholder="请输入已分配金额" />
        </el-form-item>
        <el-form-item label="未分配金额" prop="unallocated">
          <el-input v-model="form.unallocated" :disabled="true" placeholder="请输入未分配金额" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="负责教师" prop="teacherInCharge">
          <el-input v-model="form.teacherInCharge" placeholder="请输入负责教师" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm" >确 定</el-button>
        <el-button @click="cancel()">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑待提交的年度奖金汇总对话框 -->
    <el-dialog title="编辑奖金" :visible.sync="openDetail" width="800px" append-to-body>
      
    </el-dialog>

    <!-- 设置起止时间对话框 -->
    <el-dialog :title="title" :visible.sync="setTimeOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="开始时间" prop="countStartTime">
          <el-date-picker clearable
            v-model="form.countStartTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="countEndTime">
          <el-date-picker clearable
            v-model="form.countEndTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="setTimeSubmit()" >确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-form>
        
    </el-dialog>
    
  </div>
</template>

<script>
import {
  listBonusAllocation,
  getBonusAllocation,
  delBonusAllocation,
  addBonusAllocation,
  updateBonusAllocation,
  setTimeOfBonus,
  saveBonus
} from "@/api/dcims/bonusAllocation";

export default {
  name: "BonusAllocation",
  dicts: ['dcims_college'],
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
      // 奖金分配总表格数据
      bonusAllocationList: [],
      // 奖金分配个人数据
      bonusAllocationPersonalList: [],
      // 弹出层标题
      title: "",
      // 是否显示查看详情对话框
      openDetail: false,
      // 是否显示编辑信息对话框
      openEditor: false,
      // 是否显示设置起止时间的窗口
      setTimeOpen: false,
      // 是否显示计算按钮
      countButtonShow: true,
      // 上传是否不可用
      uploadDisabled: false,
      // 上传按钮上显示的文字
      uploadButtonText: "上传",
      // 是否进行过奖金计算
      isCounted: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        years: undefined,
        college: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键不能为空", trigger: "blur" }
        ],
        years: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        college: [
          { required: true, message: "学院不能为空", trigger: "change" }
        ],
        totalAmount: [
          { required: true, message: "奖金总数不能为空", trigger: "blur" }
        ],
        retentionRatio: [
          { required: true, message: "留存比例不能为空", trigger: "blur" }
        ],
        distributable: [
          { required: true, message: "可分配总额不能为空", trigger: "blur" }
        ],
        allocated: [
          { required: true, message: "已分配金额不能为空", trigger: "blur" }
        ],
        unallocated: [
          { required: true, message: "未分配金额不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
        teacherInCharge: [
          { required: true, message: "负责教师不能为空", trigger: "blur" }
        ],
        countStartTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        countEndTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询奖金分配总列表 */
    getList() {
      this.loading = true;
      listBonusAllocation(this.queryParams).then(response => {
        this.bonusAllocationList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.openDetail = false;
      this.setTimeOpen = false;
      this.openEditor = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        years: undefined,
        college: undefined,
        totalAmount: undefined,
        retentionRatio: undefined,
        distributable: undefined,
        allocated: undefined,
        unallocated: undefined,
        startTime: undefined,
        endTime: undefined,
        teacherInCharge: undefined,
        version: undefined,
        createTime: undefined,
        createBy: undefined,
        updateTime: undefined,
        updateBy: undefined,
        delFlag: undefined,
        countStartTime: undefined,
        countEndTime: undefined,
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
      this.openDetail = true;
      
      this.title = "添加奖金分配总";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getBonusAllocation(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.openDetail = true;
        this.title = "修改奖金分配总";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateBonusAllocation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.openDetail = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addBonusAllocation(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.openDetail = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        }
      });
    },

    // 设定时间提交按钮
    setTimeSubmit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
         
          this.buttonLoading = true;
          setTimeOfBonus(this.form).then(response => {
            this.$modal.msgSuccess("计算成功");
            this.countButtonShow = false;
            this.isCounted = true;
            this.setTimeOpen = false;
            this.openDetail = false;
            //TODO 替换表格内容
            this.bonusAllocationList = response[0];
            this.bonusAllocationPersonalList = response[1];
          }).finally(() => {
            this.buttonLoading = false;
          });
        }
      });
    },

    //上传按钮
    upload() {
      this.$modal.confirm('是否确认上传并发布本年度奖金分配，注意一年只能发布一次！！！').then(() => {
        saveBonus(this.bonusAllocationList, this.bonusAllocationPersonalList).then(response => {
        // 按钮改为不可用
        this.uploadDisabled = true;
        this.$modal.msgWarning("上传成功");
        this.uploadButtonText = "已上传";
        }).finally(() =>{
          this.getList;
        })
      })

    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除奖金分配总编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delBonusAllocation(ids);
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
      this.download('dcims/bonusAllocation/export', {
        ...this.queryParams
      }, `bonusAllocation_${new Date().getTime()}.xlsx`)
    },
    // 设置时间按钮操作
    setTime() {
      this.setTimeOpen = true;
      this.title = "设定结算起止时间";
    }
  }
};
</script>

<style>
.countButton{
  margin: 25px 0 ;
}
.dialog-footer{
  text-align: right;
}
.stayRight{
  text-align: right;
}
</style>
