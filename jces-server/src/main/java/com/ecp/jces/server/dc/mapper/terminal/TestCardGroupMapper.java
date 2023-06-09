package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestCardGroupForm;
import com.ecp.jces.vo.TestCardGroupVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestCardGroupMapper {

    List<TestCardGroupVo> findList(TestCardGroupForm testCardGroupForm);

    void insert(TestCardGroupForm testCardGroupForm);

    void update(TestCardGroupForm testCardGroupForm);

    void delete(TestCardGroupForm testCardGroupForm);

    void updateStatus(TestCardGroupForm testCardGroupForm);

    void changeOtherActiveStatusToPublishStatus(TestCardGroupForm testCardGroupForm);

    TestCardGroupVo getById(TestCardGroupForm testCardGroupForm);

    TestCardGroupVo getByName(TestCardGroupForm testCardGroupForm);

}
