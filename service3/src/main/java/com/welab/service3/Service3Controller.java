package com.welab.service3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xiaoqian.wen
 * @create 2016-12-26 10:44
 **/
@Api("service的API接口")
@RestController
@RequestMapping("/service3")
public class Service3Controller {

    @Autowired
    private ZipkinService zipkinService;

    @ApiOperation("trace第三步")
    @RequestMapping("/test")
    public String service3() throws Exception {
        Thread.sleep(300);

        Map<String,String> trace = zipkinService.getTrace("8524003509599932621");
        System.out.println(trace.toString());

        return "service3";
    }

}
