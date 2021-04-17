package com.qzh.eggcloud.cron.task;

import com.qzh.eggcloud.service.impl.ShareFileServiceImpl;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName RemoveShareFileJob
 * @Author DiangD
 * @Date 2021/3/17
 * @Version 1.0
 * @Description 删除过期分享定时任务
 **/
@Component
@DisallowConcurrentExecution
public class RemoveShareFileJob implements Job {

    @Autowired
    private ShareFileServiceImpl shareFileService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        shareFileService.removeShareFilesExpired();
    }
}
