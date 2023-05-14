package com.ruoyi.system.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.HashMap;

public class ParameterUtils {
    public static HashMap<String, String> parseStringToHashMap(String parameter){
        HashMap<String, String> map = new HashMap<>();
        String[] keyValuePairs = parameter.split(",");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            map.put(entry[0], entry[1]);
        }
        return map;
    }
}
