package com.paiagent.engine.llm.provider.impl;

import com.paiagent.engine.llm.provider.AbstractOpenAICompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * 智谱 (ZhiPu) Provider
 *
 * 通过智谱 AI 的 OpenAI 兼容接口访问 GLM 系列模型
 * 支持 Function Calling 和流式输出
 *
 * 此前在硬编码的 ChatClientFactory 中不支持，现已通过扩展此 Provider 得到完整支持
 */
@Component
public class ZhiPuProvider extends AbstractOpenAICompatibleProvider {

    @Override
    public String getProviderId() {
        return "zhipu";
    }

    @Override
    protected String getDefaultApiUrl() {
        return "https://open.bigmodel.cn/api/paas/v4";
    }
}
