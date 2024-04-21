package com.ruoyi.system.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ExcelWriteHandler implements SheetWriteHandler {
    @Autowired
    IDcimsCompetitionService competitionService;
    @Autowired
    IDcimsGlobalSettingService globalSettingService;
    @Autowired
    ISysDictTypeService dictService;

    /**
     * 处理批量导入获奖团队模板
     */
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        // 获取可选竞赛名和获奖级别
        List<String> annualList = globalSettingService.getAnnualList();
        // 只取最后年份的可选竞赛名
        DcimsCompetitionBo competitionBo = new DcimsCompetitionBo();
        competitionBo.setAnnual(Integer.valueOf(annualList.get(annualList.size() - 1)));
        String[] competitionNames = competitionService.queryList(competitionBo).stream().map(DcimsCompetitionVo::getName).toArray(String[]::new);
        // 从字典中获取获奖级别
        List<SysDictData> dcimsAwardLevel = dictService.selectDictDataByType("dcims_award_level");
        String[] awardLevels = dcimsAwardLevel.stream().map(SysDictData::getDictLabel).toArray(String[]::new);

        // 设置可选年份
        CellRangeAddressList cellRangeAddressListAnnual = new CellRangeAddressList(3, 9999, 0, 0);
        DataValidationHelper AnnualValidationHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        DataValidationConstraint AnnualConstraint = AnnualValidationHelper.createExplicitListConstraint(annualList.toArray(new String[0]));
        DataValidation AnnualValidation = AnnualValidationHelper.createValidation(AnnualConstraint, cellRangeAddressListAnnual);
        context.getWriteSheetHolder().getSheet().addValidationData(AnnualValidation);

        // 设置可选竞赛名称
        CellRangeAddressList cellRangeAddressListCompetitionName = new CellRangeAddressList(3, 9999, 1, 1);
        DataValidationHelper NameValidationHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        DataValidationConstraint NameConstraint = NameValidationHelper.createExplicitListConstraint(competitionNames);
        DataValidation NameValidation = NameValidationHelper.createValidation(NameConstraint, cellRangeAddressListCompetitionName);
        context.getWriteSheetHolder().getSheet().addValidationData(NameValidation);

        // 设置获奖级别
        CellRangeAddressList cellRangeAddressListAwardLevel = new CellRangeAddressList(3, 9999, 2, 2);
        DataValidationHelper AwardLevelValidationHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        DataValidationConstraint AwardLevelConstraint = AwardLevelValidationHelper.createExplicitListConstraint(awardLevels);
        DataValidation AwardLevelValidation = AwardLevelValidationHelper.createValidation(AwardLevelConstraint, cellRangeAddressListAwardLevel);
        context.getWriteSheetHolder().getSheet().addValidationData(AwardLevelValidation);
    }
}
