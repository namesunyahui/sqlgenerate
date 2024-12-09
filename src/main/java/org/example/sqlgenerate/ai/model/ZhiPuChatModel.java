package org.example.sqlgenerate.ai.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智普用的对话模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ZhiPuChatModel extends ChatModel{

    /**
     * 由用户端传递，需要唯一；用于区分每次请求的唯一标识符。如果用户端未提供，平台将默认生成。
     */
    private String requestId;

    /**
     * 是否是异步 true 是 false 否
     */
    private Boolean isAsync = false;

}
