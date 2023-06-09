package com.ecp.jces.server.dc.service.cos.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.VmCosFileForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.cos.VmCosFileMapper;
import com.ecp.jces.server.dc.mapper.cos.VmCosMapper;
import com.ecp.jces.server.dc.service.cos.VmCosFileService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.UserVo;
import com.ecp.jces.vo.VmCosFileVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class VmCosFileServiceImpl implements VmCosFileService {
    @Autowired
    private VmCosFileMapper vmCosFileMapper;

    @Autowired
    private VmCosMapper vmCosMapper;
    public void add(VmCosFileForm vmCosFileForm) {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        vmCosFileForm.setId(StrUtil.newGuid());
        if (vmCosFileForm.getCosId() == null) {
            String cosId = vmCosMapper.findByVersionNo(vmCosFileForm.getVersionNo()).getId();
            vmCosFileForm.setCosId(cosId);
        }
        if (vmCosFileForm.getNo() == null && vmCosFileMapper.list(vmCosFileForm).size() == 0) {
            vmCosFileForm.setNo(1);
        } else {
            vmCosFileForm.setNo(vmCosFileMapper.list(vmCosFileForm).size() + 1);
        }

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

    // 将del_flg设置为1
    public void del(VmCosFileForm vmCosFileForm) throws FrameworkRuntimeException {
        vmCosFileMapper.del(vmCosFileForm);
    }

    // 将数据从数据库中删除
    public void delete(VmCosFileForm vmCosFileForm) throws FrameworkRuntimeException{
        vmCosFileMapper.delete(vmCosFileForm);
    }

    public Pagination<VmCosFileVo> page(VmCosFileForm vmCosFileForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(vmCosFileForm.getPage(), vmCosFileForm.getPageCount());
        List<VmCosFileVo> list = vmCosFileMapper.list(vmCosFileForm);
        Pagination<VmCosFileVo> pagination = new Pagination<>(vmCosFileForm.getPage(), vmCosFileForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public List<VmCosFileVo> getCosChildVersion() {
        return vmCosFileMapper.getCosChildVersion();
    }
}
