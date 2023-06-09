package com.ecp.jces.server.dc.service.api;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiLogForm;
import com.ecp.jces.vo.ApiLogVo;


public interface ApiLogService {
    Pagination<ApiLogVo> page(ApiLogForm form);

    void add(ApiLogForm form);
}
