package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestMatrixCardForm;
import com.ecp.jces.vo.TestMatrixCardVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMatrixCardMapper {
    void add(List<TestMatrixCardForm> list);
    List<TestMatrixCardVo> findByMatrix(String id);
    void deleteByMatrixId(String id);
}
