package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiLinkForm  extends BaseForm{
    private String id;
    private String apiRoleId;
    private String forbiddenId;

    private List<String> forbiddenIdList;
    private List<ApiLinkIdForm> idList;

}
