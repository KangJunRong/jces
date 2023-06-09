package com.ecp.jces.server.dc.mapper.task;

import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.vo.TestTaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestTaskMapper {
    int del(@Param("id") String id);

    int add(TestTaskForm form);

    TestTaskVo findById(@Param("id") String id);

    int edit(TestTaskForm form);

    List<TestTaskVo> list(TestTaskForm form);

    List<Map<String, Object>> testTaskCreateCount(TestTaskForm form);

    List<Map<String, Object>> testTaskStatusCount(TestTaskForm form);

    void editForStart(TestTaskForm form);

    int count(TestTaskForm form);

    int uploadProgress(@Param("id")String id, @Param("rate")Integer rate);

    int updateParamTest(@Param("id")String id);

    TestTaskVo findByApplet(AppletForm appletForm);

    List<TestTaskVo> timeOutList();

    double diSumTime(@Param("testTaskId") String testTaskId);

    double downloadMaxTime(@Param("testTaskId") String testTaskId);
}