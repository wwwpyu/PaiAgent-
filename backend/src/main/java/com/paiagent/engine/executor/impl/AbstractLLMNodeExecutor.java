package com.paiagent.engine.executor.impl;

import com.paiagent.dto.ExecutionEvent;
import com.paiagent.engine.executor.NodeExecutor;
import com.paiagent.engine.llm.ChatClientFactory;
import com.paiagent.engine.llm.LLMNodeConfig;
import com.paiagent.engine.llm.PromptTemplateService;
import com.paiagent.engine.llm.conversation.ConversationMessage;
import com.paiagent.engine.llm.tool.ToolDefinition;
import com.paiagent.engine.model.WorkflowNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * LLM 节点执行器抽象基类（模板方法模式）
 *
 * <h3>模板方法生命周期</h3>
 * <ol>
 *   <li><b>提取配置</b> — {@link #extractConfig(WorkflowNode)} 从节点数据中解析 LLMNodeConfig</li>
 *   <li><b>模板处理</b> — 通过 {@link PromptTemplateService} 替换 {{variable}} 占位符</li>
 *   <li><b>创建客户端</b> — 通过 {@link ChatClientFactory} 创建 ChatClient（含工具注册）</li>
 *   <li><b>调用 LLM</b> — {@link #executeNormal(ChatClient, String, boolean)} 或 {@link #executeStreaming(ChatClient, String, WorkflowNode, java.util.function.Consumer)}</li>
 *   <li><b>构建输出</b> — {@link #buildOutput(LLMResponse, List)} 组装输出 Map</li>
 * </ol>
 *
 * <h3>子类覆写点</h3>
 * 子类仅需实现 {@link #getNodeType()} 返回节点类型标识即可获得完整的 LLM 执行能力。
 *
 * <p>支持的子类：OpenAI, DeepSeek, Qwen, ZhiPu, AIPing — 每个约 10 行代码。</p>
 */
@Slf4j
public abstract class AbstractLLMNodeExecutor implements NodeExecutor {

    /** Function Calling 最大轮次，防止无限循环 */
    private static final int MAX_TOOL_CALL_ROUNDS = 3;

    @Autowired
    protected ChatClientFactory chatClientFactory;

    @Autowired
    protected PromptTemplateService promptTemplateService;

    /**
     * 获取节点类型标识
     * 子类必须实现，返回与前端/数据库一致的节点类型字符串
     *
     * @return 节点类型（如 "openai", "deepseek", "qwen", "zhipu", "ai_ping"）
     */
    protected abstract String getNodeType();

    @Override
    public Map<String, Object> execute(WorkflowNode node, Map<String, Object> input) throws Exception {
        return execute(node, input, null);
    }

    @Override
    public Map<String, Object> execute(WorkflowNode node, Map<String, Object> input,
                                       java.util.function.Consumer<ExecutionEvent> progressCallback) throws Exception {
        // 1. 提取节点配置
        LLMNodeConfig config = extractConfig(node);

        log.info("{} 节点配置 - API: {}, Model: {}, Temperature: {}",
                getNodeType().toUpperCase(), config.getApiUrl(), config.getModel(), config.getTemperature());
        log.info("{} 输入参数配置: {}", getNodeType().toUpperCase(), config.getInputParams());
        log.info("{} 输入数据: {}", getNodeType().toUpperCase(), input);

        // 2. 处理 prompt 模板
        String finalPrompt = promptTemplateService.processTemplate(
                config.getPromptTemplate(),
                config.getInputParams(),
                input
        );
        log.info("最终提示词: {}", finalPrompt);

        // 3. 创建 ChatClient（含工具注册）
        ChatClient chatClient = chatClientFactory.createClient(
                getNodeType(),
                config.getApiUrl(),
                config.getApiKey(),
                config.getModel(),
                config.getTemperature(),
                config.getTools(),
                config.getConversationHistory()
        );

        // 4. 调用 LLM（支持流式/非流式，支持 Function Calling）
        boolean hasTools = config.getTools() != null && !config.getTools().isEmpty();
        LLMResponse llmResponse;
        if (config.isStreaming() && progressCallback != null) {
            llmResponse = executeStreaming(chatClient, finalPrompt, node, progressCallback);
        } else {
            llmResponse = executeNormal(chatClient, finalPrompt, hasTools);
        }

        log.info("{} API响应: {}", getNodeType().toUpperCase(), llmResponse.getContent());
        log.info("{} Token统计: 输入={}, 输出={}, 总计={}",
                getNodeType().toUpperCase(),
                llmResponse.getInputTokens(),
                llmResponse.getOutputTokens(),
                llmResponse.getTotalTokens());

        // 5. 构建输出
        Map<String, Object> output = buildOutput(llmResponse, config.getOutputParams());
        log.info("{} 节点输出: {}", getNodeType().toUpperCase(), output);

        return output;
    }

    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }

    /**
     * LLM 响应包装类
     */
    protected static class LLMResponse {
        private final String content;
        private final Integer inputTokens;
        private final Integer outputTokens;
        private final Integer totalTokens;

        public LLMResponse(String content, Integer inputTokens, Integer outputTokens, Integer totalTokens) {
            this.content = content;
            this.inputTokens = inputTokens != null ? inputTokens : 0;
            this.outputTokens = outputTokens != null ? outputTokens : 0;
            this.totalTokens = totalTokens != null ? totalTokens : (this.inputTokens + this.outputTokens);
        }

        public String getContent() { return content; }
        public Integer getInputTokens() { return inputTokens; }
        public Integer getOutputTokens() { return outputTokens; }
        public Integer getTotalTokens() { return totalTokens; }
    }

    /**
     * 普通（非流式）调用
     *
     * @param chatClient ChatClient 实例
     * @param prompt     用户提示词
     * @param hasTools   是否注册了工具函数
     * @return LLM 响应
     */
    private LLMResponse executeNormal(ChatClient chatClient, String prompt, boolean hasTools) {
        var chatResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        int functionCallRounds = 0;

        // Function Calling 循环：处理 LLM 返回的工具调用请求
        while (hasTools && functionCallRounds < MAX_TOOL_CALL_ROUNDS) {
            var assistantMessage = chatResponse.getResult().getOutput();
            var toolCalls = assistantMessage.getToolCalls();

            if (toolCalls == null || toolCalls.isEmpty()) {
                break; // 无需工具调用，结束循环
            }

            log.info("{} 检测到 {} 个工具调用请求（第 {} 轮）",
                    getNodeType().toUpperCase(), toolCalls.size(), functionCallRounds + 1);

            // 在下一轮调用中追加工具调用上下文
            // Spring AI 的 ChatClient 通过 .functions() 自动处理工具回调
            // 此处仅需重新调用；工具结果由 Spring AI 自动注入
            chatResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .chatResponse();

            functionCallRounds++;
        }

        if (functionCallRounds >= MAX_TOOL_CALL_ROUNDS) {
            log.warn("{} 达到最大函数调用轮次 ({})，停止继续调用",
                    getNodeType().toUpperCase(), MAX_TOOL_CALL_ROUNDS);
        }

        String content = chatResponse.getResult().getOutput().getContent();

        // 提取 token 统计
        var metadata = chatResponse.getMetadata();
        Integer inputTokens = null;
        Integer outputTokens = null;
        Integer totalTokens = null;

        if (metadata != null && metadata.getUsage() != null) {
            var usage = metadata.getUsage();
            inputTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().intValue() : null;
            outputTokens = usage.getGenerationTokens() != null ? usage.getGenerationTokens().intValue() : null;
            totalTokens = usage.getTotalTokens() != null ? usage.getTotalTokens().intValue() : null;
        }

        return new LLMResponse(content, inputTokens, outputTokens, totalTokens);
    }

    /**
     * 流式调用
     */
    private LLMResponse executeStreaming(ChatClient chatClient, String prompt,
                                    WorkflowNode node, java.util.function.Consumer<ExecutionEvent> progressCallback) {
        StringBuilder accumulated = new StringBuilder();

        // 注意：流式调用时无法获取 token 统计，因为 metadata 在流式模式下不可用
        chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    accumulated.append(chunk);
                    if (progressCallback != null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("chunk", chunk);
                        data.put("accumulated", accumulated.toString());
                        progressCallback.accept(
                                ExecutionEvent.nodeProgress(node.getId(), node.getType(),
                                        "生成中...", data)
                        );
                    }
                })
                .blockLast();

        // 流式调用无 token 统计
        return new LLMResponse(accumulated.toString(), null, null, null);
    }

    /**
     * 从节点数据中提取配置
     */
    @SuppressWarnings("unchecked")
    protected LLMNodeConfig extractConfig(WorkflowNode node) {
        Map<String, Object> data = node.getData();

        LLMNodeConfig config = new LLMNodeConfig();
        config.setApiUrl(trimString(data.get("apiUrl")));
        config.setApiKey(trimString(data.get("apiKey")));
        config.setModel(trimString(data.get("model")));
        config.setTemperature(data.get("temperature") != null
                ? ((Number) data.get("temperature")).doubleValue()
                : 0.7);
        config.setPromptTemplate((String) data.get("prompt"));
        config.setInputParams((List<Map<String, Object>>) data.get("inputParams"));
        config.setOutputParams((List<Map<String, Object>>) data.get("outputParams"));
        config.setStreaming(Boolean.TRUE.equals(data.get("streaming")));

        // 新增：系统提示词
        config.setSystemPrompt(trimString(data.get("systemPrompt")));

        // 新增：maxTokens
        if (data.get("maxTokens") != null) {
            config.setMaxTokens(((Number) data.get("maxTokens")).intValue());
        }

        // 新增：topP
        if (data.get("topP") != null) {
            config.setTopP(((Number) data.get("topP")).doubleValue());
        }

        // 新增：工具定义（从节点数据反序列化）
        List<Map<String, Object>> toolsData = (List<Map<String, Object>>) data.get("tools");
        if (toolsData != null && !toolsData.isEmpty()) {
            List<ToolDefinition> tools = new ArrayList<>();
            for (Map<String, Object> toolData : toolsData) {
                ToolDefinition tool = new ToolDefinition();
                tool.setName((String) toolData.get("name"));
                tool.setDescription((String) toolData.get("description"));
                tool.setParameters((Map<String, Object>) toolData.get("parameters"));
                // handler 需要在运行时由调用方注入
                // 此处只解析元数据，handler 由 ChatClientFactory 的 createClient 处理
                tools.add(tool);
            }
            config.setTools(tools);
        }

        // 新增：对话历史（从全局上下文状态提取）
        // conversationHistory 在 LangGraph 状态中由 StateManager / NodeAdapter 维护
        List<Map<String, Object>> historyData = (List<Map<String, Object>>) data.get("conversationHistory");
        if (historyData != null && !historyData.isEmpty()) {
            List<ConversationMessage> history = new ArrayList<>();
            for (Map<String, Object> msgData : historyData) {
                ConversationMessage msg = new ConversationMessage();
                String roleStr = (String) msgData.get("role");
                if (roleStr != null) {
                    msg.setRole(ConversationMessage.Role.valueOf(roleStr.toUpperCase()));
                }
                msg.setContent((String) msgData.get("content"));
                msg.setToolCallId((String) msgData.get("toolCallId"));
                history.add(msg);
            }
            config.setConversationHistory(history);
        }

        return config;
    }

    /**
     * 构建输出结果
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> buildOutput(LLMResponse llmResponse, List<Map<String, Object>> outputParams) {
        Map<String, Object> output = new HashMap<>();

        String content = llmResponse.getContent();

        if (outputParams != null && !outputParams.isEmpty()) {
            for (Map<String, Object> param : outputParams) {
                String paramName = (String) param.get("name");
                output.put(paramName, content);
            }
        } else {
            output.put("output", content);
        }

        // 添加 token 统计
        output.put("inputTokens", llmResponse.getInputTokens());
        output.put("outputTokens", llmResponse.getOutputTokens());
        output.put("totalTokens", llmResponse.getTotalTokens());
        output.put("tokens", llmResponse.getTotalTokens()); // 保持向后兼容

        return output;
    }

    /**
     * 去除字符串两端空格
     */
    private String trimString(Object value) {
        return value != null ? value.toString().trim() : null;
    }

    @Override
    public String getSupportedNodeType() {
        return getNodeType();
    }
}
