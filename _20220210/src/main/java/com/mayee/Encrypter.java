package com.mayee;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.phprpc.util.PHPSerializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @program: xgc-shumei
 * @description: 重构 php 的 laravel 框架中 Encrypter 类的 encrypt 和 decrypt 方法。
 * 该类中默认的加/解密方式为 AES-128-CBC，可通过外部文件 app.php 配置其加密方式和秘钥，这里有外部配置 AES-256-CBC。
 * AES-256-CBC 对应秘钥字节长度为 32，偏移量直接长度为 16。
 * @author: Bobby.Ma
 * @create: 2022-02-10 12:16
 **/
@Slf4j
public class Encrypter {

    /**
     * 秘钥
     */
    private final String key = "YqrxNCQvKu5hnHu1qrEb1EzmSagWSzEM12aBASV+kG4=";

    /*
        php 在外部需要配置加密模式 AES-128-CBC 或 AES-256-CBC，并且只支持这两种模式。
        但 java 无需指定模式，会根据秘钥长度来自动判断。
     */


    /**
     * 加密给定的文本
     *
     * @param value 原文对象
     * @return 密文
     */
    public String encrypt(Object value) {
        byte[] iv = RandomUtil.randomBytes(16);
        byte[] serialize;
        PHPSerializer ps = new PHPSerializer();
        try {
            serialize = ps.serialize(value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Could not serialize the data: {}", e.getMessage());
            return null;
        }
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, Base64.decode(key), iv);
        String encrypt = aes.encryptBase64(serialize);
        String mac = this.hash(Base64.encode(iv), encrypt);
        JSONObject json = new JSONObject(true);
        json.put("iv", Base64.encode(iv));
        json.put("value", encrypt);
        json.put("mac", mac);
        return Base64.encode(json.toJSONString());
    }

    /**
     * 解密给定的文本
     *
     * @param payload 密文
     * @return 原文
     */
    public <T> T decrypt(String payload, Class<T> type) {
        JSONObject payloadObj = this.getJsonPayload(payload);
        byte[] iv = Base64.decode(payloadObj.getString("iv"));
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, Base64.decode(key), iv);
        byte[] decrypt = aes.decrypt(payloadObj.getString("value"));
        PHPSerializer ps = new PHPSerializer();
        try {
            return (T) ps.unserialize(decrypt, type);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Could not unserialize the data: {}", e.getMessage());
        }
        return null;
    }

    private JSONObject getJsonPayload(String payload) {
        JSONObject payloadObj = JSON.parseObject(Base64.decodeStr(payload));
        if (!this.validPayload(payloadObj)) {
            throw new RuntimeException("The payload is invalid.");
        }
        if (!this.validMac(payloadObj)) {
            throw new RuntimeException("The MAC is invalid.");
        }
        return payloadObj;
    }

    /**
     * 验证 payload 是否有效
     *
     * @param payloadObj
     * @return
     */
    private boolean validPayload(JSONObject payloadObj) {
        if (Objects.nonNull(payloadObj)) {
            return payloadObj.containsKey("iv") && payloadObj.containsKey("value") && payloadObj.containsKey("mac") &&
                    Base64.decode(payloadObj.getString("iv")).length == 16;
        }
        return false;
    }

    private boolean validMac(JSONObject payloadObj) {
        byte[] bytes = RandomUtil.randomBytes(16);
        String calculated = this.calculateMac(payloadObj, bytes);
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, bytes);
        return Objects.equals(mac.digestHex(payloadObj.getString("mac")), calculated);
    }

    private String calculateMac(JSONObject payloadObj, byte[] bytes) {
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, bytes);
        return mac.digestHex(this.hash(payloadObj.getString("iv"), payloadObj.getString("value")));
    }

    private String hash(String iv, String value) {
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, Base64.decode(key));
        return mac.digestHex(iv + value);
    }

    public static void main(String[] args) {
        User origin = new User();
        origin.setName("bobby");
        origin.setId(1);
        origin.setAge(26);
        Encrypter encrypter = new Encrypter();
        String encrypt = encrypter.encrypt(origin);
        System.out.println("密文：" + encrypt);
        User user = encrypter.decrypt(encrypt, User.class);
        System.out.println("原文：" + JSON.toJSONString(user));
    }
}
