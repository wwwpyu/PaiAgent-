package com.paiagent.engine.llm.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 多轮对话消息 DTO
 *
 * 封装单条对话消息，支持 system/user/assistant/tool 四种角色
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {

    /**
     * 消息角色
     */
    public enum Role {
        /** 系统指令 */
        SYSTEM,
        /** 用户消息 */
        USER,
        /** 助手回复 */
        ASSISTANT,
        /** 工具调用结果 */
        TOOL
    }

    /**
     * 消息角色
     */
    private Role role;

    /**
     * 消息文本内容
     */
    private String content;

    /**
     * 工具调用 ID（当 role=TOOL 时，对应 assistant 消息中的 toolCallId）
     */
    private String toolCallId;

    /**
     * 附加元数据
     */
    private Map<String, Object> metadata;

    /**
     * 创建用户消息
     */
    public static ConversationMessage user(String content) {
        return new ConversationMessage(Role.USER, content, null, null);
    }

    /**
     * 创建助手消息
     */
    public static ConversationMessage assistant(String content) {
        return new ConversationMessage(Role.ASSISTANT, content, null, null);
    }

    /**
     * 创建系统消息
     */
    public static ConversationMessage system(String content) {
        return new ConversationMessage(Role.SYSTEM, content, null, null);
    }

    /**
     * 创建工具结果消息
     */
    public static ConversationMessage tool(String toolCallId, String content) {
        return new ConversationMessage(Role.TOOL, content, toolCallId, null);
    }
}
