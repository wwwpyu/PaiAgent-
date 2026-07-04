package com.paiagent.engine.llm.provider;

import com.paiagent.engine.llm.conversation.ConversationMessage;
import com.paiagent.engine.llm.tool.ToolDefinition;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LLM 提供商统一接口
 *
 * 定义所有 LLM 提供商必须实现的核心能力：
 * - ChatModel 和 ChatClient 的创建
 * - 函数/工具调用 (Function Calling)
 * - 多轮对话上下文构建
 */
public interface LLMProvider {

    /**
     * 获取提供商标识符（与节点类型对应，如 "openai", "deepseek"）
     *
     * @return 提供商 ID
     */
    String getProviderId();

    /**
     * 创建该提供商的 ChatModel 实例
     *
     * @param config 提供商配置
     * @return ChatModel 实例
     */
    ChatModel createChatModel(ProviderConfig config);

    /**
     * 获取该提供商的 ChatClient 实例
     * 默认基于 createChatModel 构建
     *
     * @param config 提供商配置
     * @return ChatClient 实例
     */
    default ChatClient getChatClient(ProviderConfig config) {
        return ChatClient.builder(createChatModel(config)).build();
    }

    /**
     * 检查该提供商是否支持函数调用 (Function Calling)
     *
     * @return true 表示支持
     */
    default boolean supportsFunctionCalling() {
        return false;
    }

    /**
     * 注册工具/函数定义到 ChatClient
     *
     * @param chatClient 原始 ChatClient
     * @param tools 工具定义列表
     * @return 增强后的 ChatClient
     */
    default ChatClient registerFunctions(ChatClient chatClient, List<ToolDefinition> tools) {
        if (!supportsFunctionCalling()) {
            throw new UnsupportedOperationException(
                "Provider [" + getProviderId() + "] does not support function calling");
        }
        return chatClient;
    }

    /**
     * 构建多轮对话提示
     *
     * @param chatClient  ChatClient 实例
     * @param history     历史消息列表
     * @param systemPrompt 系统提示词（可为 null）
     * @return ChatClient 请求规格
     */
    default ChatClient.ChatClientRequestSpec buildConversation(
            ChatClient chatClient,
            List<ConversationMessage> history,
            String systemPrompt) {

        List<Message> messages = new ArrayList<>();

        // 添加系统提示词
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(new SystemMessage(systemPrompt));
        }

        // 添加对话历史
        for (ConversationMessage msg : history != null ? history : Collections.<ConversationMessage>emptyList()) {
            switch (msg.getRole()) {
                case USER -> messages.add(new UserMessage(msg.getContent()));
                case ASSISTANT -> messages.add(new AssistantMessage(msg.getContent()));
                case SYSTEM -> messages.add(new SystemMessage(msg.getContent()));
                // TOOL messages are handled contextually via the ChatClient tool callback mechanism
                default -> { /* TOOL messages handled by Spring AI internally */ }
            }
        }

        return chatClient.prompt().messages(messages);
    }
}
