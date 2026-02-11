package com.cloud.alibaba.ai.example.agent.model;

import java.util.List;


public class EmailInfo {
    /**
     * 收件人邮箱地址列表
     */
    private List<String> to;
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 邮件正文内容
     */
    private String body;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
