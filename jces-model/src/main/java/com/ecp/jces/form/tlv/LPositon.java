package com.ecp.jces.form.tlv;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-08-27 11:24
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class LPositon {
    private Integer vl;
    private Integer position;
    public LPositon(int vL, int position) {
        this.vl = vL;
        this.position = position;
    }
}
