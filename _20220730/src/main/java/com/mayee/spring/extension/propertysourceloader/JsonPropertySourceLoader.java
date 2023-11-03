package com.mayee.spring.extension.propertysourceloader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 在SpringBoot环境下，外部化的配置文件支持properties和yaml两种格式。但是，现在不想使用properties和yaml格式的文件.
 对于PropertySourceLoader的实现，SpringBoot两个实现:
 PropertiesPropertySourceLoader：可以解析properties或者xml结尾的配置文件
 YamlPropertySourceLoader：解析以yml或者yaml结尾的配置文件

 SpringBoot对于配置文件的处理，就是依靠SPI机制，
 SpringBoot会先通过SPI机制加载所有PropertySourceLoader，然后遍历每个PropertySourceLoader，判断当前遍历的PropertySourceLoader，通过getFileExtensions获取到当前PropertySourceLoader能够支持哪些配置文件格式的解析，让后跟当前需要解析的文件格式进行匹配，如果能匹配上，那么就会使用当前遍历的PropertySourceLoader来解析配置文件。
 PropertySourceLoader其实就属于策略接口，配置文件的解析就是策略模式的运用
*/
public class JsonPropertySourceLoader implements PropertySourceLoader {
    @Override
    public String[] getFileExtensions() {
        //这个方法表明这个类支持解析以json结尾的配置文件
        return new String[]{"json"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {

        ReadableByteChannel readableByteChannel = resource.readableChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate((int) resource.contentLength());

        //将文件内容读到 ByteBuffer 中
        readableByteChannel.read(byteBuffer);
        //将读出来的字节转换成字符串
        String content = new String(byteBuffer.array());
        // 将字符串转换成 JSONObject
        JSONObject jsonObject = JSON.parseObject(content);

        Map<String, Object> map = new HashMap<>(jsonObject.size());
        //将 json 的键值对读出来，放入到 map 中
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getString(key));
        }

        return Collections.singletonList(new MapPropertySource("jsonPropertySource", map));
    }
}
