package com.ecp.jces.server.util;

import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.form.tlv.LPositon;
import com.ecp.jces.form.tlv.Tlv;
import com.ecp.jces.jctool.util.GPUtil;
import com.ecp.jces.jctool.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-08-27 11:21
 **/
public class TlvUtils {

    /**
     * 将16进制字符串转换为TLV对象列表
     *
     * @param hexString
     * @return
     */
    public static List<Tlv> builderTlvList(String hexString) {
        List<Tlv> tlvs = new ArrayList<Tlv>();

        int position = 0;
        while (position != StringUtils.length(hexString)) {
            String _hexTag = getTag(hexString, position);
            position += _hexTag.length();

            LPositon l_position = getLengthAndPosition(hexString, position);
            int _vl = l_position.getVl();

            position = l_position.getPosition();

            String _value = StringUtils.substring(hexString, position, position + _vl * 2);

            position = position + _value.length();

            tlvs.add(new Tlv(_hexTag, _vl, _value));
        }
        return tlvs;
    }

    /**
     * 将16进制字符串转换为TLV对象MAP
     *
     * @param hexString
     * @return
     */
    public static Map<String, Tlv> builderTlvMap(String hexString) {

        Map<String, Tlv> tlvs = new HashMap<String, Tlv>();

        int position = 0;
        while (position != hexString.length()) {
            String _hexTag = getTag(hexString, position);

            position += _hexTag.length();

            LPositon l_position = getLengthAndPosition(hexString, position);

            int _vl = l_position.getVl();
            position = l_position.getPosition();
            String _value = hexString.substring(position, position + _vl * 2);
            position = position + _value.length();

            tlvs.put(_hexTag, new Tlv(_hexTag, _vl, _value));
        }
        return tlvs;
    }

    /**
     * 返回最后的Value的长度
     *
     * @param hexString
     * @param position
     * @return
     */
    private static LPositon getLengthAndPosition(String hexString, int position) {
        String firstByteString = hexString.substring(position, position + 2);
        int i = Integer.parseInt(firstByteString, 16);
        String hexLength = "";

        if (((i >>> 7) & 1) == 0) {
            hexLength = hexString.substring(position, position + 2);
            position = position + 2;
        } else {
            // 当最左侧的bit位为1的时候，取得后7bit的值，
            int _L_Len = i & 127;
            position = position + 2;
            hexLength = hexString.substring(position, position + _L_Len * 2);
            // position表示第一个字节，后面的表示有多少个字节来表示后面的Value值
            position = position + _L_Len * 2;
        }
        return new LPositon(Integer.parseInt(hexLength, 16), position);

    }

    /**
     * 取得子域Tag标签
     *
     * @param hexString
     * @param position
     * @return
     */
    private static String getTag(String hexString, int position) {
        String firstByte = StringUtils.substring(hexString, position, position + 2);
        int i = Integer.parseInt(firstByte, 16);
        if ((i & 0x1f) == 0x1f) {
            return hexString.substring(position, position + 4);

        } else {
            return hexString.substring(position, position + 2);
        }
    }


    public static String genInstallForInstallParam(int c7, int c8, String installParam) {
        if (StrUtil.isBlank(installParam)) {
            return GPUtil.genInstallForInstallParam(c7, c8);
        }
        //转换成大写
        installParam = installParam.toUpperCase();
        Map<String, Tlv> map = builderTlvMap(installParam);
        String header = "";
        if (map.get("C9") == null) {
            header = "C900";
        }
        Tlv data = map.get("EF");
        if (data == null) {
            StringBuilder apdu = new StringBuilder();
            apdu.append(GPUtil._genIntTag("C7", c7));
            apdu.append(GPUtil._genIntTag("C8", c8));
            return header + installParam + "EF" + HexUtil.intToHex(apdu.length() / 2) + apdu.toString();
        }

        Map<String, Tlv> mapData = builderTlvMap(data.getValue());
        String efLength = HexUtil.intToHex(data.getLength());
        installParam = installParam.replace("EF" + efLength + data.getValue(), "");

        //有的话先替换空，再接上
        String oldData = data.getValue();
        Tlv c7Tlv = mapData.get("C7");
        Tlv c8Tlv = mapData.get("C8");
        if (c7Tlv != null) {
            oldData = oldData.replace(c7Tlv.getTag() + HexUtil.intToHex(c7Tlv.getLength())
                    + c7Tlv.getValue(), "");
        }
        if (c8Tlv != null) {
            oldData = oldData.replace(c8Tlv.getTag() + HexUtil.intToHex(c8Tlv.getLength())
                    + c8Tlv.getValue(), "");
        }
        StringBuilder apdu = new StringBuilder();
        apdu.append(GPUtil._genIntTag("C7", c7));
        apdu.append(GPUtil._genIntTag("C8", c8));
        apdu.append(oldData);
        return installParam + "EF" + HexUtil.intToHex(apdu.length() / 2) + apdu.toString();
    }

/*    public static void main(String[] args){
        String data = "C900EF10C7020C00C8025800D7020C00D8025800EA1D800FFF00110100000102010203B000520081040001000082040001000000";
        Map<String, Tlv> map = builderTlvMap(data);
        System.out.println(JSONUtils.toJSONString(map));
    }*/
}
