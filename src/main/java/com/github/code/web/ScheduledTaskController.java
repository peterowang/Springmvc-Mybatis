package com.github.code.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduled")
public class ScheduledTaskController {
    @RequestMapping("/test")
    public String tet(){
        return "hello world";
    }
}
