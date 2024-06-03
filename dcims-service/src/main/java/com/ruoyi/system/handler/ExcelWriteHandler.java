package com.ruoyi.system.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.utils.ExcelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ExcelWriteHandler implements SheetWriteHandler {

    Integer annual;
    List<String> competitionNames;
    List<String> awardLevels;

    /**
     * 处理批量导入获奖团队模板
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 构造下拉选项单元格列的位置，以及下拉选项可选参数值的map集合
        // key：下拉选项要放到哪个单元格，比如A列的单元格那就是0，C列的单元格，那就是2
        // value：key对应的那个单元格下拉列表里的数据项，比如这里就是下拉选项1..100
        Map<Integer, List<String>> selectParamMap = new HashMap<>();
        ArrayList<String> annuals = new ArrayList<>();
        annuals.add(String.valueOf(annual));
        selectParamMap.put(0, annuals);
        selectParamMap.put(1, competitionNames);
        selectParamMap.put(2, awardLevels);

        // 获取第一个sheet页
        Sheet sheet = writeSheetHolder.getCachedSheet();
        // 获取sheet页的数据校验对象
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 获取工作簿对象，用于创建存放下拉数据的字典sheet数据页
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        // 迭代索引，用于存放下拉数据的字典sheet数据页命名
        int index = 1;
        for (Map.Entry<Integer, List<String>> entry : selectParamMap.entrySet()) {

            // 设置存放下拉数据的字典sheet，并把这些sheet隐藏掉，这样用户交互更友好
            String dictSheetName = "dict_hide_sheet" + index;
            Sheet dictSheet = workbook.createSheet(dictSheetName);
            // 隐藏字典sheet页
            workbook.setSheetHidden(index++, true);

            // 设置下拉列表覆盖的行数，从第一行开始到最后一行，这里注意，Excel行的
            // 索引是从0开始的，我这边第0行是标题行，第1行开始时数据化，可根据实
            // 际业务设置真正的数据开始行，如果要设置到最后一行，那么一定注意，
            // 最后一行的行索引是1048575，千万别写成1048576，不然会导致下拉列表
            // 失效，出不来
            CellRangeAddressList infoList = new CellRangeAddressList(3, 1048575, entry.getKey(), entry.getKey());
            int rowLen = entry.getValue().size();
            for (int i = 0; i < rowLen; i++) {
                // 向字典sheet写数据，从第一行开始写，此处可根据自己业务需要，自定
                // 义从第几行还是写，写的时候注意一下行索引是从0开始的即可
                dictSheet.createRow(i).createCell(0).setCellValue(entry.getValue().get(i));
            }

            // 设置关联数据公式，这个格式跟Excel设置有效性数据的表达式是一样的
            String refers = dictSheetName + "!$A$1:$A$" + entry.getValue().size();
            Name name = workbook.createName();
            name.setNameName(dictSheetName);
            // 将关联公式和sheet页做关联
            name.setRefersToFormula(refers);

            // 将上面设置好的下拉列表字典sheet页和目标sheet关联起来
            DataValidationConstraint constraint = helper.createFormulaListConstraint(dictSheetName);
            DataValidation dataValidation = helper.createValidation(constraint, infoList);
            sheet.addValidationData(dataValidation);
        }

        // 为日期列添加数据验证
//        CellRangeAddressList infoList = new CellRangeAddressList(3, 104857, 7, 8);
//        DataValidationConstraint constraint = helper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1900, 1, 1)","Date(9999, 12, 31)","MM/dd/yyyy");
//        DataValidation dataValidation = helper.createValidation(constraint, infoList);
//        dataValidation.createErrorBox("提示","请输入[yyyy-MM-dd]格式日期");
//        dataValidation.setShowErrorBox(true);
//        sheet.addValidationData(dataValidation);

    }
}
