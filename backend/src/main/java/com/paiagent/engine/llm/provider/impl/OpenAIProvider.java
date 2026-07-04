package com.paiagent.engine.llm.provider.impl;

import com.paiagent.engine.llm.provider.AbstractOpenAICompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * OpenAI Provider
 *
 * 使用 OpenAI 原生 API，完整支持 Function Calling 和流式输出
 */
@Component
public class OpenAIProvider extends AbstractOpenAICompatibleProvider {

    @Override
    public String getProviderId() {
        return "openai";
    }

    @Override
    protected String getDefaultApiUrl() {
        return "https://api.openai.com";
    }
}
