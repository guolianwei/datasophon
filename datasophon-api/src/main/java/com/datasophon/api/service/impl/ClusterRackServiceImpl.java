package com.datasophon.api.service.impl;

import com.datasophon.api.service.ClusterHostService;
import com.datasophon.common.Constants;
import com.datasophon.common.utils.Result;
import com.datasophon.dao.entity.ClusterHostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.datasophon.dao.mapper.ClusterRackMapper;
import com.datasophon.dao.entity.ClusterRack;
import com.datasophon.api.service.ClusterRackService;


@Service("clusterRackService")
public class ClusterRackServiceImpl extends ServiceImpl<ClusterRackMapper, ClusterRack> implements ClusterRackService {

    @Autowired
    private ClusterHostService hostService;

    @Override
    public List<ClusterRack> queryClusterRack(Integer clusterId) {
        return this.list(new QueryWrapper<ClusterRack>().eq(Constants.CLUSTER_ID,clusterId));
    }

    @Override
    public void saveRack(Integer clusterId, String rack) {
        ClusterRack clusterRack = new ClusterRack();
        clusterRack.setRack(rack);
        clusterRack.setClusterId(clusterId);
        this.save(clusterRack);
    }

    @Override
    public Result deleteRack(Integer rackId) {
        ClusterRack clusterRack = this.getById(rackId);
        if(rackInUse(clusterRack)){
            return Result.error("rack is using");
        }
        this.removeById(rackId);
        return Result.success();
    }

    @Override
    public void createDefaultRack(Integer clusterId) {
        ClusterRack clusterRack = new ClusterRack();
        clusterRack.setRack("default");
        clusterRack.setClusterId(clusterId);
        this.save(clusterRack);
    }

    private boolean rackInUse(ClusterRack clusterRack) {
        List<ClusterHostEntity> list = hostService.getClusterHostByRack(clusterRack.getClusterId(),clusterRack.getRack());
        if(list.size() > 0){
            return true;
        }
        return false;
    }

}
