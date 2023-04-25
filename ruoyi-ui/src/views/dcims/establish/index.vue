<template> <!--赛事立项 -->
<div>

   <div style="border-bottom: 1px solid #dbdbdb;height:60px;width:600px ;">
     <h1 style="margin-top:20px">竞赛立项</h1>
   </div>
   
  <el-form :model="ruleForm"  ref="ruleForm" label-width="150px" class="demo-ruleForm" label-position="right" style="margin-top:20px">
  

 <el-row>
   <el-col>
 <el-form-item label="立项赛事" prop="region" required>
    <el-select v-model="ruleForm.name" placeholder="请选择赛事名称">
      <el-option label="蓝桥杯" value="蓝桥杯"></el-option>
      <el-option label="icpc" value="icpc"></el-option>
      <el-option label="服务外包" value="服务外包"></el-option>
    </el-select>
  </el-form-item>
  </el-col>

 <el-col :span="5">
  <el-form-item label="立项届次" prop="term" >
    <el-input type="text" v-model="ruleForm.term" style="width:100px" placeholder="2届"></el-input>
  </el-form-item>
   </el-col>


<el-col :span="5">
<el-form-item label="赛事性质" style="width:250px">
 <el-select v-model="ruleForm.level" placeholder="A类">
      
      <el-option label="A类" value="A类"></el-option>
       <el-option label="B类" value="B类"></el-option>
        <el-option label="C类" value="C类"></el-option>
        <el-option label="其他" value="其他"></el-option>

    </el-select> 

  </el-form-item>

</el-col>

<el-col :span="5">
   <el-form-item label="赛事年份" prop="annual"  required>
    <el-input type="text" v-model="ruleForm.annual" style="width:150px" ></el-input>
  </el-form-item>
</el-col>


 </el-row>



<el-row>
<el-col :span="5">
   <el-form-item label="主办单位" prop="organizer"  required>
    <el-input type="text" v-model="ruleForm.organizer" style="width:150px" ></el-input>
  </el-form-item>
</el-col>

<el-col :span="6">
     <el-form-item label="竞赛负责人" prop="responsiblePersonName"  required>
    <el-input type="text" v-model="ruleForm.responsiblePersonName" style="width:150px" ></el-input>
  </el-form-item>
</el-col>

<el-col :span="5">
   <el-form-item label="竞赛负责人工号" prop="responsiblePersonId"  required>
    <el-input type="text" v-model="ruleForm.responsiblePersonId" style="width:150px" ></el-input>
  </el-form-item>
</el-col>
</el-row>




<el-row>
<el-col :span="5">
   <el-form-item label="立项开始时间" prop="startTime" >
    <el-input type="text" v-model="ruleForm.startTime" style="width:150px" ></el-input>
  </el-form-item>
</el-col>

<el-col :span="5">
   <el-form-item label="立项结束时间" prop="stopTime" >
    <el-input type="text" v-model="ruleForm.stopTime" style="width:150px" ></el-input>
  </el-form-item>
</el-col>

</el-row>

 
  <!-- <el-form-item label="赛事性质" prop="type">
     <el-radio-group v-model="ruleForm.radio">
    <el-radio :label="3">A类</el-radio>
    <el-radio :label="6">B类</el-radio>
    <el-radio :label="9">C类</el-radio>
    <el-radio :label="12">其他</el-radio>
  </el-radio-group>
  </el-form-item> -->



  

<!-- <el-form-item label="立项时间" style="width:500px ">
    <el-col :span="11">
      <el-form-item prop="day">
        <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.day" style="width: 100%;"></el-date-picker>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-form-item prop="time">
        <el-time-picker placeholder="选择时间" v-model="ruleForm.time" style="width: 100%;"></el-time-picker>
      </el-form-item>
    </el-col>
  </el-form-item> -->



    <el-form-item label="校内选拔赛时间" required style="width:500px">
    <el-col :span="11">
      <el-form-item prop="date1">
        <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.date1" style="width: 100%;"></el-date-picker>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-form-item prop="date2">
        <el-time-picker placeholder="选择时间" v-model="ruleForm.date2" style="width: 100%;"></el-time-picker>
      </el-form-item>
    </el-col>
  </el-form-item>


    <el-form-item label="省赛时间" required style="width:500px">
    <el-col :span="11">
      <el-form-item prop="prodate1">
        <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.prodate1" style="width: 100%;"></el-date-picker>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-form-item prop="prodate2">
        <el-time-picker placeholder="选择时间" v-model="ruleForm.prodate2" style="width: 100%;"></el-time-picker>
      </el-form-item>
    </el-col>
  </el-form-item>


    <el-form-item label="国赛时间" required style="width:500px">
    <el-col :span="11">
      <el-form-item prop="bigdate1">
        <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.bigdate1" style="width: 100%;"></el-date-picker>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-form-item prop="bigdate2">
        <el-time-picker placeholder="选择时间" v-model="ruleForm.bigdate2" style="width: 100%;"></el-time-picker>
      </el-form-item>
    </el-col>
  </el-form-item>



<el-row>
  <el-col :span="6">

   <el-form-item label="本年度申报经费"  required style="width:500px" >
    <el-col :span="11">
      <el-form-item prop="yearmoney">
        <el-input type="number" v-model="ruleForm.yearmoney" ></el-input>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">万元</el-col>
    </el-form-item>

  </el-col>

  <el-col :span="6">


    <el-form-item label="本年度拨款"  required style="width:500px" >
    <el-col :span="11">
      <el-form-item prop="yearneedmoney">
        <el-input type="number" v-model="ruleForm.yearneedmoney" ></el-input>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">万元</el-col>
    </el-form-item>

  </el-col>
  
  
  <el-col :span="6">


    <el-form-item label="集中授课时数"  required style="width:500px" >
    <el-col :span="11">
      <el-form-item prop="teachtime">
        <el-input type="number" v-model="ruleForm.teachtime" ></el-input>
      </el-form-item>
    </el-col>
    <el-col class="line" :span="2">课时</el-col>
    </el-form-item>


  </el-col>

  </el-row>
  

  
     
  <el-form-item label="本年度获奖目标" prop="rewardtarget">
    <el-input type="text" v-model="ruleForm.rewardtarget" style="width:300px" ></el-input>
  </el-form-item>



 
<!--竞赛申报书上传 -->
<el-form-item label="竞赛申报书上传:">
  
<el-upload
  class="avatar-uploader"
  action="https://jsonplaceholder.typicode.com/posts/"
  :show-file-list="false"
  :on-success="handleAvatarSuccess"
  :before-upload="beforeAvatarUpload">
  <img v-if="imageUrl" :src="imageUrl" class="avatar">
  <i v-else class="el-icon-plus avatar-uploader-icon"></i>
</el-upload>
</el-form-item>




  <!-- 最下方按钮 -->
  <el-form-item>
    <el-button type="primary" @click="submitForm(ruleForm)">立即创建</el-button>
    <el-button @click="resetForm('ruleForm')">重置</el-button>
  </el-form-item>
</el-form>

</div>
</template>
<script>
import axios from 'axios'
  export default {
      name:'LiXiang',
    data() {
      return {
        //用于上传图片的data
        imageUrl: '',
        //
      

      
        ruleForm: {
          level:'',         //赛事性质
          name: '', //立项赛事名称
          term:'',   //立项届次
          annual:'', //赛事年份

          startTime: '', //立项时间
          stopTime:'', //立项结束时间
          innerTime:'',     //校内选拔赛时间
          provinceTime:'',//省赛时间
          nationalTime:'' , //国赛时间
          
          nextAuditId:''  , //审核人id        
          organizer:'',   //主办单位
          responsiblePersonName:'', //赛事负责人
          responsiblePersonId:'',   //赛事负责人ID
          
          prodate1:'',      //省赛时间
          prodate2:'',
          bigdate1:'',      //国赛时间1
          bigdate2:'',      //国赛时间
          yearmoney:'',     //年度申报经费
          yearneedmoney:'', //年度拨款
          teachtime:'',     //授课时长
          rewardtarget:'',  //本年度获奖目标
          type: '',
        
          resource: '',
          desc: ''
        },
         time1:'',
         person:'',
         person2:'',
         money:'',
         money2:'',
         teachtime:'',
         rewardtarget:'',

         
        rules: {
          name: [
            { required: true, message: '请输入活动名称', trigger: 'blur' },
            { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ],
          region: [
            { required: true, message: '请选择活动区域', trigger: 'change' }
          ],
          date1: [
            { type: 'date', required: true, message: '请选择日期', trigger: 'change' }
          ],
          date2: [
            { type: 'date', required: true, message: '请选择时间', trigger: 'change' }
          ],
          type: [
            { type: 'array', required: true, message: '请至少选择一个活动性质', trigger: 'change' }
          ],
          resource: [
            { required: true, message: '请选择活动资源', trigger: 'change' }
          ],
          desc: [
            { required: true, message: '请填写活动形式', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {

      //上交项目
      submitForm(formName){

         var data={
            "name": formName.name,
            "level":formName.level,
            'term':formName.term,
            "annual": formName.annual,
            "responsiblePersonName": formName.responsiblePersonName,
            "organizer":formName.organizer,
            'responsiblePersonId':formName.responsiblePersonId
              
         }
          
          const axios1 = axios.create({
            baseURL:'/dev-api/dcims',
          }) 
          // axios.post({
          //    baseURL:'',
             
          // })


            var config = {
              method: 'post',
               headers: {
                'Authorization':"Bearer " + window.localStorage.getItem('token')
                },
              url: '/competition',
              data:data
            };

            axios1(config)
            .then(function (response) {
              console.log(JSON.stringify(response.data));
              alert("立项增加成功 请前往当前年份赛事信息查看")
            })
            .catch(function (error) {
              console.log(error);
            });






        // this.$refs[formName].validate((valid) => {
        //   if (valid) {
        //     alert('submit!');
        //   } else {
        //     console.log('error submit!!');
        //     return false;
        //   }
        // });
      },




      resetForm(formName) {
        this.$refs[formName].resetFields();
      },
  

//用于上传图片格式限制

           handleAvatarSuccess(res, file) {
        this.imageUrl = URL.createObjectURL(file.raw);
      },


      
      beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 1024 < 2;

        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
      }
    }
  }
</script>



<style  scoped>
   /* 此处style全部为申报书上传style */
  .avatar-uploader  {
    width:300px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>>
  