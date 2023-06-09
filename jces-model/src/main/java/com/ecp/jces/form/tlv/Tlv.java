package com.ecp.jces.form.tlv;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-08-27 11:22
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class Tlv {
    public Tlv(String tag, int length, String value) {
        this.length = length;
        this.tag = tag;
        this.value = value;
    }

    /** 子域Tag标签 */
    private String tag;

    /** 子域取值的长度 */
    private int length;

    /** 子域取值 */
    private String value;
}
