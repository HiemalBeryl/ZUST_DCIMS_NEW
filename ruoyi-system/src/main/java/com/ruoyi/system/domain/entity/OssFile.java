package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.SysOss;
import com.ruoyi.system.domain.vo.SysOssVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.InputStream;

@Data
public class OssFile {
    /**
     * OSS对象存储对象
     */
    SysOssVo sysOssVo;

    /**
     * OSS文件内容
     */
    InputStream fileContent;
}

