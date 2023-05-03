package com.ruoyi.system.utils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictDataMapper extends BaseMapper<SysDictData> {
    @Insert("insert into `sys_dict_data` (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status) values ${str}")
    public boolean InsertList(String str);
}
