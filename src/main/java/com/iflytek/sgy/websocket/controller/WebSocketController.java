package com.iflytek.sgy.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.sgy.websocket.config.MyWebSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woni on 18/5/29.
 */
@Controller
@RequestMapping("websocket")
public class WebSocketController {

    @Resource
    MyWebSocketHandler handler;

    @RequestMapping("pushMessage")
    @ResponseBody
    public String pushVideoListToWeb(String message, String id) {


        Map<String,Object> result =new HashMap<String,Object>();

        try {
            handler.sendMessageToUser(id,new TextMessage(message));
            result.put("operationResult", true);
        }catch (Exception e) {
            result.put("operationResult", false);
        }
        return JSONObject.toJSONString(result);
    }


}
