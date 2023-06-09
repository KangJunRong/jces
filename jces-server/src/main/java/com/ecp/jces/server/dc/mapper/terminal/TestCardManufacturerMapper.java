package com.ecp.jces.server.dc.mapper.terminal;


import com.ecp.jces.form.TestCardManufacturerForm;
import com.ecp.jces.vo.TestCardManufacturerVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestCardManufacturerMapper {

    List<TestCardManufacturerVo> findList(TestCardManufacturerForm TestCardManufacturerForm);

    void insert(TestCardManufacturerForm TestCardManufacturerForm);

    void update(TestCardManufacturerForm TestCardManufacturerForm);

    void delete(TestCardManufacturerForm TestCardManufacturerForm);

    @MapKey("code")
    Map<String,TestCardManufacturerVo> allForCode();

    TestCardManufacturerVo getByCode(TestCardManufacturerForm TestCardManufacturerForm);
    TestCardManufacturerVo getByName(TestCardManufacturerForm TestCardManufacturerForm);
    void updateName(TestCardManufacturerForm TestCardManufacturerForm);

}
