package com.paiagent.engine.llm.provider;

import lombok.Data;

/**
 * LLM Provider 每次调用的配置 DTO
 *
 * 封装所有与具体 Provider 无关的通用配置参数
 */
@Data
public class ProviderConfig {

    /**
     * API 端点 URL
     */
    private String apiUrl;

    /**
     * API 密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 温度参数 (0.0 - 2.0)
     */
    private Double temperature;

    /**
     * 是否启用流式输出
     */
    private boolean streaming;

    /**
     * 最大输出 Token 数
     */
    private Integer maxTokens;

    /**
     * 核采样参数 topP (0.0 - 1.0)
     */
    private Double topP;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    public ProviderConfig() {
    }

    public ProviderConfig(String apiUrl, String apiKey, String model, Double temperature) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
    }
}
