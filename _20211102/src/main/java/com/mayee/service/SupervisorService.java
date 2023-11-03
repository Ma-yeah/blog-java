package com.mayee.service;

import java.util.logging.Logger;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-03 14:39
 **/
public class SupervisorService implements IService {

    private final static Logger log = Logger.getLogger(SupervisorService.class.getName());

    public Long getSupervisor(String department) {
        log.info("rpc 调用 supervisor 服务...");
        remote();
        return 1L;
    }
}
