package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.constant.CacheNames;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.service.OssService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.oss.core.OssClient;
import com.ruoyi.oss.entity.UploadResult;
import com.ruoyi.oss.enumd.AccessPolicyType;
import com.ruoyi.oss.factory.OssFactory;
import com.ruoyi.system.domain.SysOss;
import com.ruoyi.system.domain.bo.SysOssBo;
import com.ruoyi.system.domain.entity.OssFile;
import com.ruoyi.system.domain.vo.SysOssVo;
import com.ruoyi.system.mapper.SysOssMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements ISysOssService, OssService {

    private final SysOssMapper baseMapper;
    private final SysUserMapper userMapper;

    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOss> lqw = buildQueryWrapper(bo);
        Page<SysOssVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<SysOssVo> filterResult = result.getRecords().stream().map(this::matchingUrl).collect(Collectors.toList());
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysOssVo> listByIds(Collection<Long> ossIds) {
        List<SysOssVo> list = new ArrayList<>();
        for (Long id : ossIds) {
            SysOssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(this.matchingUrl(vo));
            }
        }
        return list;
    }

    @Override
    public String selectUrlByIds(String ossIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(ossIds, Convert::toLong)) {
            SysOssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(this.matchingUrl(vo).getUrl());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    private LambdaQueryWrapper<SysOss> buildQueryWrapper(SysOssBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysOss> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), SysOss::getFileName, bo.getFileName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginalName()), SysOss::getOriginalName, bo.getOriginalName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSuffix()), SysOss::getFileSuffix, bo.getFileSuffix());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), SysOss::getUrl, bo.getUrl());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            SysOss::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        lqw.eq(StringUtils.isNotBlank(bo.getCreateBy()), SysOss::getCreateBy, bo.getCreateBy());
        lqw.eq(StringUtils.isNotBlank(bo.getService()), SysOss::getService, bo.getService());
        return lqw;
    }

    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public SysOssVo getById(Long ossId) {
        return baseMapper.selectVoById(ossId);
    }

    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {
        SysOssVo sysOss = SpringUtils.getAopProxy(this).getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = OssFactory.instance();
        try(InputStream inputStream = storage.getObjectContent(sysOss.getUrl())) {
            int available = inputStream.available();
            IoUtil.copy(inputStream, response.getOutputStream(), available);
            response.setContentLength(available);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * @param ossIds
     * @throws IOException
     */
    @Override
    public List<OssFile> downloadBatchFiles(Collection<Long> ossIds) throws IOException {
        System.out.println(ossIds.toString());
        List<SysOssVo> SysOssVoList = SpringUtils.getAopProxy(this).listByIds(ossIds);
        if (SysOssVoList.isEmpty()) {
            throw new ServiceException("文件数据不存在!");
        }

        OssClient storage = OssFactory.instance();
        List<OssFile> ossFiles = new ArrayList<>();
        for (SysOssVo sysOssVo:SysOssVoList) {
            System.out.println(sysOssVo.getUrl());
            InputStream storageObjectContent = null;
            for (int retryTimes = 0; retryTimes <= 5 && storageObjectContent == null; retryTimes++){
                try{
                    Thread.sleep(200);
                    storageObjectContent = storage.getObjectContent(sysOssVo.getUrl());
                }catch (Exception e){
                    System.out.println("retryTimes is " + retryTimes);
                }
            }
            OssFile ossFile = new OssFile();
            ossFile.setFileContent(storageObjectContent);
            ossFile.setSysOssVo(sysOssVo);
            ossFiles.add(ossFile);
        }

        return ossFiles;
    }


    /**
     * @param ossIds
     * @throws IOException
     */
    @Override
    public List<OssFile> downloadBatchFilesByHttp(Collection<Long> ossIds) throws IOException {
        System.out.println(ossIds.toString());
        List<SysOssVo> SysOssVoList = SpringUtils.getAopProxy(this).listByIds(ossIds);
        if (SysOssVoList.isEmpty()) {
            throw new ServiceException("文件数据不存在!");
        }


        List<OssFile> ossFiles = new ArrayList<>();
        for (SysOssVo sysOssVo:SysOssVoList) {
            System.out.println(sysOssVo.getUrl());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ByteArrayInputStream storageObjectContent = new ByteArrayInputStream(new byte[10]);

            try{
                String url = sysOssVo.getUrl().replace("http://kjjs-443.webvpn.zust.edu.cn/minio-oss/", "http://127.0.0.1:9000/");
                System.out.println(url);
                HttpUtil.download(url, os, false);
                storageObjectContent = new ByteArrayInputStream(os.toByteArray());
            }catch (Exception ignored){

            }
            OssFile ossFile = new OssFile();
            ossFile.setFileContent(storageObjectContent);
            ossFile.setSysOssVo(sysOssVo);
            ossFiles.add(ossFile);
        }

        return ossFiles;
    }


    @Override
    public SysOssVo upload(MultipartFile file) {
        String originalfileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(storage.getConfigKey());
        baseMapper.insert(oss);
        SysOssVo sysOssVo = new SysOssVo();
        BeanCopyUtils.copy(oss, sysOssVo);
        return this.matchingUrl(sysOssVo);
    }


    @Override
    public SysOssVo upload(File file, String originalfileName) {
        byte[] bytes = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
        } catch (FileNotFoundException e) {
            throw new ServiceException(e.getMessage());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        uploadResult = storage.uploadSuffix(bytes, suffix, null);
        // 保存文件信息
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(storage.getConfigKey());
        baseMapper.insert(oss);
        SysOssVo sysOssVo = new SysOssVo();
        BeanCopyUtils.copy(oss, sysOssVo);
        return this.matchingUrl(sysOssVo);
    }


    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        List<Long> idList = new ArrayList<>();
        if (isValid) {
            // 只允许上传者删除oss文件，其它用户只能查看不能删除，用户删除非自己上传的文件不会报错、不会影响其它自己上传的文件的删除
            LambdaQueryWrapper<SysOss> lqw1 = new LambdaQueryWrapper<>();
            lqw1.in(SysOss::getOssId,ids);
            List<SysOss> ossList = baseMapper.selectList(lqw1);
            SysUser user = userMapper.selectById(StpUtil.getLoginIdAsString().substring(9));
            ossList.forEach(oss -> {
                System.out.println(oss.toString());
                if(oss.getCreateBy().equals(user.getUserName())){
                    idList.add(oss.getOssId());
                }
            });
        }
        if (!idList.isEmpty()){
            List<SysOss> list = baseMapper.selectBatchIds(idList);
            for (SysOss sysOss : list) {
                OssClient storage = OssFactory.instance(sysOss.getService());
                storage.delete(sysOss.getUrl());
            }
            return baseMapper.deleteBatchIds(idList) > 0;
        }else{
            return false;
        }
    }

    /**
     * 匹配Url
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssVo matchingUrl(SysOssVo oss) {
        OssClient storage = OssFactory.instance(oss.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }
}
