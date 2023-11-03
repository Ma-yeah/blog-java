package com.mayee.service;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.mayee.model.NacosRouterProps;
import com.mayee.router.entity.RouteGroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

/**
 * @Description: nacos 操作
 * @Author: Bobby.Ma
 * @Date: 2021/11/14 23:54
 */
@Slf4j
@Component
public class NacosRouterService {

    @Autowired
    private NacosRouterProps nacosRouterProps;
    @Autowired
    private NacosConfigManager nacosConfigManager;
    private ConfigService configService;

    @PostConstruct
    public void configService() {
        // 初始化 configService
        this.configService = nacosConfigManager.getConfigService();
    }

    /**
     * @Description: 获取配置
     * @return: com.alibaba.fastjson.JSONObject
     * @Author: Bobby.Ma
     * @Date: 2021/11/14 23:57
     */
    public RouteGroupEntity getRouter() throws NacosException {
        String config = configService.getConfig(nacosRouterProps.getDataId(), nacosRouterProps.getGroup(), 500);
        return JSONObject.parseObject(config, RouteGroupEntity.class);
    }

    /**
     * @param config
     * @Description: 发布配置
     * @return: void
     * @Author: Bobby.Ma
     * @Date: 2021/11/15 0:01
     */
    public void pushRouter(String config) throws NacosException {
        JSONObject object = JSON.parseObject(config);
        configService.publishConfig(nacosRouterProps.getDataId(), nacosRouterProps.getGroup(),
                object.toString(SerializerFeature.PrettyFormat), ConfigType.JSON.getType());
    }

    /**
     * @Description: 监听 nacos server 上的改动
     * @return: void
     * @Author: Bobby.Ma
     * @Date: 2021/11/15 0:03
     */
    public void refreshRouter(Consumer<RouteGroupEntity> assembleRouteDefinition) throws NacosException {
        // 工程启动时，从nacos 中读取配置
        RouteGroupEntity router = this.getRouter();
        assembleRouteDefinition.accept(router);

        Listener listener = new AbstractListener() {
            @Override
            public void receiveConfigInfo(String config) {
                log.info("listener router, data-id: {}, group: {}", nacosRouterProps.getDataId(), nacosRouterProps.getGroup());
                RouteGroupEntity routeGroup = JSON.parseObject(config, RouteGroupEntity.class);
                assembleRouteDefinition.accept(routeGroup);
            }
        };
        //设置监听 nacos 变动
        configService.addListener(nacosRouterProps.getDataId(), nacosRouterProps.getGroup(), listener);
    }
}
