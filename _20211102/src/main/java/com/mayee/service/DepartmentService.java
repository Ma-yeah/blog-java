package com.mayee.service;

import java.util.logging.Logger;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-03 14:39
 **/
public class DepartmentService implements IService {

    private final static Logger log = Logger.getLogger(DepartmentService.class.getName());

    public String getDepartment(Long uid) {
        log.info("rpc 调用 department 服务...");
        remote();
        return "云计算部门";
    }
}
