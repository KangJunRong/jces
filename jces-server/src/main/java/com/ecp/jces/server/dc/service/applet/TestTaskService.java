package com.ecp.jces.server.dc.service.applet;

import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestScheduleForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.vo.TestReportDataVo;
import com.ecp.jces.vo.TestScheduleVo;

import java.util.List;
import java.util.Map;


public interface TestTaskService {

    List<Map<String, Object>> testTaskCreateCount(TestTaskForm form) throws FrameworkRuntimeException;

    List<Map<String, Object>> testTaskStatusCount(TestTaskForm form) throws FrameworkRuntimeException;

    void taskResult(StartTestForm form) throws FrameworkRuntimeException;

    void progress(Map<String, Object> form) throws FrameworkRuntimeException;

    int count(TestTaskForm form) throws FrameworkRuntimeException;

    List<TestReportDataVo> dataDetail(TestScheduleForm form) throws FrameworkRuntimeException;

    List<TestReportDataVo> installDetail(TestScheduleForm form) throws FrameworkRuntimeException;

    List<TestScheduleVo> findByTestBusinessScriptId(TestTaskForm form) throws FrameworkRuntimeException;
}
