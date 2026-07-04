package com.paiagent.engine.llm.provider.impl;

import com.paiagent.engine.llm.provider.AbstractOpenAICompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * AI Ping Provider
 *
 * 通过 AI Ping 的 OpenAI 兼容接口访问模型
 * 支持流式输出；Function Calling 取决于具体 API 版本
 *
 * 此前在硬编码的 ChatClientFactory 中不支持，现已通过扩展此 Provider 得到完整支持
 */
@Component
public class AIPingProvider extends AbstractOpenAICompatibleProvider {

    @Override
    public String getProviderId() {
        return "ai_ping";
    }

    @Override
    protected String getDefaultApiUrl() {
        return "https://api.ai-ping.com/v1";
    }
}
