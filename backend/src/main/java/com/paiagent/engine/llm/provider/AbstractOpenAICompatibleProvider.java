package com.paiagent.engine.llm.provider;

import com.paiagent.engine.llm.tool.ToolDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI 兼容的 Provider 抽象基类
 *
 * 所有使用 OpenAI 兼容 API 的提供商（OpenAI, DeepSeek, Qwen, ZhiPu, AIPing）
 * 都继承此类，只需覆盖 getProviderId() 并提供默认 API URL。
 *
 * 子类可以覆盖 createChatModel()、supportsFunctionCalling()、registerFunctions()
 * 来适配各自 API 的差异。
 */
@Slf4j
public abstract class AbstractOpenAICompatibleProvider implements LLMProvider {

    /**
     * 获取默认的 API 端点 URL
     * 子类必须提供
     */
    protected abstract String getDefaultApiUrl();

    @Override
    public ChatModel createChatModel(ProviderConfig config) {
        String apiUrl = config.getApiUrl() != null ? config.getApiUrl() : getDefaultApiUrl();
        String apiKey = config.getApiKey();
        String model = config.getModel();
        Double temperature = config.getTemperature() != null ? config.getTemperature() : 0.7;

        log.debug("创建 {} ChatModel: url={}, model={}, temperature={}",
                getProviderId(), apiUrl, model, temperature);

        OpenAiApi openAiApi = new OpenAiApi(apiUrl, apiKey);

        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature);

        if (config.getMaxTokens() != null) {
            optionsBuilder.maxTokens(config.getMaxTokens());
        }
        if (config.getTopP() != null) {
            optionsBuilder.topP(config.getTopP());
        }

        return new OpenAiChatModel(openAiApi, optionsBuilder.build());
    }

    @Override
    public boolean supportsFunctionCalling() {
        return true; // OpenAI 兼容 API 通常支持函数调用
    }

    @Override
    public org.springframework.ai.chat.client.ChatClient registerFunctions(
            org.springframework.ai.chat.client.ChatClient chatClient,
            List<ToolDefinition> tools) {

        if (tools == null || tools.isEmpty()) {
            return chatClient;
        }

        List<FunctionCallback> functionCallbacks = new ArrayList<>();
        for (ToolDefinition tool : tools) {
            FunctionCallback callback = createFunctionCallback(tool);
            functionCallbacks.add(callback);
        }

        log.debug("为 Provider [{}] 注册 {} 个工具函数", getProviderId(), functionCallbacks.size());
        return chatClient.mutate().defaultFunctions(functionCallbacks.toArray(new FunctionCallback[0])).build();
    }

    /**
     * 将 ToolDefinition 转换为 Spring AI FunctionCallback
     */
    @SuppressWarnings("unchecked")
    protected FunctionCallback createFunctionCallback(ToolDefinition tool) {
        return FunctionCallback.builder()
            .function(tool.getName(), (java.util.function.Function<java.util.Map<String, Object>, String>) input -> {
                try {
                    return tool.getHandler().apply(input);
                } catch (Exception e) {
                    log.error("工具函数 [{}] 执行失败", tool.getName(), e);
                    return "工具执行错误: " + e.getMessage();
                }
            })
            .description(tool.getDescription())
            .build();
    }
}
