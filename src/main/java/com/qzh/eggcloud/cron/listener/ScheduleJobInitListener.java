package com.qzh.eggcloud.cron.listener;

import com.qzh.eggcloud.service.SysTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName ScheduleJobInitListener
 * @Author DiangD
 * @Date 2021/3/17
 * @Version 1.0
 * @Description 任务监听器
 **/
@Component
@Order(value = 1)
public class ScheduleJobInitListener implements CommandLineRunner {

    @Autowired
    private SysTaskService taskService;

    @Override
    public void run(String... args) {
        //不能抛出到启动类，不然发生异常，服务崩溃
        try {
            taskService.readSchedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
