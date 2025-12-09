package com.edos.Middleware.controller;

import com.edos.Middleware.entity.ResponseMessage;
import org.apache.coyote.Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(String message, Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        ResponseMessage response = new ResponseMessage();
        response.setContent("Response from " + principal.getName() + "to: " + message);
        return response;
    }
}
