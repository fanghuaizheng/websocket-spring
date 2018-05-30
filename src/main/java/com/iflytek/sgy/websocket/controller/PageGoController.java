package com.iflytek.sgy.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by woni on 18/5/29.
 */
@Controller
public class PageGoController {

    @RequestMapping("index")
    public String goIndex(){
        return "index";
    }

    @RequestMapping("hello")
    public String goHello(){
        return "hello";
    }
}
