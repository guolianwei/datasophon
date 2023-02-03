package com.datasophon.api.master.handler.host;

import com.datasophon.api.configuration.ConfigBean;
import com.datasophon.api.utils.CommonUtils;
import com.datasophon.api.utils.MinaUtils;
import com.datasophon.api.utils.SpringTool;
import com.datasophon.common.Constants;
import com.datasophon.common.enums.InstallState;
import com.datasophon.common.model.HostInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static com.datasophon.common.utils.HostUtils.fetchHostName;

public class StartWorkerHandler implements DispatcherWorkerHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartWorkerHandler.class);

    private Integer clusterId;

    private String clusterFrame;

    public StartWorkerHandler(Integer clusterId, String clusterFrame) {
        this.clusterId = clusterId;
        this.clusterFrame = clusterFrame;
    }

    @Override
    public boolean handle(ClientSession session, HostInfo hostInfo) throws UnknownHostException {
        ConfigBean configBean = SpringTool.getApplicationContext().getBean(ConfigBean.class);
        String installPath = Constants.INSTALL_PATH;
        String localHostName = fetchHostName();
        String updateCommonPropertiesResult = MinaUtils.execCmdWithResult(session,
                Constants.UPDATE_COMMON_CMD +
                        localHostName +
                        Constants.SPACE +
                        configBean.getServerPort() +
                        Constants.SPACE +
                        this.clusterFrame +
                        Constants.SPACE +
                        this.clusterId);
        String agentHostName = hostInfo.getHostname();
        if (StringUtils.isBlank(updateCommonPropertiesResult) || "failed".equals(updateCommonPropertiesResult)) {
            logger.error("common.properties update failed");
            hostInfo.setErrMsg("common.properties update failed");
            hostInfo.setMessage("配置文件修改失败");
            CommonUtils.updateInstallState(InstallState.FAILED, hostInfo);
        } else {
            setHostName(session, agentHostName);
            disableFirewalld(session, agentHostName);
            //设置开机自动启动
            MinaUtils.execCmdWithResult(session, "\\cp " + installPath + "/datasophon-worker/script/datasophon-worker /etc/rc.d/init.d/");
            MinaUtils.execCmdWithResult(session, "chmod +x /etc/rc.d/init.d/datasophon-worker");
            MinaUtils.execCmdWithResult(session, "chkconfig --add datasophon-worker");
            MinaUtils.execCmdWithResult(session, "\\cp " + installPath + "/datasophon-worker/script/profile /etc/");
            MinaUtils.execCmdWithResult(session, "source /etc/profile");
            hostInfo.setMessage("启动主机管理agent");
            MinaUtils.execCmdWithResult(session, "service datasophon-worker restart");
            hostInfo.setProgress(75);
            hostInfo.setCreateTime(new Date());
        }

        logger.info("end dispatcher host agent :{}", agentHostName);
        return true;
    }

    private static void setHostName(ClientSession session, String agentHostName) {
        String hostNameSetterScript = String.format("hostnamectl set-hostname %s", agentHostName);
        logger.info("Set hostName agent :{},script:{}", agentHostName, hostNameSetterScript);
        MinaUtils.execCmdWithResult(session, hostNameSetterScript);
    }

    private static void disableFirewalld(ClientSession session, String agentHostName) {
        String disableFirewalld = "systemctl disable firewalld";
        logger.info("Disable Firewalld agent :{},script:{}", agentHostName, disableFirewalld);
        MinaUtils.execCmdWithResult(session, disableFirewalld);
        String stopFirewalld = "systemctl stop firewalld";
        logger.info("Stop Firewalld agent :{},script:{}", agentHostName, stopFirewalld);
        MinaUtils.execCmdWithResult(session, stopFirewalld);
    }
}
