package com.ecp.jces.jctool.util;

import com.ecp.jces.jctool.exception.TlvAnalysisException;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class Tlv {

    private String tag;
    private int length;
    private String data;

    public Tlv(String tag, String data) {
        this.tag = tag;
        this.data = data;

        this.length = data.length() / 2;
    }

    public void setData(String data) {
        this.data = data;
        this.length = data.length() / 2;
    }

    public static LinkedList<Tlv> process(String tlvs) throws TlvAnalysisException {
        LinkedList<Tlv> tlvList = new LinkedList<>();

        if (!StringUtil.isHexString(tlvs)) {
            throw new TlvAnalysisException("不符合 HEX 字符串格式。");
        }

        tlvs = tlvs.toUpperCase();
        int offset = 0;
        int length = tlvs.length();
        Tlv tlv = null;

        String _tag = null;
        int tLen;
        while (offset < length) {
            if (offset + 4 > length) {
                throw new TlvAnalysisException("不符合 TLV 格式。");
            }

            _tag = tlvs.substring(offset, offset + 2);
            tLen = Integer.parseInt(tlvs.substring(offset + 2, offset + 4), 16);

            if (offset + 4 + tLen * 2 > length) {
                throw new TlvAnalysisException("不符合 TLV 格式。");
            }
            tlv = new Tlv(_tag, tlvs.substring(offset + 4, offset + 4 + tLen * 2));

            tlvList.add(tlv);

            offset += (4 + tLen * 2);
        }

        Map<String, Tlv> tlvMap = new HashMap<>();
        for (Tlv _tlv : tlvList) {
            if (tlvMap.containsKey(_tlv.tag)) {
                throw new TlvAnalysisException("HEX 字符串包含多个TAG：" + _tlv.tag);
            }

            tlvMap.put(_tlv.tag, _tlv);
        }

        return tlvList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tlv) {
            Tlv tlv = (Tlv) obj;
            if (this.getTag().equalsIgnoreCase(tlv.getTag())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getTag(), 16);
    }

    //    private String intToHexString(int v) {
//        if (v <= 0x7FFF) {
//            return HexUtil.intToHex(v, 2);
//        } else {
//            return HexUtil.intToHex(v, 4);
//        }
//    }
//
//    public void setInstallSpace(int c7, int c8) throws TlvAnalysisException {
//        LinkedList<Tlv> tlvList = Tlv.process(data);
//
//        Map<String, Tlv> tlvMap = new HashMap<>();
//
//        for (Tlv tlv : tlvList) {
//            tlvMap.put(tlv.getTag(), tlv);
//
//            switch (tlv.getTag()) {
//                case "D7":
//                    if (c7 > 0x7FFF) {
//                        tlv.setData(intToHexString(0x7FFF));
//                    } else {
//                        tlv.setData(intToHexString(c7));
//                    }
//                    break;
//                case "C7":
//                    tlv.setData(intToHexString(c7));
//                    break;
//                case "D8":
//                    if (c8 > 0x7FFF) {
//                        tlv.setData(intToHexString(0x7FFF));
//                    } else {
//                        tlv.setData(intToHexString(c8));
//                    }
//                    break;
//                case "C8":
//                    tlv.setData(intToHexString(c8));
//                    break;
//            }
//        }
//
//        Tlv tlv = null;
//        if (!tlvMap.containsKey("D8")) {
//            if (c8 > 0x7FFF) {
//                tlv = new Tlv("D8", intToHexString(0x7FFF));
//            } else {
//                tlv = new Tlv("D8", intToHexString(c8));
//            }
//            tlvList.addFirst(tlv);
//        }
//
//        if (!tlvMap.containsKey("D7")) {
//            if (c7 > 0x7FFF) {
//                tlv = new Tlv("D7", intToHexString(0x7FFF));
//            } else {
//                tlv = new Tlv("D7", intToHexString(c7));
//            }
//            tlvList.addFirst(tlv);
//        }
//
//        if (!tlvMap.containsKey("C8")) {
//            tlvList.addFirst(new Tlv("C8", intToHexString(c8)));
//        }
//
//        if (!tlvMap.containsKey("C7")) {
//            tlvList.addFirst(new Tlv("C7", intToHexString(c7)));
//        }
//
//        StringBuilder hexData = new StringBuilder();
//        for (Tlv _tlv : tlvList) {
//            hexData.append(_tlv.toHex());
//        }
//
//        setData(hexData.toString());
//    }

    public String toHex() {
        StringBuilder hex = new StringBuilder();
        hex.append(tag);
        hex.append(HexUtil.intToHex(length));
        hex.append(data);

        return hex.toString().toUpperCase();
    }

}
