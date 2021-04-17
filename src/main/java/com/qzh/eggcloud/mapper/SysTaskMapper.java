package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.SysTask;
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

    List<SysTask> findBySearchKey(@Param("key") String key);
}
