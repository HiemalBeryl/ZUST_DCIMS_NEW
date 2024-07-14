package com.ruoyi.system.utils;





import cn.hutool.core.io.FileUtil;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class WordUtil {

    /**
     * 根据模板导出Word
     * @param map
     * @param modelFileName
     * @param outFileName
     */
    public static void exportWordByModel(HttpServletResponse response, Map<String, Object> map, String modelFileName, String outFileName) {
        try {
            // 1.获取模板文件路径 - 重点
            //XWPFDocument word = WordExportUtil.exportWord07(modelFileName, map);）
            File file = filePath(modelFileName);
            FileInputStream inputStream = new FileInputStream(file);

            XWPFDocument document = new XWPFDocument(inputStream);
            XWPFTemplate compile = XWPFTemplate.compile(document);
            XWPFTemplate template = compile.render(map);
            OutputStream out = response.getOutputStream();
            template.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件名获取文件对象
     * @param modelFileName
     * @return
     */
    public static File filePath(String modelFileName) {
        // 获取类加载器
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        // 尝试从类路径中加载资源
        URL resource = classLoader.getResource(modelFileName);
        return new File(resource.getFile());
    }


}
