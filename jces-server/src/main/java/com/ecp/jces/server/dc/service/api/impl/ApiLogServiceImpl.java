package com.ecp.jces.server.dc.service.api.impl;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiLogForm;
import com.ecp.jces.server.dc.mapper.api.ApiLogMapper;
import com.ecp.jces.server.dc.service.api.ApiLogService;
import com.ecp.jces.vo.ApiLogVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ApiLogServiceImpl implements ApiLogService {
    @Autowired
    private ApiLogMapper apiLogMapper;
    public Pagination<ApiLogVo> page(ApiLogForm form){
        Calendar cal = Calendar.getInstance();
        if(form.getEndTime()!=null && form.getStartTime()==null) {
            cal.set(1000,01,01);
            form.setStartTime(cal.getTime());
        }
        if(form.getStartTime()!=null && form.getEndTime()==null)
            form.setEndTime(new Date());
        if(form.getStartTime()!=null) {
            Date sTime = form.getStartTime();
            cal.setTime(sTime);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            form.setStartTime(cal.getTime());
        }
        if(form.getEndTime()!=null) {
            Date eTime = form.getEndTime();
            cal.setTime(eTime);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            form.setEndTime(cal.getTime());
        }
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        List<ApiLogVo> list = apiLogMapper.list(form);
        Pagination<ApiLogVo> pagination = new Pagination<>(form.getPage(),form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Async("uploadMsgExecutor")
    public void add(ApiLogForm form) {
        apiLogMapper.insert(form);
    }
}
