package com.github.code.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticJobQuartz {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticJobQuartz.class);
    public void execute(){
        LOGGER.info("Static Job Quartz Execute Timestamp : " + System.currentTimeMillis());
        //do something
    }
}
