package com.mayee.model;

import com.mayee.function.Lazy;

import java.util.Set;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-11-02 11:44
 **/
public class User {
    // 用户 id
    private Long uid;

    // 用户的部门，为了保持示例简单，这里就用普通的字符串
    // 需要远程调用 通讯录系统 获得
    private Lazy<String> department;
    // 用户的主管，为了保持示例简单，这里就用一个 id 表示
    // 需要远程调用 通讯录系统 获得
    private Lazy<Long> supervisor;
    // 用户所持有的权限
    // 需要远程调用 权限系统 获得
    private Lazy<Set<String>> permission;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDepartment() {
        return department.get();
    }

    /**
     * 因为 department 是一个惰性加载的属性，所以 set 方法必须传入计算函数，而不是具体值
     */
    public void setDepartment(Lazy<String> department) {
        this.department = department;
    }

    public Long getSupervisor() {
        return supervisor.get();
    }

    public void setSupervisor(Lazy<Long> supervisor) {
        this.supervisor = supervisor;
    }

    public Set<String> getPermission() {
        return permission.get();
    }

    public void setPermission(Lazy<Set<String>> permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", department=" + department.get() +
                ", supervisor=" + supervisor.get() +
                ", permission=" + permission.get() +
                '}';
    }
}
