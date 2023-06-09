package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestEngineReaderForm;
import com.ecp.jces.vo.TestEngineReaderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestEngineReaderMapper {

    List<TestEngineReaderVo> findList(TestEngineReaderForm TestEngineReaderForm);

    void bindCard(@Param("id") String id, @Param("testCardId") String testCardId);

    void unbindCard(@Param("id") String id);

    void setOffStatusByEngineId(@Param("engineId") String engineId, @Param("status") String status);

    TestEngineReaderVo findByEngineIdAndName(@Param("engineId") String engineId,@Param("name") String name);

    void add(TestEngineReaderForm form);

    void updateByEngine(TestEngineReaderForm form);

    void delete(TestEngineReaderForm form);

    void changeStatusById(@Param("id") String id, @Param("status") String status);
}
