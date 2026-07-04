package com.paiagent.engine.llm;

import com.paiagent.engine.llm.conversation.ConversationMessage;
import com.paiagent.engine.llm.tool.ToolDefinition;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * LLM节点配置DTO
 */
@Data
public class LLMNodeConfig {

    /**
     * API端点URL
     */
    private String apiUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 提示词模板
     */
    private String promptTemplate;

    /**
     * 输入参数配置
     */
    private List<Map<String, Object>> inputParams;

    /**
     * 输出参数配置
     */
    private List<Map<String, Object>> outputParams;

    /**
     * 是否启用流式输出
     */
    private boolean streaming;

    /**
     * 工具/函数定义列表（用于 Function Calling）
     */
    private List<ToolDefinition> tools;

    /**
     * 多轮对话历史
     */
    private List<ConversationMessage> conversationHistory;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 最大输出 Token 数
     */
    private Integer maxTokens;

    /**
     * 核采样参数 topP (0.0 - 1.0)
     */
    private Double topP;
}
