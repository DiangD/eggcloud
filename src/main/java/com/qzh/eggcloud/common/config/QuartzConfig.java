package com.qzh.eggcloud.common.config;

import com.qzh.eggcloud.cron.factory.JobFactory;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @ClassName QuartzConfig
 * @Author DiangD
 * @Date 2021/3/16
 * @Version 1.0
 * @Description quartz配置
 **/
@Configuration
public class QuartzConfig {
    @Autowired
    private JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        try {
            schedulerFactoryBean.setOverwriteExistingJobs(true);
            schedulerFactoryBean.setJobFactory(jobFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedulerFactoryBean;
    }


    // 创建schedule
    @Bean(name = "scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}
