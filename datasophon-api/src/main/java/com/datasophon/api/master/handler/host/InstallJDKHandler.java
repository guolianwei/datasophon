package com.datasophon.api.master.handler.host;

import com.datasophon.api.utils.MinaUtils;
import com.datasophon.common.Constants;
import com.datasophon.common.model.HostInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallJDKHandler implements DispatcherWorkerHandler {

    private static final Logger logger = LoggerFactory.getLogger(InstallJDKHandler.class);

    @Override
    public boolean handle(ClientSession session, HostInfo hostInfo) {
        hostInfo.setProgress(60);
        String arch = MinaUtils.execCmdWithResult(session, "arch");
        String testResult = MinaUtils.execCmdWithResult(session, "test -d /usr/local/jdk1.8.0_333");
        boolean exists = true;
        if (StringUtils.isNotBlank(testResult) && "failed".equals(testResult)) {
            exists = false;
        }
        String jdkTarPath = fetchJdkTar(arch);
        if (!exists) {
            hostInfo.setMessage("开始安装jdk");
            MinaUtils.uploadFile(session, "/usr/local", jdkTarPath);
            MinaUtils.execCmdWithResult(session, "tar -zxvf /usr/local/jdk-8u333-linux-x64.tar.gz -C /usr/local/");
        }
        return true;
    }

    private static String fetchJdkTar(String arch) {
        String jdkTarFromSysPro = System.getProperty(Constants.SYSPRO_NAME_JDK_TAR_FILEPATH);
        if (jdkTarFromSysPro != null && !jdkTarFromSysPro.trim().equals("")) {
            return jdkTarFromSysPro;
        }
        String jdkTarPath = Constants.MASTER_MANAGE_PACKAGE_PATH + Constants.SLASH + Constants.X86JDK;
        if ("aarch64".equals(arch)) {
            jdkTarPath = Constants.MASTER_MANAGE_PACKAGE_PATH + Constants.SLASH + Constants.ARMJDK;
        }
        return jdkTarPath;
    }
}
