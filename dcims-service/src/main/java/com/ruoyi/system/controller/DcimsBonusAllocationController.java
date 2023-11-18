package com.ruoyi.system.controller;

import java.util.*;

import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 奖金分配总
 *
 * @author Andy
 * @date 2023-05-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/bonusAllocation")
public class DcimsBonusAllocationController extends BaseController {

    private final IDcimsBonusAllocationService iDcimsBonusAllocationService;

    /**
     * 查询奖金分配总列表
     */
    @SaCheckPermission("dcims:bonusAllocation:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsBonusAllocationVo> list(DcimsBonusAllocationBo bo, PageQuery pageQuery) {
        return iDcimsBonusAllocationService.queryPageList(bo, pageQuery);
    }

    /**
     * 根据登录账号的工号查询奖金分配部分列表
     */
    @SaCheckPermission("dcims:bonusAllocationPersonal:list")
    @GetMapping("/listByTeacherId")
    public List<DcimsBonusAllocationVo> queryListByTeacherId(){
        return iDcimsBonusAllocationService.queryListByTeacherId();
    }

    /**
     * 导出奖金分配总列表
     */
    @SaCheckPermission("dcims:bonusAllocation:export")
    @Log(title = "奖金分配总", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsBonusAllocationBo bo, HttpServletResponse response) {
        List<DcimsBonusAllocationVo> list = iDcimsBonusAllocationService.queryList(bo);
        ExcelUtil.exportExcel(list, "奖金分配总", DcimsBonusAllocationVo.class, response);
    }

    /**
     * 获取奖金分配总详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:bonusAllocation:query")
    @GetMapping("/{id}")
    public R<DcimsBonusAllocationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsBonusAllocationService.queryById(id));
    }

    /**
     * 新增奖金分配总
     */
    @SaCheckPermission("dcims:bonusAllocation:add")
    @Log(title = "奖金分配总", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsBonusAllocationBo bo) {
        return toAjax(iDcimsBonusAllocationService.insertByBo(bo));
    }

    /**
     * 修改奖金分配总
     */
    @SaCheckPermission("dcims:bonusAllocation:edit")
    @Log(title = "奖金分配总", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsBonusAllocationBo bo) {
        return toAjax(iDcimsBonusAllocationService.updateByBo(bo));
    }

    /**
     * 删除奖金分配总
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:bonusAllocation:remove")
    @Log(title = "奖金分配总", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsBonusAllocationService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 生成并查询某一段时间的竞赛奖金数据
     */
    @SaCheckPermission("dcims:bonusAllocation:generate")
    @GetMapping("/generate")
    public List<Object> generateBonusDataByTime(
        @NotNull(message = "起始时间不能为空") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
        @NotNull(message = "终止时间不能为空") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
        @NotNull(message = "年份不能为空") @RequestParam Integer year
    ){
        return iDcimsBonusAllocationService.generateBonusDataByTime(startTime, endTime, year);
    }

    /**
     * 保存竞赛奖金数据
     */
    @SaCheckPermission("dcims:bonusAllocation:generate")
    @Log(title = "奖金分配总", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/saveList")
    public R<Void> saveBonus(@RequestBody Map<String, Object> json){
        //List<DcimsBonusAllocationBo> allBo = JsonUtils.parseArray((String) json.get("allBo"),DcimsBonusAllocationBo.class);
        List<DcimsBonusAllocationBo> allBo = (ArrayList<DcimsBonusAllocationBo>) json.get("allBo");
        //List<DcimsBonusAllocationPersonalBo> personalBo = JsonUtils.parseArray((String) json.get("personalBo"),DcimsBonusAllocationPersonalBo.class);
        List<DcimsBonusAllocationPersonalBo> personalBo = (ArrayList<DcimsBonusAllocationPersonalBo>) json.get("personalBo");
        System.out.println(allBo);
        System.out.println(personalBo);
        return toAjax(iDcimsBonusAllocationService.insertYearsBonusData(allBo,personalBo));
    }
}
