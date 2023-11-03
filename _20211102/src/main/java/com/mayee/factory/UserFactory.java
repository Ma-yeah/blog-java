package com.mayee.factory;

import com.mayee.function.Lazy;
import com.mayee.model.User;
import com.mayee.service.DepartmentService;
import com.mayee.service.PermissionsService;
import com.mayee.service.SupervisorService;

import java.util.Set;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-02 17:05
 **/
public class UserFactory {

    // 假设注入
    private DepartmentService departmentService = new DepartmentService();
    private SupervisorService supervisorService = new SupervisorService();
    private PermissionsService permissionsService = new PermissionsService();

    public User build(Long uid) {
        Lazy<String> departmentLazy = Lazy.of(() -> departmentService.getDepartment(uid));
        Lazy<Long> supervisorLazy = departmentLazy.map(supervisorService::getSupervisor);
        Lazy<Set<String>> permissions = departmentLazy.flatMap(department -> supervisorLazy.map(supervisor -> permissionsService.getPermissions(department, supervisor)));

        User user = new User();
        user.setUid(uid);
        user.setDepartment(departmentLazy);
        user.setSupervisor(supervisorLazy);
        user.setPermission(permissions);

        return user;
    }
}
