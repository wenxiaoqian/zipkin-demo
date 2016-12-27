package com.welab.service4;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoqian.wen
 * @create 2016-12-26 10:44
 **/
@Api("service的API接口")
@RestController
@RequestMapping("/service4")
public class Service4Controller {

    @ApiOperation("trace第四步")
    @RequestMapping("/test")
    public String service4() throws Exception {
        Thread.sleep(300);
        int i = 1/0;
        return "service4";
    }

}
