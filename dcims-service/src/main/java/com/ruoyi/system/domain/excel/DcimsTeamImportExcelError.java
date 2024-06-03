package com.ruoyi.system.domain.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DcimsTeamImportExcelError {
    /**
     * 错误类型
     */
    public enum ErrorType{
        ExcelPropertyError,
        competitionNameError,
        awardLevelError,
        competitionTimeError,
        awardTimeError,
        FileNotFoundError,
        isSingleError,
        studentNameRepeatError,
        studentNameNotFoundError,
        teacherNameRepeatError,
        teacherNameNotFoundError,
    }

    private ErrorType errorType;

    /**
     * 错误消息;
     */
    private String errorMessage;

    /**
     * 修复错误消息的选项
     */
    private Object options;

    public DcimsTeamImportExcelError(ErrorType errorType,String errorMessage){
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }
}
