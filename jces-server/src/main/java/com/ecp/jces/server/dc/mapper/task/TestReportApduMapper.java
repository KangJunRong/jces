package com.ecp.jces.server.dc.mapper.task;

import com.ecp.jces.form.TestReportApduForm;
import com.ecp.jces.vo.TestReportApduVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestReportApduMapper {

    void add(TestReportApduForm form);
    List<TestReportApduVo> list(TestReportApduForm form);
}