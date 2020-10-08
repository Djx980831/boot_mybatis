package com.example.demo.controller;

import com.example.demo.service.SendMailService;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.SendMailServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.util.RpcResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户接口", value = "实现用户的相关操作")
@RestController
@RequestMapping("/testMail")
public class MailController {

    @Autowired
    private SendMailServiceImpl sendMail;

    @PostMapping("/send")
    public RpcResponse<String> send() throws Exception {
        sendMail.sendMail();
        return RpcResponse.success("success");
    }
}
