package com.ecp.jces.server.dc.mapper.task;

import com.ecp.jces.form.TestScheduleForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.vo.TestScheduleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestScheduleMapper {
    int del(@Param("id") String id);

    int add(TestScheduleForm form);

    TestScheduleVo findById(@Param("id") String id);

    int edit(TestScheduleForm form);

    List<TestScheduleVo> list(TestScheduleForm form);

    void adds(@Param("list") List<TestScheduleForm> forms);

    void changeStatus(@Param("taskId") String taskId);

    void addLog(@Param("id")String id, @Param("scheduleId")String scheduleId,
                @Param("customizeLogPath")String customizeLogPath,
                @Param("errorInfo")String errorInfo);

    List<TestScheduleVo> findByTestTaskId(TestTaskForm form);
}