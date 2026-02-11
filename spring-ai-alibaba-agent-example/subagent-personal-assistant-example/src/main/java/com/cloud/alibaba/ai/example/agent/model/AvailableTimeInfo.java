package com.cloud.alibaba.ai.example.agent.model;

import java.util.List;

public class AvailableTimeInfo {
    /**
     * 日期信息，表示可用时间的具体日期
     */
    private String date;
    
    /**
     * 持续时间（分钟），表示该时间段的长度
     */
    private Integer durationMinutes;
    
    /**
     * 参与者列表，包含所有参与此时间段的人员信息
     */
    private List<String> attendees;

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}

