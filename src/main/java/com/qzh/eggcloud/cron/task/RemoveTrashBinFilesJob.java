package com.qzh.eggcloud.cron.task;

import com.qzh.eggcloud.service.SysFileService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName RemoveTashbinFilesJob
 * @Author DiangD
 * @Date 2021/3/17
 * @Version 1.0
 * @Description 删除回收站垃圾定时任务
 **/
@DisallowConcurrentExecution
@Component
public class RemoveTrashBinFilesJob implements Job {

    @Autowired
    private SysFileService sysFileService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        sysFileService.removeFilesDeletedOver30Days();
    }
}
