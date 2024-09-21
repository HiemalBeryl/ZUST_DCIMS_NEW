package com.ruoyi.system.converter;

import net.sf.sevenzipjbinding.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 7Z解压文件回调函数
 */
public class ExtractCallback implements IArchiveExtractCallback {
    private final IInArchive inArchive;
    private final String extractPath;
    public ExtractCallback(IInArchive inArchive, String extractPath) {
        this.inArchive = inArchive;
        if (!extractPath.endsWith("/") && !extractPath.endsWith("\\")) {
            extractPath += File.separator;
        }
        this.extractPath = extractPath;
    }

    @Override
    public void setTotal(long total) {

    }

    @Override
    public void setCompleted(long complete) {

    }

    @Override
    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
        return data -> {
            String filePath = inArchive.getStringProperty(index, PropID.PATH);
            FileOutputStream fos = null;
            try {
                File path = new File(extractPath + filePath);
                if(!path.getParentFile().exists()){
                    path.getParentFile().mkdirs();
                }

                if(!path.exists()){
                    path.createNewFile();
                }
                fos = new FileOutputStream(path, true);
                fos.write(data);
            } catch (IOException e) {
                System.out.println("IOException while extracting " + filePath);
            } finally{
                try {
                    if(fos != null){
                        fos.flush();
                        fos.close();
                    }
                } catch (IOException e) {
                    System.out.println("Could not close FileOutputStream" +  e);
                }
            }
            return data.length;
        };
    }

    @Override
    public void prepareOperation(ExtractAskMode extractAskMode) {

    }

    @Override
    public void setOperationResult(ExtractOperationResult extractOperationResult) {
    }
}
