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

public class GetUserDataTool implements BiFunction<UserInfo, ToolContext, String> {

    private static List<UserInfo> USER_INFO_LIST = new ArrayList<>();

    static {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("张三");
        userInfo.setEmail("zhangsan@agent.cn");
        userInfo.setDepartmentName("研发部");
        USER_INFO_LIST.add(userInfo);
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUserName("李四");
        userInfo1.setEmail("lisi@agent.cn");
        userInfo1.setDepartmentName("研发部");
        USER_INFO_LIST.add(userInfo1);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserName("王五");
        userInfo2.setEmail("wangwu@agent.cn");
        userInfo2.setDepartmentName("设计团队");

        USER_INFO_LIST.add(userInfo2);

    }

    @Override
    public String apply(UserInfo userInfo, ToolContext toolContext) {
        String userName = userInfo.getUserName();

        if (StringUtils.hasLength(userName)) {
            List<UserInfo> userInfos = USER_INFO_LIST.stream().filter(f -> f.getUserName().equals(userName)).toList();
            if (userInfos != null && !userInfos.isEmpty()) {
                String str = JSON.toJSONString(userInfos);
                return String.format("Available user list for %s", str);
            }
        }
        String department = userInfo.getDepartmentName();

        if (StringUtils.hasLength(department)) {
            List<UserInfo> userInfos = USER_INFO_LIST.stream().filter(f -> f.getDepartmentName().equals(department)).toList();
            if (userInfos != null && !userInfos.isEmpty()) {
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
