package com.ruoyi.test;


import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestComtroller {

    @GetMapping("/return")
    public R<Void> test1(){
        return R.ok();
    }
}
