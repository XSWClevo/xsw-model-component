package com.xshiwu.controller;

import com.xshiwu.component.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/webSocket")
public class WebSocketController {

    @Resource
    private WebSocket webSocket;

    @PostMapping("/sentMessage")
    public void sentMessage(String userId, String message) {
        try {
            webSocket.sendMessageByUserId(userId, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/broadcast")
    public void broadcast(@RequestBody String msg) {
        webSocket.sendMessageForAll(msg);
    }

}
