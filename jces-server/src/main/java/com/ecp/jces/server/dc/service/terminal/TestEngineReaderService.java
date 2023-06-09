package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestEngineReaderForm;
import com.ecp.jces.form.extra.CardInfoForm;
import com.ecp.jces.vo.TestEngineReaderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestEngineReaderService {

    List<TestEngineReaderVo> findList(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException;

    Pagination<TestEngineReaderVo> page(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException;

    void bindCard(@Param("id")String id, @Param("testCardId") String testCardId) throws FrameworkRuntimeException;

    void unbindCard(@Param("id")String id) throws FrameworkRuntimeException;

    void syncReaders(TestEngineReaderForm testEngineReaderForm)throws FrameworkRuntimeException;

    void setReadersOffLine(TestEngineReaderForm testEngineReaderForm)throws FrameworkRuntimeException;
}
