package com.brotherhui;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {
	

    @RequestMapping("/user")
    public String user() throws IOException {
        return "I am lxh";
    }


}
