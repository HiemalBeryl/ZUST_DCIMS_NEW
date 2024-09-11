package com.ruoyi.system.domain.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        fileNotFoundError,
        isSingleError,
        studentNameRepeatError,
        studentNameNotFoundError,
        teacherNameRepeatError,
        teacherNameNotFoundError,
        tooMuchStudentError,
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
