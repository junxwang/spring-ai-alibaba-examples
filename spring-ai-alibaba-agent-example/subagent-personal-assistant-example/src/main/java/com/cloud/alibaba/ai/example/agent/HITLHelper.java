/*
 * Copyright 2026-2027 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloud.alibaba.ai.example.agent;

import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;

public class HITLHelper {
    /**
     * 批准所有工具调用
     */
    public static InterruptionMetadata approveAll(InterruptionMetadata interruptionMetadata) {
        InterruptionMetadata.Builder builder = InterruptionMetadata.builder()
                .nodeId(interruptionMetadata.node())
                .state(interruptionMetadata.state());

        interruptionMetadata.toolFeedbacks().forEach(toolFeedback -> {
            builder.addToolFeedback(
                    InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                            .result(InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED)
                            .description("Agree to tool execution.")
                            .build()
            );
        });

        return builder.build();
    }

    /**
     * 拒绝所有工具调用
     */
    public static InterruptionMetadata rejectAll(
            InterruptionMetadata interruptionMetadata,
            String reason) {
        InterruptionMetadata.Builder builder = InterruptionMetadata.builder()
                .nodeId(interruptionMetadata.node())
                .state(interruptionMetadata.state());

        interruptionMetadata.toolFeedbacks().forEach(toolFeedback -> {
            builder.addToolFeedback(
                    InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                            .result(InterruptionMetadata.ToolFeedback.FeedbackResult.REJECTED)
                            .description(reason)
                            .build()
            );
        });

        return builder.build();
    }

    /**
     * 编辑特定工具的参数
     */
    public static InterruptionMetadata editTool(
            InterruptionMetadata interruptionMetadata,
            String toolName,
            String newArguments) {
        InterruptionMetadata.Builder builder = InterruptionMetadata.builder()
                .nodeId(interruptionMetadata.node())
                .state(interruptionMetadata.state());

        interruptionMetadata.toolFeedbacks().forEach(toolFeedback -> {
            if (toolFeedback.getName().equals(toolName)) {
                builder.addToolFeedback(
                        InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                                .arguments(newArguments)
                                .result(InterruptionMetadata.ToolFeedback.FeedbackResult.EDITED)
                                .build()
                );
            } else {
                builder.addToolFeedback(
                        InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                                .result(InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED)
                                .build()
                );
            }
        });

        return builder.build();
    }

}
