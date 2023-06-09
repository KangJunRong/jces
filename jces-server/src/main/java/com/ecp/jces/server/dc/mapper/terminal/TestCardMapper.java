package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.vo.TestCardVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestCardMapper {

    List<TestCardVo> findList(TestCardForm testCardForm);

    void insert(TestCardForm testCardForm);

    void update(TestCardForm testCardForm);

    void delete(TestCardForm testCardForm);

    List<TestCardVo> findListByCardGroup(TestCardForm testCardForm);

    TestCardVo findByManufacturerAndModel(@Param("manufacturerId") String manufacturerId, @Param("model") String model);

    TestCardVo getByModelAndCardManufacturer(TestCardForm testCardForm);

    TestCardVo findById(@Param("id") String id);
}
