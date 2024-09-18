package com.ruoyi.system.service;

import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.excel.DcimsTeamExportExcel;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcel;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;
import org.apache.commons.compress.archivers.ArchiveException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参赛团队Service接口
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
public interface IDcimsTeamService {

    /**
     * 查询参赛团队
     */
    DcimsTeamVoV2 queryById(Long id);

    /**
     * 查询参赛团队列表
     */
    TableDataInfo<DcimsTeamVoV2> queryPageList(DcimsTeamBo bo, PageQuery pageQuery);

    /**
     * 根据教师工号查询参赛团队列表
     */
    TableDataInfo<DcimsTeamVoV2> queryPageListByTeacherId(DcimsTeamBo bo, PageQuery pageQuery);

    /**
     * 查询参赛团队列表
     */
    List<DcimsTeamVo> queryList(DcimsTeamBo bo);

    /**
     * 导出Excel
     */
    List<DcimsTeamExportExcel> exportExcel(DcimsTeamBo bo);

    /**
     * 新增参赛团队
     */
    Boolean insertByBo(DcimsTeamBo bo);

    /**
     * 修改参赛团队
     */
    Boolean updateByBo(DcimsTeamBo bo);

    /**
     * 为团队添加获奖信息
     */

    Boolean declareAwardByBo(DcimsDeclareAwardBo bo);

    /**
     * 校验并批量删除参赛团队信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 获取批量导入模板
     */
    File getImportTemplate(Integer annual) throws IOException;

    /**
     * 读取用户上传的模板内数据
     */
    List<DcimsTeamImportExcel> readDataFromTemplate(InputStream file) throws IOException, ArchiveException;

    /**
     * 将数据保存在Redis中
     */
    Map<String, Object> saveDataToRedis(List<DcimsTeamImportExcel> importTeamData);

    /**
     * 手动修改批量导入的数据
     */
    Map<String, Object> editImportData(String id, List<DcimsTeamImportExcel> importTeamData);

    /**
     * 为批量导入追加数据
     */
    Map<String, Object> appendImportData(String id, String type, InputStream file) throws IOException, ArchiveException;

    /**
     * 批量导入数据保存
     */
    boolean submitImportData(String ImportDataId);

    /**
     * 下载获奖团队信息以及附件
     */
    void download(List<DcimsTeamVoV2> downloadList, HttpServletResponse response) throws IOException;

    /*
    *查询获奖情况并处理数据
    * */
    HashMap<String, Object> queryAward(DcimsTeamBo bo);
}
