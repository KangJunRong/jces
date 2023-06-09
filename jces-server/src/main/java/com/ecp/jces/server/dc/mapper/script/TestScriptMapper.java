package com.ecp.jces.server.dc.mapper.script;

import com.ecp.jces.form.TestScriptForm;
import com.ecp.jces.vo.TestScriptVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestScriptMapper {

    List<TestScriptVo> findList(TestScriptForm testScriptForm);
    void insert(TestScriptForm testScriptForm);
    void delete(TestScriptForm testScriptForm);
    void updateStatus(TestScriptForm testScriptForm);
    void changeOtherActiveStatusToNotActiveStatus(TestScriptForm testScriptForm);
    Integer maxVersion();
    TestScriptVo getActive();
}
