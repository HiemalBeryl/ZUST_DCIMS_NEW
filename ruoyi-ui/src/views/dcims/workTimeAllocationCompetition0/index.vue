<template>
    <div>
        <div id="launchAllocation" v-if="status == 0">
            <h1>发起年度工作量分配</h1>
            <el-divider></el-divider>
            <!-- 需要设置起止时间、计算公式、每个奖项对应的系数，后端拿到后应该存储起来！ -->
            <el-form>
                <el-form-item label="起止时间">
                  <el-date-picker
                    v-model="initForm.Time"
                    style="width: 240px"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :default-time="['00:00:00', '23:59:59']"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item label="工作量计算公式" prop="pastName">
                    <el-input
                    v-model="initForm.formula"
                    placeholder="请输入工作量计算公式"
                    clearable
                    @keyup.enter.native="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="奖项对应系数" prop="">
                    <el-input
                    v-model="initForm.factor"
                    placeholder="请输入奖项对应系数"
                    clearable
                    @keyup.enter.native="handleQuery"
                    />
                </el-form-item>
            </el-form>
        </div>
        <div id="duringAllocation" v-if="status == 1">
            <h1>年度工作量分配一览</h1>
            <el-divider></el-divider>

        </div>
    </div>
</template>

<script>
import { getWorkTimeAllocationStatus, } from "@/api/dcims/workTimeAllocationCompetition0";

export default {
    name: "workTimeAllocation",
    dicts: [],
    data() {
        return {
            // 工作量分配模块的状态,0-未开始,1-进行中,2-已结束
            status: undefined,
            // 发起年度工作量分配页面表单
            initForm: [],

        }
    },
    created() {

    },
    methods: {
        // 查询工作量分配的状态
        getStatus(){
            getWorkTimeAllocationStatus().then(response => {
                this.status = response.data;
            })
        },
    },
}
</script>
