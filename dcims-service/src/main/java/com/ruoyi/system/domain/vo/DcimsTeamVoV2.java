package com.ruoyi.system.domain.vo;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import com.ruoyi.system.converter.StringArrayConverter;
import lombok.Data;


/**
 * 参赛团队视图对象 dcims_team
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsTeamVoV2 {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 竞赛id
     */
    @ExcelProperty(value = "竞赛id", index = 0)
    private Long competitionId;

    /**
     * 竞赛名称
     */
    @ExcelProperty(value = "竞赛名称", index = 1)
    private String competitionName;

    /**
     * 竞赛详情
     */
    private DcimsCompetitionVo competition;

    /**
     * 队伍名称
     */
    @ExcelProperty(value = "队伍名称", index = 2)
    private String name;

    /**
     * 作品名称
     */
    @ExcelProperty(value = "作品名称", index = 3)
    private String worksName;

    /**
     * 比赛类型
     */
    @ExcelProperty(value = "比赛类型", converter = ExcelDictConvert.class, index = 4)
    @ExcelDictFormat(dictType = "dcims_award_type")
    private String competitionType;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院", converter = ExcelDictConvert.class, index = 5)
    @ExcelDictFormat(dictType = "dcims_college")
    private Long college;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = "奖项等级", converter = ExcelDictConvert.class, index = 7)
    @ExcelDictFormat(dictType = "dcims_award_level")
    private String awardLevel;

    /**
     * 指导教师工号
     */
    @ExcelProperty(value = "指导教师工号", converter = StringArrayConverter.class, index = 8)
    private String[] teacherId;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = "指导教师姓名", converter = StringArrayConverter.class, index = 9)
    private String[] teacherName;

    /**
     * 参赛学生学号
     */
    @ExcelProperty(value = "参赛学生学号", converter = StringArrayConverter.class, index = 10)
    private String[] studentId;

    /**
     * 参赛学生姓名
     */
    @ExcelProperty(value = "参赛学生姓名", converter = StringArrayConverter.class, index = 11)
    private String[] studentName;

    /**
     * 更高级奖项id
     */
    private Long advancedAwardNumber;

    /**
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @ExcelProperty(value = "获奖时间", index = 6)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date awardTime;

    /**
     * 佐证材料
     */
    private Long supportMaterial;

    /**
     * 佐证材料地址
     */
    private String supportMaterialURL;

    /**
     * 佐证材料实体类
     */
    private SysOssVo oss;

    /**
     * 佐证材料文件名
     */
    @ExcelProperty(value = "佐证材料", index = 12)
    private String supportMaterialName;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态", converter = ExcelDictConvert.class, index = 13)
    @ExcelDictFormat(dictType = "dcims_declare_award_status")
    private Integer audit;

    /**
     * 下一级审核人工号
     */
    private Long nextAuditId;

    /**
     * 创建人
     */
    private String createBy;
}
