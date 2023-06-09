package com.ecp.jces.server.dc.service.async;

import com.ecp.jces.form.extra.StartTestForm;

public interface AsyncService {
    void handleSaveApi(StartTestForm testForm);
}
