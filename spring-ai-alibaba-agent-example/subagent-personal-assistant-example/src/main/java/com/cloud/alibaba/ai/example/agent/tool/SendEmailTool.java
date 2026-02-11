package com.cloud.alibaba.ai.example.agent.tool;

import com.cloud.alibaba.ai.example.agent.model.EmailInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.function.BiFunction;

public class SendEmailTool implements BiFunction<EmailInfo, ToolContext, String> {
    @Override
    public String apply(EmailInfo args, ToolContext toolContext) {
        // 参数解析
        @SuppressWarnings("unchecked")
        List<String> to = (List<String>) args.getTo();
        String subject = (String) args.getSubject();
        String body = (String) args.getBody();
        // 验证邮箱格式（简化版）
        for (String email : to) {
            if (!isValidEmail(email)) {
                return "Error: Invalid email address: " + email;
            }
        }
        // 模拟发送邮件
        System.out.println("Sending email...");
        String format = String.format("Email sent to %s - Subject: %s", String.join(", ", to), subject);
        System.out.println( format);
        return format;
    }
    private boolean isValidEmail(String email) {
        // 简单验证邮箱格式（实际应使用更严格的验证）
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("send_email",this)
                .description("""
                        Send emails using natural language.
                                                
                            Use this when the user wants to send notifications, reminders, or any email
                            communication. Handles recipient extraction, subject generation, and email
                            composition.
                                                
                            Input: Natural language email request (e.g., 'send them a reminder about
                            the meeting
                        """)
                .inputType(EmailInfo.class)
                .build();

    }
}
