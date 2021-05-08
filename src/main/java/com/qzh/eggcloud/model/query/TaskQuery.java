package com.qzh.eggcloud.model.query;

/**
 * @ClassName TaskQuery
 * @Author DiangD
 * @Date 2021/5/7
 * @Version 1.0
 * @Description
 **/
public class TaskQuery extends QueryBase {
    String jobName;
    String description;
    String jobGroup;

    public TaskQuery(String jobName, String description, String jobGroup) {
        this.jobName = jobName;
        this.description = description;
        this.jobGroup = jobGroup;
    }

    public TaskQuery() {
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
}
