package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.RoleForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.role.RoleService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController extends Base {
    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody RoleForm roleForm) {
        roleService.add(roleForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody RoleForm roleForm) {
        roleService.update(roleForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody RoleForm roleForm) {
        VerificationUtils.string("id", roleForm.getId());
        roleService.delete(roleForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody RoleForm roleForm) {
        VerificationUtils.integer("page", roleForm.getPage());
        VerificationUtils.integer("pageCount", roleForm.getPageCount());
        Pagination<RoleVo> pagination = roleService.page(roleForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/list", produces = GlobalContext.PRODUCES)
    public String list(@RequestBody RoleForm roleForm) {
        List<RoleVo> list = roleService.findList(roleForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, list);
    }




}
