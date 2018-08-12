package com.github.code.web;

import com.github.code.quartz.DynamicJobQuartz;
import com.github.code.scheduled.QuartzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduled")
public class ScheduledTaskController {
    @Autowired
    private QuartzManager quartzManager;

    /**
     * 测试mvc
     *
     * @return
     */
    @RequestMapping("/home")
    public String success() {
        return "success";
    }

    @RequestMapping("/addScheduledJob")
    public String addScheduledJob(){
        quartzManager.addJob("execute", "execute", "动态任务触发器", "动态任务触发器", DynamicJobQuartz.class, "0/60 * * * * ? ");
        return "ADD JOB";
    }
    @RequestMapping("/removeScheduledJob")
    public String removeScheduledJob(){
        quartzManager.removeJob("execute", "execute", "动态任务触发器", "动态任务触发器");
        return "REMOVE JOB";
    }
    @RequestMapping("/modifyScheduledJob")
    public String modifyScheduledJob(){
        quartzManager.modifyJobTime("execute", "execute", "动态任务触发器", "动态任务触发器","0/5 * * * * ? ");
        return "MODIFY JOB";
    }

}
