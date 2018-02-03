package com.web.controller;

import com.web.bean.SysUser;
import org.kaze.framework.ioc.stereotype.Controller;
import org.kaze.framework.mvc.annotation.RequestMapping;
import org.kaze.framework.mvc.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/index")
    @ResponseBody
    public String index() {
        return "success";
    }

}
