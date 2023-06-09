package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.vo.TestEngineVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TestEngineMapper {

    List<TestEngineVo> findList(TestEngineForm testEngineForm);

    void insert(TestEngineForm testEngineForm);

    void update(TestEngineForm testEngineForm);

    void delete(TestEngineForm testEngineForm);

    TestEngineVo getById(TestEngineForm testEngineForm);

    void updateStatus(TestEngineForm testEngineForm);

    TestEngineVo getByIp(TestEngineForm testEngineForm);

    TestEngineVo getByName(TestEngineForm testEngineForm);

    void setOffLine(@Param("date") Date date);

    TestEngineVo findByMatrixId(@Param("matrixId")String matrixId);

    void checkOffLine(@Param("offLineTime")int offLineTime);

    void updateExMsg(@Param("engineId")String engineId, @Param("exMsg")String exMsg);
}
