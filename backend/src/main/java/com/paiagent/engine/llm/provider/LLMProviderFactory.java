package com.paiagent.engine.llm.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM Provider 工厂
 *
 * 通过 Spring 的服务加载器模式，自动发现并注册所有 LLMProvider 实现。
 * 与 NodeExecutorFactory 使用相同的模式。
 *
 * 添加新提供商只需：
 * 1. 实现 LLMProvider 接口
 * 2. 添加 @Component 注解
 * 无需修改任何其他类。
 */
@Slf4j
@Component
public class LLMProviderFactory {

    private final Map<String, LLMProvider> providers = new HashMap<>();

    @Autowired
    public LLMProviderFactory(List<LLMProvider> providerList) {
        for (LLMProvider provider : providerList) {
            String providerId = provider.getProviderId();
            providers.put(providerId, provider);
            log.info("注册 LLM Provider: {} -> {}", providerId, provider.getClass().getSimpleName());
        }
        log.info("LLM Provider Factory 初始化完成，共 {} 个提供商", providers.size());
    }

    /**
     * 根据提供商 ID 获取 LLMProvider 实例
     *
     * @param providerId 提供商 ID（如 "openai", "deepseek"）
     * @return LLMProvider 实例
     * @throws IllegalArgumentException 如果未找到匹配的提供商
     */
    public LLMProvider getProvider(String providerId) {
        LLMProvider provider = providers.get(providerId);
        if (provider == null) {
            throw new IllegalArgumentException(
                "不支持的 LLM Provider: " + providerId + "，可用的提供商: " + providers.keySet());
        }
        return provider;
    }

    /**
     * 获取所有已注册的提供商 ID
     *
     * @return 提供商 ID 集合
     */
    public java.util.Set<String> getAvailableProviderIds() {
        return java.util.Collections.unmodifiableSet(providers.keySet());
    }
}
