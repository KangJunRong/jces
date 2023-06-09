package com.ecp.jces.server.dc.mapper.task;

import com.ecp.jces.form.TestReportForm;
import com.ecp.jces.vo.TestReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestReportMapper {

    void del(@Param("id") String id);

    void add(TestReportForm form);

    TestReportVo findById(@Param("id")String id);
    TestReportVo findByTestScheduleId(@Param("testScheduleId")String testScheduleId);

    void edit(TestReportForm form);

    List<TestReportVo> listByTaskId(@Param("taskId")String taskId);

    List<TestReportVo> listCommByTaskId(@Param("taskId")String taskId);
    List<TestReportVo> listParamByTaskId(@Param("taskId")String taskId);

    String findUserIdByLogPath(@Param("logPath")String logPath);
}