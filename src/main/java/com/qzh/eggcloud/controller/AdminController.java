package com.qzh.eggcloud.controller;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.annotation.Log;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.JobStatus;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.query.StoreQuery;
import com.qzh.eggcloud.model.query.UserQuery;
import com.qzh.eggcloud.model.vo.UserStoreVo;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysTaskService;
import com.qzh.eggcloud.service.SysUserService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName AdminController
 * @Author DiangD
 * @Date 2021/3/17
 * @Version 1.0
 * @Description admin控制器
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysTaskService sysTaskService;

    @Autowired
    private FileStoreService fileStoreService;

    /**
     * 用户管理
     */

    @GetMapping("/list/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> listUsers(UserQuery query, PageEntity pageEntity) {
        PageInfo<SysUserEntity> users = sysUserService.findUsersWithDetail(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(users));
    }

    @PatchMapping("/edit/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> editUser(@ModelAttribute SysUserEntity userEntity) throws BaseException {
        sysUserService.updateUserInfo(userEntity);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete/user")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<JsonResult<Object>> deleteUser(@ModelAttribute SysUserEntity userEntity) {
        sysUserService.removeUser(userEntity.getUserId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/status/user")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<JsonResult<Object>> changeUserStatus(@ModelAttribute SysUserEntity userEntity) {
        sysUserService.changeUserStatus(userEntity.getUserId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    /**
     * 用户仓库管理
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list/stores")
    public ResponseEntity<JsonResult<Object>> listUserStore(StoreQuery query, PageEntity pageEntity) {
        PageInfo<UserStoreVo> stores = fileStoreService.getUserStores(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(stores));
    }

    @PostMapping("/edit/store")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> editUserStore(FileStore fileStore) throws BaseException {
        fileStoreService.updateFileStore(fileStore);
        return ResponseEntity.ok(RespUtil.success(null));
    }


    /**
     * 定时任务
     */

    @GetMapping("/list/tasks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> listCronTasks(String key, PageEntity page) {
        PageInfo<SysTask> tasks;
        if (key == null) {
            tasks = sysTaskService.findAll(page);
            return ResponseEntity.ok(RespUtil.success(tasks));
        }
        tasks = sysTaskService.findBySearchKey(key, page);
        return ResponseEntity.ok(RespUtil.success(tasks));
    }

    @PostMapping("/add/task")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> addTask(@ModelAttribute SysTask task) {
        sysTaskService.addTask(task);
        return ResponseEntity.ok(RespUtil.success(null));
    }


    @PatchMapping("/edit/task")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> editTask(@ModelAttribute SysTask task) throws SchedulerException {
        SysTask sysTask = sysTaskService.findTaskById(task.getId());
        if (JobStatus.RUNNING.getCode().equals(sysTask.getJobStatus())) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.StopCronTask, null));
        }
        sysTaskService.updateTask(task);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/status/task")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> changeTaskStatus(@ModelAttribute SysTask task) throws Exception {
        sysTaskService.changeTaskStatus(task.getId(), task.getJobStatus());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete/task")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> removeTask(@ModelAttribute SysTask task) {
        SysTask sysTask = sysTaskService.findTaskById(task.getId());
        if (JobStatus.RUNNING.getCode().equals(sysTask.getJobStatus())) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.StopCronTask, null));
        }
        sysTaskService.deleteTask(task.getId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/run/task")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Log
    public ResponseEntity<JsonResult<Object>> runTask(@ModelAttribute SysTask task) throws SchedulerException {
        SysTask sysTask = sysTaskService.findTaskById(task.getId());
        if (JobStatus.STOP.getCode().equals(sysTask.getJobStatus())) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.StartCronTask, null));
        }
        sysTaskService.run(sysTask);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete/tasks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<JsonResult<Object>> batchRemoveTask(@RequestParam("ids") Long[] ids) {
        for (Long id : ids) {
            SysTask task = sysTaskService.findTaskById(id);
            if (JobStatus.RUNNING.getCode().equals(task.getJobStatus())) {
                return ResponseEntity.ok(RespUtil.generate(ErrorCode.StopCronTask, null));
            }
        }
        sysTaskService.batchDeleteTasks(ids);
        return ResponseEntity.ok(RespUtil.success(null));
    }

}
