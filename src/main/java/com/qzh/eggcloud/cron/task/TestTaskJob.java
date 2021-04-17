package com.qzh.eggcloud.cron.task;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName TestTask
 * @Author DiangD
 * @Date 2021/3/17
 * @Version 1.0
 * @Description
 **/
//作业不并发
@DisallowConcurrentExecution
@Component
public class TestTaskJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("hello world" + LocalDateTime.now());
    }
}
