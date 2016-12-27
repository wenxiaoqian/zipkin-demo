package com.welab.service1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author xiaoqian.wen
 * @create 2016-12-26 10:44
 **/
@Api("service的API接口")
@RestController
@RequestMapping("/service1")
public class Service1Controller {

    @Autowired
    private OkHttpClient client;

    @ApiOperation("trace第一步")
    @RequestMapping("/test")
    public String service1() throws Exception {
        Thread.sleep(100);
        Request request = new Request.Builder().url("http://localhost:8082/service2/test?aa=1&bb=2&cc=3&dd=4&ee=5").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
