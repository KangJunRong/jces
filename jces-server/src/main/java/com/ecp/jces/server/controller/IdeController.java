package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.IdeLogMsgForm;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.sys.IdeLogMsgService;
import com.ecp.jces.server.dc.service.sys.impl.IdeLogMsgServiceImpl;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.IdeLogMsgVo;
import com.ecp.jces.vo.SysConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ide")
public class IdeController extends Base {

    @Autowired
    private IdeLogMsgService ideLogMsgService;


    /**
     * ide日志上报
     *
     * @param request
     * @param list
     * @throws IOException
     */
    @PostMapping(value = "/logUpload")
    public String logUpload(HttpServletRequest request, @RequestBody List<IdeLogMsgForm> list) throws IOException {
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        ideLogMsgService.adds(list, ip);
        return ResultCode.Success;
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody IdeLogMsgForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        Pagination<IdeLogMsgVo> pagination = ideLogMsgService.page(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(@RequestBody IdeLogMsgForm form) {
        VerificationUtils.longs("id", form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, ideLogMsgService.findById(form));
    }
}
