package com.cloud.alibaba.ai.example.agent.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.alibaba.fastjson.JSON;
import com.cloud.alibaba.ai.example.agent.HITLHelper;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class PersonalAssistantController {

    private static final Map<String, OverAllState> ALL_STATE_MAP = new ConcurrentHashMap<String, OverAllState>();
    private static final Map<String, List<InterruptionMetadata.ToolFeedback>> TOOL_FEEDBACK_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ReactAgent reactAgent;


    @GetMapping(value = "/react/agent/supervisorAgent", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux supervisorAgentTest(String query, String threadId, String nodeId) throws GraphRunnerException {
        RunnableConfig config;
        if (nodeId != null && ALL_STATE_MAP.containsKey(nodeId)) {
            System.out.println("人工介入开始...");
            // 人工介入利用检查点机制。
            // 你必须提供线程ID以将执行与会话线程关联，
            // 以便可以暂停和恢复对话（人工审查所需）。
            OverAllState overAllState = ALL_STATE_MAP.get(nodeId);
            InterruptionMetadata metadata = InterruptionMetadata.builder(nodeId, overAllState).toolFeedbacks(TOOL_FEEDBACK_MAP.get(nodeId)).build();
            InterruptionMetadata approvalMetadata = HITLHelper.approveAll(metadata);
            // 使用批准决策恢复执行
            config = RunnableConfig.builder()
                    .threadId(threadId) // 相同的线程ID
                    .addHumanFeedback(approvalMetadata)
                    .build();
            ALL_STATE_MAP.remove(nodeId);
            TOOL_FEEDBACK_MAP.remove(nodeId);
            return resumeStreamingWithHack(config);
        } else {

            //String threadId = "user-session-123";
            config = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();
        }
        return reactAgent.stream(query, config)
                .doOnNext(this::println);

    }

    public Flux resumeStreamingWithHack(RunnableConfig config) {

        return Mono.fromCallable(() -> {
                    // 使用 invokeAndGetOutput 恢复（同步阻塞）
                    Optional<NodeOutput> result = reactAgent.invokeAndGetOutput("", config);
                    if (result.isPresent()) {
                        return extractMessageFromState(result.get());
                    }
                    return "";
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .concatMap(Flux::just);
    }

    private NodeOutput extractMessageFromState(NodeOutput nodeOutput) {
        OverAllState state = nodeOutput.state();
        List<Message> messages = (List<Message>) state.data().get("messages");
        // 解析 messages 列表获取最后一条 AI 消息...
        Message lastMessage = messages.get(messages.size() - 1);
        System.out.println(lastMessage);
        return nodeOutput;
    }

    private void println(NodeOutput nodeOutput) {
        if (nodeOutput instanceof StreamingOutput streamingOutput) {
            String node = streamingOutput.node();
            Message message = streamingOutput.message();
            if (message == null) {
                return;
            }
            if ("_AGENT_MODEL_".equals(node)) {
                System.out.print(message.getText());
            }
            if ("_AGENT_TOOL_".equals(node)) {
                ToolResponseMessage responseMessage = (ToolResponseMessage) message;
                List<ToolResponseMessage.ToolResponse> responses = responseMessage.getResponses();
                System.out.println("_AGENT_TOOL_");
                for (ToolResponseMessage.ToolResponse respons : responses) {
                    String string = respons.responseData();
                    System.out.println("id: " + respons.id());
                    System.out.println("name: " + respons.name());
                    System.out.println("responseData: " + string);

                }
            }
        } else if (nodeOutput instanceof InterruptionMetadata interruptionMetadata) {
            System.out.println("检测到中断，需要人工审批");
            List<InterruptionMetadata.ToolFeedback> toolFeedbacks =
                    interruptionMetadata.toolFeedbacks();

            for (InterruptionMetadata.ToolFeedback feedback : toolFeedbacks) {
                System.out.println("工具: " + feedback.getName());
                System.out.println("参数: " + feedback.getArguments());
                System.out.println("描述: " + feedback.getDescription());
            }
            String node = interruptionMetadata.node();
            OverAllState state = interruptionMetadata.state();
            System.out.println("参数 node: " + node);
            ALL_STATE_MAP.put(node, state);
            TOOL_FEEDBACK_MAP.put(node, toolFeedbacks);
        } else {
            System.out.println("其它:" + JSON.toJSONString(nodeOutput));
        }
    }
}
