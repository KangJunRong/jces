package com.ecp.jces.jctool.smartcardio;

import com.ecp.jces.jctool.util.HexUtil;

import javax.smartcardio.*;
import java.security.Security;
import java.util.List;

public class PcscTest {



    public static void main(String[] args) {
        TerminalFactory context;
        CardTerminals terminals;


        try {
            if (true) {
                Security.addProvider(new Smartcardio());
                context = TerminalFactory.getInstance("PC/SC", null, Smartcardio.PROVIDER_NAME);

                terminals = context.terminals();
            } else {
                TerminalFactory terminalFactory = TerminalFactory.getDefault();
                terminals = terminalFactory.terminals();
            }

            List<CardTerminal> terminalList = terminals.list();

            for (CardTerminal terminal : terminalList) {
                System.out.println("name: " + terminal.getName());

                if (terminal.isCardPresent()) {
                    System.out.println("CardPresent");
                } else {
                    System.out.println("CardAbsent");
                }
            }


            CardTerminal cardTerminal = terminals.getTerminal("OMNIKEY CardMan 5x21 0");
            System.out.println("Card: " + cardTerminal.getName());


            //
            cardTerminal.isCardPresent();

            Card  card = cardTerminal.connect("*");

            ATR atr = card.getATR();
            System.out.println("ATR: " + HexUtil.byteArr2HexStr(atr.getBytes()));

            CardChannel channel = card.getBasicChannel();

            byte[] apDuByte = {(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x2f, (byte) 0x00};


            ResponseAPDU response = channel.transmit(new CommandAPDU(apDuByte));
            System.out.println("data1: " + HexUtil.byteArr2HexStr(response.getBytes()));

            byte[] apDuCommand = {(byte) 0x00, (byte) 0xb2, (byte) 0x00, (byte) 0x02, (byte) 0x00};
            response = channel.transmit(new CommandAPDU(apDuCommand));

            System.out.println("data: " + HexUtil.byteArr2HexStr(response.getBytes()));


            String flag = bytesToHexString(response.getBytes());
            System.out.println("flag: " + flag);

            if (flag.endsWith("9000")) {
                String cardTypeName = flag.substring(flag.length() - 22, flag.length() - 6);
                String shorterName = flag.substring(flag.length() - 28, flag.length() - 22);
                cardTypeName = convertHexToString(cardTypeName);
                shorterName = convertHexToString(shorterName);
                System.out.println("cardTypeName: " + cardTypeName);
                System.out.println("shorterName: " + shorterName);
            }

            int sw = response.getSW();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }
}
