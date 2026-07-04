package com.paiagent.engine.llm.provider.impl;

import com.paiagent.engine.llm.provider.AbstractOpenAICompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * DeepSeek Provider
 *
 * DeepSeek 提供 OpenAI 兼容 API，支持 Function Calling 和流式输出
 */
@Component
public class DeepSeekProvider extends AbstractOpenAICompatibleProvider {

    @Override
    public String getProviderId() {
        return "deepseek";
    }

    @Override
    protected String getDefaultApiUrl() {
        return "https://api.deepseek.com";
    }
}
