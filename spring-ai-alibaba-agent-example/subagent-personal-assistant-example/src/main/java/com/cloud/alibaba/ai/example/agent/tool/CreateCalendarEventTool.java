package com.cloud.alibaba.ai.example.agent.tool;

import com.cloud.alibaba.ai.example.agent.model.CalendarInfo;
import com.cloud.alibaba.ai.example.agent.model.UserInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.function.BiFunction;

public class CreateCalendarEventTool implements BiFunction<CalendarInfo, ToolContext, String> {
    @Override
    public String apply(CalendarInfo calendarInfo, ToolContext toolContext) {
        // 参数解析
        String title = calendarInfo.getTitle();
        String startTime =calendarInfo.getStartTime();
        String endTime = calendarInfo.getEndTime();
        @SuppressWarnings("unchecked")
        List<String> attendees =calendarInfo.getAttendees();
        // 验证时间格式（简化版）
        if (!isValidIsoDateTime(startTime) || !isValidIsoDateTime(endTime)) {
            return "Error: Invalid ISO datetime format";
        }
        // 模拟创建事件
        return String.format("Event created: %s from %s to %s with %d attendees",
                title, startTime, endTime, attendees.size());
    }


    private boolean isValidIsoDateTime(String datetime) {
        // 简单验证 ISO 8601 格式（实际应使用更严格的验证）
        return datetime != null && datetime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("create_calendar_event", this)
                .description("create_calendar_event")
                .inputType(CalendarInfo.class)
                .build();

    }
}
