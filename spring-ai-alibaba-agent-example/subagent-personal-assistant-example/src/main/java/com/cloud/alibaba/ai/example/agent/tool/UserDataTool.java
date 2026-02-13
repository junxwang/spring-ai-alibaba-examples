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

package com.cloud.alibaba.ai.example.agent.tool;

import com.alibaba.fastjson.JSON;
import com.cloud.alibaba.ai.example.agent.model.UserInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A tool class that provides functionality to retrieve user information based on username or department.
 * It implements BiFunction interface to process UserInfo and ToolContext inputs and return relevant user data as a JSON string.
 * The class contains a predefined list of users and supports filtering by username or department name.
 *
 * @author wangjx
 * @since 2026-02-13
 */
public class UserDataTool implements BiFunction<UserInfo, ToolContext, String> {

    private static final List<UserInfo> USER_INFO_LIST = new ArrayList<>();

    static {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("zhangsan(张三)");
        userInfo.setEmail("zhangsan@agent.cn");
        userInfo.setDepartmentName("development(研发部)");
        USER_INFO_LIST.add(userInfo);
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUserName("lisi(李四)");
        userInfo1.setEmail("lisi@agent.cn");
        userInfo1.setDepartmentName("development(研发部)");
        USER_INFO_LIST.add(userInfo1);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserName("wangwu(王五)");
        userInfo2.setEmail("wangwu@agent.cn");
        userInfo2.setDepartmentName("design(设计团队)");

        USER_INFO_LIST.add(userInfo2);

    }

    @Override
    public String apply(UserInfo userInfo, ToolContext toolContext) {
        String userName = userInfo.getUserName();

        if (StringUtils.hasLength(userName)) {
            List<UserInfo> userInfos = USER_INFO_LIST.stream().filter(f -> f.getUserName().contains(userName)).toList();
            if (!userInfos.isEmpty()) {
                String str = JSON.toJSONString(userInfos);
                return String.format("Available user list for %s", str);
            }
        }
        String department = userInfo.getDepartmentName();

        if (StringUtils.hasLength(department)) {
            List<UserInfo> userInfos = USER_INFO_LIST.stream().filter(f -> f.getDepartmentName().contains(department)).toList();
            if (!userInfos.isEmpty()) {
                String str = JSON.toJSONString(userInfos);
                return String.format("Available user list for %s", str);
            }
        }
        return "";
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("get_user_email_tool", this)
                .description("You can provide the functionality to retrieve a user's email address by their username, and to obtain all user names within a department by specifying the department name.")
                .inputType(UserInfo.class)
                .build();
    }
}
