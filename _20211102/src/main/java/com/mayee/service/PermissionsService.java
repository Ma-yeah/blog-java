package com.mayee.service;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-03 14:39
 **/
public class PermissionsService implements IService {

    private final static Logger log = Logger.getLogger(PermissionsService.class.getName());

    public Set<String> getPermissions(String department, Long supervisor) {
        log.info("rpc 调用 permissions 服务...");
        remote();
        return Collections.singleton("管理员");
    }
}
