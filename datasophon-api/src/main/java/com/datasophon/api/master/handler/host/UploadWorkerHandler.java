package com.datasophon.api.master.handler.host;

import com.datasophon.api.utils.CommonUtils;
import com.datasophon.api.utils.MinaUtils;
import com.datasophon.common.Constants;
import com.datasophon.common.enums.InstallState;
import com.datasophon.common.model.HostInfo;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.datasophon.common.utils.PathUtils.fetchMasterManagePackagePath;

public class UploadWorkerHandler implements DispatcherWorkerHandler {
    private static final Logger logger = LoggerFactory.getLogger(StartWorkerHandler.class);


    @Override
    public boolean handle(ClientSession session, HostInfo hostInfo) {
        String workerTarFilePath = fetchWorkTar();
        boolean uploadFile = MinaUtils.uploadFile(session, Constants.INSTALL_PATH,
                workerTarFilePath);
        if (uploadFile) {
            hostInfo.setMessage("分发成功，开始校验md5");
            hostInfo.setProgress(25);
        } else {
            hostInfo.setMessage("分发主机管理agent安装包失败");
            hostInfo.setErrMsg("dispatcher host agent to " + hostInfo.getHostname() + " failed");
            CommonUtils.updateInstallState(InstallState.FAILED, hostInfo);
        }
        return uploadFile;
    }

    private static String fetchWorkTar() {
        String workerTarFilePathFromSystemD = System.getProperty(Constants.SYSPRO_NAME_WORKER_TAR_FILEPATH);
        if (workerTarFilePathFromSystemD != null && !workerTarFilePathFromSystemD.trim().equals("")) {
            return workerTarFilePathFromSystemD;
        }
        String workerTarFilePath =
                fetchMasterManagePackagePath() +
                        File.separator +
                        Constants.WORKER_PACKAGE_NAME;
        return workerTarFilePath;
    }
}
