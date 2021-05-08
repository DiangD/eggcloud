package com.qzh.eggcloud.controller.setting;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.model.JobStatus;
import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.model.dto.TaskDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.TaskQuery;
import com.qzh.eggcloud.service.SysTaskService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName TaskController
 * @Author DiangD
 * @Date 2021/5/7
 * @Version 1.0
 * @Description
 **/
@RestController
@RequestMapping("/task")
@Validated
public class TaskController {
    @Autowired
    private SysTaskService taskService;

    @GetMapping("/list")
    public ResponseEntity<Object> listTask(TaskQuery query, PageEntity pageEntity) {
        PageInfo<TaskDTO> page = taskService.listTask(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(page));
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> addTask(@ModelAttribute @Validated TaskDTO task) throws BaseException {
        taskService.addTask(task);
        return ResponseEntity.ok(RespUtil.success(null));
    }


    @PostMapping("/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> editTask(@ModelAttribute @Validated TaskDTO task) throws SchedulerException, BaseException {
        taskService.updateTask(task);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> changeTaskStatus(@ModelAttribute SysTask task) throws Exception {
        taskService.changeTaskStatus(task.getId(), task.getJobStatus());
        return ResponseEntity.ok(RespUtil.success(null));
    }


    @PostMapping("/run")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> runTask(@ModelAttribute SysTask task) throws SchedulerException {
        SysTask sysTask = taskService.findTaskById(task.getId());
        if (JobStatus.STOP.getCode().equals(sysTask.getJobStatus())) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.StartCronTask, null));
        }
        taskService.run(sysTask);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> batchRemoveTask(@RequestParam("taskIds") Long[] taskIds) throws BaseException, SchedulerException {
        taskService.batchDeleteTasks(taskIds);
        return ResponseEntity.ok(RespUtil.success(null));
    }
}
