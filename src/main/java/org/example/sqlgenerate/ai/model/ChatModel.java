package org.example.sqlgenerate.ai.model;

import lombok.Data;

/**
 * 聊天模型
 */
@Data
public class ChatModel {


    /**
     * 大模型预设角色
     */
    private String getChatModelRole;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 大模型
     */
    private String chatModel;

    /**
     * 密钥
     */
    private String aptKey;

    /**
     * 提问消息记录
     */
    private String message;

    /**
     * 用户
     *
     */
    private String userName;

    /**
     * 是否启用流式返回
     */
    private Boolean isStream  = false;


}
