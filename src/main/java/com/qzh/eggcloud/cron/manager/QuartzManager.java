package com.qzh.eggcloud.cron.manager;

import com.qzh.eggcloud.model.SysTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName QuartzManager
 * @Author DiangD
 * @Date 2021/3/16
 * @Version 1.0
 * @Description 控制器
 **/
@Service
@Slf4j
public class QuartzManager {
    @Autowired
    private Scheduler scheduler;

    public void addJob(SysTask task) throws Exception {

        // 创建jobDetail实例，绑定Job实现类
        // 指明job的名称，所在组的名称，以及绑定job类

        Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(task.getBeanClass()).newInstance().getClass());
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(task.getJobName(), task.getJobGroup())// 任务名称和组构成任务key
                .build();
        // 定义调度触发规则
        // 使用cornTrigger规则
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobName(), task.getJobGroup())// 触发器key
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression())).startNow().build();
        // 把作业和触发器注册到任务调度中
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    /**
     * 暂停一个job
     */
    public void pauseJob(SysTask task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复一个job
     */
    public void resumeJob(SysTask task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除一个job
     */
    public void deleteJob(SysTask task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
        scheduler.deleteJob(jobKey);

    }

    /**
     * 立即执行job
     */
    public void runJobNow(SysTask task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    /**
     * 更新job时间表达式
     */
    public void updateJobCron(SysTask task) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobName(), task.getJobGroup());

        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression());

        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 获取所有计划中的任务列表
     */
    public List<SysTask> getAllJob() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<SysTask> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                SysTask job = new SysTask();
                buildSysTask(jobList, jobKey, trigger, job);
            }
        }
        return jobList;
    }

    private void buildSysTask(List<SysTask> jobList, JobKey jobKey, Trigger trigger, SysTask job) throws SchedulerException {
        job.setJobName(jobKey.getName());
        job.setJobGroup(jobKey.getGroup());
        job.setDescription("触发器:" + trigger.getKey());
        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
        job.setJobStatus(triggerState.name());
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            String cronExpression = cronTrigger.getCronExpression();
            job.setCronExpression(cronExpression);
        }
        jobList.add(job);
    }

    /**
     * 所有正在运行的job
     */
    public List<SysTask> getRunningJob() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<SysTask> jobList = new ArrayList<>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            SysTask job = new SysTask();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            buildSysTask(jobList, jobKey, trigger, job);
        }
        return jobList;
    }
}
