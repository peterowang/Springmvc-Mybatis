package com.github.code.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicJobQuartz implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicJobQuartz.class);
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("Dynamic Job Quartz Execute Timestamp : " + System.currentTimeMillis());
    }
}