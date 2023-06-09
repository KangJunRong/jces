package com.ecp.jces.jctool.detection.model;

import lombok.Data;

@Data
public class PackageInfo {

    private String aid;
    private String name;

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PackageInfo) {
            PackageInfo pkg = (PackageInfo)obj;
            if (this.aid != null && this.aid.toLowerCase().equals(pkg.getAid().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
