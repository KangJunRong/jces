package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.form.extra.ResultForm;
import com.ecp.jces.vo.TestEngineVo;
import com.ecp.jces.vo.extra.JavaCardDataVo;

import java.util.List;

public interface TestEngineService {

    List<TestEngineVo> findList(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    Pagination<TestEngineVo> page(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void add(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void update(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void delete(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    TestEngineVo detail(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void updateStatus(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void heartbeat(TestEngineForm form) throws FrameworkRuntimeException;

    TestEngineVo findByIp(TestEngineForm form) throws FrameworkRuntimeException;

    TestEngineVo getById(TestEngineForm form) throws FrameworkRuntimeException;

    void vmCosResult(String ip, JavaCardDataVo result) throws FrameworkRuntimeException;

    void uploadResult(String ip, ResultForm form) throws FrameworkRuntimeException;

    void uploadBusinessResult(String ip, ResultForm form) throws FrameworkRuntimeException;

    void register(TestEngineForm testEngineForm) throws FrameworkRuntimeException;

    void uploadEngineInfo(TestEngineForm form)throws FrameworkRuntimeException;

    void callbackTesting(String ip, TestTaskForm form)throws FrameworkRuntimeException;

    void callbackStop(String ip, TestTaskForm form)throws FrameworkRuntimeException;
}
