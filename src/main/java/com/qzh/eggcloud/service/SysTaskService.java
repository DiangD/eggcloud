package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.dto.TaskDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.model.query.TaskQuery;
import org.quartz.SchedulerException;

public interface SysTaskService {
    SysTask findTaskById(Long id);

    PageInfo<TaskDTO> listTask(TaskQuery query, PageEntity pageEntity);

    int addTask(TaskDTO task) throws BaseException;

    int updateTask(TaskDTO task) throws SchedulerException, BaseException;

    int deleteTask(Long id);

    int batchDeleteTasks(Long[] ids) throws BaseException, SchedulerException;

    void readSchedule() throws Exception;

    void run(SysTask task) throws SchedulerException;

    void updateCron(Long id) throws SchedulerException;


    void pause(SysTask task) throws SchedulerException;

    void resume(SysTask task) throws SchedulerException;

    void changeTaskStatus(Long jobId, String jobStatus) throws Exception;
}
