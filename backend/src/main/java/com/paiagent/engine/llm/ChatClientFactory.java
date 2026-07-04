package com.paiagent.engine.llm;

import com.paiagent.engine.llm.conversation.ConversationMessage;
import com.paiagent.engine.llm.provider.LLMProvider;
import com.paiagent.engine.llm.provider.LLMProviderFactory;
import com.paiagent.engine.llm.provider.ProviderConfig;
import com.paiagent.engine.llm.tool.ToolDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ChatClient 动态工厂
 *
 * 根据节点配置在运行时创建不同类型的 ChatClient 实例。
 * 通过 LLMProviderFactory 委托给对应的 LLMProvider 实现，
 * 支持自动发现和注册新的提供商。
 */
@Slf4j
@Component
public class ChatClientFactory {

    @Autowired
    private LLMProviderFactory providerFactory;

    /**
     * 根据节点类型和配置创建 ChatClient（基础版本，向后兼容）
     *
     * @param nodeType    节点类型 (openai/deepseek/qwen/zhipu/ai_ping)
     * @param apiUrl      API 端点 URL
     * @param apiKey      API 密钥
     * @param model       模型名称
     * @param temperature 温度参数
     * @return ChatClient 实例
     */
    public ChatClient createClient(String nodeType, String apiUrl, String apiKey,
                                   String model, Double temperature) {
        return createClient(nodeType, apiUrl, apiKey, model, temperature, null, null);
    }

    /**
     * 根据节点类型和配置创建 ChatClient（完整版本，支持 Function Calling 和对话历史）
     *
     * @param nodeType    节点类型 (openai/deepseek/qwen/zhipu/ai_ping)
     * @param apiUrl      API 端点 URL
     * @param apiKey      API 密钥
     * @param model       模型名称
     * @param temperature 温度参数
     * @param tools       工具定义列表（可为 null）
     * @param history     对话历史（可为 null）
     * @return ChatClient 实例
     */
    public ChatClient createClient(String nodeType, String apiUrl, String apiKey,
                                   String model, Double temperature,
                                   List<ToolDefinition> tools,
                                   List<ConversationMessage> history) {
        log.info("创建 ChatClient - 类型: {}, URL: {}, 模型: {}, 温度: {}, 工具数: {}",
                nodeType, apiUrl, model, temperature,
                tools != null ? tools.size() : 0);

        LLMProvider provider = providerFactory.getProvider(nodeType);

        ProviderConfig config = new ProviderConfig(apiUrl, apiKey, model, temperature);
        ChatClient chatClient = provider.getChatClient(config);

        // 注册工具函数
        if (tools != null && !tools.isEmpty() && provider.supportsFunctionCalling()) {
            chatClient = provider.registerFunctions(chatClient, tools);
            log.info("已为 {} 注册 {} 个工具函数", nodeType, tools.size());
        }

        return chatClient;
    }

    /**
     * 获取指定提供商是否支持函数调用
     *
     * @param nodeType 节点类型
     * @return true 表示支持 Function Calling
     */
    public boolean supportsFunctionCalling(String nodeType) {
        try {
            return providerFactory.getProvider(nodeType).supportsFunctionCalling();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
