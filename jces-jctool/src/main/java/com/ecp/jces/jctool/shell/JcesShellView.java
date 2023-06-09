package com.ecp.jces.jctool.shell;

import java.util.ArrayList;
import java.util.Iterator;


public class JcesShellView {

    private static JcesShellView jcsv;

    public static JcesShellView newInstance() {
        if (jcsv == null) {
            jcsv = new JcesShellView();
        }

        return jcsv;
    }

    public void append(final String out, final int style) {
        System.out.println(out);
    }

    public void appendLine(final String out, final int style) {
        System.out.println(out);
    }

    public void setPrint(boolean isPrint) {

    }

    public void cardEnable(boolean cmactive) {

    }
}
