package com.datasophon.common.utils;

import com.datasophon.common.Constants;

public class PathUtils {
    public static String fetchMasterManagePackagePath() {
        String masterManagePackagePath = System.getProperty(Constants.SYSPRO_NAME_MASTER_MANAGE_PACKAGE_PATH);
        if (masterManagePackagePath != null && !masterManagePackagePath.trim().equals("")) {
            return masterManagePackagePath;
        }
        return Constants.MASTER_MANAGE_PACKAGE_PATH;
    }
}
