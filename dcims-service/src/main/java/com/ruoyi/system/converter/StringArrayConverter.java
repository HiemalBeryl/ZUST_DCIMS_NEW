package com.ruoyi.system.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StringArrayConverter implements Converter<String[]> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String[].class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String[] convertToJavaData(ReadConverterContext<?> context){
        return context.getReadCellData().getStringValue().split(",");
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String[]> context){
        String result = Arrays.toString(context.getValue());
        return new WriteCellData<>(result.substring(1, result.length() - 1));
    }
}
