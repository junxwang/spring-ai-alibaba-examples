package com.cloud.alibaba.ai.example.agent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.AgentTool;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import com.alibaba.cloud.ai.graph.agent.hook.hip.ToolConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.cloud.alibaba.ai.example.agent.model.UserInfo;
import com.cloud.alibaba.ai.example.agent.tool.*;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class AgentConfig {


    private final static String CALENDAR_AGENT_PROMPT = """
            You are a calendar scheduling assistant.
            Parse natural language scheduling requests (e.g., 'next Tuesday at 2pm')
            into proper ISO datetime formats.
            Use get_available_time_slots to check availability when needed.
            Use create_calendar_event to schedule events.
            Always confirm what was scheduled in your final response.
            """;

    private final static String EMAIL_AGENT_PROMPT = """
               You are an email assistant.
               Compose professional emails based on natural language requests.
               Extract recipient information and craft appropriate subject lines and body text.
               Use send_email to send the message.
               Always confirm what was sent in your final response.
            """;

    private final static String SUPERVISOR_PROMPT = """
             You are a helpful personal assistant.
             You can schedule calendar events and send emails.
             Break down user requests into appropriate tool calls and coordinate the results.
             When a request involves multiple actions, use multiple tools in sequence.
            """;

    private final DashScopeChatModel dashScopeChatModel;

    public AgentConfig(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @Bean("reactAgent")
    public ReactAgent reactAgent() {
        // 配置检查点保存器（人工介入需要检查点来处理中断）
        MemorySaver memorySaver = new MemorySaver();
        ToolCallback calendarAgent = AgentTool.getFunctionToolCallback(calendarAgent());
        ToolCallback emailAgent = AgentTool.getFunctionToolCallback(emailAgent());
        FunctionToolCallback<UserInfo, String> getUser = FunctionToolCallback.builder("get_user_email_tool", new GetUserDataTool())
                .description("You can provide the functionality to retrieve a user's email address by their username, and to obtain all user names within a department by specifying the department name.")
                .inputType(UserInfo.class)
                .build();

        return ReactAgent.builder()
                .name("supervisor_agent")
                .model(dashScopeChatModel)
                .systemPrompt(SUPERVISOR_PROMPT)
                .hooks(createHumanInTheLoopHook())
                .tools(List.of(calendarAgent, emailAgent, getUser))
                //.tools(getUser)
                .saver(memorySaver)
                .build();
    }

    @Bean("emailAgent")
    public ReactAgent emailAgent() {

        String instruction =
                """
                         Send emails using natural language.
                                                
                        Use this when the user wants to send notifications, reminders, or any email
                        communication. Handles recipient extraction, subject generation, and email
                        composition.
                                                
                        Input: Natural language email request (e.g., 'send them a reminder about
                        the meeting')
                        """;
        // 创建 Agent
        return ReactAgent.builder()
                .name("emailAgent")
                .model(dashScopeChatModel)
                .tools(List.of(new SendEmailTool().toolCallback()))
                .systemPrompt(EMAIL_AGENT_PROMPT)
                .instruction(instruction)
                .inputType(String.class)
                .hooks(createHumanInTheLoopHook())
                .build();
    }

    @Bean("calendarAgent")
    public ReactAgent calendarAgent() {

        String instruction = """
                Schedule calendar events using natural language.
                                
                Use this when the user wants to create, modify, or check calendar appointments.
                Handles date/time parsing, availability checking, and event creation.
                                
                Input: Natural language scheduling request (e.g., 'meeting with design team
                next Tuesday at 2pm')
                """;

        // 创建 Agent
        return ReactAgent.builder()
                .name("calendarAgent")
                .model(dashScopeChatModel)
                .tools(List.of(new CreateCalendarEventTool().toolCallback(), new GetAvailableTimeSlotsTool().toolCallback(), new DateTimeTools().toolCallback()))
                .systemPrompt(CALENDAR_AGENT_PROMPT)
                .instruction(instruction)
                .inputType(String.class)
                .build();

    }

    private HumanInTheLoopHook createHumanInTheLoopHook() {
        // 创建人工介入Hook
        return HumanInTheLoopHook.builder()
                .approvalOn("calendarAgent", ToolConfig.builder()
                        .description("Calendar event pending approval")
                        .build())
                .approvalOn("emailAgent", ToolConfig.builder()
                        .description("Outbound email pending approval")
                        .build()).build();
    }
}
