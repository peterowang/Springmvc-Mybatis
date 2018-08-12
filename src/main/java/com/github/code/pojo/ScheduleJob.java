package com.github.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ScheduleJob {
    /**
     * 任务id
     */
    private String jobId;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务状态 0禁用 1启用 2删除
     */
    private String jobStatus;
    /**
     * 任务运行时间表达式
     */
    private String cronExpression;
    /**
     * 任务描述
     */
    private String desc;
}