package com.paiagent.engine.llm.provider.impl;

import com.paiagent.engine.llm.provider.AbstractOpenAICompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * 通义千问 (Qwen) Provider
 *
 * 通过阿里云 DashScope 的 OpenAI 兼容接口访问通义千问模型
 * 支持 Function Calling 和流式输出
 */
@Component
public class QwenProvider extends AbstractOpenAICompatibleProvider {

    @Override
    public String getProviderId() {
        return "qwen";
    }

    @Override
    protected String getDefaultApiUrl() {
        return "https://dashscope.aliyuncs.com/compatible-mode/v1";
    }
}
