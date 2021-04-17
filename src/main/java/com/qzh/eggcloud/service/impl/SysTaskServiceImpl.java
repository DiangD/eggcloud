package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.cron.manager.QuartzManager;
import com.qzh.eggcloud.model.JobStatus;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.service.SysTaskService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName SysTaskServiceImpl
 * @Author DiangD
 * @Date 2021/3/16
 * @Version 1.0
 * @Description
 **/
@Service
public class SysTaskServiceImpl extends BaseService implements SysTaskService {

    @Autowired
    private QuartzManager quartzManager;

    @Override
    public SysTask findTaskById(Long id) {
        return sysTaskMapper.findById(id);
    }

    @Override
    public PageInfo<SysTask> findAll(PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysTask> tasks = sysTaskMapper.findAll();
        return PageInfo.of(tasks);
    }

    @Override
    public int addTask(SysTask task) {
        String username = SecurityUtil.getSysUserDetail().getUsername();
        task.setCreateUser(username);
        task.setUpdateUser(username);
        task.setCreateAt(LocalDateTime.now());
        task.setUpdateAt(LocalDateTime.now());
        return sysTaskMapper.insertSysTask(task);
    }

    @Override
    public int updateTask(SysTask task) throws SchedulerException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        task.setUpdateAt(LocalDateTime.now());
        task.setUpdateUser(userDetail.getUsername());
        return sysTaskMapper.updateSysTask(task);
    }

    @Override
    public int saveTask(SysTask task) throws SchedulerException {
        if (task.getId() != null) {
            return updateTask(task);
        }
        return addTask(task);
    }

    @Override
    public int deleteTask(Long id) {
        try {
            SysTask task = findTaskById(id);
            quartzManager.deleteJob(task);
            return sysTaskMapper.deleteById(id);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    @Transactional
    public int batchDeleteTasks(Long[] ids) {
        for (Long id : ids) {
            try {
                SysTask task = findTaskById(id);
                quartzManager.deleteJob(task);
            } catch (SchedulerException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return sysTaskMapper.batchDelete(ids);
    }

    @Override
    public void readSchedule() throws Exception {
        List<SysTask> tasks = sysTaskMapper.findAll();
        for (SysTask task : tasks) {
            if (JobStatus.RUNNING.getCode().equals(task.getJobStatus())) {
                quartzManager.addJob(task);
            }
        }
    }

    @Override
    public void run(SysTask task) throws SchedulerException {
        quartzManager.runJobNow(task);
    }

    @Override
    public void updateCron(Long id) throws SchedulerException {
        SysTask task = findTaskById(id);
        if (task == null) {
            return;
        }
        if (JobStatus.RUNNING.getCode().equals(task.getJobStatus())) {
            quartzManager.updateJobCron(task);
        }
    }

    @Override
    public void pause(SysTask task) throws SchedulerException {
        quartzManager.pauseJob(task);
    }

    @Override
    public void resume(SysTask task) throws SchedulerException {
        quartzManager.resumeJob(task);
    }

    @Override
    public PageInfo<SysTask> findBySearchKey(String key, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysTask> tasks = sysTaskMapper.findBySearchKey(key);
        return PageInfo.of(tasks);
    }

    @Override
    public void changeTaskStatus(Long jobId, String jobStatus) throws Exception {
        SysTask task = findTaskById(jobId);
        if (task == null || task.getJobStatus().equals(jobStatus)) {
            return;
        }
        if (JobStatus.STOP.getCode().equals(jobStatus)) {
            quartzManager.deleteJob(task);
            task.setJobStatus(JobStatus.STOP.getCode());
        } else {
            task.setJobStatus(JobStatus.RUNNING.getCode());
            quartzManager.addJob(task);
        }
        sysTaskMapper.updateSysTask(task);
    }
}
