package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestCardGroupRelateForm;
import com.ecp.jces.vo.TestCardGroupRelateVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestCardGroupRelateMapper {


    void insert(TestCardGroupRelateForm testCardGroupRelateForm);

    void insertBatch(@Param("list")List<TestCardGroupRelateForm> list);

    void deleteByCardGroupId(@Param("cardGroupId")String cardGroupId);

    void deleteByCardId(@Param("cardId")String cardId);

    List<TestCardGroupRelateVo> findList(TestCardGroupRelateForm testCardGroupRelateForm);


}
