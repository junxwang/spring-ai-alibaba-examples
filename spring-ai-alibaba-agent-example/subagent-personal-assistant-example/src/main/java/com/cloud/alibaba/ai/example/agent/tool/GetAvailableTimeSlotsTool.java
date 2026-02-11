package com.cloud.alibaba.ai.example.agent.tool;

import com.cloud.alibaba.ai.example.agent.model.AvailableTimeInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class GetAvailableTimeSlotsTool implements BiFunction<AvailableTimeInfo, ToolContext, String> {
    @Override
    public String apply(AvailableTimeInfo args, ToolContext toolContext) {
        // 参数解析
        @SuppressWarnings("unchecked")
        List<String> attendees = (List<String>) args.getAttendees();
        String date = (String) args.getDate();
        int durationMinutes = (Integer) args.getDurationMinutes();
        // 验证日期格式（简化版）
        if (!isValidIsoDate(date)) {
            return "Error: Invalid ISO date format";
        }
        // 模拟查询可用时间槽
        List<String> timeSlots = List.of("09:00", "14:00", "16:00");
        return String.format("Available time slots for %s: %s", date, String.join(", ", timeSlots));
    }

    private boolean isValidIsoDate(String date) {
        // 简单验证 ISO 8601 日期格式
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("get_available_time_slots", this)
                .description("get_available_time_slots")
                .inputType(AvailableTimeInfo.class)
                .build();
    }
}
