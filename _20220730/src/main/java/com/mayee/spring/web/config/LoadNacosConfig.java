package com.mayee.spring.web.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadNacosConfig implements EnvironmentPostProcessor {

    // 匹配属性是否设置了环境变量替换
    private final static Pattern PATTERN = Pattern.compile("^\\$\\{(.*)\\}$");

    // nacos 上的配置
    private static PropertySource<?> NACOS_CONFIG;

    // 先加载的优先级值小，后加载的优先级值大。值越大，优先级越高

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 先获取到属性源
        MutablePropertySources sources = environment.getPropertySources();
        // 遍历
        for (PropertySource<?> source : sources) {
            if (source.getName().contains("bootstrap.yaml")) {
                loadRemoteConfig(sources.get("systemEnvironment"), source);
                break;
            } else if (source.getName().contains("application.yaml")) {
                // 替换配置值
                sources.replace(source.getName(), refreshConfig(source));
                break;
            }
        }
    }

    // 加载远程配置
    private void loadRemoteConfig(PropertySource<?> environment, PropertySource<?> bootstrap) {
        // 没有 nacos 配置，无需加载
        if (!bootstrap.containsProperty("nacos.config.server-addr")){
            return;
        }
        try {
            String serverAddr = bootstrap.getProperty("nacos.config.server-addr").toString();
            String appName = bootstrap.getProperty("spring.application.name").toString();
            String suffix = bootstrap.getProperty("nacos.config.file-extension").toString();
            String dataId = realValue(environment, appName) + "." + realValue(environment, suffix);
            String group = "DEFAULT_GROUP";
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, realValue(environment, serverAddr));
            properties.put(PropertyKeyConst.USERNAME, "nacos");
            properties.put(PropertyKeyConst.PASSWORD, "nacos");
            properties.put(PropertyKeyConst.NAMESPACE, "public");
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, 5000);
//            Yaml yaml = new Yaml();
//            Properties load = yaml.loadAs(content, Properties.class);
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            NACOS_CONFIG = loader.load("nacosConfig", new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8))).get(0);
        } catch (NacosException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 刷新配置
    private PropertySource<?> refreshConfig(PropertySource<?> application) {
        // 说明没有 nacos 配置
        if (NACOS_CONFIG == null) {
            return application;
        }
        Map<String, Object> map = new HashMap<>();
        EnumerablePropertySource<?> enumerableSource = (EnumerablePropertySource<?>) application;
        String[] names = enumerableSource.getPropertyNames();
        for (String name : names) {
            String realValue = NACOS_CONFIG.containsProperty(name)?NACOS_CONFIG.getProperty(name).toString():application.getProperty(name).toString();
            map.put(name, realValue);
        }
        return new MapPropertySource(application.getName(), map);
    }

    // 真实值
    private String realValue(PropertySource<?> environment, String origin) {
        Matcher matcher = PATTERN.matcher(origin);
        // 真实值直接返回
        if (!matcher.matches()) {
            return origin;
        }
        String[] split = matcher.group(1).split(":", 2);
        // 占位符
        String placeholder = split[0];
        // 环境变量有值，则返回
        if (environment.containsProperty(placeholder)) {
            return environment.getProperty(placeholder).toString();
        }
        // 否则再看是否有默认值，有则返回
        if (split.length > 1) {
            return split[1];
        }
        // 说明既没有环境变量，也没有默认值
        throw new RuntimeException("environment not found: " + placeholder);
    }
}
