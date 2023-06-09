package com.ecp.jces.server.dc.service.cos.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.VmCosFileForm;
import com.ecp.jces.form.VmCosForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.cos.VmCosFileMapper;
import com.ecp.jces.server.dc.mapper.cos.VmCosMapper;
import com.ecp.jces.server.dc.service.cos.VmCosService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.UserVo;
import com.ecp.jces.vo.VmCosVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class VmCosServiceImpl implements VmCosService {
    @Autowired
    private VmCosMapper vmCosMapper;

    @Autowired
    private VmCosFileMapper vmCosFileMapper;
    @Override
    public List<VmCosVo> list(VmCosForm cosForm) throws FrameworkRuntimeException {
        return vmCosMapper.list(cosForm);
    }

    @Override
    public void del(VmCosForm cosForm) throws FrameworkRuntimeException {
        cosForm.setDelFlg(ResultCode.DEL);
        vmCosMapper.del(cosForm);
    }

    public void updateStatus(VmCosForm cosForm) throws FrameworkRuntimeException {
        if (cosForm.getStatus() == 1) {
            cosForm.setStatus(ConstantCode.STATUS_PUBLISH);
        } else if (cosForm.getStatus() == 2) {
            cosForm.setStatus(ConstantCode.STATUS_PIGEONHOLE);
        }
        vmCosMapper.edit(cosForm);
    }

    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(VmCosForm form){

        if(findByVersionNo(form.getVersionNo())!=null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "该版本号已经被使用");
        }

        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        form.setId(StrUtil.newGuid());
        form.setStatus(ConstantCode.STATUS_SAVE);
        form.setCreateUser(vo.getName());
        form.setCreateDate(date);
        form.setUpdateUser(vo.getName());
        form.setUpdateDate(date);
        form.setDelFlg(ResultCode.NOT_DEL);
        vmCosMapper.add(form);

        VmCosFileForm vmCosFileForm = form.getFileForm();
        vmCosFileForm.setId(StrUtil.newGuid());
        vmCosFileForm.setCosId(form.getId());
        vmCosFileForm.setVersionNo(form.getVersionNo());
        vmCosFileForm.setNo(1);

        String path = AesUtil2.decryptAES2(vmCosFileForm.getFileId());
        try {
            String filePath = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
            String hash = filePath.substring(filePath.length()-64-vmCosFileForm.getFileName().length()-1,
                    filePath.length()-vmCosFileForm.getFileName().length()-1);
            vmCosFileForm.setFileHash(hash);
        } catch (UnsupportedEncodingException e) {
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }
        vmCosFileForm.setDelFlg(ResultCode.NOT_DEL);
        vmCosFileForm.setUpdateUser(vo.getName());
        vmCosFileForm.setUpdateDate(date);
        vmCosFileForm.setCreateUser(vo.getName());
        vmCosFileForm.setCreateDate(date);
        vmCosFileMapper.add(vmCosFileForm);
    }

    public VmCosVo findByVersionNo(String versionNo) throws  FrameworkRuntimeException{
         return vmCosMapper.findByVersionNo(versionNo);
    }

    public void edit(VmCosForm cosForm) throws FrameworkRuntimeException{
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        cosForm.setUpdateUser(vo.getName());
        cosForm.setUpdateDate(date);
        vmCosMapper.edit(cosForm);
    }

    @Override
    public Pagination<VmCosVo> page(VmCosForm cosForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(cosForm.getPage(), cosForm.getPageCount());
        List<VmCosVo> list = vmCosMapper.list(cosForm);
        Pagination<VmCosVo> pagination = new Pagination<>(cosForm.getPage(), cosForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }
}
