package com.cloud.alibaba.ai.example.agent.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiFunction;

public class DateTimeTools implements BiFunction<Map<String, Object>, ToolContext, String> {
    @Override
    public String apply(Map<String, Object> map, ToolContext toolContext) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public ToolCallback toolCallback() {
       return FunctionToolCallback.builder("getCurrentDateTime", this)
                .description("get_current_date_time")
                .inputType(Map.class)
                .build();
    }
}
