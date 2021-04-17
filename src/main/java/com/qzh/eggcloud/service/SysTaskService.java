package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysTask;
import org.quartz.SchedulerException;

public interface SysTaskService {
    SysTask findTaskById(Long id);

    PageInfo<SysTask> findAll(PageEntity pageEntity);

    int addTask(SysTask task);

    int updateTask(SysTask task) throws SchedulerException;

    int saveTask(SysTask task) throws SchedulerException;

    int deleteTask(Long id);

    int batchDeleteTasks(Long[] ids);

    void readSchedule() throws Exception;

    void run(SysTask task) throws SchedulerException;

    void updateCron(Long id) throws SchedulerException;


    void pause(SysTask task) throws SchedulerException;

    void resume(SysTask task) throws SchedulerException;

    PageInfo<SysTask> findBySearchKey(String key, PageEntity pageEntity);

    void changeTaskStatus(Long jobId, String jobStatus) throws Exception;
}
