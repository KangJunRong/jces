package com.ecp.jces.server.dc.service.cos.impl;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.VmCosDownloadForm;
import com.ecp.jces.server.dc.mapper.cos.VmCosDownloadMapper;
import com.ecp.jces.server.dc.service.cos.VmCosDownloadService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.VmCosDownloadVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class VmCosDownloadServiceImpl implements VmCosDownloadService {
    @Autowired
    private VmCosDownloadMapper vmCosDownloadMapper;

    public Pagination<VmCosDownloadVo> page(VmCosDownloadForm cosForm){
        Calendar cal = Calendar.getInstance();
        if(cosForm.getEndTime()!=null && cosForm.getStartTime() ==null){
            cal.set(1000,01,01);
            cosForm.setStartTime(cal.getTime());
        }
        if(cosForm.getStartTime()!=null && cosForm.getEndTime()==null)
            cosForm.setEndTime(new Date());
        if(cosForm.getStartTime()!=null) {
            Date sTime = cosForm.getStartTime();
            cal.setTime(sTime);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cosForm.setStartTime(cal.getTime());
        }
        if(cosForm.getEndTime()!=null) {
            Date eTime = cosForm.getEndTime();
            cal.setTime(eTime);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cosForm.setEndTime(cal.getTime());
        }
        Page<Object> pageHelper = PageHelper.startPage(cosForm.getPage(),cosForm.getPageCount());
        List<VmCosDownloadVo> list = vmCosDownloadMapper.list(cosForm);
        Pagination<VmCosDownloadVo> pagination = new Pagination<>(cosForm.getPage(),cosForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    public void add(VmCosDownloadForm cosDownloadForm){
        cosDownloadForm.setId(StrUtil.newGuid());
        vmCosDownloadMapper.add(cosDownloadForm);
    }
}
