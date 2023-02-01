package com.datasophon.api.master.handler.host;

import cn.hutool.core.io.FileUtil;
import com.datasophon.api.utils.CommonUtils;
import com.datasophon.api.utils.MinaUtils;
import com.datasophon.common.Constants;
import com.datasophon.common.enums.InstallState;
import com.datasophon.common.model.HostInfo;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.StyledEditorKit;
import java.io.File;
import java.nio.charset.Charset;

import static com.datasophon.common.utils.PathUtils.fetchMasterManagePackagePath;

public class CheckWorkerMd5Handler implements DispatcherWorkerHandler {
    private static final Logger logger = LoggerFactory.getLogger(CheckWorkerMd5Handler.class);

    @Override
    public boolean handle(ClientSession session, HostInfo hostInfo) {
        String checkTarMd5Switch = System.getProperty(Constants.SYSPRO_NAME_CEHCK_TAR_MD5_SWITCH);
        if (checkTarMd5Switch != null && Boolean.valueOf(checkTarMd5Switch)) {
            if (chenckMd5(session, hostInfo)) {
                return false;
            }
        }
        return true;
    }

    private static boolean chenckMd5(ClientSession session, HostInfo hostInfo) {
        String checkWorkerMd5Result = MinaUtils.execCmdWithResult(session, Constants.CHECK_WORKER_MD5_CMD).trim();
        String md5 = FileUtil.readString(
                fetchMasterManagePackagePath() +
                        File.separator +
                        Constants.WORKER_PACKAGE_NAME + ".md5",
                Charset.defaultCharset()).trim();
        logger.info("{} worker package md5 value is : {}", hostInfo.getHostname(), md5);
        if (!md5.equals(checkWorkerMd5Result)) {
            logger.error("worker package md5 check failed");
            hostInfo.setErrMsg("worker package md5 check failed");
            hostInfo.setMessage("md5校验失败");
            CommonUtils.updateInstallState(InstallState.FAILED, hostInfo);
            return true;
        }
        hostInfo.setProgress(35);
        hostInfo.setMessage("md5校验成功，开始解压安装包");
        return false;
    }
}
