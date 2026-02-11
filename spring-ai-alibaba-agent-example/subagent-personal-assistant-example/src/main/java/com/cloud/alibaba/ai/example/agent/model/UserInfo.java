package com.cloud.alibaba.ai.example.agent.model;

public class UserInfo {
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 部门名称
     */
    private String departmentName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
