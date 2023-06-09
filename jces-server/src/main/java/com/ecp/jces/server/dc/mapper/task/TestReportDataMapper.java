package com.ecp.jces.server.dc.mapper.task;


import com.ecp.jces.form.TestReportDataForm;
import com.ecp.jces.vo.TestReportDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestReportDataMapper {
    void addInstall(TestReportDataForm form);
    void addLoadBatch(List<TestReportDataForm> list);
    List<TestReportDataVo> list(TestReportDataForm form);

    List<TestReportDataVo> maxLoadList(@Param("taskId")String taskId);
    List<TestReportDataVo> maxInstallList(@Param("taskId")String taskId);

    List<TestReportDataVo> installList(TestReportDataForm form);
    void addInstallBatch(List<TestReportDataForm> list);
}