package com.ecp.jces.server.dc.mapper.terminal;

import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.vo.TestMatrixVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestMatrixMapper {
    void add(List<TestMatrixForm> list);
    List<TestMatrixVo> list(TestMatrixForm form);
    void update(TestMatrixForm form);
    void matrixInfoClean(@Param("engineId") String engineId);

    int isExist(@Param("versionNo") String versionNo);

    List<TestMatrixVo> findFreeList(TestMatrixForm testMatrixForm);

    TestMatrixVo findById(@Param("matrixId")String matrixId);
}
