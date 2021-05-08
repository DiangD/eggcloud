package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.SysTask;
import com.qzh.eggcloud.model.dto.TaskDTO;
import com.qzh.eggcloud.model.query.TaskQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysTaskMapper {
    SysTask findById(@Param("id") Long id);

    List<SysTask> findAll();

    int insertSysTask(@Param("task") SysTask task);

    int updateSysTask(@Param("task") SysTask task);

    int deleteById(Long id);

    int batchDelete(Long[] ids);

    List<TaskDTO> findByQuery(@Param("query") TaskQuery query);

    int findCountByName(@Param("jobName") String jobName);

    SysTask findByName(@Param("jobName") String jobName);

    int findCountByBeanClass(@Param("beanClass") String beanClass);

    SysTask findByBeanClass(@Param("beanClass") String beanClass);

}
