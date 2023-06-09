package com.ecp.jces.form;

import com.ecp.jces.vo.TestMatrixVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
public class TestEngineForm extends BaseEntityForm{

    private String name;
    private String ip;//卡片型号
    private String description;
    private String status;
    private Date commDate;
    private String port;

    //引擎异常信息
    private String exMsg;

    private List<TestMatrixForm> list;
}
