package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.cron.manager.QuartzManager;
import com.qzh.eggcloud.model.JobStatus;
import com.qzh.eggcloud.model.dto.TaskDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.model.query.TaskQuery;
import com.qzh.eggcloud.service.SysTaskService;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public PageInfo<TaskDTO> listTask(TaskQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<TaskDTO> list = sysTaskMapper.findByQuery(query);
        return PageInfo.of(list);
    }

    @Override
    public int addTask(TaskDTO task) throws BaseException {
        if (isExistsJobName(task.getJobName())) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该任务名称已存在");
        }
        if (isExistsBeanClass(task.getBeanClass())) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该任务类已存在");
        }

        String username = SecurityUtil.getSysUserDetail().getUsername();
        SysTask sysTask = new SysTask();
        BeanUtils.copyProperties(task, sysTask);
        sysTask.setJobStatus(JobStatus.STOP.getCode());
        sysTask.setCreateUser(username);
        sysTask.setUpdateUser(username);
        sysTask.setCreateAt(LocalDateTime.now());
        sysTask.setUpdateAt(LocalDateTime.now());
        return sysTaskMapper.insertSysTask(sysTask);
    }

    private boolean isExistsJobName(String jobName) {
        return sysTaskMapper.findCountByName(jobName) > 0;
    }

    private boolean isSelfByJobName(TaskDTO taskDTO) {
        SysTask byName = sysTaskMapper.findByName(taskDTO.getJobName());
        if (byName == null) {
            return true;
        }
        return taskDTO.getId().equals(byName.getId());
    }

    private boolean isExistsBeanClass(String beanClass) {
        return sysTaskMapper.findCountByBeanClass(beanClass) > 0;
    }

    private boolean isSelfByBeanClass(TaskDTO taskDTO) {
        SysTask byBeanClass = sysTaskMapper.findByBeanClass(taskDTO.getBeanClass());
        if (byBeanClass == null) {
            return true;
        }
        return taskDTO.getId().equals(byBeanClass.getId());
    }

    @Override
    public int updateTask(TaskDTO task) throws BaseException {
        SysTask sysTask = findTaskById(task.getId());
        if (JobStatus.RUNNING.getCode().equals(sysTask.getJobStatus())) {
            throw new BaseException(ErrorCode.StopCronTask);
        }
        if (!isSelfByJobName(task)) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该任务名称已存在");
        }
        if (!isSelfByBeanClass(task)) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该任务类已存在");
        }

        sysTask = new SysTask();
        BeanUtils.copyProperties(task, sysTask);
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        task.setUpdateAt(LocalDateTime.now());
        task.setUpdateUser(userDetail.getUsername());
        return sysTaskMapper.updateSysTask(sysTask);
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
    public int batchDeleteTasks(Long[] ids) throws BaseException, SchedulerException {
        for (Long id : ids) {
            SysTask task = findTaskById(id);
            if (JobStatus.RUNNING.getCode().equals(task.getJobStatus())) {
                throw new BaseException(ErrorCode.StopCronTask);
            }
        }
        for (Long id : ids) {
            SysTask task = findTaskById(id);
            quartzManager.deleteJob(task);
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
        task.setUpdateUser(SecurityUtil.getSysUserDetail().getUsername());
        task.setUpdateAt(LocalDateTime.now());
        sysTaskMapper.updateSysTask(task);
    }
}
