package com.ecp.jces.jctool.util;

import com.ecp.jces.jctool.exception.GenApduException;
import com.ecp.jces.jctool.exception.TlvAnalysisException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GPUtil {

    //public static String DEFAULT = "(--------)";
    public static char NO = '-';
    //public static String PRIVILEGE_CODE = "(SVMLTDPA)";

    public static String getApplicationPrivilegeCode(byte privilege) {

        StringBuffer pc = new StringBuffer("(");
        byte cursor = (byte) 0x80;
        int bitIndex = 8;

        while (bitIndex > 0) {
            if ((privilege & cursor) > 0) {
                switch (bitIndex) {
                    case 8:
                        pc.append('S');
                        break;
                    case 7:
                        pc.append('V');
                        break;
                    case 6:
                        pc.append('M');
                        break;
                    case 5:
                        pc.append('L');
                        break;
                    case 4:
                        pc.append('T');
                        break;
                    case 3:
                        pc.append('D');
                        break;
                    case 2:
                        pc.append('P');
                        break;
                    case 1:
                        pc.append('A');
                        break;
                    default:
                        pc.append(NO);
                        break;
                }
            } else {
                pc.append(NO);
            }

            // 移位
            bitIndex--;
            cursor = (byte) (cursor >> 1);
        }
        pc.append(')');
        return pc.toString();
    }

    public static String genInstallForInstallApdu(String pkgAid, String moduleAid) throws GenApduException {
        return genInstallForInstallApdu(pkgAid, moduleAid, moduleAid, null);
    }

    public static String genInstallForLoadApdu(String pkgAid, String loadParam) throws GenApduException {

        if (!StringUtil.isHexString(pkgAid)) {
            throw new GenApduException("pkgAid is not HexString.");
        }

//		if (!StringUtil.isHexString(appletAid)) {
//			throw new GenApduException("appletAid is not HexString.");
//		}
//
//		if (!StringUtil.isHexString(instanceAid)) {
//			throw new GenApduException("instanceAid is not HexString.");
//		}

//		if (!StringUtil.isEmpty(loadParam) && !StringUtil.isHexString(loadParam)) {
//			throw new GenApduException("loadParam is not HexString.");
//		}

        StringBuilder apdu = new StringBuilder();
        apdu.append("80E60200");

        StringBuilder data = new StringBuilder();

        //pkg aid
        data.append(HexUtil.intToHex(pkgAid.length() / 2));
        data.append(pkgAid);

        data.append("10D1560001010001600000000100000000");
//		data.append("00"); // isd
        data.append("00"); // block
        //load param
        if (!StringUtil.isEmpty(loadParam)) {
            data.append(HexUtil.intToHex(loadParam.length() / 2));
            data.append(loadParam);
        } else {
            data.append("00");
        }

        data.append("00");

        apdu.append(HexUtil.intToHex(data.length() / 2));
        apdu.append(data);

        return apdu.toString();
    }

    public static String genInstallForInstallApdu(String pkgAid, String moduleAid, String instanceAid, String installParam) throws GenApduException {

        if (!StringUtil.isHexString(pkgAid)) {
            throw new GenApduException("pkgAid is not HexString.");
        }

        if (!StringUtil.isHexString(moduleAid)) {
            throw new GenApduException("moduleAid is not HexString.");
        }

        if (!StringUtil.isHexString(instanceAid)) {
            throw new GenApduException("instanceAid is not HexString.");
        }

        if (!StringUtil.isEmpty(installParam) && !StringUtil.isHexString(installParam)) {
            throw new GenApduException("installParam is not HexString.");
        }

        StringBuilder apdu = new StringBuilder();
        apdu.append("80E60C00");

        StringBuilder data = new StringBuilder();

        //pkg aid
        data.append(HexUtil.intToHex(pkgAid.length() / 2));
        data.append(pkgAid);

        //applet aid
        data.append(HexUtil.intToHex(moduleAid.length() / 2));
        data.append(moduleAid);

        //instance aid
        data.append(HexUtil.intToHex(instanceAid.length() / 2));
        data.append(instanceAid);

        //Privileges
        data.append("0100");

        //Install Parameters field
        if (!StringUtil.isEmpty(installParam)) {
            data.append(HexUtil.intToHex(installParam.length() / 2));
            data.append(installParam);
        } else {
            data.append("02C900");
        }

        //Install Token
        data.append("00");

        apdu.append(HexUtil.intToHex(data.length() / 2));
        apdu.append(data);

        return apdu.toString();
    }

    public static String _genIntTag(String tag, int v) {
        StringBuilder tv = new StringBuilder();

        tv.append(tag);
        if (v <= 0x7FFF) {
            tv.append("02").append(HexUtil.intToHex(v, 2));
        } else {
            tv.append("04").append(HexUtil.intToHex(v, 4));
        }
        return tv.toString();
    }


    public static String genInstallForLoadParam(int c6, int c7, int c8) {
        StringBuilder apdu = new StringBuilder();

//        apdu.append(_genIntTag("C6", c6));
        apdu.append(_genIntTag("C7", c7));
        apdu.append(_genIntTag("C8", c8));

        return "EF" + HexUtil.intToHex(apdu.length() / 2) + apdu.toString();
    }

    private static void setLoadSpace(Tlv ptlv, int c6, int c7, int c8) throws TlvAnalysisException {
        LinkedList<Tlv> tlvList = Tlv.process(ptlv.getData());

        Map<String, Tlv> tlvMap = new HashMap<>();

        for (Tlv tlv : tlvList) {
            tlvMap.put(tlv.getTag(), tlv);

            switch (tlv.getTag()) {
                case "C6":
                    tlv.setData(intToHexString(c6));
                    break;
                case "C7":
                    tlv.setData(intToHexString(c7));
                    break;
                case "C8":
                    tlv.setData(intToHexString(c8));
                    break;
            }
        }

        if (!tlvMap.containsKey("C8")) {
            tlvList.addFirst(new Tlv("C8", intToHexString(c8)));
        }

        if (!tlvMap.containsKey("C7")) {
            tlvList.addFirst(new Tlv("C7", intToHexString(c7)));
        }

        if (!tlvMap.containsKey("C6")) {
            tlvList.addFirst(new Tlv("C6", intToHexString(c6)));
        }

        StringBuilder hexData = new StringBuilder();
        for (Tlv _tlv : tlvList) {
            hexData.append(_tlv.toHex());
        }

        ptlv.setData(hexData.toString());
    }

    public static String genInstallForLoadParam(int c6, int c7, int c8, String loadParam) throws TlvAnalysisException {

        if (StringUtil.isEmpty(loadParam)) {
            return genInstallForLoadParam(c6, c7, c8);
        }

        LinkedList<Tlv> tlvList = Tlv.process(loadParam);

        StringBuilder param = new StringBuilder();

        for (Tlv tlv : tlvList) {
            if ("EF".equalsIgnoreCase(tlv.getTag())) {
                setLoadSpace(tlv, c6, c7, c8);
            }

            param.append(tlv.toHex());
        }

        return param.toString();
    }

    public static String genInstallForInstallParam(int c7, int c8) {
        StringBuilder apdu = new StringBuilder();

        apdu.append(_genIntTag("C7", c7));
        apdu.append(_genIntTag("C8", c8));

        if (c7 >= 0x7FFF) {
            apdu.append(_genIntTag("D7", 0x7FFF));
        } else {
            apdu.append(_genIntTag("D7", c7));
        }

        if (c8 >= 0x7FFF) {
            apdu.append(_genIntTag("D8", 0x7FFF));
        } else {
            apdu.append(_genIntTag("D8", c8));
        }

        return "C900EF" + HexUtil.intToHex(apdu.length() / 2) + apdu.toString();
    }

    private static String intToHexString(int v) {
        if (v <= 0x7FFF) {
            return HexUtil.intToHex(v, 2);
        } else {
            return HexUtil.intToHex(v, 4);
        }
    }

    private static void setInstallSpace(Tlv ptlv, int c7, int c8) throws TlvAnalysisException {
        String sdata = ptlv.getData();
        LinkedList<Tlv> tlvList = null;
        if (!StringUtil.isEmpty(sdata)) {
            tlvList = Tlv.process(sdata);
        } else {
            tlvList = new LinkedList<>();
        }

        Map<String, Tlv> tlvMap = new HashMap<>();

        for (Tlv tlv : tlvList) {
            tlvMap.put(tlv.getTag(), tlv);

            switch (tlv.getTag()) {
                case "D7":
                    if (c7 > 0x7FFF) {
                        tlv.setData(intToHexString(0x7FFF));
                    } else {
                        tlv.setData(intToHexString(c7));
                    }
                    break;
                case "C7":
                    tlv.setData(intToHexString(c7));
                    break;
                case "D8":
                    if (c8 > 0x7FFF) {
                        tlv.setData(intToHexString(0x7FFF));
                    } else {
                        tlv.setData(intToHexString(c8));
                    }
                    break;
                case "C8":
                    tlv.setData(intToHexString(c8));
                    break;
            }
        }

        Tlv tlv = null;
        if (!tlvMap.containsKey("D8")) {
            if (c8 > 0x7FFF) {
                tlv = new Tlv("D8", intToHexString(0x7FFF));
            } else {
                tlv = new Tlv("D8", intToHexString(c8));
            }
            tlvList.addFirst(tlv);
        }

        if (!tlvMap.containsKey("D7")) {
            if (c7 > 0x7FFF) {
                tlv = new Tlv("D7", intToHexString(0x7FFF));
            } else {
                tlv = new Tlv("D7", intToHexString(c7));
            }
            tlvList.addFirst(tlv);
        }

        if (!tlvMap.containsKey("C8")) {
            tlvList.addFirst(new Tlv("C8", intToHexString(c8)));
        }

        if (!tlvMap.containsKey("C7")) {
            tlvList.addFirst(new Tlv("C7", intToHexString(c7)));
        }

        StringBuilder hexData = new StringBuilder();
        for (Tlv _tlv : tlvList) {
            hexData.append(_tlv.toHex());
        }

        ptlv.setData(hexData.toString());
    }

    public static String genInstallForInstallParam(int c7, int c8, String installParam) throws TlvAnalysisException {

        if (StringUtil.isEmpty(installParam)) {
            return genInstallForInstallParam(c7, c8);
        }

        LinkedList<Tlv> tlvList = Tlv.process(installParam);

        int efIndex = tlvList.indexOf(new Tlv("EF", ""));
        if (efIndex >= 0) {
            setInstallSpace(tlvList.get(efIndex), c7, c8);
        } else {
            Tlv efTlv = new Tlv("EF", "");
            tlvList.addFirst(efTlv);
            setInstallSpace(efTlv, c7, c8);
        }

        int index = tlvList.indexOf(new Tlv("C9", ""));
        if (index >= 0) {
            Tlv c9Tlv = tlvList.remove(index);
            tlvList.addFirst(c9Tlv);
        } else {
            tlvList.addFirst(new Tlv("C9", ""));
        }

        StringBuilder param = new StringBuilder();

        for (Tlv tlv : tlvList) {
//            if ("EF".equalsIgnoreCase(tlv.getTag())) {
//                setInstallSpace(tlv, c7, c8);
//            }

            param.append(tlv.toHex());
        }

        return param.toString();
    }
    public static void main(String[] args) {
        try {
//            System.out.println("   installForLoad: " + genInstallForLoadParam(44786, 3072, 44786, "EF19C6020200C7020000C8020000CD01AADD02BBCCD60411223344B60411223344"));

//            System.out.println("installForInstall: " + genInstallForInstallParam(0xF20, 0xF50, "EF38C7020260C8023000A003810101A121A3104F0E325041592E5359532E4444463031A60D50084D4F545F545F4550870102D7020260D8022134C903650101"));
            System.out.println("installForInstall33333: " + genInstallForInstallParam(0xF20, 0xF50, "C900EA1D800FFF00110100000102010203B0002B00810400010000820400010000"));
        } catch (TlvAnalysisException e) {
            e.printStackTrace();
        }

        /*
        System.out.println(genInstallForLoadParam(44786, 3072, 44786));
        System.out.println(genInstallForInstallParam(3072, 44786));

        try {
            System.out.println(GPUtil.genInstallForLoadApdu("01020304050607080906", "0102030405060708090006"));
        } catch (GenApduException e) {
            e.printStackTrace();
        }
         */
    }
}
